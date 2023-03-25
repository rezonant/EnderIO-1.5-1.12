package com.enderio.core.common.fluid;

import java.util.EnumMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.api.common.util.ITankAccess;

import com.enderio.core.common.util.NullHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class FluidWrapper {
  private static final Capability<IFluidHandler> FLUID_HANDLER = CapabilityManager.get(new CapabilityToken<>(){});

  public static @Nullable IFluidWrapper wrap(BlockGetter world, BlockPos pos, Direction side) {
    if (world == null || pos == null) {
      return null;
    }
    return wrap(world.getBlockEntity(pos), side);
  }

  public static @Nullable IFluidWrapper wrap(@Nullable BlockEntity te, Direction side) {
    if (te != null && te.hasLevel() && !te.isRemoved()) {
      if (te instanceof IFluidWrapper) {
        return (IFluidWrapper) te;
      }
      final Capability<IFluidHandler> fluidHandler = FLUID_HANDLER;
      if (fluidHandler == null) {
        throw new NullPointerException("Capability<IFluidHandler> missing");
      }
      if (te.getCapability(fluidHandler, side).isPresent()) {
        return wrap(te.getCapability(fluidHandler, side).orElse(null));
      }
    }
    return null;
  }

  public static TankAccessFluidWrapper wrap(final ITankAccess tankAccess) {
    return new TankAccessFluidWrapper(tankAccess);
  }

  public static CapabilityFluidWrapper wrap(final IFluidHandler fluidHandler) {
    if (fluidHandler != null) {
      return new CapabilityFluidWrapper(fluidHandler);
    }
    return null;
  }

  public static @Nullable IFluidWrapper wrap(@Nullable IFluidWrapper wrapper) {
    return wrapper;
  }

  public static @Nullable IFluidWrapper wrap(@Nonnull IFluidTank tank) {
    return new TankFluidWrapper(tank);
  }

  public static @Nullable IFluidWrapper wrap(@Nonnull ItemStack itemStack) {
    return wrap(NullHelper.notnull(FluidUtil.getFluidHandler(itemStack).orElse(null), "No fluid handler on ItemStack"));
  }

  public static Map<Direction, IFluidWrapper> wrapNeighbours(BlockGetter world, BlockPos pos) {
    Map<Direction, IFluidWrapper> res = new EnumMap<Direction, IFluidWrapper>(Direction.class);
    for (Direction dir : Direction.values()) {
      if (dir == null) {
        throw new NullPointerException("Direction.values() contains null values???");
      }
      IFluidWrapper wrapper = wrap(world, pos.relative(dir), dir.getOpposite());
      if (wrapper != null) {
        res.put(dir, wrapper);
      }
    }
    return res;
  }

  // Some helpers:

  public static int transfer(IFluidTank from, IFluidTank to, int limit) {
    return transfer(wrap(from), wrap(to), limit);
  }

  public static int transfer(IFluidTank from, IFluidWrapper to, int limit) {
    return transfer(wrap(from), wrap(to), limit);
  }

  public static int transfer(IFluidTank from, BlockGetter world, BlockPos topos, Direction toside, int limit) {
    return transfer(wrap(from), wrap(world, topos, toside), limit);
  }

  public static int transfer(IFluidTank from, BlockEntity to, Direction toside, int limit) {
    return transfer(wrap(from), wrap(to, toside), limit);
  }

  //

  public static int transfer(IFluidWrapper from, IFluidTank to, int limit) {
    return transfer(wrap(from), wrap(to), limit);
  }

  public static int transfer(IFluidWrapper from, BlockGetter world, BlockPos topos, Direction toside, int limit) {
    return transfer(wrap(from), wrap(world, topos, toside), limit);
  }

  public static int transfer(IFluidWrapper from, BlockEntity to, Direction toside, int limit) {
    return transfer(wrap(from), wrap(to, toside), limit);
  }

  //

  public static int transfer(BlockGetter world, BlockPos frompos, Direction fromside, IFluidWrapper to, int limit) {
    return transfer(wrap(world, frompos, fromside), wrap(to), limit);
  }

  public static int transfer(BlockGetter world, BlockPos frompos, Direction fromside, IFluidTank to, int limit) {
    return transfer(wrap(world, frompos, fromside), wrap(to), limit);
  }

  public static int transfer(BlockGetter world, BlockPos frompos, Direction fromside, BlockPos topos, Direction toside, int limit) {
    return transfer(wrap(world, frompos, fromside), wrap(world, topos, toside), limit);
  }

  public static int transfer(BlockGetter world, BlockPos frompos, Direction fromside, BlockEntity to, Direction toside, int limit) {
    return transfer(wrap(world, frompos, fromside), wrap(to, toside), limit);
  }

  //

  public static int transfer(BlockEntity from, Direction fromside, IFluidWrapper to, int limit) {
    return transfer(wrap(from, fromside), wrap(to), limit);
  }

  public static int transfer(BlockEntity from, Direction fromside, IFluidTank to, int limit) {
    return transfer(wrap(from, fromside), wrap(to), limit);
  }

  public static int transfer(BlockEntity from, Direction fromside, BlockGetter world, BlockPos topos, Direction toside, int limit) {
    return transfer(wrap(from, fromside), wrap(world, topos, toside), limit);
  }

  public static int transfer(BlockEntity from, Direction fromside, BlockEntity to, Direction toside, int limit) {
    return transfer(wrap(from, fromside), wrap(to, toside), limit);
  }

  //

  public static int transfer(IFluidWrapper from, IFluidWrapper to, int limit) {
    if (from == null || to == null || limit <= 0) {
      return 0;
    }

    FluidStack drainable = from.getAvailableFluid();
    if (drainable == null || drainable.getAmount() <= 0) {
      return 0;
    }
    drainable = drainable.copy();

    if (drainable.getAmount() > limit) {
      drainable.setAmount(limit);
    }

    int fillable = to.offer(drainable);
    if (fillable <= 0 || fillable > drainable.getAmount()) {
      return 0;
    }

    if (fillable < drainable.getAmount()) {
      drainable.setAmount(fillable);
    }

    return to.fill(from.drain(drainable));
  }

}
