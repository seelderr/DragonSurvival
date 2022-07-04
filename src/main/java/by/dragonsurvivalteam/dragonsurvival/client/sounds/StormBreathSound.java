package by.dragonsurvivalteam.dragonsurvival.client.sounds;


import by.dragonsurvivalteam.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.magic.abilities.SeaDragon.active.StormBreathAbility;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn( Dist.CLIENT )
public class StormBreathSound extends AbstractTickableSoundInstance{
	private final StormBreathAbility ability;

	public StormBreathSound(StormBreathAbility ability){
		super(SoundRegistry.stormBreathLoop, SoundSource.PLAYERS);

		this.looping = true;

		this.ability = ability;
	}

	@Override
	public void tick(){
		if(ability.getPlayer() != null){
			DragonStateProvider.getCap(ability.getPlayer()).ifPresent((cap) -> {
				if(cap.getMagic().getCurrentlyCasting() != ability){
					this.stop();
				}
			});
		}

		if(ability.chargeTime == 0){
			this.stop();
		}
	}

	public boolean canStartSilent(){
		return true;
	}
}