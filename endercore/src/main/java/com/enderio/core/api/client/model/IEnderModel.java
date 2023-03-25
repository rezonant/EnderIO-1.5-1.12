package com.enderio.core.api.client.model;

import net.minecraft.client.renderer.block.ModelBlockRenderer;

/**
 * To be used on techne-style box models
 */
@Deprecated
public interface IEnderModel {
  /**
   * Renders all parts of the model
   *
   * @param size
   *          - Size of the model (usually 0.0625f)
   */
  public void render(float size);

  /**
   * Rotates the model part in the given directions
   *
   * @param model
   *          - part of the model to rotate
   * @param x
   * @param y
   * @param z
   */
  public void setRotation(ModelBlockRenderer model, float x, float y, float z);
}
