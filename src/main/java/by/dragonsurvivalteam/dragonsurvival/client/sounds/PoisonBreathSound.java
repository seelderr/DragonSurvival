package by.dragonsurvivalteam.dragonsurvival.client.sounds;

<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/client/sounds/PoisonBreathSound.java
import by.jackraidenph.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.jackraidenph.dragonsurvival.common.magic.abilities.Actives.BreathAbilities.ForestBreathAbility;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn( Dist.CLIENT)
public class PoisonBreathSound extends AbstractTickableSoundInstance
{
	private ForestBreathAbility ability;
	public PoisonBreathSound(ForestBreathAbility ability)
	{
		super(SoundRegistry.forestBreathLoop, SoundSource.PLAYERS);
=======
import by.dragonsurvivalteam.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.magic.abilities.Actives.BreathAbilities.ForestBreathAbility;
import net.minecraft.client.audio.TickableSound;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn( Dist.CLIENT )
public class PoisonBreathSound extends TickableSound{
	private final ForestBreathAbility ability;

	public PoisonBreathSound(ForestBreathAbility ability){
		super(SoundRegistry.forestBreathLoop, SoundCategory.PLAYERS);
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/client/sounds/PoisonBreathSound.java
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

		if(ability.castingTicks == 0){
			this.stop();
		}
	}

	public boolean canStartSilent(){
		return true;
	}
}