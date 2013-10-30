package crazypants.enderio.conduit.item;

import crazypants.enderio.conduit.AbstractConduitNetwork;

public class ItemConduitNetwork extends AbstractConduitNetwork<IItemConduit> {

  @Override
  public Class<? extends IItemConduit> getBaseConduitType() {
    return IItemConduit.class;
  }

}
