package by.jackraidenph.dragonsurvival.client.sounds;

import by.jackraidenph.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.jackraidenph.dragonsurvival.common.magic.abilities.Actives.BreathAbilities.StormBreathAbility;
import net.minecraft.client.audio.TickableSound;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn( Dist.CLIENT)
public class StormBreathSound extends TickableSound
{
	private StormBreathAbility ability;
	public StormBreathSound(StormBreathAbility ability)
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
