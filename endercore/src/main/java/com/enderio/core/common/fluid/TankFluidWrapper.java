package com.enderio.core.common.fluid;

import com.enderio.core.api.common.util.ITankAccess;
import com.enderio.core.common.util.NNList;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class TankFluidWrapper implements IFluidWrapper {

  private final IFluidTank tank;

  public TankFluidWrapper(IFluidTank tank) {
    this.tank = tank;
  }

  @Override
  public int offer(FluidStack resource) {
    return tank.fill(resource, IFluidHandler.FluidAction.SIMULATE);
  }

  @Override
  public int fill(FluidStack resource) {
    return tank.fill(resource, IFluidHandler.FluidAction.EXECUTE);
  }

  @Override
  @Nullable
  public FluidStack drain(FluidStack resource) {
    return tank.drain(resource, IFluidHandler.FluidAction.EXECUTE);
  }

  @Override
  @Nullable
  public FluidStack getAvailableFluid() {
    return tank.getFluid();
  }
}
