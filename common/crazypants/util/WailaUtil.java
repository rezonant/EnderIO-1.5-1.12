package crazypants.util;

import net.minecraft.tileentity.TileEntity;
import crazypants.enderio.machine.AbstractMachineEntity;
import crazypants.enderio.machine.RedstoneControlMode;
import crazypants.enderio.machine.power.PowerDisplayUtil;

public class WailaUtil {
	public static final String GREEN = "\u00a7a";
	public static final String CYAN = "\u00a7b";
	public static final String PINK = "\u00a7c";
	public static final String PURPLE = "\u00a7d";
	public static final String YELLOW = "\u00a7e";
	public static final String WHITE = "\u00a7f";
	public static final String DARK_BLUE = "\u00a79";
	public static final String DARK_GRAY = "\u00a78";
	public static final String GRAY = "\u00a77";
	public static final String ORANGE = "\u00a76";
	public static final String PURPLE_2 = "\u00a75";
	public static final String RED = "\u00a74";
	public static final String CYAN_2 = "\u00a73";
	public static final String GREEN_2 = "\u00a72";
	public static final String DARKER_BLUE = "\u00a71";
	public static final String BLACK = "\u00a70";

	public static final String ITALIC = "\u00a7o";
	public static final String BOLD = "\u00a7l";
	public static final String UNDERLINE = "\u00a7n";
	public static final String RANDOM = "\u00a7k";
	public static final String STRIKETHROUGH = "\u00a7m";

	public static String formatColoredWailaValue(float value, boolean perTick)
	{
		String color = "";
		if (value == 0)
			color = WailaUtil.GRAY;
		else if (value < 0)
			color = WailaUtil.RED;
		else
			color = WailaUtil.GREEN;
		
		return color+formatWailaValue(value, perTick)+WailaUtil.GRAY;
	}
	
	public static String formatRedstoneStatus(AbstractMachineEntity te)
	{
		boolean redstoneCheckPassed = te.hasRedstoneCheckPassed();
		String rsModeStr = null;
		RedstoneControlMode rsMode = te.getRedstoneControlMode();

		String onStr = Lang.localize("gui.tooltip.redstoneControlMode.meter.on");
		String offStr = Lang.localize("gui.tooltip.redstoneControlMode.meter.off");
		String withSignalStr = Lang.localize("gui.tooltip.redstoneControlMode.meter.withSignal");
		String withoutSignalStr = Lang.localize("gui.tooltip.redstoneControlMode.meter.withoutSignal");
		
		if (te.getRedstoneControlMode() == RedstoneControlMode.NEVER)
			rsModeStr = "Disabled";
		else if (te.getRedstoneControlMode() == RedstoneControlMode.ON)
			rsModeStr = redstoneCheckPassed ? 
					WailaUtil.GREEN_2+onStr+WailaUtil.DARK_GRAY+" "+withSignalStr 
					: WailaUtil.RED+offStr+WailaUtil.DARK_GRAY+" "+withoutSignalStr;
		else if (te.getRedstoneControlMode() == RedstoneControlMode.OFF)
			rsModeStr = redstoneCheckPassed ? 
					WailaUtil.GREEN_2+onStr+WailaUtil.DARK_GRAY+" "+withoutSignalStr
					: WailaUtil.RED+offStr+WailaUtil.DARK_GRAY+" "+withSignalStr;
		
		if (rsModeStr == null)
			return null;
		
		return WailaUtil.DARK_GRAY+rsModeStr+WailaUtil.GRAY;
	}
	
	public static String formatWailaValue(float value, boolean perTick)
	{
		return PowerDisplayUtil.formatPower(value)+PowerDisplayUtil.abrevation()
				+(perTick? PowerDisplayUtil.perTickStr() : "");
	}
	
	public static String getWailaModByLine(String module)
	{
		return DARK_BLUE+ITALIC+"Ender IO"+DARK_GRAY+" "+module;
	}
}
