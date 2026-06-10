package com.lifemod.item;

import com.lifemod.entity.projectile.MusketBallEntity;
import com.lifemod.registry.ModEntities;
import com.lifemod.registry.ModItems;
import com.lifemod.util.InventoryUtil;

import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

/**
 * A pirate musket. Cannot be crafted — claim one from a fallen musketeer.
 * Each shot consumes one musket ball and one powder pouch from the inventory.
 */
public class MusketItem extends Item {
    public MusketItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        boolean creative = player.getAbilities().instabuild; // VERIFY-MAPPING: Player#getAbilities

        if (!creative && (InventoryUtil.count(player, ModItems.MUSKET_BALL) == 0
                || InventoryUtil.count(player, ModItems.POWDER_POUCH) == 0)) {
            if (!level.isClientSide()) {
                player.sendSystemMessage(Component.translatable("message.life-mod.musket.no_ammo"));
            }

            return InteractionResult.FAIL;
        }

        if (!level.isClientSide()) {
            if (!creative) {
                InventoryUtil.consume(player, ModItems.MUSKET_BALL, 1);
                InventoryUtil.consume(player, ModItems.POWDER_POUCH, 1);
            }

            MusketBallEntity ball = new MusketBallEntity(ModEntities.MUSKET_BALL, level);
            ball.setOwner(player); // VERIFY-MAPPING: Projectile#setOwner
            ball.setPos(player.getX(), player.getEyeY() - 0.1, player.getZ()); // VERIFY-MAPPING: Entity#getEyeY
            // VERIFY-MAPPING: Projectile#shootFromRotation
            ball.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 3.5F, 1.5F);
            level.addFreshEntity(ball);
            // VERIFY-MAPPING: SoundEvents constant may be a Holder; unwrap with .value() if needed
            player.playSound(SoundEvents.FIREWORK_ROCKET_BLAST, 2.0F, 0.6F);
            player.getItemInHand(hand).hurtAndBreak(1, player, hand); // VERIFY-MAPPING: ItemStack#hurtAndBreak overload
            // VERIFY-MAPPING: ItemCooldowns#addCooldown — reload time between shots
            player.getCooldowns().addCooldown(player.getItemInHand(hand), 40);
        }

        return InteractionResult.SUCCESS;
    }
}
