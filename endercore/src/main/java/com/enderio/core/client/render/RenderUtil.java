package com.enderio.core.client.render;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.client.model.pipeline.QuadBakingVertexConsumer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.lwjgl.opengl.GL11;

import com.enderio.core.api.client.render.VertexTransform;
import com.enderio.core.client.handlers.ClientHandler;
import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NNList.Callback;
import com.enderio.core.common.vecmath.Matrix4d;
import com.enderio.core.common.vecmath.VecmathUtil;
import com.enderio.core.common.vecmath.Vector2f;
import com.enderio.core.common.vecmath.Vector3d;
import com.enderio.core.common.vecmath.Vector3f;
import com.enderio.core.common.vecmath.Vector4d;
import com.enderio.core.common.vecmath.Vector4f;
import com.enderio.core.common.vecmath.Vertex;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.fluids.FluidStack;
import static org.lwjgl.opengl.GL11.GL_SMOOTH;
import static org.lwjgl.opengl.GL11.glDepthMask;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glRotatef;

public class RenderUtil {

  public static final @Nonnull Vector4f DEFAULT_TEXT_SHADOW_COL = new Vector4f(0.33f, 0.33f, 0.33f, 0.33f);

  public static final @Nonnull Vector4f DEFAULT_TXT_COL = new Vector4f(1, 1, 1, 1);

  public static final @Nonnull Vector4f DEFAULT_TEXT_BG_COL = new Vector4f(0.275f, 0.08f, 0.4f, 0.75f);

  public static final @Nonnull Vector3d UP_V = new Vector3d(0, 1, 0);

  public static final @Nonnull Vector3d ZERO_V = new Vector3d(0, 0, 0);

  //private static final @Nonnull FloatBuffer MATRIX_BUFFER = GLAllocation.createDirectFloatBuffer(16);

  public static final @Nonnull ResourceLocation BLOCK_TEX = InventoryMenu.BLOCK_ATLAS;

  public static final @Nonnull ResourceLocation GLINT_TEX = new ResourceLocation("textures/misc/enchanted_item_glint.png");

  public static int BRIGHTNESS_MAX = 15 << 20 | 15 << 4;

//  public static void loadMatrix(@Nonnull Matrix4d mat) {
//    PoseStack stack;
//
//    MATRIX_BUFFER.rewind();
//    MATRIX_BUFFER.put((float) mat.m00);
//    MATRIX_BUFFER.put((float) mat.m01);
//    MATRIX_BUFFER.put((float) mat.m02);
//    MATRIX_BUFFER.put((float) mat.m03);
//    MATRIX_BUFFER.put((float) mat.m10);
//    MATRIX_BUFFER.put((float) mat.m11);
//    MATRIX_BUFFER.put((float) mat.m12);
//    MATRIX_BUFFER.put((float) mat.m13);
//    MATRIX_BUFFER.put((float) mat.m20);
//    MATRIX_BUFFER.put((float) mat.m21);
//    MATRIX_BUFFER.put((float) mat.m22);
//    MATRIX_BUFFER.put((float) mat.m23);
//    MATRIX_BUFFER.put((float) mat.m30);
//    MATRIX_BUFFER.put((float) mat.m31);
//    MATRIX_BUFFER.put((float) mat.m32);
//    MATRIX_BUFFER.put((float) mat.m33);
//    MATRIX_BUFFER.rewind();
//    GL11.glLoadMatrix(MATRIX_BUFFER);
//  }

  public static @Nonnull TextureManager engine() {
    return Minecraft.getInstance().textureManager;
  }

  public static void bindBlockTexture() {
    bindTexture(BLOCK_TEX);
  }

  public static void bindGlintTexture() {
    bindTexture(GLINT_TEX);
  }

  public static void bindTexture(@Nonnull String string) {
    bindTexture(new ResourceLocation(string));
  }

  public static void bindTexture(@Nonnull ResourceLocation tex) {
    RenderSystem.bindTexture(engine().getTexture(tex).getId());
  }

  public static @Nonnull Font font() {
    return Minecraft.getInstance().font;
  }

  public static @Nonnull float[] getDefaultPerSideBrightness() {
    float[] brightnessPerSide = new float[6];
    for (Direction dir : Direction.values()) {
      brightnessPerSide[dir.ordinal()] = RenderUtil.getColorMultiplierForFace(dir);
    }
    return brightnessPerSide;
  }

