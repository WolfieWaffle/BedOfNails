package com.github.wolfiewaffle.bon.event.player;

import com.github.wolfiewaffle.bon.capability.temperature.BodyTemp;
import com.github.wolfiewaffle.bon.capability.temperature.TempModifier;
import com.github.wolfiewaffle.bon.config.Config;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.registries.ForgeRegistries;
import toughasnails.api.enchantment.TANEnchantments;

public class ArmorProvider {

    /**
     * Should be gotten after ambient temperature is determined
     * @param player
     * @param ambientTemp
     * @return
     */
    public static TempModifier getOld(Player player, float currentTemp, float ambientTemp) {
        Iterable<ItemStack> slots = player.getArmorSlots();
        float linerFactor = 1f;
        float ambientDirection = ambientTemp - currentTemp;
        float optimalDirection = BodyTemp.OPTIMAL_TEMP - currentTemp;
        float totalMod = 0f;
        float totalInsulation = 0f;

        for (ItemStack stack : slots) {
            Item item = stack.getItem();

            if (BodyTemp.ARMOR_MODS.containsKey(item)) {
                /*
                    TEMP MOD
                 */
                float mod = BodyTemp.ARMOR_MODS.get(item)[0];

                // Apply liners
                float linerMod = mod;
                Float[] linerArray = getLinerMod(stack);
                if (linerArray != null) {
                    linerMod = linerArray[0];
                }
                mod = ((1f - linerFactor) * mod) + (linerFactor * linerMod);
                System.out.println(mod + " TOT " + linerMod);

                // Thermal Tuning: Nullify mod if it points away from optimal temperature
                if (EnchantmentHelper.getItemEnchantmentLevel(TANEnchantments.THERMAL_TUNING, stack) > 0) {
                    if (Math.signum(mod) != Math.signum(optimalDirection)) mod = 0f;
                }
                totalMod += mod;

                /*
                    INSULATION MOD
                 */
                // Index is 1 for heat insulation, 2 for cold insulation
                int insulIndex = 1;
                if (ambientDirection < 0) insulIndex++;
                float insulation = BodyTemp.ARMOR_MODS.get(item)[insulIndex];
                if (ambientDirection == 0) insulation = 0f; // No insulation effect if we match ambient

                // Liners
                float linerInsulMod = insulation;
                if (linerArray != null) {
                    linerInsulMod = linerArray[insulIndex];
                }
                insulation = ((1f - linerFactor) * insulation) + (linerFactor * linerInsulMod);
                System.out.println(insulation + " ISN " + linerInsulMod);

                // Thermal Tuning: Nullify insulation if it is bringing you away from optimal temperature
                if (EnchantmentHelper.getItemEnchantmentLevel(TANEnchantments.THERMAL_TUNING, stack) > 0) {
                    // Insulation is based on ambient, so if ambient matches optimal, it should be good
                    // HOWEVER insulation REDUCES ambient effects, so we keep it if it DOESN'T match
                    // We must also multiply by the sign of the insulation, since it can be negative
                    if (Math.signum(ambientDirection * Math.signum(insulation)) == Math.signum(optimalDirection)) insulation = 0f;
                }
                totalInsulation += insulation;
            }
        }

        return new TempModifier("armor", totalMod, totalInsulation);
    }

    public static TempModifier get(Player player, float currentTemp, float ambientTemp) {
        Iterable<ItemStack> slots = player.getArmorSlots();
        float totalMod = 0f;
        float totalInsulation = 0f;

        for (ItemStack stack : slots) {
            float baseFactor = (float) (1 - Config.linerFactor.get());
            Item item = stack.getItem();

            Float[] armorData = getMod(item);
            Float[] linerData = new Float[]{0f, 0f};
            Item linerItem = getLinerItem(stack);

            if (linerItem != Items.AIR) {
                linerData = getMod(linerItem);
            } else {
                baseFactor = 1f;
            }

            totalMod += (baseFactor * armorData[0]) + (Config.linerFactor.get() * linerData[0]);
            totalInsulation += (baseFactor * armorData[1]) + (Config.linerFactor.get() * linerData[1]);
        }

        return new TempModifier("armor", totalMod, totalInsulation);
    }

    public static Float[] getModWithLiner(ItemStack stack) {
        float itemMod = 0f;
        float itemInsulation = 0f;
        float baseFactor = (float) (1 - Config.linerFactor.get());
        Item item = stack.getItem();

        Float[] armorData = getMod(item);
        Float[] linerData = new Float[]{0f, 0f};
        Item linerItem = getLinerItem(stack);

        if (linerItem != Items.AIR) {
            linerData = getMod(linerItem);
        } else {
            baseFactor = 1f;
        }

        itemMod += (baseFactor * armorData[0]) + (Config.linerFactor.get() * linerData[0]);
        itemInsulation += (baseFactor * armorData[1]) + (Config.linerFactor.get() * linerData[1]);

        return new Float[]{itemMod, itemInsulation};
    }

    public static Float[] getMod(Item item) {
        Float[] mod = new Float[]{0f, 0f};

        if (item != null && BodyTemp.ARMOR_MODS.containsKey(item)) {
            mod[0] = BodyTemp.ARMOR_MODS.get(item)[0];
            mod[1] = BodyTemp.ARMOR_MODS.get(item)[1];
        }

        return mod;
    }

    public static Item getLinerItem(ItemStack stack) {
        CompoundTag tag = stack.getTag();

        if (tag != null) {
            String liner = tag.getString("LinerItem");

            if (liner != null) {
                if (ResourceLocation.isValidResourceLocation(liner)) {
                    Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(liner));

                    return item;
                }
            }
        }

        return Items.AIR;
    }

    private static Float[] getLinerMod(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        Float[] mod = new Float[2];

        if (tag != null) {
            String liner = tag.getString("LinerItem");

            if (liner != null) {
                if (ResourceLocation.isValidResourceLocation(liner)) {
                    Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(liner));

                    if (item != null && BodyTemp.ARMOR_MODS.containsKey(item)) {
                        mod[0] = BodyTemp.ARMOR_MODS.get(item)[0];
                        mod[1] = BodyTemp.ARMOR_MODS.get(item)[1];
                        return mod;
                    }
                }
            }
        }

        return null;
    }
}
