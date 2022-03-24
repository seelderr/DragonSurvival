package by.dragonsurvivalteam.dragonsurvival.mixins;

<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/mixins/MixinClientWorld.java
import by.jackraidenph.dragonsurvival.common.blocks.SourceOfMagicBlock;
import by.jackraidenph.dragonsurvival.server.tileentity.SourceOfMagicPlaceholder;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.WritableLevelData;
=======
import by.dragonsurvivalteam.dragonsurvival.common.blocks.SourceOfMagicBlock;
import by.dragonsurvivalteam.dragonsurvival.server.tileentity.SourceOfMagicPlaceholder;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.profiler.IProfiler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.storage.ISpawnWorldInfo;
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/mixins/MixinClientWorld.java
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/mixins/MixinClientWorld.java
@Mixin( ClientLevel.class )
public abstract class MixinClientWorld extends Level
{
	@Shadow
	@Final
	private LevelRenderer levelRenderer;
	
	protected MixinClientWorld(WritableLevelData pLevelData, ResourceKey<Level> pDimension, DimensionType pDimensionType, Supplier<ProfilerFiller> pProfiler, boolean pIsClientSide, boolean pIsDebug, long pBiomeZoomSeed)
	{
		super(pLevelData, pDimension, pDimensionType, pProfiler, pIsClientSide, pIsDebug, pBiomeZoomSeed);
	}
	
	
	@Inject( at = @At("HEAD"), method = "destroyBlockProgress", cancellable = true)
	public void destroyBlockProgress(int playerId, BlockPos pos, int progress, CallbackInfo ci) {
=======
@Mixin( ClientWorld.class )
public abstract class MixinClientWorld extends World{
	@Shadow
	@Final
	private WorldRenderer levelRenderer;

	protected MixinClientWorld(ISpawnWorldInfo p_i241925_1_, RegistryKey<World> p_i241925_2_, DimensionType p_i241925_3_, Supplier<IProfiler> p_i241925_4_, boolean p_i241925_5_, boolean p_i241925_6_, long p_i241925_7_){
		super(p_i241925_1_, p_i241925_2_, p_i241925_3_, p_i241925_4_, p_i241925_5_, p_i241925_6_, p_i241925_7_);
	}

	@Inject( at = @At( "HEAD" ), method = "destroyBlockProgress", cancellable = true )
	public void destroyBlockProgress(int playerId, BlockPos pos, int progress, CallbackInfo ci){
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/mixins/MixinClientWorld.java
		BlockState state = getBlockState(pos);

		if(state.getBlock() instanceof SourceOfMagicBlock){
			if(!state.getValue(SourceOfMagicBlock.PRIMARY_BLOCK)){
				BlockEntity blockEntity = getBlockEntity(pos);
				BlockPos pos1 = pos;

				if(blockEntity instanceof SourceOfMagicPlaceholder){
					pos1 = ((SourceOfMagicPlaceholder)blockEntity).rootPos;
				}

				this.levelRenderer.destroyBlockProgress(playerId, pos1, progress);
				ci.cancel();
			}
		}
	}
}