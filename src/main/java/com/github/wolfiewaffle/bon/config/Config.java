package com.github.wolfiewaffle.bon.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public class Config {

    public static ForgeConfigSpec CLIENT_CONFIG;
    public static ForgeConfigSpec COMMON_CONFIG;
    //public static ForgeConfigSpec SERVER_CONFIG;

    public static ForgeConfigSpec.BooleanValue clientOption;

    public static ForgeConfigSpec.DoubleValue baseSpeedModifier;

    public static void init() {
        //initServer();
        initCommon();
        //initClient();

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, COMMON_CONFIG);
        //ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, CLIENT_CONFIG);
    }

    private static void initServer() {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
    }

    private static void initCommon() {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.comment("General Settings").push("general");
        baseSpeedModifier = builder.comment("Multiply the speed at which you change temperature.").defineInRange("baseSpeedModifier", 1.0, 0, Double.MAX_VALUE);
        builder.pop();

        COMMON_CONFIG = builder.build();
    }

    private static void initClient() {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.comment("General Settings").push("general");
        clientOption = builder.comment("Client option description.").define("clientOption", false);
        builder.pop();

        CLIENT_CONFIG = builder.build();
    }

}
