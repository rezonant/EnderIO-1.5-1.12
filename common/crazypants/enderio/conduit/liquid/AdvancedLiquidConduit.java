package crazypants.enderio.conduit.liquid;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.ModObject;
import crazypants.enderio.conduit.AbstractConduitNetwork;
import crazypants.enderio.conduit.ConduitUtil;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.RaytraceResult;
import crazypants.enderio.conduit.geom.CollidableComponent;
import crazypants.render.IconUtil;
import crazypants.util.BlockCoord;

public class AdvancedLiquidConduit extends AbstractLiquidConduit {

  public static final String ICON_KEY = "enderio:liquidConduitAdvnaced";
  public static final String ICON_CORE_KEY = "enderio:liquidConduitCoreAdvanced";
  public static final String ICON_EXTRACT_KEY = "enderio:liquidConduitAdvancedInput";
  public static final String ICON_INSERT_KEY = "enderio:liquidConduitAdvancedOutput";

  static final Map<String, Icon> ICONS = new HashMap<String, Icon>();

  @SideOnly(Side.CLIENT)
  public static void initIcons() {
    IconUtil.addIconProvider(new IconUtil.IIconProvider() {

      @Override
      public void registerIcons(IconRegister register) {
        ICONS.put(ICON_KEY, register.registerIcon(ICON_KEY));
        ICONS.put(ICON_CORE_KEY, register.registerIcon(ICON_CORE_KEY));
        ICONS.put(ICON_EXTRACT_KEY, register.registerIcon(ICON_EXTRACT_KEY));
        ICONS.put(ICON_INSERT_KEY, register.registerIcon(ICON_INSERT_KEY));
      }

      @Override
      public int getTextureType() {
        return 0;
      }

    });
  }

  private AdvancedLiquidConduitNetwork network;

  @Override
  public boolean onBlockActivated(EntityPlayer player, RaytraceResult res, List<RaytraceResult> all) {
    if(player.getCurrentEquippedItem() == null) {
      return false;
    }
    if(ConduitUtil.isToolEquipped(player)) {

      if(!getBundle().getEntity().worldObj.isRemote) {

        if(res != null && res.component != null) {

          ForgeDirection connDir = res.component.dir;
          ForgeDirection faceHit = ForgeDirection.getOrientation(res.movingObjectPosition.sideHit);

          if(connDir == ForgeDirection.UNKNOWN || connDir == faceHit) {
            BlockCoord loc = getLocation().getLocation(faceHit);
            ILiquidConduit n = ConduitUtil.getConduit(getBundle().getEntity().worldObj, loc.x, loc.y, loc.z, ILiquidConduit.class);
            if(n == null) {
              return false;
            }
            if(!(n instanceof AdvancedLiquidConduit)) {
              return false;
            }
            AdvancedLiquidConduit neighbour = (AdvancedLiquidConduit) n;
            //TODO:
            //            if(neighbour.getFluidType() == null || getFluidType() == null) {
            //              FluidStack type = getFluidType();
            //              type = type != null ? type : neighbour.getFluidType();
            //              neighbour.setFluidType(type);
            //              setFluidType(type);
            //            }
            return ConduitUtil.joinConduits(this, faceHit);
          } else if(containsExternalConnection(connDir)) {
            // Toggle extraction mode
            setConnectionMode(connDir, getNextConnectionMode(connDir));
          } else if(containsConduitConnection(connDir)) {
            //            FluidStack curFluidType = null;
            //            if(network != null) {
            //              curFluidType = network.getFluidType();
            //            }
            ConduitUtil.disconectConduits(this, connDir);
            //            setFluidType(curFluidType);

          }
        }
      }
      return true;

    } else if(player.getCurrentEquippedItem().itemID == Item.bucketEmpty.itemID) {

      //      if(!getBundle().getEntity().worldObj.isRemote) {
      //        long curTick = getBundle().getEntity().worldObj.getWorldTime();
      //        if(curTick - lastEmptyTick < 20) {
      //          numEmptyEvents++;
      //        } else {
      //          numEmptyEvents = 1;
      //        }
      //        lastEmptyTick = curTick;
      //
      //        if(numEmptyEvents < 2) {
      //          tank.setAmount(0);
      //        } else if(network != null) {
      //          network.setFluidType(null);
      //          numEmptyEvents = 0;
      //        }
      //      }
      //
      //      return true;
    } else {

      //      FluidStack fluid = FluidContainerRegistry.getFluidForFilledItem(player.getCurrentEquippedItem());
      //      if(fluid != null) {
      //        if(!getBundle().getEntity().worldObj.isRemote) {
      //          if(network != null && (network.getFluidType() == null || network.getTotalVolume() < 500)) {
      //            network.setFluidType(fluid);
      //            ChatMessageComponent c = ChatMessageComponent.createFromText("Fluid type set to " + FluidRegistry.getFluidName(fluid));
      //            player.sendChatToPlayer(c);
      //          }
      //        }
      //        return true;
      //      }
    }

    return false;
  }

