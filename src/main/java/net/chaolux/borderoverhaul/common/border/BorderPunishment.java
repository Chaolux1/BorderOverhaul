package net.chaolux.borderoverhaul.common.border;

import net.chaolux.borderoverhaul.Config;

public enum BorderPunishment {
    NO_SLEEP("no_sleep"),NO_MENDING("no_mending"),NO_ELYTRA_FIREWORKS("no_elytra_fireworks"),NO_EATING_WHILE_MOVING("no_eating_while_moving"),NO_SOUL_SPEED("no_soul_speed"),
    AXE_BREAKS_SHIELD("axe_breaks_shield"),NO_FISHING_TREASURE("no_fishing_treasure"),NO_BEACON_BUFFS("no_beacon_buffs"),NO_VILLAGER_DISCOUNTS("no_villager_discounts"),BOW_BLINDNESS("bow_blindness");
    private final String string;

    BorderPunishment(String string) {
        this.string=string;
    }

    public String getString() {
        return string;
    }

    public boolean isEnableInConfig() {
        return switch (this) {
            case NO_SLEEP -> Config.PUNISHMENT_NO_SLEEP.get();
            case NO_MENDING -> Config.PUNISHMENT_NO_MENDING.get();
            case NO_ELYTRA_FIREWORKS -> Config.PUNISHMENT_NO_ELYTRA_FIREWORKS.get();
            case NO_EATING_WHILE_MOVING -> Config.PUNISHMENT_NO_EATING_WHILE_MOVING.get();
            case NO_SOUL_SPEED -> Config.PUNISHMENT_NO_SOUL_SPEED.get();
            case AXE_BREAKS_SHIELD -> Config.PUNISHMENT_AXE_BREAKS_SHIELD.get();
            case NO_FISHING_TREASURE -> Config.PUNISHMENT_NO_FISHING_TREASURE.get();
            case NO_BEACON_BUFFS -> Config.PUNISHMENT_NO_BEACON_BUFFS.get();
            case NO_VILLAGER_DISCOUNTS -> Config.PUNISHMENT_NO_VILLAGER_DISCOUNTS.get();
            case BOW_BLINDNESS -> Config.PUNISHMENT_BOW_BLINDNESS.get();
        };
    }
}
