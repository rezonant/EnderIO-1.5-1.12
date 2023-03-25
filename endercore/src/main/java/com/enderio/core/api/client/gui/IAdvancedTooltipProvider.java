package com.enderio.core.api.client.gui;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IAdvancedTooltipProvider {

  @OnlyIn(Dist.CLIENT)
  default void addCommonEntries(@Nonnull ItemStack itemstack, @Nullable Player entityplayer, @Nonnull List<Component> list, boolean flag) {
  }

  @OnlyIn(Dist.CLIENT)
  default void addBasicEntries(@Nonnull ItemStack itemstack, @Nullable Player entityplayer, @Nonnull List<Component> list, boolean flag) {
  }

  @OnlyIn(Dist.CLIENT)
  default void addDetailedEntries(@Nonnull ItemStack itemstack, @Nullable Player entityplayer, @Nonnull List<Component> list, boolean flag) {
  }

}
