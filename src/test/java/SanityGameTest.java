import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.gametest.GameTestHolder;

@GameTestHolder(DragonSurvivalMod.MODID)
public class SanityGameTest {
    @GameTest
    public static void sanityGameTest(GameTestHelper helper) {
        helper.setBlock(new BlockPos(0, 0, 0), Blocks.DIAMOND_BLOCK);
        helper.assertBlock(new BlockPos(0, 0, 0), block -> block == Blocks.DIAMOND_BLOCK, "Block should be diamond block!");
    }
}
