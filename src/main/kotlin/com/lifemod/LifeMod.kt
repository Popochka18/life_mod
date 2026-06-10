package com.lifemod

import com.lifemod.faction.FactionEvents
import com.lifemod.faction.WarEvents
import com.lifemod.quest.QuestEvents
import com.lifemod.registry.ModAttachments
import com.lifemod.registry.ModCreativeTabs
import com.lifemod.registry.ModEntities
import com.lifemod.registry.ModItems
import com.lifemod.registry.ModSpawns
import com.lifemod.tavern.TavernEvents
import net.fabricmc.api.ModInitializer
import org.slf4j.LoggerFactory

object LifeMod : ModInitializer {
    const val MOD_ID = LifeModIds.MOD_ID

    private val logger = LoggerFactory.getLogger("life-mod")

    override fun onInitialize() {
        ModItems.init()
        ModEntities.init()
        ModCreativeTabs.init()
        ModAttachments.init()
        ModSpawns.init()

        FactionEvents.init()
        WarEvents.init()
        QuestEvents.init()
        TavernEvents.init()

        logger.info("Life Mod initialised: 13 factions are settling the world…")
    }
}