  @Override
  public Class<? extends IConduit> getBaseConduitType() {
    return ILiquidConduit.class;
  }

  @Override
  public ItemStack createItem() {
    return new ItemStack(ModObject.itemLiquidConduit.actualId, 1, 1);
  }

  @Override
  public AbstractConduitNetwork<?, ?> getNetwork() {
    return network;
  }

  @Override
  public boolean setNetwork(AbstractConduitNetwork<?, ?> network) {
    if(network == null) {
      this.network = null;
      return true;
    }
    if(!(network instanceof AdvancedLiquidConduitNetwork)) {
      return false;
    }

    AdvancedLiquidConduitNetwork n = (AdvancedLiquidConduitNetwork) network;
    //TODO:
    //    if(tank.getFluid() == null) {
    //      tank.setLiquid(n.getFluidType() == null ? null : n.getFluidType().copy());
    //    } else if(n.getFluidType() == null) {
    //      n.setFluidType(tank.getFluid());
    //    } else if(!tank.getFluid().isFluidEqual(n.getFluidType())) {
    //      return false;
    //    }
    this.network = n;
    return true;

  }

  @Override
  public boolean canConnectToExternal(ForgeDirection direction, boolean ignoreDisabled) {
    return getExternalHandler(direction) != null;
  }

  @Override
  public boolean canConnectToConduit(ForgeDirection direction, IConduit con) {
    if(!super.canConnectToConduit(direction, con)) {
      return false;
    }
    if(!(con instanceof AdvancedLiquidConduit)) {
      return false;
    }
    return true;
  }

  @Override
  public Icon getTextureForState(CollidableComponent component) {
    if(component.dir == ForgeDirection.UNKNOWN) {
      return ICONS.get(ICON_CORE_KEY);
    }
    return ICONS.get(ICON_KEY);
  }

  public Icon getTextureForInputMode() {
    return ICONS.get(ICON_EXTRACT_KEY);
  }

  public Icon getTextureForOutputMode() {
    return ICONS.get(ICON_INSERT_KEY);
  }

  @Override
  public Icon getTransmitionTextureForState(CollidableComponent component) {
    //TODO:
    //    if(tank.getFluid() != null && tank.getFluid().getFluid() != null) {
    //      return tank.getFluid().getFluid().getStillIcon();
    //    }
    return null;
  }

  // ------------------------------------------- Fluid API

  @Override
  public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
    return 0;
  }

  @Override
  public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
    return null;
  }

  @Override
  public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
    return null;
  }

  @Override
  public boolean canFill(ForgeDirection from, Fluid fluid) {
    return false;
  }

  @Override
  public boolean canDrain(ForgeDirection from, Fluid fluid) {
    return false;
  }

  @Override
  public FluidTankInfo[] getTankInfo(ForgeDirection from) {
    return null;
  }

}
