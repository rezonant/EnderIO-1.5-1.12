package crazypants.enderio.machine.solar;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumSkyBlock;
import net.minecraftforge.common.util.ForgeDirection;
import buildcraft.api.power.IPowerEmitter;
import crazypants.enderio.Config;
import crazypants.enderio.power.BasicCapacitor;
import crazypants.enderio.power.IInternalPowerReceptor;
import crazypants.enderio.power.IPowerInterface;
import crazypants.enderio.power.PowerHandlerUtil;
import crazypants.util.BlockCoord;

public class TileEntitySolarPanel extends TileEntity implements IInternalPowerReceptor, IPowerEmitter {

  private BasicCapacitor capacitor;

  private final List<Receptor> receptors = new ArrayList<Receptor>();
  private ListIterator<Receptor> receptorIterator = receptors.listIterator();
  private boolean receptorsDirty = true;

  private float energyPerTick = (float) Config.maxPhotovoltaicOutput;

  private float lastCollectionValue = -1;
  private int checkOffset;
  private static final int CHECK_INTERVAL = 100;

  private float energyStored;

  public TileEntitySolarPanel() {
    checkOffset = (int) (Math.random() * 20);
    capacitor = new BasicCapacitor(0, 10000, 10);
  }

  @Override
  public boolean canEmitPowerFrom(ForgeDirection side) {
    return side == ForgeDirection.DOWN;
  }


  public void onNeighborBlockChange() {
    receptorsDirty = true;
  }

  // RF Power

  @Override
  public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
    return 0;
  }

  @Override
  public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {
    return 0;
  }

  @Override
  public boolean canInterface(ForgeDirection from) {
    return true;
  }

  @Override
  public int getEnergyStored(ForgeDirection from) {
    return (int) (energyStored * 10);
  }

  @Override
  public int getMaxEnergyStored(ForgeDirection from) {
    return capacitor.getMaxEnergyStored() * 10;
  }

  @Override
  public void updateEntity() {
    if(worldObj == null || worldObj.isRemote) {
      return;
    }
    collectEnergy();
    transmitEnergy();
  }

  private void collectEnergy() {
    if(!worldObj.canBlockSeeTheSky(xCoord, yCoord, zCoord)) {
      return;
    }

    if(lastCollectionValue == -1 || (worldObj.getWorldTime() + checkOffset) % CHECK_INTERVAL == 0) {
      float fromSun = calculateLightRatio();
      lastCollectionValue = energyPerTick * fromSun;
    }
    float collected = lastCollectionValue;
    energyStored = Math.min(capacitor.getMaxEnergyStored(), energyStored + collected);
  }

  private float calculateLightRatio() {
    int lightValue = worldObj.getSavedLightValue(EnumSkyBlock.Sky, xCoord, yCoord, zCoord) - worldObj.skylightSubtracted;
    float sunAngle = worldObj.getCelestialAngleRadians(1.0F);

    if(sunAngle < (float) Math.PI) {
      sunAngle += (0.0F - sunAngle) * 0.2F;
    } else {
      sunAngle += (((float) Math.PI * 2F) - sunAngle) * 0.2F;
    }

    lightValue = Math.round(lightValue * MathHelper.cos(sunAngle));

    lightValue = MathHelper.clamp_int(lightValue, 0, 15);
    return lightValue / 15f;
  }

  private boolean transmitEnergy() {

    if(energyStored <= 0) {
      return false;
    }

    double canTransmit = Math.min(energyStored, capacitor.getMaxEnergyExtracted());
    float transmitted = 0;

    checkReceptors();

    if(!receptors.isEmpty() && !receptorIterator.hasNext()) {
      receptorIterator = receptors.listIterator();
    }

    int appliedCount = 0;
    int numReceptors = receptors.size();
    while (receptorIterator.hasNext() && canTransmit > 0 && appliedCount < numReceptors) {

      Receptor receptor = receptorIterator.next();
      IPowerInterface pp = receptor.receptor;
      if(pp != null && pp.getMinEnergyReceived(receptor.fromDir.getOpposite()) <= canTransmit) {
        double used = pp.recieveEnergy(receptor.fromDir.getOpposite(), (float) canTransmit);
        transmitted += used;
        canTransmit -= used;
      }

      if(canTransmit <= 0) {
        break;
      }

      if(!receptors.isEmpty() && !receptorIterator.hasNext()) {
        receptorIterator = receptors.listIterator();
      }
      appliedCount++;
    }

    energyStored = energyStored - transmitted;

    return transmitted > 0;

  }

  private void checkReceptors() {
    if(!receptorsDirty) {
      return;
    }
    receptors.clear();
    BlockCoord bc = new BlockCoord(xCoord, yCoord, zCoord);
    ForgeDirection dir = ForgeDirection.DOWN;
    BlockCoord checkLoc = bc.getLocation(dir);
    TileEntity te = worldObj.getTileEntity(checkLoc.x, checkLoc.y, checkLoc.z);
    IPowerInterface pi = PowerHandlerUtil.create(te);
    if(pi != null) {
      receptors.add(new Receptor(pi, dir));
    }
    receptorIterator = receptors.listIterator();
    receptorsDirty = false;

  }

  static class Receptor {
    IPowerInterface receptor;
    ForgeDirection fromDir;

    private Receptor(IPowerInterface rec, ForgeDirection fromDir) {
      this.receptor = rec;
      this.fromDir = fromDir;
    }
  }

}
