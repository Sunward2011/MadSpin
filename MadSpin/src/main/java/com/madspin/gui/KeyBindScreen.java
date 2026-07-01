package com.madspin.gui;

import com.mojang.blaze3d.platform.InputConstants;
import com.madspin.ClientKeyHandler;
import com.madspin.MadSpinMod;
import com.madspin.config.MadspinConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFW;

@OnlyIn(Dist.CLIENT)
public class KeyBindScreen extends Screen {
    private static final Component TITLE = Component.translatable("screen.madspin.title");
    private static final Component BIND_BUTTON = Component.translatable("screen.madspin.bind");
    private static final Component WAITING_TEXT = Component.translatable("screen.madspin.waiting");
    private static final Component RESET_BUTTON = Component.translatable("screen.madspin.reset");
    private static final Component SPEED_LABEL = Component.translatable("screen.madspin.speed");
    private static final Component MODE_LABEL = Component.translatable("screen.madspin.mode");
    private static final Component MODE_SPIN = Component.translatable("screen.madspin.mode.spin");
    private static final Component MODE_BACK = Component.translatable("screen.madspin.mode.back");
    private static final Component HEAD_ANGLE_LABEL = Component.translatable("screen.madspin.head_angle");

    private boolean isWaitingForKey = false;
    private Button bindButton;
    private Button resetButton;
    private Button modeButton; // ★★★ 改用普通按钮替代 CycleButton ★★★
    private int currentKeyCode;
    private float currentSpeed;
    private boolean isSpinMode;
    private AbstractSliderButton speedSlider;
    private CustomAngleSlider headAngleSlider;
    private float currentHeadAngleDeg;

    private static class CustomAngleSlider extends AbstractSliderButton {
        public CustomAngleSlider(int x, int y, int width, int height, Component message, double value) {
            super(x, y, width, height, message, value);
        }

        @Override
        protected void updateMessage() {
            float mapped = (float) (this.value * 180 - 90);
            this.setMessage(Component.literal("Head: " + String.format("%.0f°", mapped)));
        }

        @Override
        protected void applyValue() {
            float mapped = (float) (this.value * 180 - 90);
            if (MadSpinMod.isSpinMode) {
                MadSpinMod.spinHeadAngleDeg = mapped;
            } else {
                MadSpinMod.backHeadAngleDeg = mapped;
            }
        }

        public void setAngleValue(float angleDeg) {
            this.value = (angleDeg + 90f) / 180f;
            this.updateMessage();
        }
    }

    public KeyBindScreen() {
        super(TITLE);
        currentKeyCode = MadSpinMod.SPIN_KEY_CODE;
        if (currentKeyCode == -1) currentKeyCode = ClientKeyHandler.DEFAULT_KEY;
        currentSpeed = MadSpinMod.rotationSpeed;
        isSpinMode = MadSpinMod.isSpinMode;
        currentHeadAngleDeg = isSpinMode ? MadSpinMod.spinHeadAngleDeg : MadSpinMod.backHeadAngleDeg;
    }

