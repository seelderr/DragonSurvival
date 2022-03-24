package by.dragonsurvivalteam.dragonsurvival.network.magic;

<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/network/magic/SyncMagicAbilities.java
import by.jackraidenph.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.jackraidenph.dragonsurvival.common.magic.DragonAbilities;
import by.jackraidenph.dragonsurvival.common.magic.common.DragonAbility;
import by.jackraidenph.dragonsurvival.network.IMessage;
=======
import by.dragonsurvivalteam.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.magic.DragonAbilities;
import by.dragonsurvivalteam.dragonsurvival.common.magic.common.DragonAbility;
import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/network/magic/SyncMagicAbilities.java
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.DistExecutor.SafeRunnable;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.function.Supplier;

public class SyncMagicAbilities implements IMessage<SyncMagicAbilities>{
	public int playerId;
	public ArrayList<DragonAbility> abilities = new ArrayList<>();

	public SyncMagicAbilities(){}

	public SyncMagicAbilities(int playerId, ArrayList<DragonAbility> abilities){
		this.abilities = abilities;
		this.playerId = playerId;
	}

	@Override
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/network/magic/SyncMagicAbilities.java
	public void encode(SyncMagicAbilities message, FriendlyByteBuf buffer) {
		buffer.writeInt(message.playerId);
		
		CompoundTag tag = new CompoundTag();
=======
	public void encode(SyncMagicAbilities message, PacketBuffer buffer){
		buffer.writeInt(message.playerId);

		CompoundNBT tag = new CompoundNBT();
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/network/magic/SyncMagicAbilities.java
		for(DragonAbility ab : message.abilities){
			tag.put(ab.getId(), ab.saveNBT());
		}

		buffer.writeNbt(tag);
	}

	@Override
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/network/magic/SyncMagicAbilities.java
	public SyncMagicAbilities decode(FriendlyByteBuf buffer) {
=======
	public SyncMagicAbilities decode(PacketBuffer buffer){
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/network/magic/SyncMagicAbilities.java
		int playerId = buffer.readInt();

		ArrayList<DragonAbility> abilities = new ArrayList<>();
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/network/magic/SyncMagicAbilities.java
		CompoundTag tag = buffer.readNbt();
		
=======
		CompoundNBT tag = buffer.readNbt();

>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/network/magic/SyncMagicAbilities.java
		for(DragonAbility staticAbility : DragonAbilities.ABILITY_LOOKUP.values()){
			if(tag.contains(staticAbility.getId())){
				DragonAbility ability = staticAbility.createInstance();
				ability.loadNBT(tag.getCompound(staticAbility.getId()));
				abilities.add(ability);
			}
		}

		return new SyncMagicAbilities(playerId, abilities);
	}

	@Override
	public void handle(SyncMagicAbilities message, Supplier<NetworkEvent.Context> supplier){
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> (SafeRunnable)() -> run(message, supplier));
	}

	@OnlyIn( Dist.CLIENT )
	public void run(SyncMagicAbilities message, Supplier<NetworkEvent.Context> supplier){
		NetworkEvent.Context context = supplier.get();
		context.enqueueWork(() -> {
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/network/magic/SyncMagicAbilities.java
			Player thisPlayer = Minecraft.getInstance().player;
			if (thisPlayer != null) {
				Level world = thisPlayer.level;
				Entity entity = world.getEntity(message.playerId);
				if (entity instanceof Player) {
=======
			PlayerEntity thisPlayer = Minecraft.getInstance().player;
			if(thisPlayer != null){
				World world = thisPlayer.level;
				Entity entity = world.getEntity(message.playerId);
				if(entity instanceof PlayerEntity){
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/network/magic/SyncMagicAbilities.java
					DragonStateProvider.getCap(entity).ifPresent(dragonStateHandler -> {
						message.abilities.forEach((ab) -> ab.player = (Player)entity);
						dragonStateHandler.getMagic().getAbilities().clear();
						dragonStateHandler.getMagic().getAbilities().addAll(message.abilities);
					});
				}
			}
			context.setPacketHandled(true);
		});
	}
}