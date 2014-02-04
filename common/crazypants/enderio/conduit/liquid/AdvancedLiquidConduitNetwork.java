package crazypants.enderio.conduit.liquid;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;
import buildcraft.api.transport.IPipeTile;
import buildcraft.api.transport.IPipeTile.PipeType;
import crazypants.enderio.conduit.IConduit;
import crazypants.util.BlockCoord;

public class AdvancedLiquidConduitNetwork extends AbstractTankConduitNetwork<AdvancedLiquidConduit> {

  private final ConduitTank tank = new ConduitTank(0);

  private final Set<LiquidOutput> outputs = new HashSet<LiquidOutput>();

  private Iterator<LiquidOutput> outputIterator;

  public AdvancedLiquidConduitNetwork() {
    super(AdvancedLiquidConduit.class);
  }

  @Override
  public Class<ILiquidConduit> getBaseConduitType() {
    return ILiquidConduit.class;
  }

  @Override
  public void addConduit(AdvancedLiquidConduit con) {
    tank.setCapacity(tank.getCapacity() + AdvancedLiquidConduit.CONDUIT_VOLUME);
    if(con.getTank().containsValidLiquid()) {
      tank.addAmount(con.getTank().getFluidAmount());
    }
    for (ForgeDirection dir : con.getExternalConnections()) {
      if(con.getConectionMode(dir).acceptsOutput()) {
        outputs.add(new LiquidOutput(con.getLocation().getLocation(dir), dir.getOpposite()));
      }
    }
    outputIterator = null;
    super.addConduit(con);
  }

  @Override
  public void destroyNetwork() {

    if(tank.containsValidLiquid()) {
      FluidStack fluidPerConduit = tank.getFluid().copy();
      int numCons = conduits.size();
      int leftOvers = fluidPerConduit.amount % numCons;
      fluidPerConduit.amount = fluidPerConduit.amount / numCons;

      for (AdvancedLiquidConduit con : conduits) {
        FluidStack f = fluidPerConduit.copy();
        if(leftOvers > 0) {
          f.amount += 1;
          leftOvers--;
        }
        con.getTank().setLiquid(f);
      }

    }
    outputs.clear();
    super.destroyNetwork();
  }

  @Override
  public void onUpdateEntity(IConduit conduit) {
    World world = conduit.getBundle().getEntity().worldObj;
    if(world == null) {
      return;
    }
    if(world.isRemote || liquidType == null) {
      return;
    }

    if(outputs.isEmpty() || !tank.containsValidLiquid() || tank.isEmpty()) {
      return;
    }

    if(outputIterator == null || !outputIterator.hasNext()) {
      outputIterator = outputs.iterator();
    }

    int numVisited = 0;
    while (!tank.isEmpty() && numVisited < outputs.size()) {
      if(!outputIterator.hasNext()) {
        outputIterator = outputs.iterator();
      }
      LiquidOutput output = outputIterator.next();
      if(output != null) {
        IFluidHandler cont = getTankContainer(output.location);
        if(cont != null) {
          FluidStack offer = tank.getFluid().copy();
          int filled = cont.fill(output.dir, offer, true);
          tank.addAmount(-filled);
        }
      }
      numVisited++;
    }
  }

  public FluidStack getFluidType() {
    return tank.getFluid();
  }

  public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
    return tank.fill(resource, doFill);
  }

  public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
    if(resource == null || tank.isEmpty() || !tank.containsValidLiquid() || !LiquidConduitNetwork.areFluidsCompatable(getFluidType(), resource)) {
      return null;
    }
    int amount = Math.min(resource.amount, tank.getFluidAmount());
    FluidStack result = resource.copy();
    result.amount = amount;
    if(doDrain) {
      tank.addAmount(-amount);
    }
    return result;
  }

  public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
    if(tank.isEmpty() || !tank.containsValidLiquid()) {
      return null;
    }
    int amount = Math.min(maxDrain, tank.getFluidAmount());
    FluidStack result = tank.getFluid().copy();
    result.amount = amount;
    if(doDrain) {
      tank.addAmount(-amount);
    }
    return result;
  }

  public boolean extractFrom(AdvancedLiquidConduit advancedLiquidConduit, ForgeDirection dir, int maxExtractPerTick) {

    if(tank.isFull()) {
      return false;
    }

    IFluidHandler extTank = getTankContainer(advancedLiquidConduit, dir);
    if(extTank != null) {
      int maxExtract = Math.min(maxExtractPerTick, tank.getAvailableSpace());

      if(liquidType == null || !tank.containsValidLiquid()) {
        FluidStack drained = extTank.drain(dir.getOpposite(), maxExtract, true);
        if(drained == null || drained.amount <= 0) {
          return false;
        }
        tank.setLiquid(drained.copy());
        setFluidType(drained);
        return true;
      }

      FluidStack couldDrain = liquidType.copy();
      couldDrain.amount = maxExtract;
      FluidStack drained = extTank.drain(dir.getOpposite(), couldDrain, true);
      if(drained == null || drained.amount <= 0) {
        return false;
      }
      tank.addAmount(drained.amount);
      return true;
    }
    return false;
  }

  public IFluidHandler getTankContainer(BlockCoord bc) {
    World w = getWorld();
    if(w == null) {
      return null;
    }
    TileEntity te = w.getBlockTileEntity(bc.x, bc.y, bc.z);
    if(te instanceof IFluidHandler) {
      if(te instanceof IPipeTile) {
        if(((IPipeTile) te).getPipeType() != PipeType.FLUID) {
          return null;
        }
      }
      return (IFluidHandler) te;
    }
    return null;
  }

  public IFluidHandler getTankContainer(AdvancedLiquidConduit con, ForgeDirection dir) {
    BlockCoord bc = con.getLocation().getLocation(dir);
    return getTankContainer(bc);
  }

  World getWorld() {
    if(conduits.isEmpty()) {
      return null;
    }
    return conduits.get(0).getBundle().getWorld();
  }

  public void removeInput(LiquidOutput lo) {
    outputs.remove(lo);
    outputIterator = null;
  }

  public void addInput(LiquidOutput lo) {
    outputs.add(lo);
    outputIterator = null;
  }

}
