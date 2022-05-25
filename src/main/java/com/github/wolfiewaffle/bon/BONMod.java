package com.github.wolfiewaffle.bon;

import com.github.wolfiewaffle.bon.capability.BONCapabilityAttacher;
import com.github.wolfiewaffle.bon.capability.temperature.BodyTemp;
import com.github.wolfiewaffle.bon.client.ClientProxy;
import com.github.wolfiewaffle.bon.config.Config;
import com.github.wolfiewaffle.bon.event.player.ArmorProvider;
import com.github.wolfiewaffle.bon.event.player.PlayerTickEventHandler;
import com.github.wolfiewaffle.bon.network.BonNetworkInit;
import com.github.wolfiewaffle.bon.recipe.LinerRecipe;
import com.github.wolfiewaffle.bon.tools.command.BONCommands;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(BONMod.MOD_ID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = BONMod.MOD_ID)
public class BONMod {

    public static final String MOD_ID = "bon";

    public static boolean showDisplay;

    // Recipe Types
    private static final DeferredRegister<RecipeType<?>> RECIPE_TYPE_DEFERRED_REGISTER = DeferredRegister.create(Registry.RECIPE_TYPE_REGISTRY, MOD_ID);
    public static final RegistryObject<RecipeType<LinerRecipe>> LINER_RECIPE = RECIPE_TYPE_DEFERRED_REGISTER.register("liner", () -> new RecipeType<>() {});
    public static final RecipeSerializer<LinerRecipe> linerRecipeRecipeSerializer = new LinerRecipe.Serializer();

    public BONMod() {
        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(BodyTemp::register);

        // Config
        Config.init();

        // For recipe types
        RECIPE_TYPE_DEFERRED_REGISTER.register(FMLJavaModLoadingContext.get().getModEventBus());
        linerRecipeRecipeSerializer.setRegistryName(new ResourceLocation("bon:liner"));

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new PlayerTickEventHandler());
        MinecraftForge.EVENT_BUS.register(BONCapabilityAttacher.class);

        // Client
        if (FMLEnvironment.dist == Dist.CLIENT) {
            ClientProxy.registerClientEvents();
        }
    }

    private void setup(final FMLCommonSetupEvent event) {
        MinecraftForge.EVENT_BUS.register(BONCommands.class);

        BodyTemp.initArmorValues();
        BodyTemp.initBlockValues();
        BonNetworkInit.register();
    }

    @SubscribeEvent
    public void tooltipEvent(final ItemTooltipEvent event) {
        Item item = ArmorProvider.getLinerItem(event.getItemStack());

        if (item != Items.AIR) {
            MutableComponent component = new TextComponent("Lined with ").withStyle(ChatFormatting.GOLD);
            component.append(new ItemStack(item).getHoverName());
            event.getToolTip().add(component);
        }
    }

    @SubscribeEvent //ModBus, can't use addListener due to nested genetics.
    public static void registerRecipeSerialziers(RegistryEvent.Register<RecipeSerializer<?>> event) {
        event.getRegistry().register(linerRecipeRecipeSerializer);
    }
}
