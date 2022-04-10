package com.github.wolfiewaffle.bon.capability.temperature;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import toughasnails.api.item.TANItems;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Map.entry;

public class BodyTemp {
    public static final Capability<IBodyTemp> INSTANCE = CapabilityManager.get(new CapabilityToken<>() {});
    public static final float OPTIMAL_TEMP = 70f;
    public static Map<Player, List<TempModifier>> MOD_MAP = new HashMap<>();
    public static Map<Item, float[]> ARMOR_MODS;
    public static Map<Block, Float> BLOCK_MODS;

    @SubscribeEvent
    public static void register(RegisterCapabilitiesEvent event) {
        event.register(IBodyTemp.class);
    }

    // 1 is heat insulation, 2 is cold insulation
    public static void initArmorValues() {
        ARMOR_MODS = Map.ofEntries(
                entry(Items.LEATHER_HELMET, new float[]{        2.5f, -1f, 3f}),
                entry(Items.LEATHER_CHESTPLATE, new float[]{    2.5f, -1f, 3f}),
                entry(Items.LEATHER_LEGGINGS, new float[]{      2.5f, -1f, 3f}),
                entry(Items.LEATHER_BOOTS, new float[]{         2.5f, -1f, 3f}),
                entry(Items.IRON_HELMET, new float[]{           -0.5f, -4f, -4f}),
                entry(Items.IRON_CHESTPLATE, new float[]{       -0.5f, -4f, -4f}),
                entry(Items.IRON_LEGGINGS, new float[]{         -0.5f, -4f, -4f}),
                entry(Items.IRON_BOOTS, new float[]{            -0.5f, -4f, -4f}),
                entry(Items.DIAMOND_HELMET, new float[]{        -0.5f, 2f, 0f}),
                entry(Items.DIAMOND_CHESTPLATE, new float[]{    -0.5f, 2f, 0f}),
                entry(Items.DIAMOND_LEGGINGS, new float[]{      -0.5f, 2f, 0f}),
                entry(Items.DIAMOND_BOOTS, new float[]{         -0.5f, 2f, 0f}),
                entry(Items.CHAINMAIL_HELMET, new float[]{      -0.25f, -2f, -2f}),
                entry(Items.CHAINMAIL_CHESTPLATE, new float[]{  -0.25f, -2f, -2f}),
                entry(Items.CHAINMAIL_LEGGINGS, new float[]{    -0.25f, -2f, -2f}),
                entry(Items.CHAINMAIL_BOOTS, new float[]{       -0.25f, -2f, -2f}),
                entry(Items.GOLDEN_HELMET, new float[]{         0f, -6f, -6f}),
                entry(Items.GOLDEN_CHESTPLATE, new float[]{     0f, -6f, -6f}),
                entry(Items.GOLDEN_LEGGINGS, new float[]{       0f, -6f, -6f}),
                entry(Items.GOLDEN_BOOTS, new float[]{          0f, -6f, -6f}),
                entry(Items.NETHERITE_HELMET, new float[]{      1f, -4f, -4f}),
                entry(Items.NETHERITE_CHESTPLATE, new float[]{  1f, -4f, -4f}),
                entry(Items.NETHERITE_LEGGINGS, new float[]{    1f, -4f, -4f}),
                entry(Items.NETHERITE_BOOTS, new float[]{       1f, -4f, -4f}),
                entry(TANItems.LEAF_HELMET, new float[]{        -2.5f, 3f, 0f}),
                entry(TANItems.LEAF_CHESTPLATE, new float[]{    -2.5f, 3f, 0f}),
                entry(TANItems.LEAF_LEGGINGS, new float[]{      -2.5f, 3f, 0f}),
                entry(TANItems.LEAF_BOOTS, new float[]{         -2.5f, 3f, 0f}),
                entry(TANItems.WOOL_HELMET, new float[]{        5f, -2f, 6f}),
                entry(TANItems.WOOL_CHESTPLATE, new float[]{    5f, -2f, 6f}),
                entry(TANItems.WOOL_LEGGINGS, new float[]{      5f, -2f, 6f}),
                entry(TANItems.WOOL_BOOTS, new float[]{         5f, -2f, 6f})
        );
    }

    public static void initBlockValues() {
        BLOCK_MODS = Map.ofEntries(
                entry(Blocks.LAVA, 120f),
                entry(Blocks.CAMPFIRE, 70f),
                entry(Blocks.MAGMA_BLOCK, 50f),
                entry(Blocks.FIRE, 50f),
                entry(Blocks.LANTERN, 5f),
                entry(Blocks.TORCH, 5f),
                entry(Blocks.WALL_TORCH, 5f),
                entry(Blocks.SOUL_FIRE, -50f),
                entry(Blocks.ICE, -20f),
                entry(Blocks.PACKED_ICE, -20f),
                entry(Blocks.SNOW_BLOCK, -20f),
                entry(Blocks.POWDER_SNOW, -20f),
                entry(Blocks.SOUL_TORCH, -20f),
                entry(Blocks.SOUL_WALL_TORCH, -5f),
                entry(Blocks.SOUL_LANTERN, -5f)
        );
    }

    private BodyTemp() {
    }
}
