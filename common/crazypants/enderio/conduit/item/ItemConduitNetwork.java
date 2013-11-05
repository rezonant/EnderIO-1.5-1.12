package crazypants.enderio.conduit.item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.inventory.IInventory;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import crazypants.enderio.conduit.AbstractConduitNetwork;
import crazypants.enderio.conduit.ConnectionMode;
import crazypants.enderio.conduit.IConduit;
import crazypants.util.BlockCoord;

public class ItemConduitNetwork extends AbstractConduitNetwork<IItemConduit> {

  private long timeAtLastApply;

  private final List<NetworkedInventory> inventories = new ArrayList<ItemConduitNetwork.NetworkedInventory>();
  private final Map<BlockCoord, NetworkedInventory> invMap = new HashMap<BlockCoord, ItemConduitNetwork.NetworkedInventory>();

  @Override
  public Class<? extends IItemConduit> getBaseConduitType() {
    return IItemConduit.class;
  }

  public void inventoryAdded(ItemConduit itemConduit, ForgeDirection direction, int x, int y, int z, IInventory externalInventory) {
    BlockCoord bc = new BlockCoord(x, y, z);
    NetworkedInventory ni = new NetworkedInventory(externalInventory, itemConduit, direction, bc);
    inventories.add(ni);
    invMap.put(bc, ni);
  }

  public void inventoryRemoved(int x, int y, int z) {
    BlockCoord bc = new BlockCoord(x, y, z);
    NetworkedInventory inv = invMap.remove(bc);
    if(inv != null) {
      inventories.remove(inv);
    }
  }

  @Override
  public void onUpdateEntity(IConduit conduit) {
    World world = conduit.getBundle().getEntity().worldObj;
    if(world == null) {
      return;
    }
    if(world.isRemote) {
      return;
    }
    long curTime = world.getTotalWorldTime();
    if(curTime != timeAtLastApply) {
      timeAtLastApply = curTime;
      doTick();
    }
  }

  private void doTick() {

  }

  static int compare(int x, int y) {
    return (x < y) ? -1 : ((x == y) ? 0 : 1);
  }

  class NetworkedInventory {

    IInventory inv;
    ItemConduit con;
    ForgeDirection conDir;
    BlockCoord location;

    List<Target> sendPriority = new ArrayList<Target>();

    NetworkedInventory(IInventory inv, ItemConduit con, ForgeDirection conDir, BlockCoord location) {
      this.inv = inv;
      this.con = con;
      this.conDir = conDir;
      this.location = location;
    }

    boolean canExtract() {
      ConnectionMode mode = con.getConectionMode(conDir);
      return mode == ConnectionMode.INPUT || mode == ConnectionMode.IN_OUT;
    }

    boolean canInsert() {
      ConnectionMode mode = con.getConectionMode(conDir);
      return mode == ConnectionMode.OUTPUT || mode == ConnectionMode.IN_OUT;
    }

    void updateInsertOrder() {
      sendPriority.clear();
      if(!canExtract()) {
        return;
      }
      for (NetworkedInventory other : inventories) {
        if(other != this && other.canInsert()) {
          sendPriority.add(new Target(other, location.distanceSquared(other.location)));
        }
      }
      Collections.sort(sendPriority);
    }

  }

  class Target implements Comparable<Target> {
    NetworkedInventory inv;
    int distance;

    Target(NetworkedInventory inv, int distance) {
      this.inv = inv;
      this.distance = distance;
    }

    @Override
    public int compareTo(Target o) {
      return compare(distance, o.distance);
    }

  }

}
