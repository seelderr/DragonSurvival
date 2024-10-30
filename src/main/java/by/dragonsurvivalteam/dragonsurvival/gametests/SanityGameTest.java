package by.dragonsurvivalteam.dragonsurvival.gametests;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvival;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.capability.subcapabilities.ClawInventory;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.DragonBodies;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.DragonTypes;
import by.dragonsurvivalteam.dragonsurvival.common.handlers.DragonFoodHandler;
import by.dragonsurvivalteam.dragonsurvival.config.ConfigHandler;
import by.dragonsurvivalteam.dragonsurvival.config.ConfigUtils;
import by.dragonsurvivalteam.dragonsurvival.config.types.FoodConfigCollector;
import by.dragonsurvivalteam.dragonsurvival.util.DragonLevel;
import by.dragonsurvivalteam.dragonsurvival.util.DragonUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import java.lang.reflect.Field;
import java.util.List;

@GameTestHolder(DragonSurvival.MODID)
public class SanityGameTest {
    @PrefixGameTestTemplate(false)
    @GameTest(template = "test_templates/1x1air")
    public static void sanityGameTest(GameTestHelper helper) {
        helper.setBlock(BlockPos.ZERO, Blocks.DIAMOND_BLOCK);
        helper.assertBlock(BlockPos.ZERO, block -> block == Blocks.DIAMOND_BLOCK, "Block should be diamond block!");
        helper.succeed();
    }

    @PrefixGameTestTemplate(false)
    @GameTest(template = "test_templates/1x1air")
    public static void test(final GameTestHelper helper) {
        // Setup
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        helper.assertTrue(helper.getLevel().addFreshEntity(player), "Failed adding the player to the level");

        helper.setBlock(BlockPos.ZERO, Blocks.IRON_ORE);
        helper.assertBlock(BlockPos.ZERO, block -> block == Blocks.IRON_ORE, "Failed setting up the block");
        BlockState state = helper.getBlockState(BlockPos.ZERO);

        // Check if setting the dragon type and dragon body works correctly
        DragonStateHandler data = DragonStateProvider.getData(player);
        data.setType(DragonTypes.CAVE, player);
        data.setBody(DragonBodies.CENTER, player);
        helper.assertTrue(data.isDragon(), "Player is not a dragon - expected player to be a dragon");
        helper.assertTrue(DragonUtils.isDragonType(player, DragonTypes.CAVE), "Player has [" + data.getType() + "] dragon type - expected [CAVE]");
        helper.assertTrue(DragonUtils.isBodyType(data.getBody(), DragonBodies.CENTER), "Player has [" + data.getBody() + "] body type - expected [CENTER]");

        // Check if setting food config values works correctly
        checkAndSetConfig(helper, "caveDragonFoods", List.of(FoodConfigCollector.of(ConfigUtils.location(ItemTags.COALS), 5, 3)));
        List<Item> foods = DragonFoodHandler.getEdibleFoods(DragonTypes.CAVE);
        helper.assertTrue(foods.size() == 2, "[CAVE] dragon food list has [" + foods.size() + "] entries - expected [2]");
        helper.assertTrue(foods.contains(Items.COAL) && foods.contains(Items.CHARCOAL), "Food items were [" + foods + "] - expected [Coal] and [Charcoal]");
        FoodProperties properties = DragonFoodHandler.getDragonFoodProperties(Items.COAL, DragonTypes.CAVE);
        //noinspection DataFlowIssue -> ignore
        helper.assertTrue(properties.nutrition() == 5, "[Coal] has a nutrition value of [" + properties.nutrition() + "] - expected [5]");
        helper.assertTrue(properties.saturation() == 3, "[Coal] has a saturation value of [" + properties.saturation() + "] - expected [3]");

        // Check the break speed for a human player with a pickaxe
        data.setType(null);
        helper.assertTrue(!data.isDragon(), "Player is a dragon - expected player to not be a dragon");
        player.setItemInHand(InteractionHand.MAIN_HAND, Items.STONE_PICKAXE.getDefaultInstance());
        float breakSpeed = player.getDigSpeed(state, BlockPos.ZERO);
        helper.assertTrue(breakSpeed == 0.8f, "Human block break speed with [" + player.getItemInHand(InteractionHand.MAIN_HAND) + "] for [" + state.getBlock() + "] was [" + breakSpeed + "] - expected [0.8]");

        // Check the break speed for dragons holding a pickaxe
        data.setType(DragonTypes.CAVE, player);
        data.setSize(DragonLevel.NEWBORN.size);
        DragonLevel dragonLevel = data.getLevel();
        helper.assertTrue(dragonLevel == DragonLevel.NEWBORN, "Dragon size was [" + dragonLevel + "] - expected [NEWBORN]");
        breakSpeed = player.getDigSpeed(state, BlockPos.ZERO);
        helper.assertTrue(breakSpeed == 0.8f, "Newborn dragon block break speed with [" + player.getItemInHand(InteractionHand.MAIN_HAND) + "] for [" + state.getBlock() + "] was [" + breakSpeed + "] - expected [0.8]");
        player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);

