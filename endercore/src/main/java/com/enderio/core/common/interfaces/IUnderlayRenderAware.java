package com.enderio.core.common.interfaces;

import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

public interface IUnderlayRenderAware {
  public void renderItemAndEffectIntoGUI(@Nonnull ItemStack stack, int xPosition, int yPosition);
}