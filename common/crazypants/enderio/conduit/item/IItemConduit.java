package crazypants.enderio.conduit.item;

import net.minecraft.util.Icon;
import crazypants.enderio.conduit.IConduit;

public interface IItemConduit extends IConduit {

  Icon getTextureForInputMode();

  Icon getTextureForOutputMode();

  Icon getTextureForInOutMode();

}
