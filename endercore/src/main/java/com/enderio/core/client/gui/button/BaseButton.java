package com.enderio.core.client.gui.button;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

import javax.annotation.Nonnull;

public class BaseButton extends Button {
    private static final OnPress DUD_PRESSABLE = p_onPress_1_ -> { };

    public BaseButton(int x, int y, int width, int height, Component title) {
        super(x, y, width, height, title, DUD_PRESSABLE);
    }

    public BaseButton(int x, int y, int width, int height, Component title, OnPress pressedAction) {
        super(x, y, width, height, title, pressedAction);
    }

    public BaseButton(int x, int y, int width, int height, Component title, OnTooltip onTooltip) {
        super(x, y, width, height, title, DUD_PRESSABLE, onTooltip);
    }

    public BaseButton(int x, int y, int width, int height, Component title, OnPress pressedAction, OnTooltip onTooltip) {
        super(x, y, width, height, title, pressedAction, onTooltip);
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isActive() {
        return active;
    }

    @Override
    public boolean isHoveredOrFocused() {
        if (!isActive())
            return false;
        return super.isHoveredOrFocused();
    }

    @Override
    public void onPress() {
        if (isActive())
            super.onPress();
    }

    @Override
    protected boolean clicked(double mouseX, double mouseY) {
        return isActive() && super.clicked(mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return isActive() && super.mouseClicked(mouseX, mouseY, button);
    }

    /**
     * Override this to handle mouse clicks with other buttons than the left
     */
    public boolean buttonPressed(double mouseX, double mouseY, int button) {
        return false;
    }
}