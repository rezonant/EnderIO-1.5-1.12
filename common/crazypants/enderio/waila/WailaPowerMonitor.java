package crazypants.enderio.waila;

import java.util.List;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import crazypants.enderio.EnderIO;
import crazypants.enderio.machine.monitor.TilePowerMonitor;
import crazypants.enderio.machine.power.PowerDisplayUtil;
import crazypants.util.Lang;
import crazypants.util.TextColorUtil;
import crazypants.util.WailaUtil;

public class WailaPowerMonitor extends WailaDataProvider {
	@Override
	public String getModuleName()
	{
		return "Energy";
	}

	@Override
	public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip,
			IWailaDataAccessor accessor, IWailaConfigHandler config) {

		TileEntity te = accessor.getTileEntity();
		
		if (te instanceof TilePowerMonitor) {
			TilePowerMonitor monitor = (TilePowerMonitor)te;
			
			currenttip.add(
					TextColorUtil.DARK_GRAY+
					PowerDisplayUtil.format(monitor.getAveMjRecieved(), true)+" "+Lang.localize("gui.powerMonitor.in")+" "+
					PowerDisplayUtil.format(monitor.getAveMjSent(), true)+" "+Lang.localize("gui.powerMonitor.out")+" "
			);
			
			if (showMoreData(accessor, config)) {
				currenttip.add(
						TextColorUtil.DARK_GRAY+
						PowerDisplayUtil.format(monitor.getEnergyStored(), false)+" "+PowerDisplayUtil.ofStr()+" "+
						PowerDisplayUtil.format(monitor.getMaxEnergyStored(ForgeDirection.UP), true));
			}
		}
		
		return currenttip;
	}
}
