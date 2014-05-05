package crazypants.enderio.waila;

import java.util.List;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import crazypants.enderio.EnderIO;
import crazypants.enderio.machine.generator.TileEntityStirlingGenerator;
import crazypants.enderio.machine.power.PowerDisplayUtil;
import crazypants.enderio.machine.solar.TileEntitySolarPanel;
import crazypants.util.WailaUtil;

public class WailaStirlingGenerator extends WailaDataProvider {
	@Override
	public String getModuleName() {
		return "Machines";
	}
	
	@Override
	public String getHeadAddendum(ItemStack itemStack, IWailaDataAccessor accessor, IWailaConfigHandler config)
	{
		TileEntity te = accessor.getTileEntity();
		
		if (te instanceof TileEntityStirlingGenerator) {
			TileEntityStirlingGenerator gen = (TileEntityStirlingGenerator)te;

			float progress = gen.getProgress();
			float energyPerTick = TileEntityStirlingGenerator.ENERGY_PER_TICK;
			
			ItemStack stack = gen.getStackInSlot(0);
			
			if (progress == 0)
				energyPerTick = 0;
			
			return WailaUtil.DARK_GRAY+" ("+PowerDisplayUtil.format(energyPerTick, true, true)+")"+WailaUtil.GRAY;
		}
		
		return null;
	}

	@Override
	public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip,
			IWailaDataAccessor accessor, IWailaConfigHandler config) {

		TileEntity te = accessor.getTileEntity();
		
		if (te instanceof TileEntityStirlingGenerator) {
			TileEntityStirlingGenerator gen = (TileEntityStirlingGenerator)te;

			float progress = gen.getProgress();
			float energyPerTick = TileEntityStirlingGenerator.ENERGY_PER_TICK;
			
			ItemStack stack = gen.getStackInSlot(0);
			
			if (progress == 0)
				energyPerTick = 0;
			
			if (accessor.getPlayer().isSneaking()) {
				String rsMode = WailaUtil.formatRedstoneStatus(gen);
				currenttip.add(WailaUtil.DARK_GRAY+Math.round((1-progress)*100)
						+"% • "+stack.stackSize+" fuel"+WailaUtil.GRAY+
						(rsMode != null? " • "+rsMode : "")
				);
			}
			
		}
		
		return currenttip;
	}
}