    @Override
    protected void init() {
        super.init();
        int cx = width / 2;
        int cy = height / 2 - 80;

        // ---- ★★★ 绑定按钮 ★★★ ----
        bindButton = Button.builder(
                        BIND_BUTTON,
                        btn -> {
                            isWaitingForKey = !isWaitingForKey;
                            btn.setMessage(isWaitingForKey ? WAITING_TEXT : BIND_BUTTON);
                        })
                .pos(cx - 80, cy)
                .size(60, 20)
                .build();
        addRenderableWidget(bindButton);

        // ---- ★★★ 重置按钮 ★★★ ----
        resetButton = Button.builder(
                        RESET_BUTTON,
                        btn -> {
                            currentKeyCode = ClientKeyHandler.DEFAULT_KEY;
                            MadSpinMod.SPIN_KEY_CODE = -1;
                            if (isWaitingForKey) {
                                isWaitingForKey = false;
                                bindButton.setMessage(BIND_BUTTON);
                            }
                        })
                .pos(cx + 30, cy)
                .size(50, 20)
                .build();
        addRenderableWidget(resetButton);

        // ---- 速度滑块 ----
        speedSlider = new AbstractSliderButton(
                cx - 80, cy + 35, 160, 20,
                Component.literal("Speed: 0.5"),
                (currentSpeed - 0.1f) / 1.9f
        ) {
            @Override
            protected void updateMessage() {
                float mapped = (float) (0.1 + this.value * 1.9);
                this.setMessage(Component.literal("Speed: " + String.format("%.1f", mapped)));
            }

            @Override
            protected void applyValue() {
                float mapped = (float) (0.1 + this.value * 1.9);
                currentSpeed = mapped;
                MadSpinMod.rotationSpeed = mapped;
            }
        };
        addRenderableWidget(speedSlider);

        // ---- ★★★ 模式切换按钮（普通 Button，无冒号） ★★★ ----
        modeButton = Button.builder(
                        isSpinMode ? MODE_SPIN : MODE_BACK,
                        btn -> {
                            isSpinMode = !isSpinMode;
                            MadSpinMod.isSpinMode = isSpinMode;
                            btn.setMessage(isSpinMode ? MODE_SPIN : MODE_BACK);
                            updateHeadAngleSliderValue();
                        })
                .pos(cx - 50, cy + 70)
                .size(100, 20)
                .build();
        addRenderableWidget(modeButton);

        // ---- 头部角度滑块 ----
        headAngleSlider = new CustomAngleSlider(
                cx - 80, cy + 105, 160, 20,
                Component.literal("Head: 0°"),
                (currentHeadAngleDeg + 90f) / 180f
        );
        updateHeadAngleSliderValue();
        addRenderableWidget(headAngleSlider);

        // ---- 完成按钮 ----
        addRenderableWidget(Button.builder(
                        Component.translatable("gui.done"),
                        btn -> onClose())
                .pos(cx - 50, cy + 145)
                .size(100, 20)
                .build());
    }

    private void updateHeadAngleSliderValue() {
        if (isSpinMode) {
            currentHeadAngleDeg = MadSpinMod.spinHeadAngleDeg;
        } else {
            currentHeadAngleDeg = MadSpinMod.backHeadAngleDeg;
        }
        headAngleSlider.setAngleValue(currentHeadAngleDeg);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        int cx = width / 2;
        int cy = height / 2 - 80;

        // ---- 标题 ----
        guiGraphics.drawCenteredString(font, title, cx, 20, 0xFFFFFF);

        // ---- ★★★ 当前绑定按键显示（居中于绑定和重置之间） ★★★ ----
        String keyName = getKeyName(currentKeyCode);
        // 绑定按钮左边界 = cx - 80，重置按钮右边界 = cx + 80 (因为重置按钮在 cx+30, 宽度50)
        // 中间位置 = (cx - 80 + cx + 80) / 2 = cx
        // 偏移一点让文字居中
        int textX = cx - (font.width("bind (" + keyName + ")") / 2);
        guiGraphics.drawString(font, "bind (" + keyName + ")", textX, cy + 2, 0xFFFFFF, false);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (isWaitingForKey) {
            currentKeyCode = keyCode;
            MadSpinMod.SPIN_KEY_CODE = keyCode;
            isWaitingForKey = false;
            bindButton.setMessage(BIND_BUTTON);
            String keyName = getKeyName(keyCode);
            Minecraft.getInstance().player.displayClientMessage(
                    Component.literal("已绑定: " + keyName), true
            );
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            onClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private String getKeyName(int keyCode) {
        if (keyCode == -1) return "None";
        String name = GLFW.glfwGetKeyName(keyCode, 0);
        if (name != null) return name;
        return switch (keyCode) {
            case GLFW.GLFW_KEY_RIGHT_SHIFT -> "Right Shift";
            case GLFW.GLFW_KEY_LEFT_SHIFT -> "Left Shift";
            case GLFW.GLFW_KEY_RIGHT_CONTROL -> "Right Ctrl";
            case GLFW.GLFW_KEY_LEFT_CONTROL -> "Left Ctrl";
            case GLFW.GLFW_KEY_RIGHT_ALT -> "Right Alt";
            case GLFW.GLFW_KEY_LEFT_ALT -> "Left Alt";
            default -> "Key " + keyCode;
        };
    }

    @Override
    public void onClose() {
        isWaitingForKey = false;
        MadspinConfig.saveFromStatic();
        super.onClose();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    public static void open() {
        Minecraft.getInstance().setScreen(new KeyBindScreen());
    }
}