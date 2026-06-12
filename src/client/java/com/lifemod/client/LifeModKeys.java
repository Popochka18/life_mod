package com.lifemod.client;

import com.lifemod.LifeModIds;
import com.lifemod.client.screen.JournalScreen;
import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.client.KeyMapping;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;

import org.lwjgl.glfw.GLFW;

/** Client keybindings: J opens the journal (quests + faction reputation). */
public final class LifeModKeys {
    private LifeModKeys() {
    }

    public static void init() {
        KeyMapping.Category category = KeyMapping.Category.register(LifeModIds.id("main"));
        KeyMapping journal = KeyMappingHelper.registerKeyMapping(
                new KeyMapping("key.life-mod.journal", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_J, category));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (journal.consumeClick()) {
                if (client.player != null && client.screen == null) {
                    client.setScreen(new JournalScreen());
                }
            }
        });
    }
}
