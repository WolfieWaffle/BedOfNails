package com.github.wolfiewaffle.bon.recipe;

import com.github.wolfiewaffle.bon.BONMod;
import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import toughasnails.api.item.TANItems;

import java.util.HashSet;
import java.util.List;

public class LinerRecipe extends CustomRecipe {
    HashSet<Item> linerItems = new HashSet<>();

    public LinerRecipe(ResourceLocation id) {
        super(id);
        linerItems.add(Items.LEATHER_HELMET);
        linerItems.add(Items.LEATHER_CHESTPLATE);
        linerItems.add(Items.LEATHER_LEGGINGS);
        linerItems.add(Items.LEATHER_BOOTS);
        linerItems.add(TANItems.LEAF_HELMET);
        linerItems.add(TANItems.LEAF_CHESTPLATE);
        linerItems.add(TANItems.LEAF_LEGGINGS);
        linerItems.add(TANItems.LEAF_BOOTS);
        linerItems.add(TANItems.WOOL_HELMET);
        linerItems.add(TANItems.WOOL_CHESTPLATE);
        linerItems.add(TANItems.WOOL_LEGGINGS);
        linerItems.add(TANItems.WOOL_BOOTS);
    }

    @Override
    public boolean matches(CraftingContainer container, Level world) {
        List<ItemStack> list = Lists.newArrayList();
        ItemStack baseStack = null;
        ItemStack linerStack = null;

        for(int i = 0; i < container.getContainerSize(); ++i) {
            ItemStack itemstack = container.getItem(i);

            if (!itemstack.isEmpty()) {
                list.add(itemstack);

                // If it is armor
                if (itemstack.getItem() instanceof ArmorItem) {
                    if (linerItems.contains(itemstack.getItem())) {
                        linerStack = itemstack;
                    } else {
                        baseStack = itemstack;
                    }
                }

                if (list.size() == 2 && linerStack != null && baseStack != null) {
                    EquipmentSlot baseSlot = ((ArmorItem) baseStack.getItem()).getSlot();
                    EquipmentSlot linerSlot = ((ArmorItem) linerStack.getItem()).getSlot();

                    if (baseSlot == linerSlot) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    @Override
    public ItemStack assemble(CraftingContainer grid) {
        List<ItemStack> list = Lists.newArrayList();
        ItemStack baseStack = null;
        ItemStack linerStack = null;

        for(int i = 0; i < grid.getContainerSize(); ++i) {
            ItemStack itemstack = grid.getItem(i);

            if (!itemstack.isEmpty()) {
                list.add(itemstack);

                // If it is armor
                if (itemstack.getItem() instanceof ArmorItem) {
                    if (linerItems.contains(itemstack.getItem())) {
                        linerStack = itemstack.copy();
                    } else {
                        baseStack = itemstack.copy();
                    }
                }

                if (list.size() == 2 && linerStack.getItem() != Items.AIR && baseStack.getItem() != Items.AIR) {
                    ItemStack returnStack = baseStack.copy();

                    // Return tagged armor
                    CompoundTag tag = baseStack.getTag();
                    if (!tag.contains("LinerItem")) tag.putString("LinerItem", linerStack.getItem().getRegistryName().toString());
                    returnStack.setTag(tag);

                    return returnStack;
                }
            }
        }

        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return BONMod.linerRecipeRecipeSerializer;
    }

    public static class Serializer extends net.minecraftforge.registries.ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<LinerRecipe> {

        public LinerRecipe fromJson(ResourceLocation resourceLocation, JsonObject json) {
            return new LinerRecipe(resourceLocation);
        }

        public LinerRecipe fromNetwork(ResourceLocation resourceLocation, FriendlyByteBuf friendlyByteBuf) {
            return new LinerRecipe(resourceLocation);
        }

        @Override
        public void toNetwork(FriendlyByteBuf friendlyByteBuf, LinerRecipe linerRecipe) {
        }
    }
}
