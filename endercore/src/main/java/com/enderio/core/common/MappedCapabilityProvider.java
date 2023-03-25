package com.enderio.core.common;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

public class MappedCapabilityProvider implements ICapabilityProvider {

  private final Map<Capability<?>, Object> providers = new HashMap<>();

  public MappedCapabilityProvider() {
  }

  public @Nonnull <T> MappedCapabilityProvider add(@Nullable Capability<T> capability, @Nonnull T cap) {
    providers.put(capability, cap);
    return this;
  }

  @SuppressWarnings("unchecked")
  @Override
  public @Nullable <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
    var cap = (T)providers.get(capability);
    if (cap == null)
      return LazyOptional.empty();
    else
      return LazyOptional.of(() -> cap);
  }

}
