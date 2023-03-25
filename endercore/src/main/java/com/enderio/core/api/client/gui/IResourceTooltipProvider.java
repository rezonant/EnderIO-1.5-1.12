package com.enderio.core.api.client.gui;

import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

public interface IResourceTooltipProvider {

  @Nonnull
  String getUnlocalizedNameForTooltip(@Nonnull ItemStack itemStack);

}
