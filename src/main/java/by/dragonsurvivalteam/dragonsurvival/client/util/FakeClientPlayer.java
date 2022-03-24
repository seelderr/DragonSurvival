package by.jackraidenph.dragonsurvival.client.util;

import by.jackraidenph.dragonsurvival.common.capability.caps.DragonStateHandler;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.stats.Stat;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;
import java.util.function.Supplier;

@OnlyIn( Dist.CLIENT)
public class FakeClientPlayer extends AbstractClientPlayer
{
	public DragonStateHandler handler = new DragonStateHandler();
	public Supplier<String> animationSupplier = null;
	public Long lastAccessed;
	public int number;
	
	public FakeClientPlayer(int number)
	{
		super(Minecraft.getInstance().level, new GameProfile(UUID.randomUUID(), "FAKE_PLAYER_" + number));
		this.number = number;
	}
	
	private static final ResourceLocation STEVE_SKIN_LOCATION = new ResourceLocation("textures/entity/steve.png");
	private static final ResourceLocation ALEX_SKIN_LOCATION = new ResourceLocation("textures/entity/alex.png");
	
	@Override
	public ResourceLocation getSkinTextureLocation()
	{
		return number % 2 == 0 ? STEVE_SKIN_LOCATION : ALEX_SKIN_LOCATION;
	}
	
	@Override public Packet<?> getAddEntityPacket() {return null;}
	@Override public void onAddedToWorld() {}
	@Override public Vec3 position(){ return new Vec3(0, 0, 0); }
	@Override public BlockPos blockPosition(){ return BlockPos.ZERO; }
	@Override public void displayClientMessage(Component chatComponent, boolean actionBar){}
	@Override public void sendMessage(Component component, UUID senderUUID) {}
	@Override public void awardStat(Stat par1StatBase, int par2){}
	@Override public boolean isInvulnerableTo(DamageSource source){ return true; }
	@Override public boolean canHarmPlayer(Player player){ return false; }
	@Override public void die(DamageSource source){ return; }
	@Override public void tick(){ return; }
	@Override @Nullable public MinecraftServer getServer() { return Minecraft.getInstance().getSingleplayerServer(); }
	@Override public boolean shouldShowName()
	{
		return false;
	}
	@Override public Component getDisplayName()
	{
		return TextComponent.EMPTY;
	}
	@Nonnull @Override public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap) {return LazyOptional.empty();}
	@Override public void readAdditionalSaveData(CompoundTag pCompound) {}
	@Override public void addAdditionalSaveData(CompoundTag pCompound) {}
	@Override public boolean saveAsPassenger(CompoundTag pCompound) {return false;}
	@Override public boolean save(CompoundTag pCompound) {return false;}
}