  public static float calculateTotalBrightnessForLocation(@Nonnull Level worldObj, @Nonnull BlockPos pos) {
    return worldObj.getLightEngine().getRawBrightness(pos, 0);

    // TODO: why was there custom calculations here?
//    int i = worldObj.getLightFromNeighborsFor(EnumSkyBlock.SKY, pos);
//    int j = i % 65536;
//    int k = i / 65536;
//
//    // 0.2 - 1
//    float sunBrightness = worldObj.getSunBrightness(1);
//    float percentRecievedFromSun = k / 255f;
//
//    // Highest value received from a light
//    float fromLights = j / 255f;
//
//    // 0 - 1 for sun only, 0 - 0.6 for light only
//    // float recievedPercent = worldObj.getLightBrightness(new BlockPos(xCoord,
//    // yCoord, zCoord));
//    float highestValue = Math.max(fromLights, percentRecievedFromSun * sunBrightness);
//    return Math.max(0.2f, highestValue);
  }

  public static float getColorMultiplierForFace(@Nonnull Direction face) {
    if (face == Direction.UP) {
      return 1;
    }
    if (face == Direction.DOWN) {
      return 0.5f;
    }
    if (face.getStepX() != 0) {
      return 0.6f;
    }
    return 0.8f; // z
  }

  public static void renderQuad2D(double x, double y, double z, double width, double height, int colorRGB) {

    RenderSystem.disableTexture();

    Vector3f col = ColorUtil.toFloat(colorRGB);
    RenderSystem.setShaderColor(col.x, col.y, col.z, 1);

    Tesselator tessellator = Tesselator.getInstance();
    BufferBuilder tes = tessellator.getBuilder();
    tes.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
    tes.vertex(x, y + height, z).endVertex();
    tes.vertex(x + width, y + height, z).endVertex();
    tes.vertex(x + width, y, z).endVertex();
    tes.vertex(x, y, z).endVertex();

    tessellator.end();
    RenderSystem.enableTexture();
  }

  public static void renderQuad2D(double x, double y, double z, double width, double height, @Nonnull Vector4f colorRGBA) {
    RenderSystem.setShaderColor(colorRGBA.x, colorRGBA.y, colorRGBA.z, colorRGBA.w);
    RenderSystem.disableTexture();

    Tesselator tessellator = Tesselator.getInstance();
    BufferBuilder tes = tessellator.getBuilder();
    tes.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
    tes.vertex(x, y + height, z).endVertex();
    tes.vertex(x + width, y + height, z).endVertex();
    tes.vertex(x + width, y, z).endVertex();
    tes.vertex(x, y, z).endVertex();
    tessellator.end();
    GlStateManager._enableTexture();
  }

  public static Matrix4d createBillboardMatrix(@Nonnull BlockEntity te, @Nonnull LivingEntity entityPlayer) {
    BlockPos p = te.getBlockPos();
    return createBillboardMatrix(new Vector3d(p.getX() + 0.5, p.getY() + 0.5, p.getZ() + 0.5), entityPlayer);
  }

  public static Matrix4d createBillboardMatrix(@Nonnull Vector3d lookAt, @Nonnull LivingEntity entityPlayer) {
    Vector3d playerEye = new Vector3d(entityPlayer.getX(), entityPlayer.getY() + 1.62 - entityPlayer.getEyeY(), entityPlayer.getZ());
    Vector3d blockOrigin = new Vector3d(lookAt.x, lookAt.y, lookAt.z);
    Matrix4d lookMat = VecmathUtil.createMatrixAsLookAt(blockOrigin, playerEye, RenderUtil.UP_V);
    lookMat.setTranslation(new Vector3d());
    lookMat.invert();
    return lookMat;
  }

  public static void renderBillboard(@Nonnull Matrix4d lookMat, float minU, float maxU, float minV, float maxV, double size, int brightness) {
    Tesselator tessellator = Tesselator.getInstance();
    BufferBuilder tes = tessellator.getBuilder();
    tes.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

    double s = size / 2;
    Vector3d v = new Vector3d();
    v.set(-s, s, 0);
    lookMat.transform(v);
    tes.vertex(v.x, v.y, v.z).uv(minU, maxV).endVertex();
    v.set(s, s, 0);
    lookMat.transform(v);
    tes.vertex(v.x, v.y, v.z).uv(maxU, maxV).endVertex();
    v.set(s, -s, 0);
    lookMat.transform(v);
    tes.vertex(v.x, v.y, v.z).uv(maxU, minV).endVertex();
    v.set(-s, -s, 0);
    lookMat.transform(v);
    tes.vertex(v.x, v.y, v.z).uv(minU, minV).endVertex();

    tessellator.end();
  }

