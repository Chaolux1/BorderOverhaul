package net.chaolux.borderoverhaul.common.border;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

public class BorderPlayerData {
    private static final String ROOT="borderoverhaul_data";
    private static final String PUNISHMENTS="punishments";
    private static final String DEATH_TIMER_ACTIVE="death_timer_active";
    private static final String DEATH_TIMER_TICKS="death_timer_ticks";
    private static final String TELEPORT_COOLDOWN="teleport_cooldown";
    private static final String VOID_MARK_ACTIVE="void_mark_active";
    private static final String VOID_MARK_TICKS="void_mark_ticks";
    private static final String VOID_MARK_RECOVERING="void_mark_recovering";
    private static final String VOID_MARK_INSTANT_NEXT="void_mark_instant_next";
    private static final String VOID_PUNISHMENT_COUNT="void_punishment_count";
    private static final String LAST_SAFE_X="last_safe_x";
    private static final String LAST_SAFE_Y="last_safe_y";
    private static final String LAST_SAFE_Z="last_safe_z";
    private static final String LAST_SAFE_DIMS="last_safe_dims";
    private static final String LAST_SAFE="last_safe";
    private static CompoundTag getRoot(Player player) {
        CompoundTag compoundTag=player.getPersistentData();
        if(!compoundTag.contains(ROOT)) {
            compoundTag.put(ROOT,new CompoundTag());
        }
        return compoundTag.getCompound(ROOT);
    }

    private static CompoundTag getPunishmentTag(Player player) {
        CompoundTag root = getRoot(player);
        if (!root.contains(PUNISHMENTS)) {
            root.put(PUNISHMENTS, new CompoundTag());
        }
        return root.getCompound(PUNISHMENTS);
    }

    public static void setDeathTimerActive(Player player, boolean value) {
        getRoot(player).putBoolean(DEATH_TIMER_ACTIVE,value);
    }

    public static void setDeathTimerTicks(Player player, int ticks) {
        getRoot(player).putInt(DEATH_TIMER_TICKS,ticks);
    }

    public static boolean isDeathTimerActive(Player player) {
        return getRoot(player).getBoolean(DEATH_TIMER_ACTIVE);
    }

    public static int getDeathTimerTicks(Player player) {
        return getRoot(player).getInt(DEATH_TIMER_TICKS);
    }

    public static void clearDeathTimer(Player player) {
        CompoundTag compoundTag=getRoot(player);
        compoundTag.putBoolean(DEATH_TIMER_ACTIVE,false);
        compoundTag.putInt(DEATH_TIMER_TICKS,0);
    }

    public static void setTeleportCooldown(Player player, int ticks) {
        getRoot(player).putInt(TELEPORT_COOLDOWN,ticks);
    }

    public static int getTeleportCooldown(Player player) {
        return getRoot(player).getInt(TELEPORT_COOLDOWN);
    }

    public static void tickTeleportCooldown(Player player) {
        int current=getTeleportCooldown(player);
        if(current > 0) {
            setTeleportCooldown(player,current - 1);
        }
    }

    public static void setVoidMarkActive(Player player, boolean value) {
        getRoot(player).putBoolean(VOID_MARK_ACTIVE,value);
    }

    public static void setVoidMarkTicks(Player player, int ticks) {
        getRoot(player).putInt(VOID_MARK_TICKS,ticks);
    }

    public static boolean isVoidMarkActive(Player player) {
        return getRoot(player).getBoolean(VOID_MARK_ACTIVE);
    }

    public static int getVoidMarkTicks(Player player) {
        return getRoot(player).getInt(VOID_MARK_TICKS);
    }

    public static void setVoidMarkRecovering(Player player, boolean value) {
        getRoot(player).putBoolean(VOID_MARK_RECOVERING,value);
    }

    public static void setVoidMarkInstantNext(Player player, boolean value) {
        getRoot(player).putBoolean(VOID_MARK_INSTANT_NEXT,value);
    }

    public static void setVoidPunishmentCount(Player player, int value) {
        getRoot(player).putInt(VOID_PUNISHMENT_COUNT,value);
    }

    public static boolean isVoidMarkRecovering(Player player) {
        return getRoot(player).getBoolean(VOID_MARK_RECOVERING);
    }

    public static boolean isVoidMarkInstantNext(Player player) {
        return getRoot(player).getBoolean(VOID_MARK_INSTANT_NEXT);
    }

    public static int getVoidPunishmentCount(Player player) {
        return getRoot(player).getInt(VOID_PUNISHMENT_COUNT);
    }

    public static void clearVoidMarkState(Player player) {
        CompoundTag compoundTag=getRoot(player);
        compoundTag.putBoolean(VOID_MARK_ACTIVE,false);
        compoundTag.putInt(VOID_MARK_TICKS,0);
        compoundTag.putBoolean(VOID_MARK_RECOVERING,false);
        compoundTag.putBoolean(VOID_MARK_INSTANT_NEXT,false);
    }

    public static void setPunishments(Player player, BorderPunishment punishment) {
        getPunishmentTag(player).putBoolean(punishment.getString(),true);
    }

    public static boolean isPunishments(Player player, BorderPunishment punishment) {
        return getPunishmentTag(player).getBoolean(punishment.getString());
    }

    public static void setLastSafePos(Player player, double x, double y, double z, String dims) {
        CompoundTag compoundTag=getRoot(player);
        compoundTag.putBoolean(LAST_SAFE,true);
        compoundTag.putDouble(LAST_SAFE_X,x);
        compoundTag.putDouble(LAST_SAFE_Y,y);
        compoundTag.putDouble(LAST_SAFE_Z,z);
        compoundTag.putString(LAST_SAFE_DIMS,dims);
    }

    public static boolean isLastSafePos(Player player) {
        return getRoot(player).getBoolean(LAST_SAFE);
    }

    public static double getLastSafeX(Player player) {
        return getRoot(player).getDouble(LAST_SAFE_X);
    }

    public static double getLastSafeY(Player player) {
        return getRoot(player).getDouble(LAST_SAFE_Y);
    }

    public static double getLastSafeZ(Player player) {
        return getRoot(player).getDouble(LAST_SAFE_Z);
    }

    public static String getLastSafeDims(Player player) {
        return getRoot(player).getString(LAST_SAFE_DIMS);
    }


}
