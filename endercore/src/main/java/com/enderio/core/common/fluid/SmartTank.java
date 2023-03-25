//package com.enderio.core.common.fluid;
//
//import javax.annotation.Nonnull;
//import javax.annotation.Nullable;
//
//import com.enderio.core.api.common.util.ITankAccess;
//import com.enderio.core.common.util.FluidUtil;
//import com.enderio.core.common.util.NullHelper;
//import com.google.common.base.Strings;
//import net.minecraft.nbt.CompoundTag;
//import net.minecraft.world.level.block.entity.BlockEntity;
//import net.minecraft.world.level.material.Fluid;
//import net.minecraftforge.fluids.FluidStack;
//import net.minecraftforge.fluids.IFluidTank;
//import net.minecraftforge.fluids.capability.IFluidHandler;
//
//public class SmartTank implements IFluidTank {
//
//  // Note: NBT-safe as long as the restriction isn't using NBT
//
//  protected @Nullable Fluid restriction;
//  protected BlockEntity blockEntity;
//
//  FluidStack fluid;
//  int capacity;
//
//  public SmartTank(@Nullable FluidStack fluid, int capacity) {
//    this.fluid = fluid;
//    this.capacity = capacity;
//
//    if (fluid != null) {
//      restriction = fluid.getFluid();
//    } else {
//      restriction = null;
//    }
//  }
//
//  public SmartTank(int capacity) {
//    this.capacity = capacity;
//  }
//
//  public SmartTank(@Nullable Fluid restriction, int capacity) {
//    this(capacity);
//    this.restriction = restriction;
//  }
//
//  public void setBlockEntity(BlockEntity entity) {
//    this.blockEntity = entity;
//  }
//
//  public void setRestriction(@Nullable Fluid restriction) {
//    this.restriction = restriction;
//  }
//
//  public float getFilledRatio() {
//    return (float) getFluidAmount() / getCapacity();
//  }
//
//  public boolean isFull() {
//    return getFluidAmount() >= getCapacity();
//  }
//
//  public boolean isEmpty() {
//    return getFluidAmount() == 0;
//  }
//
//  public boolean hasFluid(@Nullable Fluid candidate) {
//    final FluidStack fluid2 = fluid;
//    return !(fluid2 == null || candidate == null || fluid2.getAmount() <= 0 || fluid2.getFluid() != candidate);
//  }
//
//  /**
//   * Checks if the given fluid can actually be removed from this tank
//   * <p>
//   * Used by: te.canDrain()
//   */
//  public boolean canDrain(@Nullable Fluid fl) {
//    final FluidStack fluid2 = fluid;
//    if (fluid2 == null || fl == null) {
//      return false;
//    }
//
//    return FluidUtil.areFluidsTheSame(fl, fluid2.getFluid());
//  }
//
//  /**
//   * Checks if the given fluid can actually be removed from this tank
//   * <p>
//   * Used by: internal
//   */
//  public boolean canDrain(@Nullable FluidStack fluidStack) {
//    final FluidStack fluid2 = fluid;
//    if (fluid2 == null || fluidStack == null) {
//      return false;
//    }
//
//    return fluidStack.isFluidEqual(fluid2);
//  }
//
//  /**
//   * Checks if the given fluid can actually be added to this tank (ignoring fill level)
//   * <p>
//   * Used by: internal
//   */
//  public boolean canFill(@Nullable FluidStack resource) {
//    if (!canFillFluidType(resource)) {
//      return false;
//    } else if (fluid != null) {
//      return fluid.isFluidEqual(resource);
//    } else {
//      return true;
//    }
//  }
//
//  /**
//   * Checks if the given fluid can actually be added to this tank (ignoring fill level)
//   * <p>
//   * Used by: te.canFill()
//   */
//  public boolean canFill(@Nullable Fluid fl) {
//    if (fl == null || !canFillFluidType(new FluidStack(fl, 1))) {
//      return false;
//    } else if (fluid != null) {
//      return FluidUtil.areFluidsTheSame(fluid.getFluid(), fl);
//    } else {
//      return true;
//    }
//  }
//
//  @Override
//  public boolean canFillFluidType(@Nullable FluidStack resource) {
//    return super.canFillFluidType(resource)
//        && (restriction == null || (resource != null && resource.getFluid() != null && FluidUtil.areFluidsTheSame(restriction, resource.getFluid())));
//  }
//
//  void setFluid(FluidStack fluid) {
//    this.fluid = fluid;
//  }
//
//  public void setFluidAmount(int amount) {
//    if (amount > 0) {
//      if (fluid != null) {
//        fluid.setAmount(Math.min(capacity, amount));
//      } else if (restriction != null) {
//        setFluid(new FluidStack(restriction, Math.min(capacity, amount)));
//      } else {
//        throw new RuntimeException("Cannot set fluid amount of an empty tank");
//      }
//    } else {
//      setFluid(null);
//    }
//    onContentsChanged();
//  }
//
//  @Override
//  public int fill(@Nullable FluidStack resource, IFluidHandler.FluidAction doFill) {
//    if (!canFill(resource)) {
//      return 0;
//    }
//    return fillInternal(resource, doFill);
//  }
//
//  @Override
//  public FluidStack drain(@Nullable FluidStack resource, IFluidHandler.FluidAction doDrain) {
//    return super.drain(resource, doDrain);
//  }
//
//  @Override
//  public FluidStack drain(int maxDrain, IFluidHandler.FluidAction doDrain) {
//    return super.drain(maxDrain, doDrain);
//  }
//
//  @Override
//  public @Nullable FluidStack getFluid() {
//    if (fluid != null) {
//      return fluid;
//    } else if (restriction != null) {
//      return new FluidStack(restriction, 0);
//    } else {
//      return null;
//    }
//  }
//
//  @Override
//  public int getFluidAmount() {
//    return this.fluid.getAmount();
//  }
//
//  @Override
//  public int getCapacity() {
//    return 0;
//  }
//
//  @Override
//  public boolean isFluidValid(FluidStack stack) {
//    if (this.restriction == null)
//        return true;
//
//    return this.restriction.isSame(stack.getFluid());
//  }
//
//  public @Nonnull FluidStack getFluidNN() {
//    return NullHelper.notnull(getFluid(), "Internal Logic Error. Non-Empty tank has no fluid.");
//  }
//
//  public int getAvailableSpace() {
//    return getCapacity() - getFluidAmount();
//  }
//
//  public void addFluidAmount(int amount) {
//    setFluidAmount(getFluidAmount() + amount);
//    if (blockEntity != null) {
//      FluidEvent.fireEvent(new FluidEvent.FluidFillingEvent(fluid, blockEntity.getLevel(), blockEntity.getBlockPos(), this, amount));
//    }
//  }
//
//  public int removeFluidAmount(int amount) {
//    int drained = 0;
//    if (getFluidAmount() > amount) {
//      setFluidAmount(getFluidAmount() - amount);
//      drained = amount;
//    } else if (!isEmpty()) {
//      drained = getFluidAmount();
//      setFluidAmount(0);
//    } else {
//      return 0;
//    }
//    if (tile != null) {
//      FluidEvent.fireEvent(new FluidEvent.FluidDrainingEvent(fluid, blockEntity.getLevel(), blockEntity.getBlockPos(), this, drained));
//    }
//    return drained;
//  }
//
//  @Override
//  public void setCapacity(int capacity) {
//    super.setCapacity(capacity);
//    if (getFluidAmount() > capacity) {
//      setFluidAmount(capacity);
//    }
//  }
//
//  public void writeCommon(@Nonnull String name, @Nonnull CompoundTag nbtRoot) {
//    CompoundTag tankRoot = new CompoundTag();
//    writeToNBT(tankRoot);
//    if (restriction != null) {
//      tankRoot.setString("FluidRestriction", NullHelper.notnullF(restriction.getName(), "encountered fluid with null name"));
//    }
//    tankRoot.setInteger("Capacity", capacity);
//    nbtRoot.setTag(name, tankRoot);
//  }
//
//  public void readCommon(@Nonnull String name, @Nonnull CompoundTag nbtRoot) {
//    if (nbtRoot.hasKey(name)) {
//      CompoundTag tankRoot = (CompoundTag) nbtRoot.getTag(name);
//      readFromNBT(tankRoot);
//      if (tankRoot.hasKey("FluidRestriction")) {
//        String fluidName = tankRoot.getString("FluidRestriction");
//        if (!Strings.isNullOrEmpty(fluidName)) {
//          restriction = FluidRegistry.getFluid(fluidName);
//        }
//      }
//      if (tankRoot.hasKey("Capacity")) {
//        capacity = tankRoot.getInteger("Capacity");
//      }
//    } else {
//      setFluid(null);
//      // not reseting 'restriction' here on purpose---it would destroy the one that was set at tank creation
//    }
//  }
//
//  public static SmartTank createFromNBT(@Nonnull String name, @Nonnull CompoundTag nbtRoot) {
//    SmartTank result = new SmartTank(0);
//    result.readCommon(name, nbtRoot);
//    if (result.getFluidAmount() > result.getCapacity()) {
//      result.setCapacity(result.getFluidAmount());
//    }
//    return result;
//  }
//
//  @Override
//  protected void onContentsChanged() {
//    super.onContentsChanged();
//    if (blockEntity instanceof ITankAccess) {
//      ((ITankAccess) blockEntity).setTanksDirty();
//    } else if (blockEntity != null) {
//
//    }
//  }
//
//}
