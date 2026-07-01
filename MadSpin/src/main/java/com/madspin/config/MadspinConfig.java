package com.madspin.config;

import com.madspin.MadSpinMod;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

public class MadspinConfig {
    public static final ForgeConfigSpec CLIENT_SPEC;
    public static final ClientConfig CLIENT;

    static {
        final Pair<ClientConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(ClientConfig::new);
        CLIENT_SPEC = specPair.getRight();
        CLIENT = specPair.getLeft();
    }

    public static class ClientConfig {
        public final ForgeConfigSpec.IntValue spinKeyCode;
        public final ForgeConfigSpec.DoubleValue rotationSpeed;
        public final ForgeConfigSpec.BooleanValue spinMode;
        public final ForgeConfigSpec.DoubleValue spinHeadAngleDeg;
        public final ForgeConfigSpec.DoubleValue backHeadAngleDeg;

        ClientConfig(ForgeConfigSpec.Builder builder) {
            builder.push("general");

            spinKeyCode = builder
                    .comment("Custom key code for toggling spin (-1 means use default X)")
                    .defineInRange("spinKeyCode", -1, -1, 512);

            rotationSpeed = builder
                    .comment("Rotation speed multiplier")
                    .defineInRange("rotationSpeed", 0.5, 0.1, 2.0);

            spinMode = builder
                    .comment("true = Spin mode, false = Back mode")
                    .define("spinMode", true);

            spinHeadAngleDeg = builder
                    .comment("Head angle in degrees for Spin mode (negative = up)")
                    .defineInRange("spinHeadAngleDeg", -30.0, -90.0, 90.0);

            backHeadAngleDeg = builder
                    .comment("Head angle in degrees for Back mode (positive = down)")
                    .defineInRange("backHeadAngleDeg", 45.0, -90.0, 90.0);

            builder.pop();
        }
    }

    public static void loadFromStatic() {
        MadSpinMod.SPIN_KEY_CODE = CLIENT.spinKeyCode.get();
        MadSpinMod.rotationSpeed = CLIENT.rotationSpeed.get().floatValue();
        MadSpinMod.isSpinMode = CLIENT.spinMode.get();
        MadSpinMod.spinHeadAngleDeg = CLIENT.spinHeadAngleDeg.get().floatValue();
        MadSpinMod.backHeadAngleDeg = CLIENT.backHeadAngleDeg.get().floatValue();
    }

    public static void saveFromStatic() {
        // ★★★ 检查配置是否已加载，未加载则跳过保存 ★★★
        if (!CLIENT_SPEC.isLoaded()) {
            return;
        }
        CLIENT.spinKeyCode.set(MadSpinMod.SPIN_KEY_CODE);
        CLIENT.rotationSpeed.set((double) MadSpinMod.rotationSpeed);
        CLIENT.spinMode.set(MadSpinMod.isSpinMode);
        CLIENT.spinHeadAngleDeg.set((double) MadSpinMod.spinHeadAngleDeg);
        CLIENT.backHeadAngleDeg.set((double) MadSpinMod.backHeadAngleDeg);
        CLIENT_SPEC.save();
    }

    public static void register() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, CLIENT_SPEC, "madspin-client.toml");
    }
}