  /**
   * @return The edge directions for a face, in the order left, bottom, right, top.
   */
  public static List<Direction> getEdgesForFace(@Nonnull Direction face) {
    List<Direction> result = new ArrayList<Direction>(4);
    if (face.getStepY() != 0) {
      result.add(Direction.NORTH);
      result.add(Direction.EAST);
      result.add(Direction.SOUTH);
      result.add(Direction.WEST);
    } else if (face.getStepX() != 0) {
      result.add(Direction.DOWN);
      result.add(Direction.SOUTH);
      result.add(Direction.UP);
      result.add(Direction.NORTH);
    } else {
      result.add(Direction.DOWN);
      result.add(Direction.WEST);
      result.add(Direction.UP);
      result.add(Direction.EAST);
    }
    return result;
  }

  public static void addVerticesToTessellator(@Nullable List<Vertex> vertices, @Nonnull VertexFormat format, boolean doBegin) {
    addVerticesToTessellator(vertices, null, format, doBegin);
  }

  public static void addVerticesToTessellator(@Nullable List<Vertex> vertices, VertexTranslation xForm, @Nonnull VertexFormat format, boolean doBegin) {
    if (vertices == null || vertices.isEmpty()) {
      return;
    }

    List<Vertex> newV;
    if (xForm != null) {
      newV = new ArrayList<Vertex>(vertices.size());
      for (Vertex v : vertices) {
        Vertex xv = new Vertex(v);
        xForm.apply(xv);
        newV.add(xv);
      }
    } else {
      newV = vertices;
    }

    Tesselator tessellator = Tesselator.getInstance();
    BufferBuilder tes = tessellator.getBuilder();
    if (doBegin) {
      tes.begin(VertexFormat.Mode.QUADS, format);
    }

    for (Vertex v : vertices) {
      for (VertexFormatElement el : format.getElements()) {
        switch (el.getUsage()) {
        case COLOR:
          if (el.getType() == VertexFormatElement.Type.FLOAT) {
            tes.color(v.r(), v.g(), v.b(), v.a());
          }
          break;
        case NORMAL:
          tes.normal(v.nx(), v.ny(), v.nz());
          break;
        case POSITION:
          tes.vertex(v.x(), v.y(), v.z());
          break;
        case UV:
          if (el.getType() == VertexFormatElement.Type.FLOAT && v.uv != null) {
            tes.uv(v.u(), v.v());
          }
          break;
        case GENERIC:
          break;
        case PADDING:
          break;
        default:
          break;

        }
      }
      tes.endVertex();
    }
  }

  public static void getUvForCorner(@Nonnull Vector2f uv, @Nonnull Vector3d corner, int x, int y, int z, @Nonnull Direction face,
      @Nonnull TextureAtlasSprite icon) {
    Vector3d p = new Vector3d(corner);
    p.x -= x;
    p.y -= y;
    p.z -= z;

    float uWidth = 1;
    float vWidth = 1;
    uWidth = icon.getU1() - icon.getU0();
    vWidth = icon.getV1() - icon.getV0();

    uv.x = (float) VecmathUtil.distanceFromPointToPlane(getUPlaneForFace(face), p);
    uv.y = (float) VecmathUtil.distanceFromPointToPlane(getVPlaneForFace(face), p);

    uv.x = icon.getU0() + (uv.x * uWidth);
    uv.y = icon.getV0() + (uv.y * vWidth);
  }

  public static @Nonnull Vector4d getVPlaneForFace(@Nonnull Direction face) {
    switch (face) {
    case DOWN:
    case UP:
      return new Vector4d(0, 0, 1, 0);
    default:
      return new Vector4d(0, -1, 0, 1);
    }
  }

