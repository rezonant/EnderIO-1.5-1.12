package crazypants.enderio.waila;

import java.util.List;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import crazypants.enderio.EnderIO;
import crazypants.util.WailaUtil;

public class WailaPowerMonitor extends WailaDataProvider {
	@Override
	public String getModuleName()
	{
		return "Energy";
	}
}
