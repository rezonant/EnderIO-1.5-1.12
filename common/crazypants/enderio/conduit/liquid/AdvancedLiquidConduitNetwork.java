package crazypants.enderio.conduit.liquid;

import crazypants.enderio.conduit.AbstractConduitNetwork;

public class AdvancedLiquidConduitNetwork extends AbstractConduitNetwork<ILiquidConduit, AdvancedLiquidConduit> {

  protected AdvancedLiquidConduitNetwork() {
    super(AdvancedLiquidConduit.class);
  }

  @Override
  public Class<ILiquidConduit> getBaseConduitType() {
    return ILiquidConduit.class;
  }

}
