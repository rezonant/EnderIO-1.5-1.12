package com.enderio.core.client.gui;

import javax.annotation.Nullable;

import com.enderio.core.client.gui.widget.GuiToolTip;
import com.mojang.blaze3d.vertex.PoseStack;

public interface IDrawingElement {

  @Nullable
  GuiToolTip getTooltip();

  void renderBackground(PoseStack pPoseStack, float pVOffset);

}
