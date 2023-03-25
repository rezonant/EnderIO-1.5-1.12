package com.enderio.core.common;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;

public class CompoundCapabilityProvider implements ICapabilityProvider {
  private final List<ICapabilityProvider> providers = new ArrayList<ICapabilityProvider>();

  public CompoundCapabilityProvider(ICapabilityProvider... provs) {
    if (provs != null) {
      for (ICapabilityProvider p : provs) {
        if (p != null) {
          providers.add(p);
        }
      }
    }
  }

  @Override
  public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @org.jetbrains.annotations.Nullable Direction side) {
    for (ICapabilityProvider prov : providers) {
      LazyOptional<T> res = prov.getCapability(cap, side);
      if (res.isPresent()) {
        return res;
      }
    }

    return LazyOptional.empty();
  }
}
