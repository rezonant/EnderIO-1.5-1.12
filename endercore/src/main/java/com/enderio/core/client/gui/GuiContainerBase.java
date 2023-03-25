package com.enderio.core.client.gui;

import java.io.IOException;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import com.enderio.core.api.client.gui.IGuiOverlay;
import com.enderio.core.api.client.gui.IGuiScreen;
import com.enderio.core.client.gui.ToolTipManager.ToolTipRenderer;
import com.enderio.core.client.gui.widget.GhostSlot;
import com.enderio.core.client.gui.widget.GuiToolTip;
import com.enderio.core.client.gui.widget.TextFieldEnder;
import com.enderio.core.client.gui.widget.VScrollbar;
import com.enderio.core.common.util.NNList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraftforge.client.gui.ScreenUtils;
import net.minecraftforge.common.MinecraftForge;

public abstract class GuiContainerBase<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> implements ToolTipRenderer, IGuiScreen {

  protected @Nonnull ToolTipManager ttMan = new ToolTipManager();
  protected @Nonnull NNList<IGuiOverlay> overlays = new NNList<IGuiOverlay>();
  protected @Nonnull NNList<TextFieldEnder> textFields = new NNList<TextFieldEnder>();
  protected @Nonnull NNList<VScrollbar> scrollbars = new NNList<VScrollbar>();
  protected @Nonnull NNList<IDrawingElement> drawingElements = new NNList<IDrawingElement>();
  protected @Nonnull GhostSlotHandler ghostSlotHandler = new GhostSlotHandler();

  protected @Nullable VScrollbar draggingScrollbar;

  public GuiContainerBase(T pMenu, Inventory pPlayerInventory, Component pTitle) {
    super(pMenu, pPlayerInventory, pTitle);
  }

  @Override
  public void init() {
    super.init();
    fixupGuiPosition();
    for (IGuiOverlay overlay : overlays) {
      overlay.init(this);
    }
    for (TextFieldEnder f : textFields) {
      f.init(this);
    }
  }

  protected void fixupGuiPosition() {
  }

  protected final void setText(@Nonnull TextFieldEnder tf, @Nonnull String newText) {
    String old = tf.getValue();
    tf.setValue(newText);
    onTextFieldChanged(tf, old);
  }

  protected void onTextFieldChanged(@Nonnull TextFieldEnder tf, @Nonnull String old) {

  }

  public boolean hideOverlays() {
    for (IGuiOverlay overlay : overlays) {
      if (overlay.isVisible()) {
        overlay.setIsVisible(false);
        return true;
      }
    }
    return false;
  }

  @Override
  public void addToolTip(@Nonnull GuiToolTip toolTip) {
    ttMan.addToolTip(toolTip);
  }

  // TODO
//  @Override
//  public void handleMouseInput() throws IOException {
//    int x = (int)(minecraft.mouseHandler.xpos() * this.width / this.minecraft.getWindow().getWidth());
//    int y = (int)(this.height - minecraft.mouseHandler.ypos() * this.height / this.minecraft.getWindow().getHeight() - 1);
//    int b = minecraft.mouseHandler.isLeftPressed() ? 0 : minecraft.mouseHandler.isMiddlePressed() ? 1 : minecraft.mouseHandler.isRightPressed() ? 2 : -1;
//    for (IGuiOverlay overlay : overlays) {
//      if (overlay != null && overlay.isVisible() && overlay.handleMouseInput(x, y, b)) {
//        return;
//      }
//    }
//  }

  @Override
  public boolean isMouseOver(double mouseX, double mouseY) {
    for (IGuiOverlay overlay : overlays) {
      if (overlay != null && overlay.isVisible() && overlay.isMouseInBounds(mouseX, mouseY)) {
        return false;
      }
    }

    return super.isMouseOver(mouseX, mouseY);
  }

  @Override
  public @Nonnull GhostSlotHandler getGhostSlotHandler() {
    return ghostSlotHandler;
  }

