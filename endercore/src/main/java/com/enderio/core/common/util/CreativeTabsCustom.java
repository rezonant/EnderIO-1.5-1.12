package com.enderio.core.common.util;

import javax.annotation.Nonnull;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class CreativeTabsCustom extends CreativeModeTab {

  private @Nonnull ItemStack displayStack = ItemStack.EMPTY;

  public CreativeTabsCustom(@Nonnull String unloc) {
    super(unloc);
  }

  /**
   * @param item
   *          Item to display
   */
  public CreativeTabsCustom setDisplay(@Nonnull Item item) {
    return setDisplay(new ItemStack(item, 1));
  }

  /**
   * @param display
   *          ItemStack to display
   */
  public CreativeTabsCustom setDisplay(@Nonnull ItemStack display) {
    this.displayStack = display.copy();
    return this;
  }

  @Override
  public @Nonnull ItemStack makeIcon() {
    return displayStack;
  }
}
