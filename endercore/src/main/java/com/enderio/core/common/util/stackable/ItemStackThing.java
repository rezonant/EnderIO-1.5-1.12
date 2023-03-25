package com.enderio.core.common.util.stackable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.NNList;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

class ItemStackThing implements IThing {

  private final @Nonnull ItemStack thing;

  ItemStackThing(@Nonnull ItemStack itemStack) {
    this.thing = itemStack;
  }

  @Override
  public @Nonnull NNList<IThing> bake() {
    return thing.isEmpty() ? NNList.emptyList() : new NNList<>(this);
  }

  @Override
  public boolean is(@Nullable Item item) {
    return thing.getItem() == item;
  }

  @Override
  public boolean is(@Nullable ItemStack itemStack) {
    return itemStack != null && !itemStack.isEmpty() && thing.getItem() == itemStack.getItem()
        && (!thing.isDamageableItem() || thing.getTag() == itemStack.getTag());
  }

  @Override
  public boolean is(@Nullable Block block) {
    return block != null && (block.asItem() == thing.getItem() || Block.byItem(thing.getItem()) == block);
  }

  @Override
  public @Nonnull NNList<Item> getItems() {
    return new NNList<Item>(thing.getItem());
  }

  @Override
  public @Nonnull NNList<ItemStack> getItemStacks() {
    return new NNList<ItemStack>(thing);
  }

  @Override
  public @Nonnull NNList<Block> getBlocks() {
    Block block = Block.byItem(thing.getItem());
    return block != Blocks.AIR ? new NNList<Block>(block) : NNList.<Block> emptyList();
  }

  @Override
  public String toString() {
    return String.format("ItemStackThing [thing=%s]", thing);
  }

}