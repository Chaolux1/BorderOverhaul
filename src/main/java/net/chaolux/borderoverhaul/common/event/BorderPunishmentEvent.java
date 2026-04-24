package net.chaolux.borderoverhaul.common.event;

import net.chaolux.borderoverhaul.Config;
import net.chaolux.borderoverhaul.common.border.BorderPlayerData;
import net.chaolux.borderoverhaul.common.border.BorderPunishment;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.ShieldBlockEvent;
import net.minecraftforge.event.entity.player.ItemFishedEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.entity.player.PlayerXpEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

import java.util.Iterator;
import java.util.Set;

@EventBusSubscriber(modid = "borderoverhaul", bus = Bus.FORGE)
public class BorderPunishmentEvent {
    private static final Set<Item> TREASURE_FISHING_ITEMS=Set.of(Items.BOW,Items.ENCHANTED_BOOK,Items.FISHING_ROD,Items.NAME_TAG,Items.NAUTILUS_SHELL,Items.SADDLE);

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if(event.phase !=TickEvent.Phase.END) return;
        if(!(event.player instanceof ServerPlayer serverPlayer)) return;
        if(isActive(serverPlayer, BorderPunishment.NO_SOUL_SPEED)) {
            if(serverPlayer.level().getBlockState(serverPlayer.blockPosition().below()).is(Blocks.SOUL_SAND) || serverPlayer.level().getBlockState(serverPlayer.blockPosition().below()).is(Blocks.SOUL_SOIL)) serverPlayer.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN,5,1,false,false,true));
        }
        if(isActive(serverPlayer,BorderPunishment.NO_BEACON_BUFFS)) {
            removeEffect(serverPlayer,MobEffects.MOVEMENT_SPEED);
            removeEffect(serverPlayer,MobEffects.DIG_SPEED);
            removeEffect(serverPlayer,MobEffects.DAMAGE_BOOST);
            removeEffect(serverPlayer,MobEffects.JUMP);
            removeEffect(serverPlayer,MobEffects.REGENERATION);
            removeEffect(serverPlayer,MobEffects.DAMAGE_RESISTANCE);
        }

        if(isActive(serverPlayer,BorderPunishment.BOW_BLINDNESS)) {
            if(serverPlayer.getMainHandItem().getItem() instanceof BowItem || serverPlayer.getOffhandItem().getItem() instanceof  BowItem) serverPlayer.addEffect(new MobEffectInstance(MobEffects.BLINDNESS,5,0,false,false,true));
        }
    }

    @SubscribeEvent
    public static void noSleep(PlayerSleepInBedEvent event) {
        if(!(event.getEntity() instanceof ServerPlayer serverPlayer)) return;
        if(!isActive(serverPlayer,BorderPunishment.NO_SLEEP)) return;
        event.setResult(Player.BedSleepingProblem.OTHER_PROBLEM);
        serverPlayer.displayClientMessage(Component.literal("You can no longer sleep"),true);
    }

    @SubscribeEvent
    public static void noMending(PlayerXpEvent.PickupXp event) {
        Player player=event.getEntity();
        if(!(player instanceof ServerPlayer serverPlayer)) return;
        if(!isActive(serverPlayer,BorderPunishment.NO_MENDING)) return;
        ExperienceOrb experienceOrb=event.getOrb();
        int value=experienceOrb.value;
        event.setCanceled(true);
        experienceOrb.discard();
        serverPlayer.giveExperiencePoints(value);
    }

    @SubscribeEvent
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        if(!(event.getEntity() instanceof ServerPlayer serverPlayer)) return;
        ItemStack itemStack=event.getItemStack();
        if(isActive(serverPlayer,BorderPunishment.NO_ELYTRA_FIREWORKS) && itemStack.getItem() instanceof FireworkRocketItem && serverPlayer.isFallFlying()) {
            event.setCanceled(true);
            serverPlayer.displayClientMessage(Component.literal("Fireworks no longer help your elytra :)"),true);
            return;
        }
        if(isActive(serverPlayer,BorderPunishment.NO_EATING_WHILE_MOVING) && itemStack.isEdible() && serverPlayer.getDeltaMovement().horizontalDistanceSqr() > (Config.PUNISHMENT_NO_EAT_MOVEMENT_THRESHOLD.get() * Config.PUNISHMENT_NO_EAT_MOVEMENT_THRESHOLD.get())) {
            event.setCanceled(true);
            serverPlayer.displayClientMessage(Component.literal("You must stand still to eat."),true);
        }
    }

    @SubscribeEvent
    public static void noShield(ShieldBlockEvent event) {
        if(!(event.getEntity() instanceof ServerPlayer serverPlayer)) return;
        if(!isActive(serverPlayer,BorderPunishment.AXE_BREAKS_SHIELD)) return;
        if(!(event.getDamageSource().getEntity() instanceof LivingEntity livingEntity)) return;
        if(!(livingEntity.getMainHandItem().getItem() instanceof AxeItem)) return;
        InteractionHand hand=serverPlayer.getUseItem() == serverPlayer.getMainHandItem() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
        ItemStack itemStack=serverPlayer.getUseItem();
        if(!itemStack.is(Items.SHIELD)) return;
        itemStack.hurtAndBreak(itemStack.getMaxDamage(),serverPlayer, p -> p.broadcastBreakEvent(hand));
        serverPlayer.stopUsingItem();
    }

    @SubscribeEvent
    public static void noFishing(ItemFishedEvent event) {
        if(!(event.getEntity() instanceof ServerPlayer serverPlayer)) return;
        if(!isActive(serverPlayer,BorderPunishment.NO_FISHING_TREASURE)) return;
        Iterator<ItemStack> iterator=event.getDrops().iterator();
        while (iterator.hasNext()) {
            ItemStack itemStack=iterator.next();
            if(TREASURE_FISHING_ITEMS.contains(itemStack.getItem())) {
                iterator.remove();
            }
        }
    }

    @SubscribeEvent
    public static void noDiscount(PlayerInteractEvent.EntityInteract event) {
        if(!(event.getEntity() instanceof ServerPlayer serverPlayer)) return;
        if(!isActive(serverPlayer,BorderPunishment.NO_VILLAGER_DISCOUNTS)) return;
        if(!(event.getTarget() instanceof AbstractVillager abstractVillager)) return;
        for(MerchantOffer merchantOffer : abstractVillager.getOffers()) {
            merchantOffer.setSpecialPriceDiff(0);
        }
    }

    private static boolean isActive(ServerPlayer serverPlayer, BorderPunishment borderPunishment) {
        return borderPunishment.isEnableInConfig() && BorderPlayerData.isPunishments(serverPlayer,borderPunishment);
    }

    private static void removeEffect(ServerPlayer serverPlayer, MobEffect effect) {
        if(serverPlayer.hasEffect(effect)) serverPlayer.removeEffect(effect);
    }


}
