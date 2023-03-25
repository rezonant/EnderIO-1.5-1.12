package com.enderio.core.client;

import net.minecraft.client.Minecraft;

public class InputUtil {
    public static boolean isMouseButtonPressed(int number) {
        if (number == 0)
            return Minecraft.getInstance().mouseHandler.isLeftPressed();
        else if (number == 1)
            return Minecraft.getInstance().mouseHandler.isMiddlePressed();
        else if (number == 2)
            return Minecraft.getInstance().mouseHandler.isRightPressed();

        return false;
    }
}
