package by.dragonsurvivalteam.dragonsurvival.client.util;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.stats.Stat;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;
import java.util.function.Supplier;

@OnlyIn( Dist.CLIENT )
public class FakeClientPlayer extends AbstractClientPlayerEntity{
	private static final ResourceLocation STEVE_SKIN_LOCATION = new ResourceLocation("textures/entity/steve.png");
	private static final ResourceLocation ALEX_SKIN_LOCATION = new ResourceLocation("textures/entity/alex.png");
	public DragonStateHandler handler = new DragonStateHandler();
	public Supplier<String> animationSupplier = null;
	public Long lastAccessed;
	public int number;
	public FakeClientPlayer(int number){
		super(Minecraft.getInstance().level, new GameProfile(UUID.randomUUID(), "FAKE_PLAYER_" + number));
		this.number = number;
	}

	@Override
	public ResourceLocation getSkinTextureLocation(){
		return number % 2 == 0 ? STEVE_SKIN_LOCATION : ALEX_SKIN_LOCATION;
	}

	@Override
	public IPacket<?> getAddEntityPacket(){return null;}

	@Override
	public void tick(){return;}

	@Override
	public void die(DamageSource source){return;}

	@Override
	public void readAdditionalSaveData(CompoundNBT pCompound){}

	@Override
	public void addAdditionalSaveData(CompoundNBT pCompound){}

	@Override
	public boolean isInvulnerableTo(DamageSource source){return true;}

	@Override
	public boolean canHarmPlayer(PlayerEntity player){return false;}

	@Override
	public void displayClientMessage(ITextComponent chatComponent, boolean actionBar){}

	@Override
	public void awardStat(Stat par1StatBase, int par2){}

	@Override
	public boolean shouldShowName(){
		return false;
	}

	@Override
	public ITextComponent getDisplayName(){
		return StringTextComponent.EMPTY;
	}

	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(
		@Nonnull
			Capability<T> cap){return LazyOptional.empty();}

	@Override
	protected void reviveCaps(){}

	@Override
	public boolean saveAsPassenger(CompoundNBT pCompound){return false;}

	@Override
	public boolean save(CompoundNBT pCompound){return false;}

	@Override
	public void sendMessage(ITextComponent component, UUID senderUUID){}

	@Override
	@Nullable
	public MinecraftServer getServer(){return Minecraft.getInstance().getSingleplayerServer();}

	@Override
	public Vector3d position(){return new Vector3d(0, 0, 0);}

	@Override
	public BlockPos blockPosition(){return BlockPos.ZERO;}

	@Override
	public void onAddedToWorld(){}
}