package me.ash.odin

import com.odtheking.odin.config.ModuleConfig
import com.odtheking.odin.features.Module
import com.odtheking.odin.features.ModuleManager
import net.fabricmc.api.ClientModInitializer
import me.ash.odin.features.impl.iron.*

object IronClient : ClientModInitializer {
    private val moduleConfig: ModuleConfig = ModuleConfig("iron")

    private val modulesToRegister: Array<Module> = arrayOf(
        FuckDiorite,
        BreakerHelper
    )

    override fun onInitializeClient() {
        ModuleManager.registerModules(moduleConfig, *modulesToRegister)
    }
}