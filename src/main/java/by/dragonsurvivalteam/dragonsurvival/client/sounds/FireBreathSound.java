package by.dragonsurvivalteam.dragonsurvival.client.sounds;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.magic.abilities.CaveDragon.active.NetherBreathAbility;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn( Dist.CLIENT )
public class FireBreathSound extends AbstractTickableSoundInstance{
	private final NetherBreathAbility ability;

	public FireBreathSound(NetherBreathAbility ability){
		super(SoundRegistry.fireBreathLoop, SoundSource.PLAYERS);
		this.looping = true;

		this.ability = ability;
	}

	@Override
	public void tick(){
		if(ability.getPlayer() != null){
			DragonStateProvider.getCap(ability.getPlayer()).ifPresent((cap) -> {
				if(cap.getMagicData().getCurrentlyCasting() != ability){
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