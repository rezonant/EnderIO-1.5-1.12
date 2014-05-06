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
				return " "+WailaUtil.DARK_GRAY+"("+Lang.localize("gui.trans.channel")+WailaUtil.WHITE+channel.getName()+WailaUtil.DARK_GRAY+")";
			}
			
			return " "+WailaUtil.DARK_GRAY+"("+Lang.localize("gui.trans.inactive")+")";	
		}
		
		return null;
	}
	
	@Override
	public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip,
			IWailaDataAccessor accessor, IWailaConfigHandler config) {
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
		
		if (cube.getItemsHeld() > 0)
			bullets.add(cube.getItemsHeld()+" "+Lang.localize("gui.trans.items"));
		
		float currentEnergy = cube.getEnergyStored(ForgeDirection.UP);
		float currentPercent = cube.getEnergyStoredScaled(100);
		if (currentEnergy > 0) {
			if (currentPercent >= 99)
				bullets.add(WailaUtil.GREEN+Lang.localize("gui.trans.energized")+WailaUtil.GRAY);
			else
				bullets.add(PowerDisplayUtil.formatPower(currentEnergy/10.0)+" "+PowerDisplayUtil.abrevation()+" "+Lang.localize("gui.powerMonitor.stored"));
		}

		float net = cube.getReceivedEnergyPerTick() - cube.getTransmittedEnergyPerTick();
		
		if ((int)net != 0)
			bullets.add(WailaUtil.formatColoredWailaValue(net, true)+" "+Lang.localize("gui.powerMonitor.net"));
		
		if (stats.transceiverCount > 1)
			bullets.add((stats.transceiverCount - 1)+" "+Lang.localize("gui.trans.peers"));
		
		if (bullets.size() > 0)
			currenttip.add(StringUtil.join(bullets, " • "));
		
		if (accessor.getPlayer().isSneaking() && channel != null) {
			currenttip.add(Lang.localize("gui.trans.channelTotals"));
			currenttip.add(PowerDisplayUtil.format(stats.energyHeld, true)+" • "+
					stats.itemsHeld+" "+Lang.localize("gui.trans.items")+" • "+
					PowerDisplayUtil.format(stats.inputEnergyMeter - stats.outputEnergyMeter, true, true));
		}
		
		return currenttip;
	}
}