        // Set the pickaxe slot in the claw inventory and then empty it again
        data.getClawToolData().set(ClawInventory.Slot.PICKAXE, Items.STONE_PICKAXE.getDefaultInstance());
        ItemStack pickaxe = data.getClawToolData().getPickaxe();
        helper.assertTrue(pickaxe.is(Items.STONE_PICKAXE), "Pickaxe in claw tool inventory was [" + pickaxe + "] - expected [Stone Pickaxe]");
        // TODO :: cannot properly test the break speed since it's only applied when actually mining a block through the tool swap logic
        //  and manually calling e.g. 'ServerPlayerGameMode#handleBlockBreakAction' cannot be evaluated in tests like this
//        breakSpeed = player.getDigSpeed(state, BlockPos.ZERO);
        data.getClawToolData().set(ClawInventory.Slot.PICKAXE, ItemStack.EMPTY);
        pickaxe = data.getClawToolData().getPickaxe();
        helper.assertTrue(pickaxe.isEmpty(), "Pickaxe in claw tool inventory was [" + pickaxe + "] - expected [EMPTY]");

        // Test the harvest level bonus for dragons
        checkAndSetConfig(helper, "bonusUnlockedAt", DragonLevel.YOUNG);
        checkAndSetConfig(helper, "bonusHarvestLevel", 1);
        data.setSize(DragonLevel.YOUNG.size);
        helper.assertTrue(state.canHarvestBlock(helper.getLevel(), BlockPos.ZERO, player), "Young dragon with unlocked bonus harvest level of [1] cannot harvest [" + state.getBlock() + "] - expected block to be harvestable");

        checkAndSetConfig(helper, "bonusUnlockedAt", DragonLevel.ADULT);
        helper.assertTrue(!state.canHarvestBlock(helper.getLevel(), BlockPos.ZERO, player), "Young dragon without unlocked bonus harvest level of [1] can harvest [" + state.getBlock() + "] - expected block to not be harvestable");

        checkAndSetConfig(helper, "bonusUnlockedAt", DragonLevel.YOUNG);
        checkAndSetConfig(helper, "bonusHarvestLevel", 0);
        helper.assertTrue(!state.canHarvestBlock(helper.getLevel(), BlockPos.ZERO, player), "Young dragon with unlocked bonus harvest level of [0] can harvest [" + state.getBlock() + "] - expected block to not be harvestable");

        // Test the break speed bonus for dragons
        checkAndSetConfig(helper, "bonusBreakSpeed", 1f);
        breakSpeed = player.getDigSpeed(state, BlockPos.ZERO);
        helper.assertTrue(breakSpeed == 0.2f, "Dragon break speed with a bonus of [0] for [" + state.getBlock() + "] was [" + breakSpeed + "] - expected [0.2]");

        checkAndSetConfig(helper, "bonusBreakSpeed", 10f);
        breakSpeed = player.getDigSpeed(state, BlockPos.ZERO);
        helper.assertTrue(breakSpeed == 2, "Dragon break speed with a bonus of [10] for [" + state.getBlock() + "] was [" + breakSpeed + "] - expected [2]");

        data.setSize(DragonLevel.ADULT.size);
        checkAndSetConfig(helper, "bonusBreakSpeed", 1f);
        checkAndSetConfig(helper, "baseBreakSpeedAdult", 1f);
        checkAndSetConfig(helper, "bonusBreakSpeedAdult", 1f);
        breakSpeed = player.getDigSpeed(state, BlockPos.ZERO);
        helper.assertTrue(breakSpeed == 0.2f, "Adult dragon break speed with a bonus of [0] (base) and [0] (bonus) for [" + state.getBlock() + "] was [" + breakSpeed + "] - expected [0.2]");

        checkAndSetConfig(helper, "baseBreakSpeedAdult", 1.5f);
        breakSpeed = player.getDigSpeed(state, BlockPos.ZERO);
        helper.assertTrue(breakSpeed == 0.3f, "Adult dragon break speed with a bonus of [1.5] (base) and [0] (bonus) for [" + state.getBlock() + "] was [" + breakSpeed + "] - expected [0.3]");

        checkAndSetConfig(helper, "bonusBreakSpeedAdult", 2.5f);
        breakSpeed = player.getDigSpeed(state, BlockPos.ZERO);
        helper.assertTrue(breakSpeed == 0.5f, "Adult dragon break speed with a bonus of [1.5] (base) and [2.5] (bonus) for [" + state.getBlock() + "] was [" + breakSpeed + "] - expected [0.5]");

        ConfigHandler.updateConfigValue("bonusBreakSpeed", 10f);
        breakSpeed = player.getDigSpeed(state, BlockPos.ZERO);
        helper.assertTrue(breakSpeed == 0.5f, "Adult dragon break speed with a bonus of [1.5] (base), [2.5] (bonus) and changed non-adult bonus for [" + state.getBlock() + "] was [" + breakSpeed + "] - expected [0.5]");
        // TODO :: is 0.5f correct? seems a bit low, 0.8 is human value with a stone pickaxe - either check if code works correctly or buff values a bit?


        // TODO
        CompoundTag tag = data.serializeNBT(helper.getLevel().registryAccess());

        helper.succeed();
    }

    private static void checkAndSetConfig(final GameTestHelper helper, final String configKey, final Object value) {
        ConfigHandler.updateConfigValue(configKey, value);
        Field field = ConfigHandler.getField(configKey);

        try {
            Object newValue = field.get(null);
            helper.assertTrue(newValue == value, "The new value [" + newValue + "] did not match the old value [" + value + "] after updating the config");
        } catch (IllegalAccessException exception) {
            helper.fail("Failed trying to access a field for the config [" + configKey + "] to validate the config: [" + exception.getMessage() + "]");
        }
    }
}
