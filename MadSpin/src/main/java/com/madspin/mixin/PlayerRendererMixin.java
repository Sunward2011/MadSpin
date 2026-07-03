package com.madspin.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.madspin.MadSpinMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerRenderer.class)
public class PlayerRendererMixin {

    @Inject(method = "render(Lnet/minecraft/client/player/AbstractClientPlayer;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At("HEAD"))
    private void onRenderHead(AbstractClientPlayer player, float entityYaw, float partialTick,
                              PoseStack poseStack, MultiBufferSource bufferSource, int packedLight,
                              CallbackInfo ci) {
        // 只对本地玩家生效
        if (!MadSpinMod.isSpinning) return;
        if (player != Minecraft.getInstance().player) return;

        float angle = 0f;
        boolean isSpinMode = MadSpinMod.isSpinMode;

        if (isSpinMode) {
            angle = (player.tickCount + partialTick) * MadSpinMod.rotationSpeed;
        } else {
            angle = (float) Math.PI;
        }

        poseStack.mulPose(com.mojang.math.Axis.YP.rotation(angle));
    }
}
