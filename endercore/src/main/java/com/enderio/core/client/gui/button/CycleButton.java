package com.enderio.core.client.gui.button;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.api.client.gui.IGuiOverlay;
import com.enderio.core.api.client.gui.IGuiScreen;
import com.enderio.core.api.client.render.IWidgetIcon;
import com.enderio.core.client.InputUtil;
import com.enderio.core.client.gui.GuiContainerBase;
import com.enderio.core.client.gui.button.CycleButton.ICycleEnum;
import com.enderio.core.client.render.ColorUtil;
import com.enderio.core.client.render.EnderWidget;
import com.enderio.core.client.render.RenderUtil;
import com.enderio.core.common.util.NNList;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import org.apache.commons.lang3.tuple.Pair;

/**
 * A button which automatically parses enum constants and cycles between them when clicked.
 *
 * @param <T>
 *          The enum type for this button.
 */
public class CycleButton<T extends Enum<T> & ICycleEnum> extends IconButton {

  public interface ICycleEnum {

    /**
     * @return The icon to display when the button has selected this mode.
     */
    @Nonnull
    IWidgetIcon getIcon();

    /**
     * @return Localized tooltip lines.
     */
    @Nonnull
    List<String> getTooltipLines();
  }

  private final @Nonnull NNList<T> modes;

  private @Nullable T mode;

  private boolean isOpened = true;
  private PickerOverlay overlay;

  public CycleButton(@Nonnull IGuiScreen gui, int x, int y, @Nonnull Class<T> enumClass) {
    super(gui, x, y, (button) -> {}, (IWidgetIcon)null);
    this.modes = NNList.of(enumClass);
    overlay = new PickerOverlay(this);
    ((GuiContainerBase)gui).addOverlay(overlay);
  }

  @Override
  public void onGuiInit() {
    super.onGuiInit();
    if (this.mode == null) {
      this.setMode((T) this.modes.get(0));
      this.setIcon(this.getMode().getIcon());
    }
  }

  @Override
  public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
    var result = super.mouseClicked(pMouseX, pMouseY, pButton);
    if (result) {
      overlay.setIsVisible(!overlay.isVisible());
    }
    return result;
  }

  public void setMode(@Nonnull T newMode) {
    if (this.mode != newMode) {
      this.mode = newMode;
      List<String> tooltip = newMode.getTooltipLines();
      this.setToolTip(tooltip.toArray(new String[tooltip.size()]));
      this.icon = newMode.getIcon();
    }
  }

  public @Nonnull T getMode() {
    return this.mode != null ? this.mode : (T) this.modes.get(0);
  }

  class PickerOverlay implements IGuiOverlay {

    CycleButton cycleButton;
    Rectangle bounds = new Rectangle(0, 0, 0, 0);
    List<Pair<Rectangle, T>> modes  = new ArrayList<>();

    boolean visible;
    int rows = 0;
    int cols = 0;

    int yOffset = 0;
    int xOffset = 0;
    int width = 0;
    int height = 0;

    public PickerOverlay(CycleButton cycleButton) {
      this.cycleButton = cycleButton;

      int clrIndex = 0;

      while (rows * cols < cycleButton.modes.size()) {
        if ((cols + 1) * rows >= cycleButton.modes.size()) {
          cols++;
        } else if ((rows + 1) * cols >= cycleButton.modes.size()) {
          rows++;
        } else if (cols > rows) {
          rows++;
        } else {
          cols++;
        }
      }

      yOffset = cycleButton.yOrigin + cycleButton.height;
      xOffset = cycleButton.xOrigin;
      width = 17 * cols + 4;
      height = 17 * rows + 4;
      setBounds(new Rectangle(xOffset, yOffset, width, height));

      for (int r = 0; r < rows; r++) {
        for (int c = 0; c < cols; c++) {
          if (clrIndex < cycleButton.modes.size()) {
            modes.add(Pair.of(new Rectangle(17 * c, 3 + 17 * r, 17, 17), (T)cycleButton.modes.get(clrIndex)));
            clrIndex++;
          }
        }
      }
    }

    @Override
    public void init(@Nonnull IGuiScreen screen) {
    }

    @Override
    public @Nonnull Rectangle getBounds() {
      return bounds;
    }

    public void setBounds(Rectangle bounds) {
      this.bounds = bounds;
    }

    @Override
    public void draw(int mouseX, int mouseY, float partialTick) {
      RenderSystem.enableDepthTest();

      if (isOpened) {
        for (Pair<Rectangle, T> pair : modes) {
          IWidgetIcon icon = pair.getRight().getIcon();

          if (cycleButton.getMode() == pair.getRight()) {
            setBounds(new Rectangle((cycleButton.xOrigin - (pair.getLeft().x)), cycleButton.yOrigin - pair.getLeft().y, width, height));
            EnderWidget.BUTTON_DOWN_HIGHLIGHT.getMap().render(EnderWidget.BUTTON_DOWN_HIGHLIGHT, getBounds().x + pair.getLeft().x, getBounds().y + pair.getLeft().y, 390, true);
          } else {
            EnderWidget.BUTTON.getMap().render(EnderWidget.BUTTON, getBounds().x + pair.getLeft().x, getBounds().y + pair.getLeft().y, 390, true);
          }
          icon.getMap().render(icon, getBounds().x + pair.getLeft().x, getBounds().y + pair.getLeft().y, 400, true);
        }

        RenderUtil.renderQuad2D(getBounds().x - 2, getBounds().y + 1, 300, width - 1, height - 1, ColorUtil.getRGB(Color.DARK_GRAY));
        RenderUtil.renderQuad2D(getBounds().x - 1, getBounds().y + 2, 300, width - 3, height - 3, ColorUtil.getRGB(Color.GRAY));
      }
    }

    @Override
    public void setIsVisible(boolean visible) {
      this.visible = visible;
    }

    @Override
    public boolean isVisible() {
      return visible;
    }

    @Override
    public boolean handleMouseInput(double x, double y, int b) {
      if (isMouseInBounds(x, y)) {
        if (b == 0 && InputUtil.isMouseButtonPressed(b)) {
          double mouseX = x - cycleButton.gui.getGuiRootLeft() - getBounds().x;
          double mouseY = y - cycleButton.gui.getGuiRootTop() - getBounds().y;
          for (Pair<Rectangle, T> pair : modes) {
            if (pair.getLeft().contains((int)mouseX, (int)mouseY)) {
              cycleButton.setMode(pair.getRight());
              setIsVisible(false);
            }
          }
        }
        return true;
      }

      if (b == 0 && InputUtil.isMouseButtonPressed(b)) {
        setIsVisible(false);
      }
      return false;
    }

    @Override
    public boolean isMouseInBounds(double mouseX, double mouseY) {
      double x = mouseX - cycleButton.gui.getGuiRootLeft();
      double y = mouseY - cycleButton.gui.getGuiRootTop();
      return bounds.contains(x, y);
    }

    @Override
    public void guiClosed() {
    }

  }
}
