package crazypants.enderio.conduit;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public interface IFacadable {

  enum FacadeRenderState {
    NONE,
    FULL,
    WIRE_FRAME
  }

  @SideOnly(Side.CLIENT)
  FacadeRenderState getFacadeRenderedAs();

  @SideOnly(Side.CLIENT)
  void setFacadeRenderAs(FacadeRenderState state);

  boolean hasFacade();

  void setFacadeId(int blockID);

  void setFacadeId(int blockID, boolean triggerUpdate);

  int getFacadeId();

  void setFacadeMetadata(int meta);

  int getFacadeMetadata();

  int getLightOpacity();

  void setLightOpacity(int opacity);

}