  @Override
  public boolean mouseClicked(double x, double y, int button) {
    // TODO: not present in 1.16 port
//    for (Button guibutton : buttonList) {
//      if (guibutton instanceof IPriorityButton && ((IPriorityButton) guibutton).isTopmost() && doHandleButtonClick(x, y, button, guibutton)) {
//        return;
//      }
//    }

    for (EditBox f : textFields) {
      f.mouseClicked(x, y, button);
    }
    if (!scrollbars.isEmpty()) {
      if (draggingScrollbar != null) {
        draggingScrollbar.mouseClicked(x, y, button);
        return true;
      }
      for (VScrollbar vs : scrollbars) {
        if (vs.mouseClicked(x, y, button)) {
          draggingScrollbar = vs;
          return true;
        }
      }
    }
    if (!getGhostSlotHandler().getGhostSlots().isEmpty()) {
      GhostSlot slot = getGhostSlotHandler().getGhostSlotAt(this, x, y);
      if (slot != null) {
        getGhostSlotHandler().ghostSlotClicked(this, slot, x, y, button);
        return true;
      }
    }

    // Right click field clearing
    if (button == 1) {
      for (TextFieldEnder tf : textFields) {
        if (tf.contains(x, y)) {
          setText(tf, "");
        }
      }
    }

    return false;
  }

  @Override
  public boolean mouseReleased(double x, double y, int button) {
    if (draggingScrollbar != null) {
      draggingScrollbar.mouseMovedOrUp(x, y, button);
      draggingScrollbar = null;
    }
    return super.mouseReleased(x, y, button);
  }

  @Override
  public boolean mouseDragged(double x, double y, int button, double dragX, double dragY) {
    if (draggingScrollbar != null) {
      draggingScrollbar.mouseClickMove(x, y, button, dragX, dragY);
      return false;
    }
    return super.mouseDragged(x, y, button, dragX, dragY);
  }

  public boolean mouseScrolled(double x, double y, double delta) {
    if (!scrollbars.isEmpty()) {
      for (VScrollbar vs : scrollbars) {
        vs.mouseScrolled(x, y, delta);
      }
    }

    if (!getGhostSlotHandler().getGhostSlots().isEmpty()) {
      GhostSlot slot = getGhostSlotHandler().getGhostSlotAt(this, x, y);
      if (slot != null) {
        getGhostSlotHandler().ghostSlotClicked(this, slot, x, y, delta < 0 ? -1 : -2);
      }
    }

    return super.mouseScrolled(x, y, delta);
  }

  public void addOverlay(@Nonnull IGuiOverlay overlay) {
    overlays.add(overlay);
  }

  public void removeOverlay(@Nonnull IGuiOverlay overlay) {
    overlays.remove(overlay);
  }

  public void addScrollbar(@Nonnull VScrollbar vs) {
    scrollbars.add(vs);
    vs.adjustPosition();
  }

  public void removeScrollbar(@Nonnull VScrollbar vs) {
    scrollbars.remove(vs);
    if (draggingScrollbar == vs) {
      draggingScrollbar = null;
    }
  }

  public void addDrawingElement(@Nonnull IDrawingElement element) {
    drawingElements.add(element);
    GuiToolTip tooltip = element.getTooltip();
    if (tooltip != null) {
      addToolTip(tooltip);
    }
  }

  public void removeDrawingElement(@Nonnull IDrawingElement element) {
    drawingElements.remove(element);
    GuiToolTip tooltip = element.getTooltip();
    if (tooltip != null) {
      removeToolTip(tooltip);
    }
  }

  private int realMx, realMy;

  @Override
  public final void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
    if (!scrollbars.isEmpty()) {
      for (VScrollbar vs : scrollbars) {
        vs.drawScrollbar(pMouseX, pMouseY);
      }
    }

    if (!ghostSlotHandler.getGhostSlots().isEmpty()) {
      getGhostSlotHandler().drawGhostSlots(pPoseStack, this, pMouseX, pMouseY);
    }

