package crazypants.enderio.waila;

import java.util.List;

import codechicken.nei.forge.GuiContainerManager;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import crazypants.enderio.machine.power.PowerDisplayUtil;
import crazypants.util.WailaUtil;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;

public class WailaDataProvider implements IWailaDataProvider {
	public WailaDataProvider()
	{
	}
	
	public String getModuleName()
	{
		return "";
	}
	
	@Override
	public ItemStack getWailaStack(IWailaDataAccessor accessor,
			IWailaConfigHandler config) {
		return null;
	}

	public String getHeadAddendum(ItemStack itemStack, IWailaDataAccessor accessor, IWailaConfigHandler config)
	{
		return null;
	}

	public String getDisplayName(ItemStack itemStack, IWailaDataAccessor accessor, IWailaConfigHandler config)
	{
		return GuiContainerManager.itemDisplayNameShort(itemStack);
	}
	
	@Override
	public final List<String> getWailaHead(ItemStack itemStack, List<String> currenttip,
			IWailaDataAccessor accessor, IWailaConfigHandler config) {
		String line = getHeadAddendum(itemStack, accessor, config);
		
		if (currenttip.size() > 0)
			currenttip.set(0, getDisplayName(itemStack, accessor, config)+(line != null ? line : ""));
		
		return currenttip;
	}
	
	@Override
	public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip,
			IWailaDataAccessor accessor, IWailaConfigHandler config) {
		return currenttip;
	}
	
	@Override
	public final List<String> getWailaTail(ItemStack itemStack, List<String> currenttip,
			IWailaDataAccessor accessor, IWailaConfigHandler config) {
		
		if (currenttip.size() > 0)
			currenttip.set(0, WailaUtil.getWailaModByLine(getModuleName()));
		return currenttip;
	}
}
