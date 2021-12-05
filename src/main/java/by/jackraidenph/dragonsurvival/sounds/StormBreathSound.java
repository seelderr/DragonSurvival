package by.jackraidenph.dragonsurvival.sounds;

import by.jackraidenph.dragonsurvival.capability.DragonStateProvider;
import by.jackraidenph.dragonsurvival.magic.abilities.Actives.BreathAbilities.LightningBreathAbility;
import net.minecraft.client.audio.TickableSound;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn( Dist.CLIENT)
public class StormBreathSound extends TickableSound
{
	private LightningBreathAbility ability;
	public StormBreathSound(LightningBreathAbility ability)
	{
		super(SoundRegistry.stormBreathLoop, SoundCategory.PLAYERS);
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
