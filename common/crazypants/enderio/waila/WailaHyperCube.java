package crazypants.enderio.waila;

import java.util.ArrayList;
import java.util.List;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import crazypants.enderio.EnderIO;
import crazypants.enderio.machine.hypercube.Channel;
import crazypants.enderio.machine.hypercube.ChannelStats;
import crazypants.enderio.machine.hypercube.TileHyperCube;
import crazypants.enderio.machine.power.PowerDisplayUtil;
import crazypants.util.Lang;
import crazypants.util.StringUtil;
import crazypants.util.TextColorUtil;
import crazypants.util.WailaUtil;

public class WailaHyperCube extends WailaDataProvider {

	@Override
	public String getModuleName()
	{
		return "Energy"; 
	}
	
	@Override
	public String getHeadAddendum(ItemStack itemStack, IWailaDataAccessor accessor, IWailaConfigHandler config) {
		Block block = EnderIO.blockHyperCube;
		TileEntity te = accessor.getTileEntity();
		
		if (te instanceof TileHyperCube) {
			TileHyperCube cube = (TileHyperCube)te;
			Channel channel = cube.getChannel();
			
			if (channel != null) {
				return " "+TextColorUtil.DARK_GRAY+"("+Lang.localize("gui.trans.channel")+" "+TextColorUtil.WHITE+channel.getName()+TextColorUtil.DARK_GRAY+")";
			}
			
			return " "+TextColorUtil.DARK_GRAY+"("+Lang.localize("gui.trans.inactive")+")";	
		}
		
		return null;
	}
	
	@Override
	public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip,
			IWailaDataAccessor accessor, IWailaConfigHandler config) {

		if (!config.getConfig("enderio.official.enabled"))
			return currenttip;
		
		Block block = EnderIO.blockHyperCube;
		TileEntity te = accessor.getTileEntity();
		
		if (!(te instanceof TileHyperCube)) {
			return currenttip;
		}
	
		TileHyperCube cube = (TileHyperCube)te;
		Channel channel = cube.getChannel();
		ChannelStats stats = new ChannelStats();

		List<String> bullets = new ArrayList<String>();

		if (channel != null)
			stats = channel.getStats();
		
		float currentEnergy = cube.getEnergyStoredSmooth();
		float currentPercent = cube.getEnergyStoredScaled(100);
		if (true || currentEnergy > 0) {
			if (currentPercent >= 99)
				bullets.add(TextColorUtil.GREEN+Lang.localize("gui.trans.energized")+TextColorUtil.GRAY);
			else
				bullets.add(TextColorUtil.WHITE+PowerDisplayUtil.formatPower(currentEnergy/10.0)+" "+PowerDisplayUtil.abrevation()+" "+TextColorUtil.DARK_GRAY+Lang.localize("gui.powerMonitor.stored"));
		}

		float net = cube.getReceivedEnergyPerTick() - cube.getTransmittedEnergyPerTick();
		
		//if ((int)net != 0)
			bullets.add(WailaUtil.formatColoredWailaValue(net, true)+" "+TextColorUtil.DARK_GRAY+Lang.localize("gui.powerMonitor.net"));

		if (true || cube.getItemsHeld() > 0)
			bullets.add(TextColorUtil.WHITE+cube.getItemsHeld()+TextColorUtil.DARK_GRAY+" "+Lang.localize("gui.trans.items"));
		
		if (stats.transceiverCount > 1)
			bullets.add(TextColorUtil.WHITE+(stats.transceiverCount - 1)+TextColorUtil.DARK_GRAY+" "+Lang.localize("gui.trans.peers"));
			
		if (bullets.size() > 0)
			currenttip.add(StringUtil.join(bullets, WailaUtil.TAB+WailaUtil.TAB/*" • "*/));

		if (config.getConfig("enderio.official.hypercube.moreInfo") && showMoreData(accessor, config) && channel != null) {
			currenttip.add("");
			currenttip.add(Lang.localize("gui.trans.channelTotals"));
			currenttip.add(TextColorUtil.WHITE+PowerDisplayUtil.format(stats.energyHeld, true)+TextColorUtil.GRAY+WailaUtil.TAB+WailaUtil.TAB+
					TextColorUtil.WHITE+PowerDisplayUtil.format(stats.inputEnergyMeter - stats.outputEnergyMeter, true, true)+TextColorUtil.GRAY+WailaUtil.TAB+WailaUtil.TAB+
					TextColorUtil.WHITE+stats.itemsHeld+" "+TextColorUtil.DARK_GRAY+Lang.localize("gui.trans.items")
			);
			
			String rsMode = WailaUtil.formatRedstoneStatus(cube.getRedstoneControlMode(), cube.hasRedstoneCheckPassed());
			if (config.getConfig("enderio.official.redstone") && rsMode != null) {
				currenttip.add(rsMode);
			}
		}
		
		return currenttip;
	}
}
