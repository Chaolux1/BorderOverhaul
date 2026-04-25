package net.chaolux.borderoverhaul.common.event;

import net.chaolux.borderoverhaul.Config;
import net.chaolux.borderoverhaul.common.border.BorderPlayerData;
import net.chaolux.borderoverhaul.common.border.BorderPunishment;
import net.chaolux.borderoverhaul.common.border.BorderShapeHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber(modid = "borderoverhaul", bus = Bus.FORGE)
public class BorderPlayerEvent {
    private static final String BORDER_KILL_FLAG="borderoverhaul_border_kill_flag";
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if(event.phase !=TickEvent.Phase.END) return;
        if(!(event.player instanceof ServerPlayer player)) return;
        BorderPlayerData.tickTeleportCooldown(player);
        ServerLevel serverLevel=player.serverLevel();
        double halfBorder=getBorderSize(serverLevel) / 2.0;
        double x=player.getX();
        double z=player.getZ();
        boolean outside= BorderShapeHelper.isOutside(x,z,halfBorder);
        double distance=BorderShapeHelper.distanceOutside(x,z,halfBorder);
        if(!outside) {
            BorderPlayerData.setLastSafePos(player,player.getX(),player.getY(),player.getZ(),serverLevel.dimension().location().toString());
        }
        switch (Config.PLAYER_BORDER_MODE.get()) {
            case PUSHBACK_EFFECT -> {

            }
            case OPPOSITE_SIDE_TELEPORT -> handleOppositeTeleport(player,serverLevel,outside,halfBorder);
            case DEATH_TIMER -> handleDeathTimer(player,outside,false,distance);
            case DEATH_TIMER_WITH_EFFECTS -> handleDeathTimer(player,outside,true,distance);
            case SKY_PENALTY -> handleSkyPenalty(player,outside);
            case VOID_MARK -> handleVoidMark(player,outside);
        }
    }

    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if(!(event.getEntity() instanceof ServerPlayer serverPlayer)) return;
        BorderPlayerData.clearDeathTimer(serverPlayer);
        BorderPlayerData.clearVoidMarkState(serverPlayer);
        serverPlayer.getPersistentData().putBoolean(BORDER_KILL_FLAG,false);
    }

    private static void handleOppositeTeleport(ServerPlayer serverPlayer, ServerLevel serverLevel, boolean outside, double halfBorder) {
        if(!outside) return;
        if(BorderPlayerData.getTeleportCooldown(serverPlayer) > 0) return;
        Vec3 vec3=BorderShapeHelper.getOutside(serverPlayer.getX(),serverPlayer.getZ(),halfBorder,Config.OPPOSITE_TELEPORT_OFFSET.get());
        BlockPos pos=BorderShapeHelper.findSafeTeleportPos(serverLevel,vec3.x,vec3.z,Config.OPPOSITE_TELEPORT_SEARCH_RADIUS.get());
        if(pos !=null) {
            serverPlayer.teleportTo(serverLevel,pos.getX() + 0.5,pos.getY(),pos.getZ() + 0.5,serverPlayer.getYRot(),serverPlayer.getXRot());
        } else {
            serverPlayer.teleportTo(serverLevel,vec3.x,serverPlayer.getY(),vec3.z,serverPlayer.getYRot(),serverPlayer.getXRot());
        }
        BorderPlayerData.setTeleportCooldown(serverPlayer,Config.OPPOSITE_TELEPORT_COOLDOWN_TICKS.get());
    }

    private static void handleDeathTimer(ServerPlayer serverPlayer, boolean outside, boolean withEffects, double distance) {
        if(outside && !BorderPlayerData.isDeathTimerActive(serverPlayer)) {
            BorderPlayerData.setDeathTimerActive(serverPlayer, true);
            BorderPlayerData.setDeathTimerTicks(serverPlayer,Config.DEATH_TIMER_SECONDS.get() * 20);
        }
            if(!BorderPlayerData.isDeathTimerActive(serverPlayer)) return;
            int ticks=BorderPlayerData.getDeathTimerTicks(serverPlayer) - 1;
            BorderPlayerData.setDeathTimerTicks(serverPlayer,ticks);
            int seconds=Math.max(0,ticks / 20);
            serverPlayer.displayClientMessage(Component.translatable("message.borderoverhaul.border_countdown",seconds),true);
            if(withEffects) BorderEvent.applyEffects(serverPlayer,Math.max(distance,Config.EFFECT_1_THRESHOLD.get()));
            if(ticks <= 0) {
                BorderPlayerData.clearDeathTimer(serverPlayer);
                killPlayer(serverPlayer);
            }
    }

    private static void applyDeathTimerEffects(ServerPlayer serverPlayer, int ticks) {
        serverPlayer.addEffect(new MobEffectInstance(MobEffects.BLINDNESS,40,0,false,false,true));
        serverPlayer.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN,40,0,false,false,true));
        if(ticks <= Config.DEATH_TIMER_SECONDS.get() * 2 / 3) {
            serverPlayer.addEffect(new MobEffectInstance(MobEffects.POISON,40,0,false,false,true));
        } if(ticks <= Config.DEATH_TIMER_SECONDS.get() / 3) {
            serverPlayer.addEffect(new MobEffectInstance(MobEffects.WITHER, 40, 0, false, false, true));
            serverPlayer.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 40, 0, false, false, true));
        }
    }

    private static void handleSkyPenalty(ServerPlayer serverPlayer, boolean outside) {
        if(!outside) return;
        if(BorderPlayerData.getTeleportCooldown(serverPlayer) > 0) return;
        ResourceLocation resourceLocation=ResourceLocation.tryParse(Config.SKY_PENALTY_DIMS.get());
        if(resourceLocation == null) return;
        ResourceKey<Level> key=ResourceKey.create(Registries.DIMENSION,resourceLocation);
        ServerLevel serverLevel=serverPlayer.server.getLevel(key);
        if(serverLevel == null) return;
        serverPlayer.teleportTo(serverLevel,Config.SKY_PENALTY_X.get(),Config.SKY_PENALTY_Y.get(),Config.SKY_PENALTY_Z.get(),serverPlayer.getYRot(),serverPlayer.getXRot());
        serverPlayer.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING,Config.SKY_PENALTY_SLOW_FALLING_SECONDS.get() * 20,0,false,false,true));
        BorderPlayerData.setTeleportCooldown(serverPlayer,Config.SKY_PENALTY_COOLDOWN_TICKS.get());
    }

    private static void handleVoidMark(ServerPlayer serverPlayer, boolean outside) {
        int mark=Config.VOID_MARK_SECONDS.get() * 20;
        int recovery=Config.VOID_MARK_RECOVERY_SECONDS.get() * 20;
        if(!BorderPlayerData.isVoidMarkActive(serverPlayer)) {
            if(!outside) return;
            if(BorderPlayerData.isVoidMarkInstantNext(serverPlayer)) {
                BorderPlayerData.setVoidMarkInstantNext(serverPlayer,false);
                killPlayer(serverPlayer);
                return;
            }
            BorderPlayerData.setVoidMarkActive(serverPlayer,true);
            BorderPlayerData.setVoidMarkRecovering(serverPlayer,false);
            BorderPlayerData.setVoidMarkTicks(serverPlayer,mark);
        }
        int ticks=BorderPlayerData.getVoidMarkTicks(serverPlayer);
        if(outside) {
            BorderPlayerData.setVoidMarkRecovering(serverPlayer,false);
            ticks--;
            BorderPlayerData.setVoidMarkTicks(serverPlayer,ticks);
            serverPlayer.displayClientMessage(Component.translatable("message.borderoverhaul.void_mark").withStyle(ChatFormatting.DARK_PURPLE).append(Component.literal(String.valueOf(Math.max(0,ticks / 20))).withStyle(ChatFormatting.GOLD)).append(Component.translatable("message.borderoverhaul.void_mark_seconds").withStyle(ChatFormatting.GOLD)),true);
            if(ticks <= 0) {
                RandomPunishment(serverPlayer);
                BorderPlayerData.clearVoidMarkState(serverPlayer);
                killPlayer(serverPlayer);
                return;
            }
        } else {
            BorderPlayerData.setVoidMarkRecovering(serverPlayer,true);
            ticks++;
            BorderPlayerData.setVoidMarkTicks(serverPlayer,ticks);
            serverPlayer.displayClientMessage(Component.translatable("message.borderoverhaul.void_mark_recovering").withStyle(ChatFormatting.GOLD).append(Component.literal(String.valueOf(Math.min(ticks / 20,Config.VOID_MARK_RECOVERY_SECONDS.get()))).withStyle(ChatFormatting.DARK_PURPLE)).append(Component.translatable("message.borderoverhaul.void_mark_seconds").withStyle(ChatFormatting.DARK_PURPLE)),true);
            if(ticks >= recovery) {
                BorderPlayerData.setVoidMarkActive(serverPlayer,false);
                BorderPlayerData.setVoidMarkRecovering(serverPlayer,false);
                BorderPlayerData.setVoidMarkTicks(serverPlayer,0);
                BorderPlayerData.setVoidMarkInstantNext(serverPlayer,true);
                serverPlayer.displayClientMessage(Component.translatable("message.borderoverhaul.border_remembers"),false);
            }
        }
    }

    private static void RandomPunishment(ServerPlayer serverPlayer) {
        int max=Config.VOID_MARK_MAX_PUNISHMENTS.get();
        int current=BorderPlayerData.getVoidPunishmentCount(serverPlayer);
        if(current >= max) return;
        List<BorderPunishment> borderPunishmentList=new ArrayList<>();
        for(BorderPunishment punishment : BorderPunishment.values()) {
            if(punishment.isEnableInConfig() && !BorderPlayerData.isPunishments(serverPlayer,punishment)) borderPunishmentList.add(punishment);
        }
        if(borderPunishmentList.isEmpty()) return;
        BorderPunishment borderPunishment=borderPunishmentList.get(serverPlayer.getRandom().nextInt(borderPunishmentList.size()));
        BorderPlayerData.setPunishments(serverPlayer,borderPunishment);
        BorderPlayerData.setVoidPunishmentCount(serverPlayer,current + 1);
//        serverPlayer.displayClientMessage(Component.translatable("message.borderoverhaul.border_countdown",borderPunishment.getString()),false);
    }

    private static void killPlayer(ServerPlayer serverPlayer) {
        serverPlayer.getPersistentData().putBoolean(BORDER_KILL_FLAG,true);
        BorderPlayerData.clearDeathTimer(serverPlayer);
        BorderPlayerData.clearVoidMarkState(serverPlayer);
        serverPlayer.kill();
    }

    private static int getBorderSize(ServerLevel serverLevel) {
        ResourceKey<Level> dims=serverLevel.dimension();
        if(dims == Level.OVERWORLD) return Config.OVERWORLD_BORDER.get();
        if(dims == Level.NETHER) return Config.NETHER_BORDER.get();
        if(dims == Level.END) return Config.END_BORDER.get();
        for(String string : Config.CUSTOM_BORDER_LIST.get()) {
            try {
                String[] strings=string.split("=");
                if(strings.length !=2) continue;
                String id=strings[0].trim();
                int value=Integer.parseInt(strings[1].trim());
                ResourceLocation resourceLocation=ResourceLocation.tryParse(id);
                if(resourceLocation == null) continue;
                if(dims.location().equals(resourceLocation)) return value;
            } catch (Exception ignored) {

            }
        }
        return 30000000;
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        Player player=event.getEntity();
        if(event.getOriginal().getPersistentData().contains("borderoverhaul_data")) {
            player.getPersistentData().put("borderoverhaul_data",event.getOriginal().getPersistentData().getCompound("borderoverhaul_data").copy());
        }
        BorderPlayerData.clearDeathTimer(player);
        BorderPlayerData.clearVoidMarkState(player);
    }
}
