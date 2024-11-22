package by.dragonsurvivalteam.dragonsurvival.gametests;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvival;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.DragonTypes;
import by.dragonsurvivalteam.dragonsurvival.config.ConfigHandler;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigSide;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.DragonBodies;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.DragonLevels;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.BeforeBatch;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@PrefixGameTestTemplate(false)
@GameTestHolder(DragonSurvival.MODID)
public class DragonBonusTests {
    @BeforeBatch(batch = "dragon_bonus_tests")
    public static void beforeTest(final ServerLevel level) {
        ConfigHandler.resetConfigValues(ConfigSide.SERVER);
    }

    @GameTest(template = TestUtils.AIR_CUBE_3X, batch = "dragon_bonus_tests")
    public static void test_break_speed_and_harvest_level_bonus(final GameTestHelper helper) {
        Player player = TestUtils.createPlayer(helper, GameType.DEFAULT_MODE);
        TestUtils.setToDragon(helper, player, DragonTypes.CAVE, DragonBodies.center, DragonLevels.newborn);

        BlockState state = TestUtils.setBlock(helper, Blocks.IRON_ORE);
        BlockPos position = helper.absolutePos(BlockPos.ZERO);
        player.setOnGround(true);

        // Set a level that has no harvest level bonus
        boolean canHarvest = player.hasCorrectToolForDrops(state, helper.getLevel(), position);
        helper.assertTrue(!canHarvest, String.format("[%s] can be harvested - expected block to not be harvestable", state));

        // Set a level that has a harvest level bonus
        TestUtils.setToDragon(helper, player, DragonTypes.CAVE, DragonBodies.center, DragonLevels.young);
        canHarvest = player.hasCorrectToolForDrops(state, helper.getLevel(), position);
        helper.assertTrue(canHarvest, String.format("[%s] cannot be harvested - expected block to be harvestable", state));

        // Check that the base harvest level bonus does not allow the harvesting of blocks not within its tier
        state = TestUtils.setBlock(helper, Blocks.ANCIENT_DEBRIS);
        canHarvest = player.hasCorrectToolForDrops(state, helper.getLevel(), position);
        helper.assertTrue(!canHarvest, String.format("[%s] can be harvested - expected block to not be harvestable", state));

        helper.succeed();
    }
}
