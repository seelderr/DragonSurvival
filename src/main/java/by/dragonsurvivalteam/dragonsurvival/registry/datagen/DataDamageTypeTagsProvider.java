package by.dragonsurvivalteam.dragonsurvival.registry.datagen;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod.MODID;

import by.dragonsurvivalteam.dragonsurvival.registry.DSDamageTypes;

import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.DamageTypeTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DataDamageTypeTagsProvider extends DamageTypeTagsProvider {
	public static TagKey<DamageType> DRAGON_BREATH = createKey("dragon_breath");
	public static TagKey<DamageType> DRAGON_MAGIC = createKey("dragon_magic");

	public DataDamageTypeTagsProvider(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pLookupProvider, String modId, @Nullable ExistingFileHelper existingFileHelper) {
		super(pOutput, pLookupProvider, modId, existingFileHelper);
	}

	@Override
	protected void addTags(@NotNull final HolderLookup.Provider provider) {
		tag(DamageTypeTags.BYPASSES_ARMOR)
				.add(DSDamageTypes.FOREST_DRAGON_DRAIN)
				.add(DSDamageTypes.CAVE_DRAGON_BURN)
				.add(DSDamageTypes.SPECTRAL_IMPACT);

		tag(DamageTypeTags.IS_FIRE)
				.add(DSDamageTypes.CAVE_DRAGON_BURN)
				.add(DSDamageTypes.CAVE_DRAGON_BREATH);

		tag(DamageTypeTags.IS_LIGHTNING)
				.add(DSDamageTypes.SEA_DRAGON_BREATH);

		tag(DRAGON_BREATH)
				.add(DSDamageTypes.DRAGON_BREATH)
				.add(DSDamageTypes.CAVE_DRAGON_BURN)
				.add(DSDamageTypes.FOREST_DRAGON_DRAIN)
				.add(DSDamageTypes.SEA_DRAGON_BREATH);

		tag(DamageTypeTags.NO_KNOCKBACK)
				.add(DSDamageTypes.CAVE_DRAGON_BURN)
				.add(DSDamageTypes.FOREST_DRAGON_DRAIN)
				.add(DSDamageTypes.CRUSHED)
				.add(DSDamageTypes.DEHYDRATION)
				.add(DSDamageTypes.WATER_BURN)
				.add(DSDamageTypes.RAIN_BURN);

		tag(DRAGON_MAGIC)
				.add(DSDamageTypes.CAVE_DRAGON_BREATH)
				.add(DSDamageTypes.CAVE_DRAGON_BURN)
				.add(DSDamageTypes.FOREST_DRAGON_BREATH)
				.add(DSDamageTypes.FOREST_DRAGON_DRAIN)
				.add(DSDamageTypes.SEA_DRAGON_BREATH)
				.add(DSDamageTypes.SPECTRAL_IMPACT)
				.add(DSDamageTypes.DRAGON_BALL_LIGHTNING)
				.add(DSDamageTypes.DRAGON_BREATH)
				.add(DamageTypes.FIREBALL);
	}

	private static TagKey<DamageType> createKey(@NotNull final String name) {
		return TagKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(MODID, name));
	}
}