  public static @Nonnull Vector4d getUPlaneForFace(@Nonnull Direction face) {
    switch (face) {
    case EAST:
      return new Vector4d(0, 0, -1, 1);
    case WEST:
      return new Vector4d(0, 0, 1, 0);
    case NORTH:
      return new Vector4d(-1, 0, 0, 1);
    case SOUTH:
      return new Vector4d(1, 0, 0, 0);
    default:
      return new Vector4d(1, 0, 0, 0);
    }
  }

  public static @Nonnull Direction getVDirForFace(@Nonnull Direction face) {
    switch (face) {
    case DOWN:
    case UP:
      return Direction.SOUTH;
    default:
      return Direction.UP;
    }
  }

  public static @Nonnull Direction getUDirForFace(@Nonnull Direction face) {
    switch (face) {
    case EAST:
      return Direction.NORTH;
    case WEST:
      return Direction.SOUTH;
    case NORTH:
      return Direction.WEST;
    case SOUTH:
      return Direction.EAST;
    default:
      return Direction.EAST;
    }
  }

  public static @Nonnull TextureAtlasSprite getStillTexture(@Nonnull FluidStack fluidstack) {
    final Fluid fluid = fluidstack.getFluid();
    if (fluid == null) {
      return getMissingSprite();
    }
    return getStillTexture(fluid);
  }

  public static @Nonnull TextureAtlasSprite getStillTexture(@Nonnull Fluid fluid) {
    ResourceLocation iconKey = IClientFluidTypeExtensions.of(fluid).getStillTexture();
    final TextureAtlasSprite textureEntry = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(iconKey);
    return textureEntry != null ? textureEntry : getMissingSprite();
  }

  public static @Nonnull TextureAtlasSprite getMissingSprite() {
    return Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(TextureManager.INTENTIONAL_MISSING_TEXTURE);
  }

  public static void renderGuiTank(@Nonnull FluidTank tank, double x, double y, double zLevel, double width, double height) {
    renderGuiTank(tank.getFluid(), tank.getCapacity(), tank.getFluidAmount(), x, y, zLevel, width, height);
  }

  public static void renderGuiTank(@Nullable FluidStack fluid, int capacity, int amount, double x, double y, double zLevel, double width, double height) {
    if (fluid == null || fluid.getFluid() == null || amount <= 0) {
      return;
    }

    TextureAtlasSprite icon = getStillTexture(fluid);

    int renderAmount = (int) Math.max(Math.min(height, amount * height / capacity), 1);
    int posY = (int) (y + height - renderAmount);

    RenderUtil.bindBlockTexture();
    int color = IClientFluidTypeExtensions.of(fluid.getFluid()).getTintColor(fluid);
    RenderSystem.setShaderColor((color >> 16 & 0xFF) / 255f, (color >> 8 & 0xFF) / 255f, (color & 0xFF) / 255f, 1);

    RenderSystem.enableBlend();
    for (int i = 0; i < width; i += 16) {
      for (int j = 0; j < renderAmount; j += 16) {
        int drawWidth = (int) Math.min(width - i, 16);
        int drawHeight = Math.min(renderAmount - j, 16);

        int drawX = (int) (x + i);
        int drawY = posY + j;

        float minU = icon.getU0();
        float maxU = icon.getU1();
        float minV = icon.getV0();
        float maxV = icon.getV1();

        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder tes = tessellator.getBuilder();
        tes.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        tes.vertex(drawX, drawY + drawHeight, 0).uv(minU, minV + (maxV - minV) * drawHeight / 16F).endVertex();
        tes.vertex(drawX + drawWidth, drawY + drawHeight, 0).uv(minU + (maxU - minU) * drawWidth / 16F, minV + (maxV - minV) * drawHeight / 16F).endVertex();
        tes.vertex(drawX + drawWidth, drawY, 0).uv(minU + (maxU - minU) * drawWidth / 16F, minV).endVertex();
        tes.vertex(drawX, drawY, 0).uv(minU, minV).endVertex();
        tessellator.end();
      }
    }
    RenderSystem.disableBlend();
    RenderSystem.setShaderColor(1, 1, 1, 1);
  }

  public static void drawBillboardedText(PoseStack poseStack, @Nonnull Vector3f pos, @Nonnull String text, float size) {
    drawBillboardedText(poseStack, pos, text, size, DEFAULT_TXT_COL, true, DEFAULT_TEXT_SHADOW_COL, true, DEFAULT_TEXT_BG_COL);
  }

