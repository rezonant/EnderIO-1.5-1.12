package crazypants.enderio.conduit.item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
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

  private boolean requiresSort = true;

  @Override
  public Class<? extends IItemConduit> getBaseConduitType() {
    return IItemConduit.class;
  }

  @Override
  public void addConduit(IItemConduit con) {
    super.addConduit(con);

    TileEntity te = con.getBundle().getEntity();
    if(te != null) {
      for (ForgeDirection direction : con.getExternalConnections()) {
        IInventory extCon = con.getExternalInventory(direction);
        if(extCon != null) {
          inventoryAdded(con, direction, te.xCoord + direction.offsetX, te.yCoord + direction.offsetY, te.zCoord + direction.offsetZ, extCon);
        }
      }
    }
  }

  public void inventoryAdded(IItemConduit itemConduit, ForgeDirection direction, int x, int y, int z, IInventory externalInventory) {
    System.out.println("ItemConduitNetwork.inventoryAdded: ");
    BlockCoord bc = new BlockCoord(x, y, z);
    NetworkedInventory inv = new NetworkedInventory(externalInventory, itemConduit, direction, bc);
    inventories.add(inv);
    invMap.put(bc, inv);
    requiresSort = true;
  }

  public void inventoryRemoved(ItemConduit itemConduit, int x, int y, int z) {
    System.out.println("ItemConduitNetwork.inventoryRemoved: ");
    BlockCoord bc = new BlockCoord(x, y, z);
    NetworkedInventory inv = invMap.remove(bc);
    if(inv != null) {
      inventories.remove(inv);
    }
    requiresSort = true;
  }

  public void connectionModeChanged(ItemConduit itemConduit, ConnectionMode mode) {
    requiresSort = true;
  }

  private boolean isRemote(ItemConduit itemConduit) {
    World world = itemConduit.getBundle().getEntity().worldObj;
    if(world != null && world.isRemote) {
      return true;
    }
    return false;
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
    for (NetworkedInventory ni : inventories) {
      if(requiresSort) {
        ni.updateInsertOrder();
      }
      ni.onTick();
    }
    requiresSort = false;
  }

  static int compare(int x, int y) {
    return (x < y) ? -1 : ((x == y) ? 0 : 1);
  }

  class NetworkedInventory {

    IInventory inv;
    ISidedInventory sidedInv;
    IItemConduit con;
    ForgeDirection conDir;
    BlockCoord location;
    int inventorySide;

    List<Target> sendPriority = new ArrayList<Target>();

    NetworkedInventory(IInventory inv, IItemConduit con, ForgeDirection conDir, BlockCoord location) {
      this.inv = inv;
      if(inv instanceof ISidedInventory) {
        sidedInv = (ISidedInventory) inv;
      }
      this.con = con;
      this.conDir = conDir;
      this.location = location;
      inventorySide = conDir.getOpposite().ordinal();
    }

    boolean canExtract() {
      ConnectionMode mode = con.getConectionMode(conDir);
      return mode == ConnectionMode.INPUT || mode == ConnectionMode.IN_OUT;
    }

    boolean canInsert() {
      ConnectionMode mode = con.getConectionMode(conDir);
      return mode == ConnectionMode.OUTPUT || mode == ConnectionMode.IN_OUT;
    }

    void onTick() {
      if(!canExtract()) {
        return;
      }
      if(sidedInv != null) {
        trasnferItemsSided();
      } else {
        tranfserItems();
      }

    }

    private void tranfserItems() {
      // TODO Auto-generated method stub

    }

    private void trasnferItemsSided() {
      int size = sidedInv.getSizeInventory();
      int[] slotIndices = sidedInv.getAccessibleSlotsFromSide(inventorySide);
      ItemStack extractItem = null;
      ItemStack remainingItem = null;
      int slot = -1;
      for (int i = 0; i < slotIndices.length && extractItem == null; i++) {
        ItemStack item = sidedInv.getStackInSlot(i);
        if(item != null) {
          extractItem = item.copy();
          remainingItem = item.copy();
          remainingItem.stackSize = item.stackSize - 1;
          extractItem.stackSize = 1;
          slot = i;
          if(!sidedInv.canExtractItem(i, extractItem, inventorySide)) {
            extractItem = null;
          }
        }
      }

      if(extractItem != null) {
        boolean doExtract = false;
        int leftToInsert = extractItem.stackSize;
        for (Target target : sendPriority) {
          int inserted = target.inv.insertItem(extractItem);
          if(inserted > 0) {
            remainingItem.stackSize -= inserted;
            if(remainingItem.stackSize > 0) {
              sidedInv.setInventorySlotContents(slot, remainingItem);
            } else {
              sidedInv.setInventorySlotContents(slot, null);
            }
            leftToInsert -= inserted;
          }
          if(leftToInsert <= 0) {
            break;
          }
        }
      }

    }

    private int insertItem(ItemStack item) {
      if(sidedInv != null) {
        return doInsertItemSided(item);
      }
      return doInsertItem(item);
    }

    private int doInsertItem(ItemStack item) {
      // TODO Auto-generated method stub
      return 0;
    }

    private int doInsertItemSided(ItemStack item) {

      for (int slot = 0; slot < sidedInv.getSizeInventory(); slot++) {
        if(sidedInv.canInsertItem(slot, item, inventorySide)) {
          ItemStack current = sidedInv.getStackInSlot(slot);
          if(current == null) {
            sidedInv.setInventorySlotContents(slot, item.copy());
            return item.stackSize;
          }
          if(current.isItemEqual(item)) {
            int insertNo = item.getMaxStackSize() - item.stackSize;
            if(insertNo <= 0) {
              return 0;
            }
            item = item.copy();
            item.stackSize = item.stackSize + insertNo;
            sidedInv.setInventorySlotContents(slot, item);
            return insertNo;
          }
        }
      }
      return 0;
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
