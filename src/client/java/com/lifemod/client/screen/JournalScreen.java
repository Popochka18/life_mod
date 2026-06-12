package com.lifemod.client.screen;

import com.lifemod.faction.Faction;
import com.lifemod.quest.QuestInstance;
import com.lifemod.quest.QuestType;
import com.lifemod.registry.ModAttachments;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.CommonColors;

import java.util.Map;

/**
 * The journal (default key: J): active quests and faction reputation.
 * Data comes from the player attachments synced by ModAttachments.
 */
public class JournalScreen extends Screen {
    public JournalScreen() {
        super(Component.translatable("screen.life-mod.journal.title"));
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        this.extractBackground(graphics, mouseX, mouseY, delta);
        super.extractRenderState(graphics, mouseX, mouseY, delta);

        graphics.centeredText(this.font, this.title, this.width / 2, 15, CommonColors.WHITE);

        Map<Faction, Integer> reputation = currentReputation();
        Map<Faction, QuestInstance> quests = currentQuests();
        int lineHeight = this.font.lineHeight + 2;

        // Left column: reputation.
        int leftX = this.width / 2 - 150;
        int y = 35;
        graphics.text(this.font, Component.translatable("screen.life-mod.journal.reputation")
                .withStyle(ChatFormatting.GOLD), leftX, y, CommonColors.WHITE);
        y += lineHeight + 2;
        boolean any = false;

        for (Faction faction : Faction.values()) {
            int value = reputation.getOrDefault(faction, 0);

            if (value == 0) {
                continue;
            }

            any = true;
            graphics.text(this.font, Component.translatable("message.life-mod.rep.line",
                    Component.translatable(faction.translationKey()), value), leftX, y, CommonColors.WHITE);
            y += lineHeight;
        }

        if (!any) {
            graphics.text(this.font, Component.translatable("screen.life-mod.journal.no_reputation")
                    .withStyle(ChatFormatting.GRAY), leftX, y, CommonColors.WHITE);
        }

        // Right column: quests.
        int rightX = this.width / 2 + 10;
        y = 35;
        graphics.text(this.font, Component.translatable("screen.life-mod.journal.quests")
                .withStyle(ChatFormatting.GOLD), rightX, y, CommonColors.WHITE);
        y += lineHeight + 2;

        if (quests.isEmpty()) {
            graphics.text(this.font, Component.translatable("screen.life-mod.journal.no_quests")
                    .withStyle(ChatFormatting.GRAY), rightX, y, CommonColors.WHITE);
            return;
        }

        for (QuestInstance quest : quests.values()) {
            Component line = Component.translatable(
                    quest.type() == QuestType.KILL ? "screen.life-mod.journal.quest.kill" : "screen.life-mod.journal.quest.gather",
                    Component.translatable(quest.faction().translationKey()),
                    quest.type() == QuestType.KILL ? quest.progress() + "/" + quest.required() : String.valueOf(quest.required()),
                    targetName(quest));
            graphics.text(this.font, line, rightX, y, CommonColors.WHITE);
            y += lineHeight;
            graphics.text(this.font, Component.translatable("screen.life-mod.journal.quest.reward",
                            quest.rewardEmeralds(), quest.rewardReputation())
                    .withStyle(ChatFormatting.DARK_GRAY), rightX + 8, y, CommonColors.WHITE);
            y += lineHeight + 2;
        }
    }

    private static Map<Faction, Integer> currentReputation() {
        var player = Minecraft.getInstance().player;
        Map<Faction, Integer> map = player == null ? null : player.getAttached(ModAttachments.REPUTATION);
        return map == null ? Map.of() : map;
    }

    private static Map<Faction, QuestInstance> currentQuests() {
        var player = Minecraft.getInstance().player;
        Map<Faction, QuestInstance> map = player == null ? null : player.getAttached(ModAttachments.ACTIVE_QUESTS);
        return map == null ? Map.of() : map;
    }

    private static Component targetName(QuestInstance quest) {
        if (quest.targetId().startsWith("faction:")) {
            Faction target = Faction.byName(quest.targetId().substring("faction:".length()));
            return Component.translatable(target.translationKey());
        }

        Identifier id = Identifier.parse(quest.targetId()); // VERIFY-MAPPING: Identifier.parse

        if (quest.type() == QuestType.KILL) {
            return BuiltInRegistries.ENTITY_TYPE.getValue(id).getDescription(); // VERIFY-MAPPING: EntityType#getDescription
        }

        var item = BuiltInRegistries.ITEM.getValue(id);
        return item.getName(item.getDefaultInstance());
    }

    @Override
    public boolean isPauseScreen() { // VERIFY-MAPPING: Screen#isPauseScreen
        return false;
    }
}
