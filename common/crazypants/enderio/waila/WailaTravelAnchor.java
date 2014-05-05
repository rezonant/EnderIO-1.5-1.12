package crazypants.enderio.waila;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import crazypants.enderio.EnderIO;
import crazypants.util.WailaUtil;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;

public class WailaTravelAnchor extends WailaDataProvider {

	@Override
	public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip,
			IWailaDataAccessor accessor, IWailaConfigHandler config) {
		return currenttip;
	}

	@Override
	public String getModuleName()
	{
		return "Remoting";
	}
}