  public static void drawBillboardedText(PoseStack poseStack, @Nonnull Vector3f pos, @Nonnull String text, float size, @Nonnull Vector4f bgCol) {
    drawBillboardedText(poseStack, pos, text, size, DEFAULT_TXT_COL, true, DEFAULT_TEXT_SHADOW_COL, true, bgCol);
  }

  public static void drawBillboardedText(PoseStack poseStack, @Nonnull Vector3f pos, @Nonnull String text, float size, @Nonnull Vector4f txtCol, boolean drawShadow,
      @Nullable Vector4f shadowCol, boolean drawBackground, @Nullable Vector4f bgCol) {

    Minecraft mc = Minecraft.getInstance();
    Font fnt = mc.font;

    poseStack.pushPose();
    poseStack.translate(pos.x, pos.y, pos.z);
    poseStack.mulPose(com.mojang.math.Vector3f.XP.rotationDegrees(180));
    float scale = size / fnt.lineHeight;
    poseStack.scale(scale, scale, scale);
    poseStack.mulPose(com.mojang.math.Vector3f.YP.rotationDegrees(mc.player.getYRot() + 180));
    poseStack.mulPose(com.mojang.math.Vector3f.XP.rotationDegrees(-mc.player.getXRot()));
    poseStack.translate(-fnt.width(text) / 2, 0, 0);
    RenderSystem.applyModelViewMatrix();

    if (drawBackground && bgCol != null) {
      renderBackground(fnt, text, bgCol);
    }

    fnt.draw(poseStack, text, 0, 0, ColorUtil.getRGBA(txtCol));

    if (drawShadow && shadowCol != null) {
      poseStack.translate(0.5f, 0.5f, 0.1f);
      fnt.draw(poseStack, text, 0, 0, ColorUtil.getRGBA(shadowCol));
    }

    poseStack.popPose();
    RenderSystem.applyModelViewMatrix();

    RenderUtil.bindBlockTexture();
  }

  public static void renderBackground(@Nonnull Font fnt, @Nonnull String toRender, @Nonnull Vector4f color) {

    RenderSystem.enableBlend(); // blend comes in as on or off depending on the player's view vector

    RenderSystem.disableTexture();
    RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
    RenderSystem.disableCull();
    RenderSystem.depthMask(false);

    float width = fnt.width(toRender);
    float height = fnt.lineHeight;
    float padding = 2f;

    RenderSystem.setShaderColor(color.x, color.y, color.z, color.w);

    BufferBuilder tes = Tesselator.getInstance().getBuilder();
    tes.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
    tes.vertex(-padding, -padding, 0).endVertex();
    tes.vertex(-padding, height + padding, 0).endVertex();
    tes.vertex(width + padding, height + padding, 0).endVertex();
    tes.vertex(width + padding, -padding, 0).endVertex();
    tes.end();

    RenderSystem.enableTexture();
    RenderSystem.enableCull();
  }

  /**
   * Renders an item entity in 3D
   *
   * @param item
   *          The item to render
   * @param rotate
   *          Whether to "spin" the item like it would if it were a real dropped entity
   */
  public static void render3DItem(PoseStack poseStack, @Nonnull ItemEntity item, boolean rotate) {
    float rot = getRotation(1.0f);

    glPushMatrix();
    glDepthMask(true);

    if (rotate && Minecraft.useFancyGraphics()) {
      glRotatef(rot, 0, 1, 0);
    }

    // TODO
//    Minecraft.getInstance().levelRenderer.renderEntity(
//            item, 0.0D, 0.0D, 0.0D, 0.0F, poseStack, false
//    );

    glPopMatrix();
  }

  public static float getRotation(float mult) {
    return ClientHandler.getTicksElapsed() * mult;
  }

  public static void renderBillboardQuad(PoseStack poseStack, float rot, double scale) {

    poseStack.pushPose();
    rotateToPlayer(poseStack);
    poseStack.pushPose();

    poseStack.mulPose(com.mojang.math.Vector3f.ZP.rotation(rot));

    RenderSystem.setShaderColor(1, 1, 1, 1);

    Tesselator tessellator = Tesselator.getInstance();
    BufferBuilder tes = tessellator.getBuilder();
    tes.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
    tes.vertex(-scale, -scale, 0).uv(0, 0).endVertex();
    tes.vertex(-scale, scale, 0).uv(0, 1).endVertex();
    tes.vertex(scale, scale, 0).uv(1, 1).endVertex();
    tes.vertex(scale, -scale, 0).uv(1, 0).endVertex();
    tessellator.end();
    glPopMatrix();
    glPopMatrix();
  }

