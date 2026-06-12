package com.lifemod.client

import com.lifemod.client.render.LifeModClientRenderers
import net.fabricmc.api.ClientModInitializer

object LifeModClient : ClientModInitializer {
    override fun onInitializeClient() {
        LifeModClientRenderers.init()
        LifeModKeys.init()
    }
}
