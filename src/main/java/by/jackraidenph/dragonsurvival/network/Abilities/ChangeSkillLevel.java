package by.jackraidenph.dragonsurvival.network.Abilities;

import by.jackraidenph.dragonsurvival.DragonSurvivalMod;
import by.jackraidenph.dragonsurvival.abilities.DragonAbilities;
import by.jackraidenph.dragonsurvival.abilities.common.ActiveDragonAbility;
import by.jackraidenph.dragonsurvival.abilities.common.DragonAbility;
import by.jackraidenph.dragonsurvival.abilities.common.PassiveDragonAbility;
import by.jackraidenph.dragonsurvival.capability.DragonStateProvider;
import by.jackraidenph.dragonsurvival.network.IMessage;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.function.Supplier;

public class ChangeSkillLevel implements IMessage<ChangeSkillLevel>
{

    private int level;
    private String skill;
    
    public ChangeSkillLevel(int level, String skill)
    {
        this.level = level;
        this.skill = skill;
    }
    
    public ChangeSkillLevel() {
    }
    
    @Override
    public void encode(ChangeSkillLevel message, PacketBuffer buffer) {
        buffer.writeInt(message.level);
        buffer.writeUtf(message.skill);
    }

    @Override
    public ChangeSkillLevel decode(PacketBuffer buffer) {
        int level = buffer.readInt();
        String skill = buffer.readUtf();
        return new ChangeSkillLevel(level, skill);
    }

    @Override
    public void handle(ChangeSkillLevel message, Supplier<NetworkEvent.Context> supplier) {
        ServerPlayerEntity playerEntity = supplier.get().getSender();

        if(playerEntity == null)
            return;
    
        DragonStateProvider.getCap(playerEntity).ifPresent(dragonStateHandler -> {
            DragonAbility staticAbility = DragonAbilities.ABILITY_LOOKUP.get(message.skill);
        
            if(staticAbility != null){
                DragonAbility playerAbility = dragonStateHandler.getAbility(staticAbility);
            
                if(playerAbility == null){
                    playerAbility = staticAbility.createInstance();
                    dragonStateHandler.getAbilities().add(playerAbility);
                }
    
                int levelCost = 0;
                
                if(playerAbility instanceof PassiveDragonAbility) {
                    PassiveDragonAbility newActivty = (PassiveDragonAbility)playerAbility.createInstance();
                    int levelDif = message.level - playerAbility.getLevel();
                    
                    if(message.level > playerAbility.getLevel()){
                        newActivty.setLevel(playerAbility.getLevel() + levelDif );
                        levelCost = -newActivty.getLevelCost();
                    }else{
                        levelCost = Math.max((int)(((PassiveDragonAbility)playerAbility).getLevelCost() * 0.8F), 1);
                    }
                }else if(playerAbility instanceof ActiveDragonAbility) {
                    ActiveDragonAbility newActivty = (ActiveDragonAbility)playerAbility.createInstance();
                    newActivty.setLevel(playerAbility.getLevel() + 1);
                    levelCost = -newActivty.getLevelCost();
                }
                
                if(levelCost != 0){
                    playerEntity.giveExperienceLevels(levelCost);
                }
                
                playerAbility.setLevel(message.level);
                DragonSurvivalMod.CHANNEL.send(PacketDistributor.PLAYER.with(() -> playerEntity), new SyncCapabilityAbility(playerEntity.getId(), dragonStateHandler.getSelectedAbilitySlot(), dragonStateHandler.getMaxMana(), dragonStateHandler.getCurrentMana(), dragonStateHandler.getAbilities(), dragonStateHandler.renderAbilityHotbar()));
            }
        });
    }
}