  public static void rotateToPlayer(PoseStack poseStack) {
    poseStack.mulPose(com.mojang.math.Vector3f.YP.rotationDegrees(-Minecraft.getInstance().player.getYRot()));
    poseStack.mulPose(com.mojang.math.Vector3f.XP.rotationDegrees(Minecraft.getInstance().player.getXRot()));
  }

  public static @Nonnull TextureAtlasSprite getTexture(@Nonnull BlockState state, Level level, BlockPos pos) {
    return Minecraft.getInstance().getBlockRenderer().getBlockModelShaper().getTexture(state, level, pos);
  }

  public static void renderBoundingBox(@Nonnull final BoundingBox bb) {
    final BufferBuilder tes = Tesselator.getInstance().getBuilder();
    tes.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
    NNList.FACING.apply(new Callback<Direction>() {
      @Override
      public void apply(@Nonnull Direction e) {
        for (Vector3f v : bb.getCornersForFace(e)) {
          tes.vertex(v.x, v.y, v.z).endVertex();
        }
      }
    });
    tes.end();
  }

  public static void renderBoundingBox(@Nonnull BoundingBox bb, @Nonnull BlockState state, Level level, BlockPos pos) {
    renderBoundingBox(bb, getTexture(state, level, pos));
  }

  public static void renderBoundingBox(@Nonnull BoundingBox bb, @Nonnull TextureAtlasSprite tex) {
    renderBoundingBox(bb, tex.getU0(), tex.getU1(), tex.getV0(), tex.getV1());
  }

  public static void renderBoundingBox(@Nonnull final BoundingBox bb, final float minU, final float maxU, final float minV, final float maxV) {

    final BufferBuilder tes = Tesselator.getInstance().getBuilder();
    tes.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
    NNList.FACING.apply(new Callback<Direction>() {
      @Override
      public void apply(@Nonnull Direction e) {
        for (Vertex v : bb.getCornersWithUvForFace(e, minU, maxU, minV, maxV)) {
          tes.vertex(v.x(), v.y(), v.z()).uv(v.u(), v.v()).endVertex();
        }
      }
    });
    tes.end();
  }

  public static void registerReloadListener(@Nonnull PreparableReloadListener obj) {
    ((ReloadableResourceManager) Minecraft.getInstance().getResourceManager()).registerReloadListener(obj);
  }

  public static void setupLightmapCoords(@Nonnull BlockPos pos, @Nonnull Level world) {
    float f = world.getLightEmission(pos);
    int l = RenderUtil.getLightBrightnessForSkyBlocks(world, pos, 0);
    int l1 = l % 65536;
    int l2 = l / 65536;
    RenderSystem.setShaderColor(f, f, f, 1);

    // TODO
    //OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, l1, l2);
  }

  public static int getLightBrightnessForSkyBlocks(@Nonnull Level world, @Nonnull BlockPos pos, int min) {
    int blockBrightness = world.getLightEngine().getRawBrightness(pos, 100);
    int skyBrightness = world.getLightEngine().getRawBrightness(pos, -100) - 100;
    if (blockBrightness < min) {
      blockBrightness = min;
    }
    return skyBrightness << 20 | blockBrightness << 4;
  }

//  public static void renderBlockModel(@Nonnull final Level world, @Nonnull final BlockPos pos, boolean translateToOrigin) {
//
//    final BlockRenderLayer oldRenderLayer = MinecraftForgeClient.getRenderLayer();
//    final BlockState state = world.getBlockState(pos);
//    final BlockRenderDispatcher blockrendererdispatcher = Minecraft.getInstance().getBlockRenderer();
//    final BakedModel ibakedmodel = blockrendererdispatcher.getBlockModel(state);
//    final Tesselator tesselator = Tesselator.getInstance();
//    final BufferBuilder wr = tesselator.getBuilder();
//    wr.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.BLOCK);
//    if (translateToOrigin) {
//      wr.setTranslation(-pos.getX(), -pos.getY(), -pos.getZ());
//    }
//
//    NNList.RENDER_LAYER.apply(new Callback<BlockRenderLayer>() {
//      @Override
//      public void apply(@Nonnull BlockRenderLayer layer) {
//        ForgeHooksClient.setRenderLayer(layer);
//        // TODO: Need to setup GL state correctly for each layer
//        blockrendererdispatcher.getModelRenderer().renderModel(world, ibakedmodel, state, pos, wr, false);
//      }
//    });
//
//    if (translateToOrigin) {
//      wr.setTranslation(0, 0, 0);
//    }
//
//    tesselator.end();
//    ForgeHooksClient.setRenderLayer(oldRenderLayer);
//  }

