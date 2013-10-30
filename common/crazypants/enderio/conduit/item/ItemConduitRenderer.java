package crazypants.enderio.conduit.item;

import static crazypants.render.CubeRenderer.addVecWithUV;

import java.util.List;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.Icon;
import crazypants.enderio.EnderIO;
import crazypants.enderio.conduit.ConnectionMode;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.geom.CollidableComponent;
import crazypants.enderio.conduit.render.DefaultConduitRenderer;
import crazypants.render.BoundingBox;
import crazypants.vecmath.Vertex;

public class ItemConduitRenderer extends DefaultConduitRenderer {

  @Override
  public boolean isRendererForConduit(IConduit conduit) {
    if(conduit instanceof IItemConduit) {
      return true;
    }
    return false;
  }

  @Override
  protected void renderConduit(Icon tex, IConduit conduit, CollidableComponent component, float brightness) {
    if(isNSEWUP(component.dir)) {
      IItemConduit ic = (IItemConduit) conduit;
      BoundingBox[] cubes = toCubes(component.bound);
      for (BoundingBox cube : cubes) {
        drawSection(cube, tex.getMinU(), tex.getMaxU(), tex.getMinV(), tex.getMaxV(), component.dir, false);
      }

    } else {
      drawSection(component.bound, tex.getMinU(), tex.getMaxU(), tex.getMinV(), tex.getMaxV(), component.dir, true);
    }

    if(conduit.getConectionMode(component.dir) == ConnectionMode.DISABLED) {
      tex = EnderIO.blockConduitBundle.getConnectorIcon();
      List<Vertex> corners = component.bound.getCornersWithUvForFace(component.dir, tex.getMinU(), tex.getMaxU(), tex.getMinV(), tex.getMaxV());
      Tessellator tessellator = Tessellator.instance;
      for (Vertex c : corners) {
        addVecWithUV(c.xyz, c.uv.x, c.uv.y);
      }
      //back face
      for (int i = corners.size() - 1; i >= 0; i--) {
        Vertex c = corners.get(i);
        addVecWithUV(c.xyz, c.uv.x, c.uv.y);
      }
    }
  }

}
