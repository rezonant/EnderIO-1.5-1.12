package com.enderio.core.api.client.gui;

import java.io.IOException;

import javax.annotation.Nonnull;

import com.enderio.core.client.gui.GhostSlotHandler;
import com.enderio.core.client.gui.widget.GuiToolTip;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Widget;

public interface IGuiScreen {

  void addToolTip(@Nonnull GuiToolTip toolTip);

  boolean removeToolTip(@Nonnull GuiToolTip toolTip);

  void clearToolTips();

  int getGuiRootLeft();

  int getGuiRootTop();

  int getGuiXSize();

  int getGuiYSize();

  @Nonnull
  <T extends Button> T addButton(@Nonnull T button);

  void removeButton(@Nonnull Button button);

  int getOverlayOffsetXLeft();

  int getOverlayOffsetXRight();

  void doActionPerformed(@Nonnull Button but) throws IOException;

  @Nonnull
  GhostSlotHandler getGhostSlotHandler();

}
