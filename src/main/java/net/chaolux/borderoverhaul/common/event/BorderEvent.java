package net.chaolux.borderoverhaul.common.event;

import net.chaolux.borderoverhaul.Config;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
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
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;

@EventBusSubscriber(modid = "borderoverhaul", bus = Bus.FORGE)
public class BorderEvent {
    private static final Map<ResourceKey<Level>, Integer> BORDER_CACHE=new HashMap<>();
    private static final Set<ResourceLocation> MOD_ENTITY_KNOCKBACK_CACHE=new HashSet<>();
    private static final Set<ResourceLocation> MOD_ENTITY_CLAMP_CACHE=new HashSet<>();

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
            boolean shapeBorder=switch(Config.BORDER_SHAPE.get()) {
                case SQUARE -> Math.abs(x) > halfBorder || Math.abs(z) > halfBorder;
                case CIRCLE -> (x * x + z * z) > (halfBorder * halfBorder);
                case OVAL -> {
                    double rx=halfBorder;
                    double rz=rx * Config.OVAL_Z_RATIO.get();
                    yield (x * x) / (rx * rx) + (z * z) / (rz * rz) > 1.0;
                }
            };

            if (!shapeBorder) continue;
                double knockbackForce = Config.KNOCKBACK_FORCE.get();
                double dx=x;
                double dz=z;
                double math=Math.sqrt(dx * dx + dz * dz);
                if(math == 0) math=0.01;
                double knockbackX = -knockbackForce * (dx / math);
                double knockbackZ = -knockbackForce * (dz / math);

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
                } else if (Config.ENABLE_MOD_ENTITY_KNOCKBACK.get() && shouldKnockbackModEntity(entity)) {
                    entity.setDeltaMovement(new Vec3(knockbackX, 0, knockbackZ));
                } else if (Config.ENABLE_MOD_ENTITY_CLAMP.get() && shouldClampModEntity(entity)) {
                    clampModEntity(entity,halfBorder);
                }
        }
    }

    private static boolean shouldKnockbackModEntity(Entity entity) {
        ResourceLocation resourceLocation= ForgeRegistries.ENTITY_TYPES.getKey(entity.getType());
        return resourceLocation !=null && MOD_ENTITY_KNOCKBACK_CACHE.contains(resourceLocation);
    }

    private static boolean shouldClampModEntity(Entity entity) {
        ResourceLocation resourceLocation=ForgeRegistries.ENTITY_TYPES.getKey(entity.getType());
        return resourceLocation !=null && MOD_ENTITY_CLAMP_CACHE.contains(resourceLocation);
    }

    private static void clampModEntity(Entity entity, double halfBorder) {
        double offset=Config.MOD_ENTITY_CLAMP_OFFSET.get();
        double min= -halfBorder + offset;
        double max= halfBorder - offset;
        double clampedX= Mth.clamp(entity.getX(), min, max);
        double clampedZ= Mth.clamp(entity.getZ(), min, max);
        entity.setDeltaMovement(Vec3.ZERO);
        entity.setPos(clampedX, entity.getY(), clampedZ);
        entity.hurtMarked=true;
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
        MOD_ENTITY_KNOCKBACK_CACHE.clear();
        MOD_ENTITY_CLAMP_CACHE.clear();
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
        List<? extends String> modEntityList=Config.MOD_ENTITY_KNOCKBACK_LIST.get();
        for(String string : modEntityList) {
            ResourceLocation resourceLocation=ResourceLocation.tryParse(string.trim());
            if(resourceLocation !=null) {
                MOD_ENTITY_KNOCKBACK_CACHE.add(resourceLocation);
            }
        }
        List<? extends String> modEntityClampList=Config.MOD_ENTITY_CLAMP_LIST.get();
        for(String string : modEntityClampList) {
            ResourceLocation resourceLocation=ResourceLocation.tryParse(string.trim());
            if(resourceLocation !=null) {
                MOD_ENTITY_CLAMP_CACHE.add(resourceLocation);
            }
        }
    }
}