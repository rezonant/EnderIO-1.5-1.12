package crazypants.util;

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
	
	public static String getWailaModByLine(String module)
	{
		return DARK_BLUE+ITALIC+"Ender IO"+DARK_GRAY+" "+module;
	}
}
