package crazypants.enderio.waila;

import java.util.List;

import crazypants.enderio.EnderIO;
import crazypants.enderio.machine.power.PowerDisplayUtil;
import crazypants.enderio.machine.power.TileCapacitorBank;
import crazypants.util.Lang;
import crazypants.util.TextColorUtil;
import crazypants.util.WailaUtil;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;

public class WailaCapacitorBank extends WailaDataProvider {
	@Override
	public String getHeadAddendum(ItemStack itemStack, IWailaDataAccessor accessor, IWailaConfigHandler config)
	{
		if (!config.getConfig("enderio.official.enabled"))
			return null;
		
		Block block = EnderIO.blockCapacitorBank;
		TileEntity te = accessor.getTileEntity();

		if (te instanceof TileCapacitorBank) {
			TileCapacitorBank capBank = (TileCapacitorBank)te;	
			String line = " ("+TextColorUtil.DARK_GRAY+
				PowerDisplayUtil.formatPower(capBank.getEnergyStored())+" "+TextColorUtil.GRAY
				 + PowerDisplayUtil.ofStr()+" "+TextColorUtil.DARK_GRAY+
				PowerDisplayUtil.formatPower(capBank.getMaxEnergyStored())+" "+PowerDisplayUtil.abrevation()+TextColorUtil.GRAY+")";
			
			return line;
		}
		
		return null;
	}
	
	@Override
	public String getModuleName()
	{
		return "Energy";
	}
	
	@Override
	public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip,
			IWailaDataAccessor accessor, IWailaConfigHandler config) {

		if (!config.getConfig("enderio.official.enabled"))
			return currenttip;
		
		TileEntity te = accessor.getTileEntity();
		
		if (te instanceof TileCapacitorBank) {
			TileCapacitorBank capBank = (TileCapacitorBank)te;	

			double receivedPerTick = capBank.getEnergyReceivedPerTick();
			double transmittedPerTick = capBank.getEnergyTransmittedPerTick();
			double chargedOutPerTick = capBank.getEnergyChargedOutPerTick();
			double net = receivedPerTick - transmittedPerTick - chargedOutPerTick;
			String netStr = WailaUtil.formatColoredWailaValue(net, true)+" "+Lang.localize("gui.powerMonitor.net");


			String rsMode = WailaUtil.formatRedstoneStatus(capBank);
			boolean showMore = config.getConfig("enderio.official.capacitor.moreInfo") && showMoreData(accessor, config);
			if (showMore) {
				currenttip.add(TextColorUtil.GREEN+"+"+WailaUtil.formatWailaValue(receivedPerTick, true)+" "+Lang.localize("gui.powerMonitor.in")+" "+TextColorUtil.GRAY+"  "+
						TextColorUtil.RED+"-"+WailaUtil.formatWailaValue(transmittedPerTick, true)+" "+Lang.localize("gui.powerMonitor.out")+"  "+
						TextColorUtil.RED+"-"+WailaUtil.formatWailaValue(chargedOutPerTick, true)+" "+Lang.localize("gui.powerMonitor.charging")+"");
				netStr = "   => "+netStr;
			}
			
			
			currenttip.add(netStr);

			if (showMore && rsMode != null)
				currenttip.add(rsMode);
		}
		
		return currenttip;
	}
}
