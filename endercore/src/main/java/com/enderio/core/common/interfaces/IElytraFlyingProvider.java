package com.enderio.core.common.interfaces;

import javax.annotation.Nonnull;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface IElytraFlyingProvider {
  public boolean isElytraFlying(@Nonnull LivingEntity entity, @Nonnull ItemStack itemstack, boolean shouldStop);
}