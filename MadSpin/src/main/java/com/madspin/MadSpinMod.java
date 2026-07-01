package com.madspin;

import com.madspin.config.MadspinConfig;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("madspin")
public class MadSpinMod {
    public static final String MOD_ID = "madspin";
    public static final Logger LOGGER = LogManager.getLogger();

    // ===== 运行时设置（由配置加载/保存） =====
    public static boolean isSpinning = false;
    public static int SPIN_KEY_CODE = -1;
    public static float rotationSpeed = 0.5f;
    public static boolean isSpinMode = true;
    public static float spinHeadAngleDeg = -30.0f;
    public static float backHeadAngleDeg = 45.0f;

    public MadSpinMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // ★★★ 注册配置 ★★★
        MadspinConfig.register();

        // 客户端设置事件，用于加载配置
        modEventBus.addListener(this::onClientSetup);

        LOGGER.info("MadSpin mod initialized!");
    }

    private void onClientSetup(FMLClientSetupEvent event) {
        // ★★★ 从配置文件加载值到静态变量 ★★★
        MadspinConfig.loadFromStatic();
        LOGGER.info("Loaded MadSpin config: key={}, speed={}, mode={}, spinAngle={}, backAngle={}",
                SPIN_KEY_CODE, rotationSpeed, isSpinMode, spinHeadAngleDeg, backHeadAngleDeg);
    }

    public static float getCurrentHeadAngleRad() {
        return (float) Math.toRadians(isSpinMode ? spinHeadAngleDeg : backHeadAngleDeg);
    }
}