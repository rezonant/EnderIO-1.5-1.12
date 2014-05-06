package crazypants.util;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

public class Lang {

  public static String localize(String s) {
    return localize(s, true);
  }

  /**
   * Look up the short localized name for the given item
   * @param stack The item stack to examine
   * @return The short localized name or the regular localized name if none other is found.
   */
  public static String localizeShortName(ItemStack stack) {
	  String[] parts = stack.getItem().getUnlocalizedName().split(":");
	  if (parts.length > 1) {
		  String shortName = localize(parts[1]+".shortName");
		  
		  if (shortName != parts[1]+".shortName")
			  return shortName;
	  }
	  
	  return stack.getDisplayName();
  }
  
  public static String localize(String s, boolean appendEIO) {
    if(appendEIO) {
      s = "enderio." + s;
    }
    return StatCollector.translateToLocal(s);
  }

  public static String[] localizeList(String string) {
    String s = localize(string);
    return s.split("\\|");
  }

}
