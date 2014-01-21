package crazypants.render;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.ForgeDirection;
import crazypants.enderio.EnderIO;
import crazypants.vecmath.Vector2d;
import crazypants.vecmath.Vector3d;
import crazypants.vecmath.Vector3f;
import crazypants.vecmath.Vector4f;
import crazypants.vecmath.Vertex;

public class CustomCubeRenderer {

  private static final Tessellator DEFAULT_TES = Tessellator.instance;

  private static final MockTesselator BUF_TES = new MockTesselator();

  private InnerRenderBlocks rb = null;

  public void renderBlock(IBlockAccess ba, Block par1Block, int par2, int par3, int par4) {
    if(rb == null) {
      rb = new InnerRenderBlocks(ba);
    }
    rb.setRenderBoundsFromBlock(par1Block);
    try {
      rb.setTesselatorEnabled(false);
      BUF_TES.reset();
      rb.renderStandardBlock(par1Block, par2, par3, par4);
    } finally {
      rb.setTesselatorEnabled(true);
    }

  }

  private class InnerRenderBlocks extends RenderBlocks {

    private InnerRenderBlocks(IBlockAccess par1iBlockAccess) {
      super(par1iBlockAccess);
    }

    void setTesselatorEnabled(boolean enabled) {
      if(enabled) {
        Tessellator.instance = DEFAULT_TES;
      } else {
        Tessellator.instance = BUF_TES;
      }
    }

    private void resetTesForFace() {
      int b = BUF_TES.brightness;
      Vector4f col = BUF_TES.color;
      BUF_TES.reset();
      BUF_TES.setBrightness(b);
      if(col != null) {
        BUF_TES.setColorRGBA_F(col.x, col.y, col.z, col.w);
      }
    }

    private void renderFace(ForgeDirection face, Block par1Block, double x, double y, double z, Icon texture) {
      renderFaceToBuffer(face, par1Block, x, y, z, texture);

      boolean forceAllEdges = false;
      boolean translateToXYZ = true;

      List<Vertex> vertices = BUF_TES.getVertices();
      List<ForgeDirection> edges;
      if(forceAllEdges) {
        edges = RenderUtil.getEdgesForFace(face);
      } else {
        edges = RenderUtil.getNonConectedEdgesForFace(blockAccess, (int) x, (int) y, (int) z, face);
      }

      float scaleFactor = 15f / 16f;
      Vector2d uv = new Vector2d();

      for (ForgeDirection edge : edges) {

        moveCorner(vertices, edge, 1 - scaleFactor, face);

        float xLen = 1 - Math.abs(edge.offsetX) * scaleFactor;
        float yLen = 1 - Math.abs(edge.offsetY) * scaleFactor;
        float zLen = 1 - Math.abs(edge.offsetZ) * scaleFactor;
        BoundingBox bb = BoundingBox.UNIT_CUBE.scale(xLen, yLen, zLen);

        List<Vector3f> corners = bb.getCornersForFace(face);
        int index = 0;
        for (Vector3f unitCorn : corners) {
          Vector3d corner = new Vector3d(unitCorn);
          if(translateToXYZ) {
            corner.x += x;
            corner.y += y;
            corner.z += z;
          }

          corner.x += (float) (edge.offsetX * 0.5) - Math.signum(edge.offsetX) * xLen / 2f;
          corner.y += (float) (edge.offsetY * 0.5) - Math.signum(edge.offsetY) * yLen / 2f;
          corner.z += (float) (edge.offsetZ * 0.5) - Math.signum(edge.offsetZ) * zLen / 2f;

          texture = EnderIO.blockAlloySmelter.getBlockTextureFromSide(3);
          if(translateToXYZ) {
            RenderUtil.getUvForCorner(uv, corner, (int) x, (int) y, (int) z, face, texture);
          } else {
            RenderUtil.getUvForCorner(uv, corner, 0, 0, 0, face, texture);
          }
          DEFAULT_TES.setBrightness(vertices.get(index).brightness);
          Vector4f col = vertices.get(index).getColor();
          if(col != null) {
            DEFAULT_TES.setColorRGBA_F(col.x, col.y, col.z, col.w);
          }
          DEFAULT_TES.addVertexWithUV(corner.x, corner.y, corner.z, uv.x, uv.y);
          index++;
        }

      }

      //Centre of the face
      RenderUtil.addVerticesToTessellator(vertices, DEFAULT_TES);
    }

    private void moveCorner(List<Vertex> vertices, ForgeDirection edge, float scaleFactor, ForgeDirection face) {

      int[] indices = getClosest(edge, vertices, face);
      vertices.get(indices[0]).xyz.x -= scaleFactor * edge.offsetX;
      vertices.get(indices[1]).xyz.x -= scaleFactor * edge.offsetX;
      vertices.get(indices[0]).xyz.y -= scaleFactor * edge.offsetY;
      vertices.get(indices[1]).xyz.y -= scaleFactor * edge.offsetY;
      vertices.get(indices[0]).xyz.z -= scaleFactor * edge.offsetZ;
      vertices.get(indices[1]).xyz.z -= scaleFactor * edge.offsetZ;

    }

    private int[] getClosest(ForgeDirection edge, List<Vertex> vertices, ForgeDirection face) {
      int[] res = new int[] { -1, -1 };
      boolean highest = edge.offsetX > 0 || edge.offsetY > 0 || edge.offsetZ > 0;
      double minMax = highest ? -Double.MAX_VALUE : Double.MAX_VALUE;
      int index = 0;
      for (Vertex v : vertices) {
        double val = get(v, edge);
        if(highest ? val >= minMax : val <= minMax) {
          if(val != minMax) {
            res[0] = index;
          } else {
            res[1] = index;
          }
          minMax = val;
        }
        index++;
      }
      return res;
    }