  public static void renderBlockModelAsItem(PoseStack poseStack, @Nonnull Level world, @Nonnull ItemStack stack, @Nonnull BlockState state) {
    BlockRenderDispatcher blockrendererdispatcher = Minecraft.getInstance().getBlockRenderer();
    BakedModel model = blockrendererdispatcher.getBlockModelShaper().getBlockModel(state);
    Minecraft.getInstance().getItemRenderer().renderGuiItem(
            stack,
            0,
            0,
            model
    );
  }

  @Nonnull
  private static final Vector4f FULL_UVS = new Vector4f(0, 0, 1, 1);
//
//  public static void addBakedQuads(@Nonnull List<BakedQuad> quads, @Nonnull BoundingBox bb, @Nonnull TextureAtlasSprite tex) {
//    addBakedQuads(quads, bb, FULL_UVS, tex);
//  }
//
//  public static void addBakedQuads(@Nonnull List<BakedQuad> quads, @Nonnull BoundingBox bb, @Nonnull Vector4f uvs, @Nonnull TextureAtlasSprite tex) {
//    addBakedQuads(quads, bb, uvs, tex, null);
//  }
//
//  public static void addBakedQuads(@Nonnull final List<BakedQuad> quads, @Nonnull final BoundingBox bb, @Nonnull final TextureAtlasSprite tex,
//      final Vector4f color) {
//    addBakedQuads(quads, bb, FULL_UVS, tex, color);
//  }
//
//  public static void addBakedQuads(@Nonnull final List<BakedQuad> quads, @Nonnull final BoundingBox bb, @Nonnull Vector4f uvs, @Nonnull final TextureAtlasSprite tex,
//      final Vector4f color) {
//    NNList.FACING.apply(new Callback<Direction>() {
//      @Override
//      public void apply(@Nonnull Direction face) {
//        addBakedQuadForFace(quads, bb, tex, face, uvs, null, false, false, true, color);
//      }
//    });
//  }
//
//  public static void addBakedQuadForFace(@Nonnull List<BakedQuad> quads, @Nonnull BoundingBox bb, @Nonnull TextureAtlasSprite tex, @Nonnull Direction face) {
//    addBakedQuadForFace(quads, bb, tex, face, FULL_UVS);
//  }
//
//  public static void addBakedQuadForFace(@Nonnull List<BakedQuad> quads, @Nonnull BoundingBox bb, @Nonnull TextureAtlasSprite tex, @Nonnull Direction face,
//      @Nonnull Vector4f uvs) {
//    addBakedQuadForFace(quads, bb, tex, face, uvs, false, false);
//  }
//
//  public static void addBakedQuadForFace(@Nonnull List<BakedQuad> quads, @Nonnull BoundingBox bb, @Nonnull TextureAtlasSprite tex, @Nonnull Direction face,
//      boolean rotateUV, boolean flipU) {
//    addBakedQuadForFace(quads, bb, tex, face, FULL_UVS, rotateUV, flipU);
//  }
//
//  public static void addBakedQuadForFace(@Nonnull List<BakedQuad> quads, @Nonnull BoundingBox bb, @Nonnull TextureAtlasSprite tex, @Nonnull Direction face,
//      @Nonnull Vector4f uvs, boolean rotateUV, boolean flipU) {
//    addBakedQuadForFace(quads, bb, tex, face, null, rotateUV, flipU, true, null);
//  }
//
//  public static void addBakedQuadForFace(@Nonnull List<BakedQuad> quads, @Nonnull BoundingBox bb, @Nonnull TextureAtlasSprite tex, @Nonnull Direction face,
//      @Nullable VertexTransform xform, boolean rotateUV, boolean flipU, boolean recolor, @Nullable Vector4f color) {
//    addBakedQuadForFace(quads, bb, tex, face, FULL_UVS, null, rotateUV, flipU, recolor, color);
//  }
//
//  public static void addBakedQuadForFace(@Nonnull List<BakedQuad> quads, @Nonnull BoundingBox bb, @Nonnull TextureAtlasSprite tex, @Nonnull Direction face,
//      @Nonnull Vector4f uvs, @Nullable VertexTransform xform, boolean rotateUV, boolean flipU, boolean recolor, @Nullable Vector4f color) {
//    UnpackedBakedQuad.Builder builder = new UnpackedBakedQuad.Builder(DefaultVertexFormat.ITEM);
//    List<Vertex> corners = bb.getCornersWithUvForFace(face, uvs.x, uvs.z, uvs.y, uvs.w);
//    builder.setQuadOrientation(face);
//    builder.setTexture(tex);
//    if (rotateUV) {
//      Vector2f vec = corners.get(corners.size() - 1).uv;
//      for (int i = corners.size() - 2; i >= 0; i--) {
//        Vertex vert = corners.get(i);
//        Vector2f temp = vert.uv;
//        vert.uv = vec;
//        vec = temp;
//      }
//      corners.get(corners.size() - 1).uv = vec;
//    }
//    for (Vertex v : corners) {
//      if (v != null) {
//        if (xform != null) {
//          xform.apply(v);
//        }
//        if (recolor) {
//          v.color = color;
//        }
//        if (flipU) {
//          v.uv.x = uvs.z - v.uv.x;
//        }
//        putVertexData(builder, v, face.getDirectionVec(), tex);
//      }
//    }
//    quads.add(builder.build());
//  }
//
//  public static void addBakedQuads(@Nonnull List<BakedQuad> quads, @Nonnull Collection<Vertex> vertices, @Nonnull TextureAtlasSprite tex,
//      @Nullable Vector4f color) {
//    Iterator<Vertex> it = vertices.iterator();
//
//
//    while (it.hasNext()) {
//      Direction face = Direction.DOWN;
//      var baker = new QuadBakingVertexConsumer(q -> quads.add(q));
//
//      for (int i = 0; i < 4; i++) {
//        Vertex v = it.next();
//        if (i == 0) {
//          face = Direction.fromNormal((int)v.nx(), (int)v.ny(), (int)v.nz());
//          baker.setDirection(face);
//          baker.setSprite(tex);
//        }
//        v.color = color;
//        baker.vertex(v.x(), v.y(), v.z());
//        putVertexData(builder, v, face.getDirectionVec(), tex);
//      }
//      quads.add(builder.build());
//    }
//  }
//
//  private static void putVertexData(@Nonnull VertexConsumer builder, @Nonnull Vertex v, @Nonnull Vec3i normal, @Nonnull TextureAtlasSprite sprite) {
//    VertexFormat format = builder.getVertexFormat();
//    for (int e = 0; e < format.getElementCount(); e++) {
//      switch (format.getElement(e).getUsage()) {
//      case POSITION:
//        builder.put(e, (float) v.x(), (float) v.y(), (float) v.z(), 1);
//        break;
//      case COLOR:
//        float d;
//        if (v.normal != null) {
//          d = LightUtil.diffuseLight(v.normal.x, v.normal.y, v.normal.z);
//        } else {
//          d = LightUtil.diffuseLight(normal.getX(), normal.getY(), normal.getZ());
//        }
//
//        if (v.color != null) {
//          builder.put(e, d * v.color.x, d * v.color.y, d * v.color.z, v.color.w);
//        } else {
//          builder.put(e, d, d, d, 1);
//        }
//        break;
//      case UV:
//        builder.put(e, sprite.getInterpolatedU(v.u() * 16), sprite.getInterpolatedV(v.v() * 16), 0, 1);
//
//        break;
//      case NORMAL:
//        if (v.normal != null) {
//          builder.put(e, v.nx(), v.ny(), v.nz(), 0);
//        } else {
//          builder.put(e, normal.getX(), normal.getY(), normal.getZ(), 0);
//        }
//        break;
//      default:
//        builder.put(e);
//      }
//    }
//  }

}
