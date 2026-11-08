package io.github.emberbocor.ignoredworkstations;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;

@Mod(IgnoredWorkstations.MODID)
public class IgnoredWorkstations {
    public static final String MODID = "ignoredworkstations";
    public static final Logger LOGGER = LogUtils.getLogger();

    public IgnoredWorkstations(IEventBus modEventBus, ModContainer modContainer) {
    }
}
