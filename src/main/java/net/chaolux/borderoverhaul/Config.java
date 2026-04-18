package net.chaolux.borderoverhaul;

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
    public static ForgeConfigSpec.IntValue EFFECT_1_THRESHOLD;
    public static ForgeConfigSpec.IntValue EFFECT_2_THRESHOLD;
    public static ForgeConfigSpec.IntValue EFFECT_3_THRESHOLD;
    public static ForgeConfigSpec.IntValue EFFECT_4_THRESHOLD;

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