package com.enderio.core.client.gui.widget;

import java.lang.reflect.Field;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.api.client.gui.IGuiScreen;
import com.enderio.core.api.client.gui.IHideable;
import com.google.common.base.Strings;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

public class TextFieldEnder extends EditBox implements IHideable {

  public interface ICharFilter {

    boolean passesFilter(@Nonnull TextFieldEnder tf, char c);
  }

  public static final ICharFilter FILTER_NUMERIC = new ICharFilter() {

    @Override
    public boolean passesFilter(@Nonnull TextFieldEnder tf, char c) {
      return Character.isDigit(c) || c == '-' && Strings.isNullOrEmpty(tf.getValue());
    }
  };

  public static ICharFilter FILTER_ALPHABETICAL = new ICharFilter() {

    @Override
    public boolean passesFilter(@Nonnull TextFieldEnder tf, char c) {
      return Character.isLetter(c);
    }
  };

  public static ICharFilter FILTER_ALPHANUMERIC = new ICharFilter() {

    @Override
    public boolean passesFilter(@Nonnull TextFieldEnder tf, char c) {
      return FILTER_NUMERIC.passesFilter(tf, c) || FILTER_ALPHABETICAL.passesFilter(tf, c);
    }
  };

  private int xOrigin;
  private int yOrigin;
  private @Nullable ICharFilter filter;

  public TextFieldEnder(Font pFont, int pX, int pY, int pWidth, int pHeight, Component pMessage) {
    this(pFont, pX, pY, pWidth, pHeight, pMessage, null);
  }

  public TextFieldEnder(Font pFont, int pX, int pY, int pWidth, int pHeight, Component pMessage, ICharFilter pCharFilter) {
    super(pFont, pX, pY, pWidth, pHeight, pMessage);
    xOrigin = pX;
    yOrigin = pY;
    filter = pCharFilter;
  }

  public void init(@Nonnull IGuiScreen gui) {
    this.x = xOrigin + gui.getGuiRootLeft();
    this.y = yOrigin + gui.getGuiRootTop();
  }

  public TextFieldEnder setCharFilter(@Nullable ICharFilter filter) {
    this.filter = filter;
    return this;
  }

  @Override
  public boolean charTyped(char c, int key) {
    final ICharFilter filter2 = filter;
    if (filter2 == null || filter2.passesFilter(this, c) || isSpecialChar(c, key)) {
      return super.charTyped(c, key);
    }
    return false;
  }

  public static boolean isSpecialChar(char c, int key) {
    // taken from the giant switch statement in GuiTextField
    return c == 1 || c == 3 || c == 22 || c == 24 || key == 14 || key == 199 || key == 203 || key == 205 || key == 207 || key == 211;
  }

  public boolean getCanLoseFocus() {
    return this.canLoseFocus;
  }

  public boolean contains(double mouseX, double mouseY) {
    return mouseX >= this.x && mouseX < this.x + width && mouseY >= this.y && mouseY < this.y + height;
  }

  @Override
  public void setIsVisible(boolean visible) {
    setVisible(visible);
  }

  public void setXOrigin(int xOrigin) {
    this.xOrigin = xOrigin;
  }

  public void setYOrigin(int yOrigin) {
    this.yOrigin = yOrigin;
  }

  public @Nullable Integer getInteger() {
    String text = getValue();
    try {
      return Integer.parseInt(text);
    } catch (Exception e) {
      return null;
    }
  }
}
