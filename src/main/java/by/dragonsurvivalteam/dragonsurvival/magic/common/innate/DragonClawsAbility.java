//package by.dragonsurvivalteam.dragonsurvival.magic.common.innate;
//
//import by.dragonsurvivalteam.dragonsurvival.DragonSurvival;
//import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
//import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
//import by.dragonsurvivalteam.dragonsurvival.magic.abilities.CaveDragon.innate.CaveClawAbility;
//import by.dragonsurvivalteam.dragonsurvival.magic.abilities.ForestDragon.innate.ForestClawAbility;
//import by.dragonsurvivalteam.dragonsurvival.magic.abilities.SeaDragon.innate.SeaClawAbility;
//import by.dragonsurvivalteam.dragonsurvival.registry.datagen.Translation;
//import net.minecraft.network.chat.Component;
//import net.minecraft.world.entity.ai.attributes.Attributes;
//import net.minecraft.world.entity.player.Player;
//import org.jetbrains.annotations.Nullable;
//
//import java.util.ArrayList;
//import java.util.Locale;
//import java.util.Objects;
//
//public abstract class DragonClawsAbility extends InnateDragonAbility {
//    @Translation(type = Translation.Type.MISC, comments = "Pickaxe")
//    private static final String CAVE_TOOL = Translation.Type.ABILITY_DESCRIPTION.wrap("claw.cave_tool");
//
//    @Translation(type = Translation.Type.MISC, comments = "Shovel")
//    private static final String SEA_TOOL = Translation.Type.ABILITY_DESCRIPTION.wrap("claw.sea_tool");
//
//    @Translation(type = Translation.Type.MISC, comments = "Axe")
//    private static final String FOREST_TOOL = Translation.Type.ABILITY_DESCRIPTION.wrap("claw.forest_tool");
//
//    @Translation(type = Translation.Type.MISC, comments = "§d■ Claws type:§r %s")
//    private static final String TOOL_TYPE = Translation.Type.ABILITY_DESCRIPTION.wrap("claw.tool_type");
//
//    @Translation(type = Translation.Type.MISC, comments = "§d■ Level:§r %s")
//    private static final String HARVEST_LEVEL = Translation.Type.ABILITY_DESCRIPTION.wrap("claw.harvest_level");
//
//    @Translation(type = Translation.Type.MISC, comments = "§d■ Claw damage bonus:§r %s")
//    private static final String DAMAGE = Translation.Type.ABILITY_DESCRIPTION.wrap("claw.damage");
//
//    @Override
//    public int getMaxLevel() {
//        return 1;
//    }
//
//    @Override
//    public int getMinLevel() {
//        return 1;
//    }
//
//    @Override
//    public ArrayList<Component> getInfo() {
//        DragonStateHandler handler = DragonStateProvider.getData(Objects.requireNonNull(DragonSurvival.PROXY.getLocalPlayer()));
//        ArrayList<Component> components = super.getInfo();
//
//        Component tool = switch (this) {
//            case CaveClawAbility ignored -> Component.translatable(CAVE_TOOL);
//            case SeaClawAbility ignored -> Component.translatable(SEA_TOOL);
//            case ForestClawAbility ignored -> Component.translatable(FOREST_TOOL);
//            default -> Component.empty();
//        };
//
//        components.add(Component.translatable(TOOL_TYPE, tool));
//        Tier tier = Tier.getByTier(getTier());
//
//        if (tier != null) {
//            components.add(Component.translatable(HARVEST_LEVEL, tier.translation()));
//        }
//
//        double damageBonus = Objects.requireNonNull(handler.getStage()).value().getAttributeValue(handler.getTypeNameLowerCase(), handler.getSize(), Attributes.ATTACK_DAMAGE);
//        damageBonus -= Objects.requireNonNull(DragonSurvival.PROXY.getLocalPlayer().getAttribute(Attributes.ATTACK_DAMAGE)).getBaseValue();
//
//        if (damageBonus > 0) {
//            components.add(Component.translatable(DAMAGE, "+" + damageBonus));
//        }
//
//        return components;
//    }
//
//    @Override
//    public int getLevel() {
//        return getTier();
//    }
//
//    public int getTier() {
//        Player player = Objects.requireNonNull(DragonSurvival.PROXY.getLocalPlayer());
//        DragonStateHandler handler = DragonStateProvider.getData(player);
//
//        if (handler.getType() == null) {
//            return 0;
//        }
//
//        int level = handler.getDragonHarvestLevel(player, null);
//
//        if (/* Wood */ level == 0) {
//            return 1;
//        } else if (/* Stone */ level == 1) {
//            return 2;
//        } else if (/* Iron */ level == 2) {
//            return 3;
//        } else if (/* Diamond */ level == 4) {
//            return 5;
//        } else if (/* Netherite */ level > 4) {
//            return 6;
//        }
//
//        return 0;
//    }
//
//    /** Maps the texture id / level from {@link DragonClawsAbility#getTier()} to the related tier */
//    public enum Tier {
//        @Translation(type = Translation.Type.ABILITY_DESCRIPTION, comments = "Wood")
//        CLAW_WOOD(1),
//        @Translation(type = Translation.Type.ABILITY_DESCRIPTION, comments = "Stone")
//        CLAW_STONE(2),
//        @Translation(type = Translation.Type.ABILITY_DESCRIPTION, comments = "Iron")
//        CLAW_IRON(3),
//        @Translation(type = Translation.Type.ABILITY_DESCRIPTION, comments = "Gold")
//        CLAW_GOLD(4),
//        @Translation(type = Translation.Type.ABILITY_DESCRIPTION, comments = "Diamond")
//        CLAW_DIAMOND(5),
//        @Translation(type = Translation.Type.ABILITY_DESCRIPTION, comments = "Netherite")
//        CLAW_NETHERITE(6);
//
//        private final int tier;
//
//        Tier(int tier) {
//            this.tier = tier;
//        }
//
//        public Component translation() {
//            return Component.translatable(Translation.Type.ABILITY_DESCRIPTION.wrap(toString().toLowerCase(Locale.ENGLISH)));
//        }
//
//        public static @Nullable Tier getByTier(int tier) {
//            for (Tier value : values()) {
//                if (value.tier == tier) {
//                    return value;
//                }
//            }
//
//            return null;
//        }
//    }
//}