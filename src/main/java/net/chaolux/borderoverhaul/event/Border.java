package net.chaolux.borderoverhaul.event;

import net.chaolux.borderoverhaul.Config;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class Border {
    @SubscribeEvent
    public static void onLevelTick(TickEvent.LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.level instanceof ServerLevel serverLevel)) return;


        ResourceKey<Level> dimension = serverLevel.dimension();
        int borderSize;

        if (dimension.equals(Level.OVERWORLD)) {
            borderSize = Config.OVERWORLD_BORDER.get();
        } else if (dimension.equals(Level.NETHER)) {
            borderSize = Config.NETHER_BORDER.get();
        } else if (dimension.equals(Level.END)) {
            borderSize = Config.END_BORDER.get();
        } else {
            borderSize = 30000000;
        }

        double halfBorder = borderSize / 2.0;

        for (Entity entity : serverLevel.getEntities().getAll()) {
            double x = entity.getX();
            double z = entity.getZ();

            if (Math.abs(x) > halfBorder || Math.abs(z) > halfBorder) {
                double knockbackForce = Config.KNOCKBACK_FORCE.get();
                double knockbackX = (x > halfBorder) ? -knockbackForce : (x < -halfBorder) ? knockbackForce : 0;
                double knockbackZ = (z > halfBorder) ? -knockbackForce : (z < -halfBorder) ? knockbackForce : 0;


                if (entity instanceof ServerPlayer player) {
                    player.setDeltaMovement(new Vec3(knockbackX, 0.2, knockbackZ));
                    player.hurtMarked = true;
                    applyEffects(player, Math.max(Math.abs(x) - halfBorder, Math.abs(z) - halfBorder));
                } else if (entity instanceof Boat boat && Config.ENABLE_BOAT_KNOCKBACK.get())  {
                    boat.setDeltaMovement(new Vec3(knockbackX, 0, knockbackZ));
                } else if (entity instanceof AbstractMinecart cart && Config.ENABLE_MINECART_KNOCKBACK.get()) {
                    cart.setDeltaMovement(new Vec3(knockbackX, 0, knockbackZ));
                } else if ((entity.getClass().getSimpleName().toLowerCase().contains("tnt"))  && Config.ENABLE_EXPLOSIVE_KNOCKBACK.get()) {
                    entity.setDeltaMovement(new Vec3(knockbackX, 0, knockbackZ));
            } else if (entity instanceof net.minecraft.world.entity.npc.Villager && Config.ENABLE_VILLAGER_KNOCKBACK.get()) {
                    entity.setDeltaMovement(new Vec3(knockbackX, 0, knockbackZ));
                } else if (entity instanceof net.minecraft.world.entity.animal.Animal && Config.ENABLE_ANIMAL_KNOCKBACK.get()) {
                    entity.setDeltaMovement(new Vec3(knockbackX, 0, knockbackZ));
                } else if (entity instanceof net.minecraft.world.entity.Mob && Config.ENABLE_MOB_KNOCKBACK.get()) {
                    entity.setDeltaMovement(new Vec3(knockbackX, 0, knockbackZ));
                }
            }
        }
    }


    private static void applyEffects(ServerPlayer player, double distance) {
        if (distance >= Config.EFFECT_1_THRESHOLD.get()) {
            player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 100, 0, false, false, true));
            player.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 100, 0, false, false, true));
        }
        if (distance >= Config.EFFECT_2_THRESHOLD.get()) {
            player.addEffect(new MobEffectInstance(MobEffects.POISON, 100, 0, false, false, true));
        }

        if (distance >= Config.EFFECT_3_THRESHOLD.get()) {
            player.addEffect(new MobEffectInstance(MobEffects.WITHER, 100, 0, false, false, true));
        }
        if (distance >= Config.EFFECT_4_THRESHOLD.get()) {
            player.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 100, 0, false, false, true));
        }
    }
}