package com.github.wolfiewaffle.bon.event.player;

import com.github.wolfiewaffle.bon.capability.temperature.BodyTemp;
import com.github.wolfiewaffle.bon.capability.temperature.IBodyTemp;
import com.github.wolfiewaffle.bon.capability.temperature.TempModifier;
import com.github.wolfiewaffle.bon.compat.CompatSereneSeasons;
import com.github.wolfiewaffle.bon.config.Config;
import com.github.wolfiewaffle.bon.network.BonNetworkInit;
import com.github.wolfiewaffle.bon.network.BonPacket;
import com.github.wolfiewaffle.bon.tools.BONMath;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.network.PacketDistributor;
import oshi.util.tuples.Pair;
import toughasnails.api.enchantment.TANEnchantments;
import toughasnails.api.potion.TANEffects;
import toughasnails.api.temperature.ITemperature;
import toughasnails.api.temperature.TemperatureHelper;
import toughasnails.api.temperature.TemperatureLevel;

import java.util.ArrayList;

public class PlayerTickEventHandler {
    public static final float TICK_RATE_MULTIPLIER = 0.025f;
    private static final int BIOME_RANGE_CHUNKS = 3;
    private static final int HELMET_SLOT = 3;

    @SubscribeEvent
    public void handleEvent(TickEvent.PlayerTickEvent event) {
        if (event.side.isClient()) return;

        LazyOptional<IBodyTemp> tempLazyOptional = event.player.getCapability(BodyTemp.INSTANCE, null);
        tempLazyOptional.ifPresent(data -> {
            Player player = event.player;
            ITemperature TANTemp = TemperatureHelper.getTemperatureData(player);

            ArrayList<TempModifier> modList = new ArrayList<>();
            Pair<Float, Float> biomeData = getBiomeData(player);
            float currentTemp = data.getTemp();
            float target = 0f;
            float tempDiff;

            // Add all modifiers
            TempModifier biomeMod = getBiomeMod(biomeData.getA());
            target += biomeMod.apply(modList);

            TempModifier weatherMod = getWeatherMod(player);
            target += weatherMod.apply(modList);

            TempModifier blockMod = getBlockMod(player, weatherMod.getTargetMod());
            target += blockMod.apply(modList);

            TempModifier armorMod = ArmorProvider.get(player, currentTemp, target);
            target += armorMod.apply(modList);

            TempModifier sunMod = getSunMod(player, biomeData.getB());
            target += sunMod.apply(modList);

            // Ambient Temp
            float ambientTemp = biomeMod.getTargetMod() + blockMod.getTargetMod() + sunMod.getTargetMod() + weatherMod.getTargetMod();

            TempModifier insulationMod = getInsulationMod(ambientTemp, armorMod.getInsulation());
            target += insulationMod.apply(modList);

            TempModifier reflectionMod = getReflectionMod(player, sunMod.getTargetMod(), currentTemp);
            target += reflectionMod.apply(modList);

            TempModifier sweatMod = getSweatMod(currentTemp, target, biomeData.getB());
            target += sweatMod.apply(modList);

            // Serene Seasons compat
            if (ModList.get().isLoaded("sereneseasons")) {
                TempModifier seasonMod = getSeasonMod(player.level);
                target += seasonMod.apply(modList);
            }


            // Set target and difference
            tempDiff = target - currentTemp;

            // Move towards target
            float changeAmount;

            // Calculate change
            changeAmount = tempDiff / 150;
            changeAmount *= Config.baseSpeedModifier.get();
            //changeAmount = BONMath.signMin(changeAmount, BodyTemp.MIN_CHANGE_AMOUNT);
            changeAmount = (float) Math.pow(changeAmount, 2) * Math.signum(changeAmount);
            changeAmount *= PlayerTickEventHandler.TICK_RATE_MULTIPLIER; // This ensures 1 changeAmount = 1 degree over one second

            // Apply change
            currentTemp += changeAmount;
            if (Math.abs(changeAmount) > Math.abs(tempDiff)) currentTemp = target; // Prevent overshooting

            // Effects
            if (player.hasEffect(TANEffects.CLIMATE_CLEMENCY)) {
                currentTemp = 70f;
            } else {
                if (player.hasEffect(MobEffects.FIRE_RESISTANCE) && currentTemp > 80f) currentTemp = 80f;
                if (player.hasEffect(TANEffects.ICE_RESISTANCE) && currentTemp < 60f) currentTemp = 60f;
            }

            // Apply
            BodyTemp.MOD_MAP.put(player, modList);
            data.setTemp(currentTemp);
            data.setTargetTemp(target);

            // TAN Integration
            if (currentTemp <= 50f) TANTemp.setTargetLevel(TemperatureLevel.ICY);
            if (currentTemp <= 50f) TANTemp.setLevel(TemperatureLevel.ICY);
            if (currentTemp > 50f && currentTemp < 65f) TANTemp.setTargetLevel(TemperatureLevel.COLD);
            if (currentTemp > 50f && currentTemp < 65f) TANTemp.setLevel(TemperatureLevel.COLD);
            if (currentTemp >= 65f && currentTemp <= 75f) TANTemp.setTargetLevel(TemperatureLevel.NEUTRAL);
            if (currentTemp >= 65f && currentTemp <= 75f) TANTemp.setLevel(TemperatureLevel.NEUTRAL);
            if (currentTemp > 75f && currentTemp < 90f) TANTemp.setTargetLevel(TemperatureLevel.WARM);
            if (currentTemp > 75f && currentTemp < 90f) TANTemp.setLevel(TemperatureLevel.WARM);
            if (currentTemp >= 90f) TANTemp.setTargetLevel(TemperatureLevel.HOT);
            if (currentTemp >= 90f) TANTemp.setLevel(TemperatureLevel.HOT);

            // Networking
                if (player instanceof ServerPlayer) {
                    BonNetworkInit.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), new BonPacket(currentTemp, target));
                }
        });
    }

    private TempModifier getSeasonMod(Level world) {
        return new TempModifier("season", CompatSereneSeasons.getSeasonMod(world));
    }

    private TempModifier getWeatherMod(Player player) {
        BlockPos pos = player.blockPosition();
        Level world = player.getLevel();

        if (world.isRainingAt(pos.above())) {
            return new TempModifier("weather", -25f);
        } else {
            return new TempModifier("weather", 0f);
        }
    }

    private TempModifier getBlockMod(Player player, float currentWetness) {
        BlockPos pos = player.blockPosition();
        Level world = player.getLevel();
        int radius = 3;
        float totalTemp = 0f;
//        float totalBlocks = 0f;
        float hottest = 0f;
        float coldest = 0f;

//        int totalBlocks = (int) Math.pow((radius + radius + 1), radius);

        for (int x = -radius; x < radius * 2 - 1; x++) {
            for (int y = -radius; y < radius * 2 - 1; y++) {
                for (int z = -radius; z < radius * 2 -1; z++) {
                    BlockState state = world.getBlockState(new BlockPos(pos.getX() + x, pos.getY() + y, pos.getZ() + z));
                    Block block = state.getBlock();

                    if (BodyTemp.BLOCK_MODS.containsKey(block)) {

                        if (block == Blocks.CAMPFIRE && state.getValue(BlockStateProperties.LIT) == false) continue;

                        // Get distance by the largest absolute value of an axis
                        float addedHeat = BodyTemp.BLOCK_MODS.get(block) * 0.25f * (radius + 1 - (Math.max(Math.abs(y), Math.max(Math.abs(x), Math.abs(z)))));
                        if (addedHeat > hottest) hottest = addedHeat;
                        if (addedHeat < coldest) coldest = addedHeat;

                        totalTemp += addedHeat;
                        //totalBlocks++;
                    }
                }
            }
        }

        float water = 0f;
        if (world.getBlockState(pos).getBlock() == Blocks.WATER) water -= 10;
        if (world.getBlockState(pos.above(1)).getBlock() == Blocks.WATER) water -= 15;

        return new TempModifier("block", BONMath.clamp(totalTemp, coldest, hottest) + Math.min(water - currentWetness, 0)); // max prevents / by 0
    }

    private TempModifier getSunMod(Player player, float biomeDownfall) {
        Level world = player.level;
        BlockPos pos = player.getOnPos();
        // quick maths
        float timeFactor = 1 - (Math.abs(BONMath.clamp(6000 - world.getDayTime(), -6000, 6000)) / 6000);
        float sunLevel = world.getBrightness(LightLayer.SKY, pos.above());

        // Being without any shade adds 5
        if (sunLevel >= 15) sunLevel += 5;
        sunLevel *= 2.5;

        // Multiply by inverted downfall, less downfall more intense sun
        sunLevel *= (1 - biomeDownfall);
        if (sunLevel > 0) sunLevel += 4; // Just brings up the minimum

        // Max 3 sun if it is raining
        if (world.isRaining() && sunLevel > 3) {
            sunLevel = 3;
        }

        if (world.isNight()) {
            return new TempModifier("sun", -44f);
        } else {
            return new TempModifier("sun", Math.max(0, sunLevel * timeFactor));
        }
    }

    private TempModifier getReflectionMod(Player player, float sunLevel, float currentTemp) {
        float insulation = 0f;

        // No reflection if no sunlight
        if (sunLevel < 0) sunLevel = 0;

        // Account for insulation level of the armor, extra effect for helmet
        Iterable<ItemStack> armor = player.getArmorSlots();

        // For each armor item
        int slot = 0;
        for (ItemStack stack : armor) {
            if (stack != ItemStack.EMPTY) {
                Item item = stack.getItem();

                // Get armor values
                Float[] armorData = ArmorProvider.getModWithLiner(stack);

                float addInsulation;
                // 1 is heat insulation
                addInsulation = Math.min(1, armorData[1]); // Cap material insulation at 1
                // Any covering is significant, unless the material is extreme
                addInsulation += 3;
                if (slot == HELMET_SLOT) addInsulation *= 3; // Helmets are extra important

                // Thermal Tuning
                float optimalDirection = BodyTemp.OPTIMAL_TEMP - currentTemp;
                if (optimalDirection <= 0f) {
                    if (EnchantmentHelper.getItemEnchantmentLevel(TANEnchantments.THERMAL_TUNING, stack) > 0) addInsulation = 0f;
                }

                insulation += addInsulation;
            }
            slot ++;
        }

        // negative insulation will increase sun heat, especially on helmets
        // 1 insulation is equivalent to taking off 3.3% of sun factor
        // each armor also gets +2 base "sun insulation" above
        float heatReduction = (sunLevel * (insulation / 30));
        if (heatReduction > sunLevel) heatReduction = sunLevel; // Cannot have net negative sun effect

        return new TempModifier("armor reflection", heatReduction * -1);
    }

    private TempModifier getInsulationMod(float ambientTemp, float insulation) {
        float ambientDiff = ambientTemp - BodyTemp.OPTIMAL_TEMP;
        float insulationPercent = (ambientDiff * (insulation / 40)) * -1;

        return new TempModifier("insulation", insulationPercent);
    }

    private TempModifier getBiomeMod(float mod) {
        return new TempModifier("biome", mod);
    }

    /**
     * rainfall of biome effects sweat mod (if above freezing)
     * @param currentTemp
     * @param humidity 0 - 1 rainfall of biome
     * @return
     */
    private TempModifier getSweatMod(float currentTemp, float targetTemp, float humidity) {
        /*
            Add sweat/humidity cooling
            Get a number inversely correlated with biome rainfall (if above freezing)
            This represents how humid the air is, and how effective sweating is
        */
        if (currentTemp > BodyTemp.OPTIMAL_TEMP) {
            float maxSweatPoint = 130f;
            float maxSweat = 25f;
            float humidFactor = Math.max(0, 1 - humidity);
            float extremityFactor = BONMath.clamp(targetTemp, 0f, maxSweatPoint) / maxSweatPoint; // 0 - 1

            return new TempModifier("sweat", extremityFactor * humidFactor * -maxSweat);
        } else {
            return new TempModifier("sweat", 0f);
        }
    }

    private Pair<Float, Float> getBiomeData(Player player) {
        Level world = player.level;
        BlockPos pos = player.getOnPos();
        float totalTemp = 0f;
        float totalDownfall = 0f;

        ArrayList<Biome> biomeList = new ArrayList<>();

        for (int x = -BIOME_RANGE_CHUNKS; x < BIOME_RANGE_CHUNKS + 1; x++) {
            for (int z = -BIOME_RANGE_CHUNKS; z < BIOME_RANGE_CHUNKS + 1; z++) {
                BlockPos newPos = pos.offset(x * 16, 0, z * 16);
                if (world.isLoaded(newPos)) biomeList.add(world.getBiome(newPos).value());
            }
        }

        for (Biome biome : biomeList) {
            totalTemp += biome.getBaseTemperature();
            totalDownfall += biome.getDownfall();
        }

        return new Pair<>((totalTemp / biomeList.size()) * 80 + 20, totalDownfall / biomeList.size());
    }
}
