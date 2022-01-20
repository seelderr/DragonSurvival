package by.jackraidenph.dragonsurvival.client.sounds;

import by.jackraidenph.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.jackraidenph.dragonsurvival.common.magic.abilities.Actives.BreathAbilities.NetherBreathAbility;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn( Dist.CLIENT)
public class FireBreathSound extends AbstractTickableSoundInstance
{
	private NetherBreathAbility ability;
	public FireBreathSound(NetherBreathAbility ability)
	{
		super(SoundRegistry.fireBreathLoop, SoundSource.PLAYERS);
		this.looping = true;
		
		this.ability = ability;
	}
	
	@Override
	public void tick()
	{
		if(ability.getPlayer() != null){
			DragonStateProvider.getCap(ability.getPlayer()).ifPresent((cap) -> {
				if(cap.getMagic().getCurrentlyCasting() != ability){
					this.stop();
				}
			});
		}
		
		if(ability.castingTicks == 0){
			this.stop();
		}
	}
	
	public boolean canStartSilent() {
		return true;
	}
}
