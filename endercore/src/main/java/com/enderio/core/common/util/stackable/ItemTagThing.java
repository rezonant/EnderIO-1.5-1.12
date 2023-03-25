package com.enderio.core.common.util.stackable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.NNList;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

class ItemTagThing implements IThing.Zwieback {

    private final @Nonnull TagKey<Item> tag;
    private @Nonnull NNList<Item> taggedItems = new NNList<Item>();

    ItemTagThing(@Nonnull TagKey<Item> tag) {
        this.tag = tag;
    }

    @Override
    public @Nonnull NNList<IThing> bake() {
        taggedItems = NNList.wrap(ForgeRegistries.ITEMS.tags().getTag(this.tag).stream().collect(Collectors.toList()));
        return new NNList<>(this);
    }

    @Override
    public @Nullable IThing rebake() {
        // BIG TODO: REBAKE WHEN TAGS ARE RELOADED
        return taggedItems.isEmpty() ? null : this;
    }

    @Override
    public boolean is(@Nullable Item item) {
        for (Item tagItem : taggedItems) {
            if (tagItem == item) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean is(@Nullable ItemStack itemStack) {
        for (Item tagItem : taggedItems) {
            // TODO: Do we have alternatives to the old OreThing checks that we should use?
            if (itemStack != null && !itemStack.isEmpty() && itemStack.getItem() == tagItem) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean is(@Nullable Block block) {
        if (block == null)
            return false;

        for (Item tagItem : taggedItems) {
            if (tagItem.asItem() instanceof BlockItem && (((BlockItem)tagItem).getBlock() == block))
                return true;

            if (Item.BY_BLOCK.get(block) == tagItem.asItem())
                return true;
        }
        return false;
    }

    @Override
    public @Nonnull NNList<Item> getItems() {
        return taggedItems;
    }

    @Override
    public @Nonnull NNList<ItemStack> getItemStacks() {
        NNList<ItemStack> result = new NNList<ItemStack>();
        for (Item tagItem : taggedItems) {
            if (!result.contains(tagItem)) { // <--- WRONG, tagItem is an Item not ItemStack now
                result.add(new ItemStack(tagItem.asItem()));
            }
        }
        return result;
    }

    @Override
    public @Nonnull NNList<Block> getBlocks() {
        NNList<Block> result = new NNList<Block>();
        for (Item tagItem : taggedItems) {
            Block block = null;
            if (tagItem.asItem() instanceof BlockItem)
                block = ((BlockItem)tagItem.asItem()).getBlock();

            if (block != null && block != Blocks.AIR) {
                result.add(block);
            }
        }
        return result;
    }

    public @Nonnull TagKey<Item> getTag() {
        return tag;
    }

    @Override
    public String toString() {
        return String.format("ItemTagThing [name=%s, ores=%s]", tag.toString(), taggedItems);
    }

}