    super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);

    pPoseStack.pushPose();
    RenderSystem.applyModelViewMatrix();
    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    RenderSystem.disableDepthTest();

    setBlitOffset(300);
    itemRenderer.blitOffset = 300.0F;
    for (IGuiOverlay overlay : overlays) {
      if (overlay != null && overlay.isVisible()) {
        overlay.draw(realMx, realMy, minecraft.getPartialTick());
      }
    }

    setBlitOffset(0);
    itemRenderer.blitOffset = 0F;
    RenderSystem.enableDepthTest();
    pPoseStack.popPose();
    RenderSystem.applyModelViewMatrix();

    // try to only draw one tooltip...
    int mx = realMx = pMouseX;
    int my = realMy = pMouseY;
    if (draggingScrollbar == null) {
      if (!renderHoveredToolTip2(pPoseStack, mx, my)) {
        if (!ghostSlotHandler.drawGhostSlotToolTip(this, pMouseX, pMouseY)) {
          ttMan.drawTooltips(this, pMouseX, pMouseY);
        }
      }
    }
  }

  @Override
  public void renderBackground(PoseStack pPoseStack, int pVOffset) {
    for (IDrawingElement drawingElement : drawingElements) {
      drawingElement.renderBackground(pPoseStack, pVOffset);
    }

    super.renderBackground(pPoseStack, pVOffset);
  }

  protected boolean renderHoveredToolTip2(PoseStack poseStack, int p_191948_1_, int p_191948_2_) {

    if (minecraft.player.getMainHandItem().isEmpty()) {

      final Slot slotUnderMouse = getSlotUnderMouse();
      if (slotUnderMouse != null && slotUnderMouse.hasItem()) {
        renderTooltip(poseStack, slotUnderMouse.getItem(), p_191948_1_, p_191948_2_);
        return true;
      }
    }
    return false;
  }

  // copied from super with hate
  protected void drawItemStack(PoseStack poseStack, @Nonnull ItemStack stack, int mouseX, int mouseY, String str) {
    if (stack.isEmpty()) {
      return;
    }

    poseStack.translate(0.0F, 0.0F, 32.0F);
    setBlitOffset(200);
    itemRenderer.blitOffset = 200.0F;
    Font font = minecraft.font;
    itemRenderer.renderGuiItem(stack, mouseX, mouseY);
    itemRenderer.renderGuiItemDecorations(font, stack, mouseX, mouseY, str);
    setBlitOffset(0);
    itemRenderer.blitOffset = 0.0F;
  }

  protected void drawFakeItemsStart() {
    setBlitOffset(100);
    itemRenderer.blitOffset = 100.0F;
    RenderSystem.enableDepthTest();
  }

  public void drawFakeItemStack(int x, int y, @Nonnull ItemStack stack) {
    itemRenderer.renderAndDecorateItem(stack, x, y);

    // TODO
    // GlStateManager.enableAlpha();
  }

  public void drawFakeItemStackStdOverlay(int x, int y, @Nonnull ItemStack stack) {
    itemRenderer.renderGuiItemDecorations(font, stack, x, y, null);
  }

  protected void drawFakeItemHover(PoseStack poseStack, int x, int y) {

    // TODO
    // GlStateManager.disableLighting();

    RenderSystem.disableDepthTest();
    RenderSystem.colorMask(true, true, true, false);
    ScreenUtils.drawGradientRect(poseStack.last().pose(), 0, x, y, x + 16, y + 16, 0x80FFFFFF, 0x80FFFFFF);
    RenderSystem.colorMask(true, true, true, true);
    RenderSystem.enableDepthTest();

    // TODO
    // GlStateManager.enableLighting();
  }

  protected void drawFakeItemsEnd() {
    itemRenderer.blitOffset = 0.0F;
    setBlitOffset(0);
  }

  /**
   * Return the current texture to allow GhostSlots to gray out by over-painting the slot background.
   */
  protected abstract @Nonnull ResourceLocation getGuiTexture();

  @Override
  public boolean removeToolTip(@Nonnull GuiToolTip toolTip) {
    return ttMan.removeToolTip(toolTip);
  }

  public void setGuiLeft(int i) {
    leftPos = i;
  }

  public void setGuiTop(int i) {
    topPos = i;
  }

  public void setXSize(int i) {
    width = i;
  }

  public void setYSize(int i) {
    height = i;
  }

  @Override
  public @Nonnull Font getFontRenderer() {
    return Minecraft.getInstance().font;
  }

  @Override
  public int getOverlayOffsetXLeft() {
    return 0;
  }

  @Override
  public int getOverlayOffsetXRight() {
    return 0;
  }

  @Override
  public void clearToolTips() {
  }

  @Override
  public void onClose() {
    super.onClose();
    for (IGuiOverlay overlay : overlays) {
      overlay.guiClosed();
    }
  }

  @Override
  public final int getGuiRootLeft() {
    return getGuiLeft();
  }

  @Override
  public final int getGuiRootTop() {
    return getGuiTop();
  }

  @Override
  public final int getGuiXSize() {
    return getXSize();
  }

  @Override
  public final int getGuiYSize() {
    return getYSize();
  }
}
