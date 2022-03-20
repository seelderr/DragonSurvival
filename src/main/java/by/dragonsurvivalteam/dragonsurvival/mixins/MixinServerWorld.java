package by.dragonsurvivalteam.dragonsurvival.mixins;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.server.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin( ServerWorld.class )
public interface MixinServerWorld{
	@Accessor( "allPlayersSleeping" )
	boolean getallPlayersSleeping();

	@Accessor( "allPlayersSleeping" )
	void setallPlayersSleeping(boolean allPlayersSleeping);

	@Accessor( "Players" )
	List<ServerPlayerEntity> getPlayers();
}