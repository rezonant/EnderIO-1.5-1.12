package com.enderio.core.common.util;

import javax.annotation.Nonnull;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

public class ArrayInventory implements IItemHandler {

  protected final @Nonnull ItemStack[] items;

  public ArrayInventory(@Nonnull ItemStack[] items) {
    this.items = items;
  }

  public ArrayInventory(int size) {
    items = new ItemStack[size];
  }

  @Override
  public int getSlots() {
    return items.length;
  }

  @Override
  public @Nonnull ItemStack getStackInSlot(int slot) {
    final ItemStack itemStack = items[slot];
    return itemStack != null ? itemStack : ItemStack.EMPTY;
  }

  @Override
  public int getSlotLimit(int slot) {
    if (slot < 0 || slot >= items.length)
      return 0;

    if (items[slot].isEmpty())
      return 64;

    return items[slot].getItem().getMaxStackSize();
  }

  @Override
  public boolean isItemValid(int slot, @NotNull ItemStack stack) {
    return false;
  }

  @Override
  public @Nonnull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
    if (slot < 0 || slot >= items.length)
      return ItemStack.EMPTY;

    var existingStack = this.items[slot];

    stack = stack.copy();

    if (simulate) {
      existingStack = stack.copy();
    }

    if (existingStack.getCount() == 0) {
      this.items[slot] = stack;
      return ItemStack.EMPTY;
    } else {
      if (!ItemUtil.areStackMergable(existingStack, stack))
        return stack;

      var maxStackSize = existingStack.getItem().getMaxStackSize(existingStack);
      var takeable = Math.min(maxStackSize - existingStack.getCount(), existingStack.getCount());

      existingStack.setCount(existingStack.getCount() + takeable);
      stack.split(takeable);

      return stack;
    }
  }

  @Override
  public @Nonnull ItemStack extractItem(int slot, int amount, boolean simulate) {
    if (slot < 0 || slot >= items.length)
      return ItemStack.EMPTY;

    var stack = this.items[slot];
    if (simulate)
        stack = stack.copy();
    return stack.split(amount);
  }
}
