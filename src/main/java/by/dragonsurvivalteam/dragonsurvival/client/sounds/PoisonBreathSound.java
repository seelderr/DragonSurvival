package by.dragonsurvivalteam.dragonsurvival.client.sounds;


import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.magic.abilities.ForestDragon.active.ForestBreathAbility;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn( Dist.CLIENT )
public class PoisonBreathSound extends AbstractTickableSoundInstance{
	private final ForestBreathAbility ability;

	public PoisonBreathSound(ForestBreathAbility ability){
		super(SoundRegistry.forestBreathLoop, SoundSource.PLAYERS);

		looping = true;

		this.ability = ability;
	}

	@Override
	public void tick(){
		if(ability.getPlayer() != null){
			DragonStateProvider.getCap(ability.getPlayer()).ifPresent(cap -> {
				if(cap.getMagicData().getCurrentlyCasting() != ability){
					stop();
				}
			});
		}

		if(ability.chargeTime == 0){
			stop();
		}
	}

	@Override
	public boolean canStartSilent(){
		return true;
	}
}