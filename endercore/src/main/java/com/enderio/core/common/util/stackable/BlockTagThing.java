package com.enderio.core.common.util.stackable;

import com.enderio.core.common.util.NNList;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BlockTagThing implements IThing.Zwieback {

    private final @Nonnull TagKey<Block> tag;
    private @Nonnull NNList<Block> taggedBlocks = new NNList<Block>();

    BlockTagThing(@Nonnull TagKey<Block> tag) {
        this.tag = tag;
    }

    @Override
    public @Nonnull NNList<IThing> bake() {
        taggedBlocks = new NNList(ForgeRegistries.BLOCKS.tags().getTag(this.tag).iterator());
        return new NNList<>(this);
    }

    @Override
    public @Nullable
    IThing rebake() {
        // BIG TODO: REBAKE WHEN TAGS ARE RELOADED
        return taggedBlocks.isEmpty() ? null : this;
    }

    @Override
    public boolean is(@Nullable Item item) {
        for (Block tagBlock : taggedBlocks) {
            if (BlockThing.findBlockItem(tagBlock) == item) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean is(@Nullable ItemStack itemStack) {
        return itemStack != null && !itemStack.isEmpty() && is(itemStack.getItem());
    }

    @Override
    public boolean is(@Nullable Block block) {
        for (Block tagBlock : taggedBlocks) {
            if (tagBlock == block) {
                return true;
            }
        }
        return false;
    }

    @Override
    public @Nonnull NNList<Item> getItems() {
        NNList<Item> result = new NNList<Item>();
        for (Block tagBlock : taggedBlocks) {
            Item blockItem = BlockThing.findBlockItem(tagBlock);
            if (tagBlock != Blocks.AIR && blockItem != null) {
                result.add(blockItem);
            }
        }
        return result;
    }

    @Override
    public @Nonnull NNList<ItemStack> getItemStacks() {
        NNList<ItemStack> result = new NNList<ItemStack>();
        for (Block tagBlock : taggedBlocks) {
            Item blockItem = BlockThing.findBlockItem(tagBlock);
            if (tagBlock != Blocks.AIR && blockItem != null) {
                result.add(new ItemStack(blockItem));
            }
        }
        return result;
    }

    @Override
    public @Nonnull NNList<Block> getBlocks() {
        return taggedBlocks;
    }

    public @Nonnull TagKey<Block> getTag() {
        return tag;
    }

    @Override
    public String toString() {
        return String.format("BlockTagThing [name=%s, ores=%s]", tag.toString(), taggedBlocks);
    }
}