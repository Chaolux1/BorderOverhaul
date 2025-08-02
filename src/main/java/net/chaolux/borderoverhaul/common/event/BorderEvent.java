package net.chaolux.borderoverhaul.common.event;

import net.chaolux.borderoverhaul.Config;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mod.EventBusSubscriber
public class BorderEvent {
    private static final Map<ResourceKey<Level>, Integer> BORDER_CACHE=new HashMap<>();

    @SubscribeEvent
    public static void onWorldLoad(LevelEvent.Load event) {
        if(!(event.getLevel() instanceof ServerLevel serverLevel)) return;
        reloadConfig();
    }
    @SubscribeEvent
    public static void onLevelTick(TickEvent.LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.level instanceof ServerLevel serverLevel)) return;
        ResourceKey<Level> dimension = serverLevel.dimension();
        int borderSize=BORDER_CACHE.getOrDefault(dimension,30000000);

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
                } else if (entity instanceof PrimedTnt && Config.ENABLE_EXPLOSIVE_KNOCKBACK.get()) {
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

    public static void reloadConfig() {
        BORDER_CACHE.clear();
        BORDER_CACHE.put(Level.OVERWORLD,Config.OVERWORLD_BORDER.get());
        BORDER_CACHE.put(Level.NETHER,Config.NETHER_BORDER.get());
        BORDER_CACHE.put(Level.END,Config.END_BORDER.get());
        List<? extends String> list=Config.CUSTOM_BORDER_LIST.get();
        for(String object:list) {
            try {
                String[] part=object.split("=");
                if(part.length !=2) continue;
                String id=part[0].trim();
                int value=Integer.parseInt(part[1].trim());
                ResourceLocation location=new ResourceLocation(id);
                ResourceKey<Level> dim=ResourceKey.create(Registries.DIMENSION,location);
                BORDER_CACHE.put(dim,value);
            } catch (Exception ignored) {

            }
        }
    }
}