package com.madspin;

import com.madspin.gui.KeyBindScreen;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = MadSpinMod.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientKeyHandler {
    public static final int DEFAULT_KEY = GLFW.GLFW_KEY_X;

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        MinecraftForge.EVENT_BUS.register(new KeyPressHandler());
    }

    public static class KeyPressHandler {
        @SubscribeEvent
        public void onKeyInput(InputEvent.Key event) {
            if (event.getAction() != GLFW.GLFW_PRESS) return;

            if (event.getKey() == GLFW.GLFW_KEY_M) {
                KeyBindScreen.open();
                return;
            }

            int targetKey = MadSpinMod.SPIN_KEY_CODE;
            if (targetKey == -1) targetKey = DEFAULT_KEY;

            if (event.getKey() == targetKey) {
                toggleSpin();
            }
        }

        private void toggleSpin() {
            MadSpinMod.isSpinning = !MadSpinMod.isSpinning;
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null) {
                String key = MadSpinMod.isSpinning ? "status.madspin.enabled" : "status.madspin.disabled";
                Component msg = Component.translatable(key).withStyle(
                        MadSpinMod.isSpinning ? ChatFormatting.GREEN : ChatFormatting.RED
                );
                mc.player.displayClientMessage(msg, true);
            }
        }
    }
}