package by.dragonsurvivalteam.dragonsurvival.common.blocks;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class SkeletonPieceBlock extends Block implements SimpleWaterloggedBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    private final SkeletonPieceBlock.Type type;
    public static final MapCodec<SkeletonPieceBlock> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(SkeletonPieceBlock.Type.CODEC.fieldOf("type").forGetter(SkeletonPieceBlock::type), propertiesCodec())
                    .apply(instance, SkeletonPieceBlock::new));
    public SkeletonPieceBlock(SkeletonPieceBlock.Type type, Properties p_56319_) {
        super(p_56319_);
        this.type = type;
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, Boolean.FALSE));
    }

    public SkeletonPieceBlock.Type type() {
        return this.type;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite());
    }

    /*@Override
    protected @NotNull VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return Block.box(4.0, 0.0, 4.0, 12.0, 8.0, 12.0);
    }*/

    @Override
    protected @NotNull VoxelShape getOcclusionShape(@NotNull BlockState pState, @NotNull BlockGetter pLevel, @NotNull BlockPos pPos) {
        return Shapes.empty();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder);
        pBuilder.add(FACING, WATERLOGGED);
    }

    @Override
    protected @NotNull MapCodec<? extends Block> codec() { return CODEC; }

    public interface Type extends StringRepresentable {
        Map<String, SkeletonPieceBlock.Type> TYPES = new Object2ObjectArrayMap<>();
        Codec<SkeletonPieceBlock.Type> CODEC = Codec.stringResolver(StringRepresentable::getSerializedName, TYPES::get);
    }

    public enum Types implements SkeletonPieceBlock.Type {
        CHEST("skeleton_dragon_chest"),
        //FLIPPER_LEFT("skeleton_dragon_flipper_left"),
        //FLIPPER_RIGHT("skeleton_dragon_flipper_right"),
        FULL("skeleton_dragon_full"),
        LEG_1("skeleton_dragon_leg_1"),
        LEG_2("skeleton_dragon_leg_2"),
        LEG_3("skeleton_dragon_leg_3"),
        NECK_1("skeleton_dragon_neck_1"),
        NECK_2("skeleton_dragon_neck_2"),
        NECK_3("skeleton_dragon_neck_3"),
        PELVIS("skeleton_dragon_pelvis"),
        SKULL_1("skeleton_dragon_skull_1"),
        SKULL_2("skeleton_dragon_skull_2"),
        //SMALL_WING_LEFT("skeleton_dragon_small_wing_left"),
        //SMALL_WING_LEFT_STRAIGHT("skeleton_dragon_small_wing_left_straight"),
        //SMALL_WING_RIGHT("skeleton_dragon_small_wing_right"),
        //SMALL_WING_RIGHT_STRAIGHT("skeleton_dragon_small_wing_right_straight"),
        TAIL_1("skeleton_dragon_tail_1"),
        TAIL_2("skeleton_dragon_tail_2"),
        TAIL_3("skeleton_dragon_tail_3"),
        TAIL_4("skeleton_dragon_tail_4");
        //WING_LEFT("skeleton_dragon_wing_left"),
        //WING_LEFT_STRAIGHT("skeleton_dragon_wing_left_straight"),
        //WING_RIGHT("skeleton_dragon_wing_right"),
        //WING_RIGHT_STRAIGHT("skeleton_dragon_wing_right_straight");

        private final String name;

        Types(String pName) {
            this.name = pName;
            TYPES.put(pName, this);
        }

        @Override
        public @NotNull String getSerializedName() {
            return this.name;
        }
    }
}
