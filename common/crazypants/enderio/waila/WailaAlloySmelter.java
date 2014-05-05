package crazypants.enderio.waila;

import java.util.ArrayList;
import java.util.List;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import crazypants.enderio.EnderIO;
import crazypants.enderio.machine.RedstoneControlMode;
import crazypants.enderio.machine.SlotDefinition;
import crazypants.enderio.machine.alloy.TileAlloySmelter;
import crazypants.enderio.machine.power.PowerDisplayUtil;
import crazypants.util.StringUtil;
import crazypants.util.WailaUtil;

public class WailaAlloySmelter extends WailaDataProvider {
	
	@Override
	public String getModuleName()
	{
		return "Machines";
	}
	
	@Override
	public String getHeadAddendum(ItemStack itemStack, IWailaDataAccessor accessor, IWailaConfigHandler config) {
		Block block = EnderIO.blockAlloySmelter;

		TileEntity te = accessor.getTileEntity();
		String line = null;
		
		if (te instanceof TileAlloySmelter) {
			TileAlloySmelter smelter = (TileAlloySmelter)te;
			
			line =
				WailaUtil.DARK_GRAY+" ("+
				PowerDisplayUtil.formatPower(smelter.getPowerHandler().getEnergyStored())+" "+
				PowerDisplayUtil.ofStr()+" "+
				PowerDisplayUtil.formatPower(smelter.getPowerHandler().getMaxEnergyStored())+" "+
				PowerDisplayUtil.abrevation()+")"+WailaUtil.GRAY;
		}
		
		return line;
	}
	
	@Override
	public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip,
			IWailaDataAccessor accessor, IWailaConfigHandler config) {

		Block block = EnderIO.blockAlloySmelter;
		TileEntity te = accessor.getTileEntity();
		String line = null;
		List<String> bullets = new ArrayList<String>();
		
		if (te instanceof TileAlloySmelter) {
			TileAlloySmelter smelter = (TileAlloySmelter)te;
			
			if (smelter.getProgress() > 0)
				bullets.add(smelter.getProgressScaled(100)+"%");
			
			String rsMode = WailaUtil.formatRedstoneStatus(smelter);
			if (rsMode != null)
				bullets.add(rsMode);
		}
		
		if (bullets.size() > 0)
			currenttip.add(StringUtil.join(bullets, " • "));
		
		return currenttip;
	}
}
