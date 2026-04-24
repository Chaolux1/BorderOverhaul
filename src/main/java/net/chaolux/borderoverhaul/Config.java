package net.chaolux.borderoverhaul;

import net.chaolux.borderoverhaul.common.border.PlayerBorderMode;
import net.chaolux.borderoverhaul.common.event.BorderEvent;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

import java.util.List;

@Mod.EventBusSubscriber(modid = BorderOverhaul.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public enum BorderShape {
        SQUARE,CIRCLE,OVAL
    }

    // Border
    public static final ForgeConfigSpec.IntValue OVERWORLD_BORDER;
    public static final ForgeConfigSpec.IntValue NETHER_BORDER;
    public static final ForgeConfigSpec.IntValue END_BORDER;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> CUSTOM_BORDER_LIST;
    public static final ForgeConfigSpec.EnumValue<BorderShape> BORDER_SHAPE;
    public static final ForgeConfigSpec.DoubleValue OVAL_Z_RATIO;

    // Knockback force
    public static final ForgeConfigSpec.DoubleValue KNOCKBACK_FORCE;

    // Experimental knockback
    public static final ForgeConfigSpec.BooleanValue ENABLE_BOAT_KNOCKBACK;
    public static final ForgeConfigSpec.BooleanValue ENABLE_MINECART_KNOCKBACK;
    public static final ForgeConfigSpec.BooleanValue ENABLE_EXPLOSIVE_KNOCKBACK;
    public static final ForgeConfigSpec.BooleanValue ENABLE_VILLAGER_KNOCKBACK;
    public static final ForgeConfigSpec.BooleanValue ENABLE_ANIMAL_KNOCKBACK;
    public static final ForgeConfigSpec.BooleanValue ENABLE_MOB_KNOCKBACK;
    public static final ForgeConfigSpec.BooleanValue ENABLE_MOD_ENTITY_KNOCKBACK;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> MOD_ENTITY_KNOCKBACK_LIST;
    public static final ForgeConfigSpec.BooleanValue ENABLE_MOD_ENTITY_CLAMP;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> MOD_ENTITY_CLAMP_LIST;
    public static final ForgeConfigSpec.DoubleValue MOD_ENTITY_CLAMP_OFFSET;

    // Effect radius
    public static final ForgeConfigSpec.IntValue EFFECT_1_THRESHOLD;
    public static final ForgeConfigSpec.IntValue EFFECT_2_THRESHOLD;
    public static final ForgeConfigSpec.IntValue EFFECT_3_THRESHOLD;
    public static final ForgeConfigSpec.IntValue EFFECT_4_THRESHOLD;

    public static final ForgeConfigSpec.EnumValue<PlayerBorderMode> PLAYER_BORDER_MODE;

    public static final ForgeConfigSpec.DoubleValue OPPOSITE_TELEPORT_OFFSET;
    public static final ForgeConfigSpec.IntValue OPPOSITE_TELEPORT_SEARCH_RADIUS;
    public static final ForgeConfigSpec.IntValue OPPOSITE_TELEPORT_COOLDOWN_TICKS;
    public static final ForgeConfigSpec.IntValue DEATH_TIMER_SECONDS;
    public static final ForgeConfigSpec.ConfigValue<String> SKY_PENALTY_DIMS;
    public static final ForgeConfigSpec.DoubleValue SKY_PENALTY_X;
    public static final ForgeConfigSpec.DoubleValue SKY_PENALTY_Y;
    public static final ForgeConfigSpec.DoubleValue SKY_PENALTY_Z;
    public static final ForgeConfigSpec.IntValue SKY_PENALTY_SLOW_FALLING_SECONDS;
    public static final ForgeConfigSpec.IntValue SKY_PENALTY_COOLDOWN_TICKS;

    public static final ForgeConfigSpec.IntValue VOID_MARK_SECONDS;
    public static final ForgeConfigSpec.IntValue VOID_MARK_RECOVERY_SECONDS;
    public static final ForgeConfigSpec.IntValue VOID_MARK_MAX_PUNISHMENTS;
    public static final ForgeConfigSpec.BooleanValue PUNISHMENT_NO_SLEEP;
    public static final ForgeConfigSpec.BooleanValue PUNISHMENT_NO_MENDING;
    public static final ForgeConfigSpec.BooleanValue PUNISHMENT_NO_ELYTRA_FIREWORKS;
    public static final ForgeConfigSpec.BooleanValue PUNISHMENT_NO_EATING_WHILE_MOVING;
    public static final ForgeConfigSpec.BooleanValue PUNISHMENT_NO_SOUL_SPEED;
    public static final ForgeConfigSpec.BooleanValue PUNISHMENT_AXE_BREAKS_SHIELD;
    public static final ForgeConfigSpec.BooleanValue PUNISHMENT_NO_FISHING_TREASURE;
    public static final ForgeConfigSpec.BooleanValue PUNISHMENT_NO_BEACON_BUFFS;
    public static final ForgeConfigSpec.BooleanValue PUNISHMENT_NO_VILLAGER_DISCOUNTS;
    public static final ForgeConfigSpec.BooleanValue PUNISHMENT_BOW_BLINDNESS;
    public static final ForgeConfigSpec.DoubleValue PUNISHMENT_NO_EAT_MOVEMENT_THRESHOLD;

    static {
        BUILDER.push("Borders");

        OVERWORLD_BORDER = BUILDER
                .comment("Border size for the Overworld (default: 1000)")
                .defineInRange("overworld", 1000, 100, 30000000);

        NETHER_BORDER = BUILDER
                .comment("Border size for the Nether (default: 100000)")
                .defineInRange("nether", 100000, 100, 30000000);

        END_BORDER = BUILDER
                .comment("Border size for the End (default: 100000)")
                .defineInRange("end", 100000, 100, 30000000);

        CUSTOM_BORDER_LIST=BUILDER.comment("Custom dimension border in format 'modid:name_dimension=value'").defineListAllowEmpty(List.of("custom_border_list"),List.of(),object -> object instanceof String && ((String) object).contains("="));

        BUILDER.pop();

        BUILDER.push("Knockback");

        KNOCKBACK_FORCE = BUILDER
                .comment("Knockback force applied to entities outside the border (default: 0.7)")
                .defineInRange("knockback_force", 0.7, 0.0, 10.0);


        BUILDER.pop();

        BUILDER.push("Experimental Entity Knockback");

        BUILDER.comment("These knockback settings are experimental and may cause bugs or unexpected behavior. Use at your own risk.");

        ENABLE_BOAT_KNOCKBACK = BUILDER
                .comment("Enable knockback for boats (default: false)")
                .define("enable_boat_knockback", false);

        ENABLE_MINECART_KNOCKBACK = BUILDER
                .comment("Enable knockback for minecarts (default: false)")
                .define("enable_minecart_knockback", false);

        ENABLE_EXPLOSIVE_KNOCKBACK = BUILDER
                .comment("Enable knockback for explosive entities (TNT, End Crystals) (default: false)")
                .define("enable_explosive_knockback", false);

        ENABLE_VILLAGER_KNOCKBACK = BUILDER
                .comment("Enable knockback for villagers (default: false)")
                .define("enable_villager_knockback", false);

        ENABLE_ANIMAL_KNOCKBACK = BUILDER
                .comment("Enable knockback for animals (default: false)")
                .define("enable_animal_knockback", false);

        ENABLE_MOB_KNOCKBACK = BUILDER
                .comment("Enable knockback for mobs (default: false)")
                .define("enable_mob_knockback", false);

        ENABLE_MOD_ENTITY_KNOCKBACK = BUILDER
                .comment("Enable knockback for custom mod entities from the list below (default: false)")
                .define("enable_mod_entity_knockback", false);

        MOD_ENTITY_KNOCKBACK_LIST=BUILDER
                .comment("List of entity ids that should be pushed back by the border","Example: modid:entity_id")
                        .defineListAllowEmpty(List.of("mod_entity_knockback_list"),List.of(),object -> object instanceof String);

        ENABLE_MOD_ENTITY_CLAMP=BUILDER
                .comment("Enable hard clamp for custom mod entities from the list below (default: false)")
                        .define("enable_mod_entity_clamp",false);

        MOD_ENTITY_CLAMP_LIST=BUILDER
                .comment("List of entity ids that should be clamped inside the border","Example: modid:entity_id")
                .defineListAllowEmpty(List.of("mod_entity_clamp_list"),List.of(),object -> object instanceof String);

        MOD_ENTITY_CLAMP_OFFSET=BUILDER
                .comment("How many blocks inside the border clamped mod entities should be placed")
                        .defineInRange("mod_entity_clamp_offset", 1.0,0.1,64.0);

        BUILDER.pop();

        BUILDER.push("Experimental Shape Border");

        BORDER_SHAPE=BUILDER.comment("Shape of a border: SQUARE, CIRCLE, OVAL").defineEnum("shape",BorderShape.SQUARE);

        OVAL_Z_RATIO=BUILDER.comment("Z-axis stretch ratio for oval shape(only applies if shape=OVAL)").defineInRange("oval_z_ratio",0.6,0.1,10.0);

        PLAYER_BORDER_MODE=BUILDER.comment("Player border mode").defineEnum("player_border_mode",PlayerBorderMode.PUSHBACK_EFFECT);

        BUILDER.pop();

        BUILDER.push("Effect thresholds (in blocks beyond the border)");

        EFFECT_1_THRESHOLD = BUILDER
                .comment("Distance for tier 1 effects: BLINDNESS, DIG_SLOWDOWN")
                .defineInRange("tier1", 5, 1, 10000);

        EFFECT_2_THRESHOLD = BUILDER
                .comment("Distance for tier 2 effects: POISON")
                .defineInRange("tier2", 20, 1, 10000);

        EFFECT_3_THRESHOLD = BUILDER
                .comment("Distance for tier 3 effects: WITHER")
                .defineInRange("tier3", 50, 1, 10000);

        EFFECT_4_THRESHOLD = BUILDER
                .comment("Distance for tier 4 effects: DARKNESS")
                .defineInRange("tier4", 100, 1, 10000);

        BUILDER.pop();

        BUILDER.push("Opposite Side Teleport");

        OPPOSITE_TELEPORT_OFFSET=BUILDER
                .comment("How many blocks inside border the target point shold be")
                .defineInRange("opposite_teleport_offset",2.0,0.1,64.0);

        OPPOSITE_TELEPORT_SEARCH_RADIUS=BUILDER
                .comment("Safe pos search radius around the target")
                .defineInRange("opposite_teleport_search_radius",8,0,64);

        OPPOSITE_TELEPORT_COOLDOWN_TICKS=BUILDER
                .comment("Cooldown to prevent teleport spam")
                .defineInRange("opposite_teleport_cooldown_ticks",20,0,200);

        BUILDER.pop();

        BUILDER.push("Death Timer");

        DEATH_TIMER_SECONDS=BUILDER
                .comment("Seconds until death in death timer mode")
                .defineInRange("death_timer_seconds",30,1,3600);

        BUILDER.pop();

        BUILDER.push("Sky Penalty");

        SKY_PENALTY_DIMS=BUILDER
                .comment("Target dimension id for sky penalty")
                .define("sky_penalty_dims","minecraft:overworld");

        SKY_PENALTY_X=BUILDER
                .defineInRange("sky_penalty_x",0.0,-30000000.0,30000000.0);

        SKY_PENALTY_Y=BUILDER
                .defineInRange("sky_penalty_y",330.0,-64.0,1024.0);

        SKY_PENALTY_Z=BUILDER
                .defineInRange("sky_penalty_z",0.0,-30000000.0,30000000.0);

        SKY_PENALTY_SLOW_FALLING_SECONDS=BUILDER
                .comment("Slow falling duration after sky penalty teleport")
                .defineInRange("sky_penalty_slow_falling_seconds",60,1,3600);

        SKY_PENALTY_COOLDOWN_TICKS=BUILDER
                .comment("Cooldown for sky penalty mode")
                .defineInRange("sky_penalty_cooldown_ticks",20,0,200);

        BUILDER.pop();

        BUILDER.push("Void Mark");

        VOID_MARK_SECONDS=BUILDER
                .comment("Void Mark countdown seconds while outside")
                .defineInRange("void_mark_seconds",30,1,3600);

        VOID_MARK_RECOVERY_SECONDS=BUILDER
                .comment("Void Mark recovery target seconds while back inside")
                .defineInRange("void_mark_recovery_seconds",30,1,3600);

        VOID_MARK_MAX_PUNISHMENTS=BUILDER
                .comment("Maximum number of punishments player can receive")
                .defineInRange("void_mark_max_punishments",10,1,10);

        PUNISHMENT_NO_SLEEP=BUILDER
                .define("punishment_no_sleep",true);

        PUNISHMENT_NO_MENDING=BUILDER
                .define("punishment_no_mending",true);

        PUNISHMENT_NO_ELYTRA_FIREWORKS=BUILDER
                .define("punishment_no_elytra_fireworks",true);

        PUNISHMENT_NO_EATING_WHILE_MOVING=BUILDER
                .define("punishment_no_eating_while_moving",true);

        PUNISHMENT_NO_SOUL_SPEED=BUILDER
                .define("punishment_no_soul_speed",true);

        PUNISHMENT_AXE_BREAKS_SHIELD=BUILDER
                .define("punishment_axe_breaks_shield",true);

        PUNISHMENT_NO_FISHING_TREASURE=BUILDER
                .define("punishment_no_fishing_treasure",true);

        PUNISHMENT_NO_BEACON_BUFFS=BUILDER
                .define("punishment_no_beacon_buffs",true);

        PUNISHMENT_NO_VILLAGER_DISCOUNTS=BUILDER
                .define("punishment_no_villager_discounts",true);

        PUNISHMENT_BOW_BLINDNESS=BUILDER
                .define("punishment_bow_blindness",true);

        PUNISHMENT_NO_EAT_MOVEMENT_THRESHOLD=BUILDER
                .comment("Movement threshold for no-eating punishment")
                .defineInRange("punishment_no_eat_movement_threshold",0.01,0.1,2.0);

        BUILDER.pop();

    }

    public static final ForgeConfigSpec SPEC = BUILDER.build();

    @SubscribeEvent
    public static void onLoad(final ModConfigEvent event) {
        BorderEvent.reloadConfig();
    }

    @SubscribeEvent
    public static void onReload(final ModConfigEvent.Reloading event) {
        BorderEvent.reloadConfig();
    }
}