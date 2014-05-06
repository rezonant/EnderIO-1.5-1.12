package crazypants.enderio.waila;

import java.util.List;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import crazypants.enderio.EnderIO;
import crazypants.enderio.machine.reservoir.TileReservoir;
import crazypants.util.WailaUtil;

public class WailaReservoir extends WailaDataProvider {
	@Override
	public String getHeadAddendum(ItemStack itemStack, IWailaDataAccessor accessor, IWailaConfigHandler config) {

		if (!config.getConfig("enderio.official.enabled"))
			return null;
		
		TileEntity te = accessor.getTileEntity();
		if (te instanceof TileReservoir) {
			TileReservoir reservoir = (TileReservoir)te;
			return " "+WailaUtil.DARK_GRAY+reservoir.getFilledRatio()*100+"%";
		}
		
		return null;
	}

	public String getModuleName() {
		return "Fluids";
	}
}
