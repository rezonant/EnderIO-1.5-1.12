package com.enderio.core.common.util.stackable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.enderio.core.common.util.NNList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

interface IThing {

  @Nonnull
  NNList<IThing> bake();

  boolean is(@Nullable Item item);

  boolean is(@Nullable ItemStack itemStack);

  boolean is(@Nullable Block block);

  @Nonnull
  NNList<Item> getItems();

  @Nonnull
  NNList<ItemStack> getItemStacks();

  @Nonnull
  NNList<Block> getBlocks();

  public static interface Zwieback extends IThing {

    @Nullable
    IThing rebake();

  }

}