    private double get(Vertex v, ForgeDirection edge) {
      if(edge == ForgeDirection.EAST || edge == ForgeDirection.WEST) {
        return v.x();
      }
      if(edge == ForgeDirection.UP || edge == ForgeDirection.DOWN) {
        return v.y();
      }
      return v.z();
    }

    private void renderFaceToBuffer(ForgeDirection face, Block par1Block, double par2, double par4, double par6, Icon par8Icon) {
      setTesselatorEnabled(false);
      resetTesForFace();
      switch (face) {
      case DOWN:
        super.renderFaceYNeg(par1Block, par2, par4, par6, par8Icon);
        break;
      case EAST:
        super.renderFaceXPos(par1Block, par2, par4, par6, par8Icon);
        break;
      case NORTH:
        super.renderFaceZNeg(par1Block, par2, par4, par6, par8Icon);
        break;
      case SOUTH:
        super.renderFaceZPos(par1Block, par2, par4, par6, par8Icon);
        break;
      case UP:
        super.renderFaceYPos(par1Block, par2, par4, par6, par8Icon);
        break;
      case WEST:
        super.renderFaceXNeg(par1Block, par2, par4, par6, par8Icon);
        break;
      case UNKNOWN:
      default:
        break;
      }

    }

    @Override
    public void renderFaceYNeg(Block par1Block, double par2, double par4, double par6, Icon par8Icon) {
      renderFace(ForgeDirection.DOWN, par1Block, par2, par4, par6, par8Icon);
    }

    @Override
    public void renderFaceYPos(Block par1Block, double par2, double par4, double par6, Icon par8Icon) {
      resetTesForFace();
      super.renderFaceYPos(par1Block, par2, par4, par6, par8Icon);
      renderFace(ForgeDirection.UP, par1Block, par2, par4, par6, par8Icon);
    }

    @Override
    public void renderFaceZNeg(Block par1Block, double par2, double par4, double par6, Icon par8Icon) {
      resetTesForFace();
      super.renderFaceZNeg(par1Block, par2, par4, par6, par8Icon);
      renderFace(ForgeDirection.NORTH, par1Block, par2, par4, par6, par8Icon);
    }

    @Override
    public void renderFaceZPos(Block par1Block, double par2, double par4, double par6, Icon par8Icon) {
      resetTesForFace();
      super.renderFaceZPos(par1Block, par2, par4, par6, par8Icon);
      renderFace(ForgeDirection.SOUTH, par1Block, par2, par4, par6, par8Icon);
    }

    @Override
    public void renderFaceXNeg(Block par1Block, double par2, double par4, double par6, Icon par8Icon) {
      resetTesForFace();
      super.renderFaceXNeg(par1Block, par2, par4, par6, par8Icon);
      renderFace(ForgeDirection.WEST, par1Block, par2, par4, par6, par8Icon);
    }

    @Override
    public void renderFaceXPos(Block par1Block, double par2, double par4, double par6, Icon par8Icon) {
      resetTesForFace();
      super.renderFaceXPos(par1Block, par2, par4, par6, par8Icon);
      renderFace(ForgeDirection.EAST, par1Block, par2, par4, par6, par8Icon);
    }

  }

  private static class MockTesselator extends Tessellator {

    private final List<Vertex> vertices = new ArrayList<Vertex>();

    private boolean hasTexture;
    private double textureU;
    private double textureV;
    private boolean hasBrightness;
    private int brightness;
    private boolean hasColor;
    private Vector4f color;
    private boolean hasNormals;
    private Vector3f normal;

    public List<Vertex> getVertices() {
      return vertices;
    }

    @Override
    public void setTextureUV(double par1, double par3) {
      super.setTextureUV(par1, par3);
      this.hasTexture = true;
      this.textureU = par1;
      this.textureV = par3;
    }

    @Override
    public void setBrightness(int par1) {
      super.setBrightness(par1);
      this.hasBrightness = true;
      this.brightness = par1;
    }

    /**
     * Sets the RGBA values for the color. Also clamps them to 0-255.
     */
    @Override
    public void setColorRGBA(int r, int g, int b, int a) {
      super.setColorRGBA(r, g, b, a);
      hasColor = true;
      color = ColorUtil.toFloat(new Color(r, g, b, a));
    }

    /**
     * Sets the normal for the current draw call.
     */
    @Override
    public void setNormal(float par1, float par2, float par3) {
      super.setNormal(par1, par2, par3);
      hasNormals = true;
      normal = new Vector3f(par1, par2, par3);
    }

    public void reset() {
      hasNormals = false;
      this.hasColor = false;
      this.hasTexture = false;
      this.hasBrightness = false;
      vertices.clear();
    }

    @Override
    public void addVertex(double x, double y, double z) {
      Vertex v = new Vertex();
      v.setXYZ(x, y, z);
      if(hasTexture) {
        v.setUV(textureU, textureV);
      }
      if(hasBrightness) {
        v.setBrightness(brightness);
      }
      if(this.hasColor) {
        v.setColor(this.color);
      }
      if(hasNormals) {
        v.setNormal(normal);
      }
      vertices.add(v);
    }

  }

}
