package by.dragonsurvivalteam.dragonsurvival.network.magic;

import by.dragonsurvivalteam.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.magic.DragonAbilities;
import by.dragonsurvivalteam.dragonsurvival.common.magic.common.DragonAbility;
import by.dragonsurvivalteam.dragonsurvival.common.magic.common.PassiveDragonAbility;
import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
import by.dragonsurvivalteam.dragonsurvival.network.NetworkHandler;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

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
	public void encode(ChangeSkillLevel message, PacketBuffer buffer){
		buffer.writeInt(message.level);
		buffer.writeUtf(message.skill);
		buffer.writeInt(message.levelChange);
	}

	@Override
	public ChangeSkillLevel decode(PacketBuffer buffer){
		int level = buffer.readInt();
		String skill = buffer.readUtf();
		int levelChange = buffer.readInt();
		return new ChangeSkillLevel(level, skill, levelChange);
	}

	@Override
	public void handle(ChangeSkillLevel message, Supplier<NetworkEvent.Context> supplier){
		ServerPlayerEntity playerEntity = supplier.get().getSender();

		if(playerEntity == null){
			return;
		}

		DragonStateProvider.getCap(playerEntity).ifPresent(dragonStateHandler -> {
			DragonAbility staticAbility = DragonAbilities.ABILITY_LOOKUP.get(message.skill);

			if(staticAbility != null){
				DragonAbility playerAbility = dragonStateHandler.getMagic().getAbility(staticAbility);

				if(playerAbility == null){
					playerAbility = staticAbility.createInstance();
					dragonStateHandler.getMagic().getAbilities().add(playerAbility);
				}

				if(playerAbility.player == null){
					playerAbility.player = playerEntity;
				}

				PassiveDragonAbility newActivty = (PassiveDragonAbility)playerAbility.createInstance();
				newActivty.setLevel(playerAbility.getLevel() + message.levelChange);
				int levelCost = message.levelChange > 0 ? -newActivty.getLevelCost() : Math.max((int)(((PassiveDragonAbility)playerAbility).getLevelCost() * 0.8F), 1);

				dragonStateHandler.getMagic().getAbilities().removeIf((c) -> c.getId() == newActivty.getId());
				dragonStateHandler.getMagic().addAbility(newActivty);

				if(levelCost != 0 && !playerEntity.isCreative()){
					playerEntity.giveExperienceLevels(levelCost);
				}

				playerAbility.setLevel(message.level);
				NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> playerEntity), new SyncMagicAbilities(playerEntity.getId(), dragonStateHandler.getMagic().getAbilities()));
			}
		});
	}
}