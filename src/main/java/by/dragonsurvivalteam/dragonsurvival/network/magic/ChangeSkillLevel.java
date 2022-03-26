package by.dragonsurvivalteam.dragonsurvival.network.magic;

import by.dragonsurvivalteam.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.magic.DragonAbilities;
import by.dragonsurvivalteam.dragonsurvival.common.magic.common.DragonAbility;
import by.dragonsurvivalteam.dragonsurvival.common.magic.common.PassiveDragonAbility;
import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
import by.dragonsurvivalteam.dragonsurvival.network.NetworkHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

public class ChangeSkillLevel implements IMessage<ChangeSkillLevel>{
	private int level;
	private int levelChange;
	private String skill;

	public ChangeSkillLevel(int level, String skill, int levelChange){
		this.level = level;
		this.skill = skill;
		this.levelChange = levelChange;
	}

	public ChangeSkillLevel(){}

	@Override
	public void encode(ChangeSkillLevel message, FriendlyByteBuf buffer){
		buffer.writeInt(message.level);
		buffer.writeUtf(message.skill);
		buffer.writeInt(message.levelChange);
	}

	@Override
	public ChangeSkillLevel decode(FriendlyByteBuf buffer){
		int level = buffer.readInt();
		String skill = buffer.readUtf();
		int levelChange = buffer.readInt();
		return new ChangeSkillLevel(level, skill, levelChange);
	}

	@Override
	public void handle(ChangeSkillLevel message, Supplier<NetworkEvent.Context> supplier){
		ServerPlayer player = supplier.get().getSender();

		if(player == null){
			return;
		}

		DragonStateProvider.getCap(player).ifPresent(dragonStateHandler -> {
			DragonAbility staticAbility = DragonAbilities.ABILITY_LOOKUP.get(message.skill);

			if(staticAbility != null){
				DragonAbility playerAbility = dragonStateHandler.getMagic().getAbility(staticAbility);

				if(playerAbility == null){
					playerAbility = staticAbility.createInstance();
					dragonStateHandler.getMagic().getAbilities().add(playerAbility);
				}

				if(playerAbility.player == null){
					playerAbility.player = player;
				}

				PassiveDragonAbility newActivty = (PassiveDragonAbility)playerAbility.createInstance();
				newActivty.setLevel(playerAbility.getLevel() + message.levelChange);
				int levelCost = message.levelChange > 0 ? -newActivty.getLevelCost() : Math.max((int)(((PassiveDragonAbility)playerAbility).getLevelCost() * 0.8F), 1);

				dragonStateHandler.getMagic().getAbilities().removeIf((c) -> c.getId() == newActivty.getId());
				dragonStateHandler.getMagic().addAbility(newActivty);

				if(levelCost != 0 && !player.isCreative()){
					player.giveExperienceLevels(levelCost);
				}

				playerAbility.setLevel(message.level);
				NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new SyncMagicAbilities(player.getId(), dragonStateHandler.getMagic().getAbilities()));
			}
		});
	}
}