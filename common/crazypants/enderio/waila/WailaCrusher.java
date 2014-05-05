package crazypants.enderio.waila;

import java.util.List;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import crazypants.enderio.EnderIO;
import crazypants.enderio.machine.alloy.TileAlloySmelter;
import crazypants.enderio.machine.crusher.TileCrusher;
import crazypants.enderio.machine.power.PowerDisplayUtil;
import crazypants.util.WailaUtil;

public class WailaCrusher extends WailaDataProvider {
	@Override
	public String getModuleName() {
		return "Machines";
	}
	
	@Override
	public String getHeadAddendum(ItemStack itemStack, IWailaDataAccessor accessor, IWailaConfigHandler config) {
		Block block = EnderIO.blockCrusher;

		TileEntity te = accessor.getTileEntity();
		String line = null;
		
		if (te instanceof TileCrusher) {
			TileCrusher crusher = (TileCrusher)te;
			
			line = WailaUtil.DARK_GRAY+" ("+
				PowerDisplayUtil.formatPower(crusher.getPowerHandler().getEnergyStored())+" "+
				PowerDisplayUtil.ofStr()+" "+
				PowerDisplayUtil.formatPower(crusher.getPowerHandler().getMaxEnergyStored())+" "+
				PowerDisplayUtil.abrevation()+")"+WailaUtil.GRAY;
		}
		
		return line;
	}
}
