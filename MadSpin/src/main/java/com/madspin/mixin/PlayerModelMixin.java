package com.madspin.mixin;

import com.madspin.MadSpinMod;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerModel.class)
public class PlayerModelMixin<T extends LivingEntity> {

    @Inject(method = "setupAnim", at = @At("RETURN"))
    private void onSetupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks,
                             float netHeadYaw, float headPitch, CallbackInfo ci) {
        if (!(entity instanceof Player) || !MadSpinMod.isSpinning) {
            return;
        }

        PlayerModel<T> model = (PlayerModel<T>) (Object) this;

        float headAngle = MadSpinMod.getCurrentHeadAngleRad();
        model.head.xRot = headAngle;
        model.hat.xRot = headAngle;
    }
}