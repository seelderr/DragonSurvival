package by.dragonsurvivalteam.dragonsurvival.data;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.SoundDefinitionsProvider;

public class DataSoundProvider extends SoundDefinitionsProvider{
	/**
	 * Creates a new instance of this data provider.
	 *
	 * @param generator The data generator instance provided by the event you are initializing this provider in.
	 * @param modId     The mod ID of the current mod.
	 * @param helper    The existing file helper provided by the event you are initializing this provider in.
	 */
	protected DataSoundProvider(DataGenerator generator, String modId, ExistingFileHelper helper){
		super(generator, modId, helper);
	}

	@Override
	public void registerSounds(){
//		this.add(SoundEvents.LAVA_EXTINGUISH, definition()
//			.subtitle("sound.examplemod.example_sound") // Set translation key
//			.with(
//				sound(new ResourceLocation(MODID, "example_sound_1")) // Set first sound
//					.weight(4) // Has a 4 / 5 = 80% chance of playing
//					.volume(0.5), // Scales all volumes called on this sound by half
//				sound(new ResourceLocation(MODID, "example_sound_2")) // Set second sound
//					.stream() // Streams the sound
//			)
//		);

	}
}