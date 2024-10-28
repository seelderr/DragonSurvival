package by.dragonsurvivalteam.dragonsurvival.registry.datagen;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;

import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DSEntityTypeTags extends EntityTypeTagsProvider {
	public static final TagKey<EntityType<?>> ANIMAL_AVOID_BLACKLIST = TagKey.create(Registries.ENTITY_TYPE, DragonSurvivalMod.res("animal_avoid_blacklist"));
	public static final TagKey<EntityType<?>> VEHICLE_WHITELIST = TagKey.create(Registries.ENTITY_TYPE, DragonSurvivalMod.res("vehicle_whitelist"));

	public DSEntityTypeTags(final PackOutput output, final CompletableFuture<HolderLookup.Provider> provider, @Nullable final ExistingFileHelper helper) {
		super(output, provider, DragonSurvivalMod.MODID, helper);
	}

	@Override
	protected void addTags(@NotNull final HolderLookup.Provider provider) {
		tag(ANIMAL_AVOID_BLACKLIST)
				.add(EntityType.WOLF)
				.add(EntityType.HOGLIN);

		tag(VEHICLE_WHITELIST)
				.addTag(Tags.EntityTypes.BOATS)
				.addTag(Tags.EntityTypes.MINECARTS)
				.addOptional(ResourceLocation.fromNamespaceAndPath("littlelogistics", "seater_barge"))
				.addOptional(ResourceLocation.fromNamespaceAndPath("create", "seat"))
				.addOptional(ResourceLocation.fromNamespaceAndPath("create", "contraption"))
				.addOptional(ResourceLocation.fromNamespaceAndPath("create", "gantry_contraption"))
				.addOptional(ResourceLocation.fromNamespaceAndPath("create", "stationary_contraption"))
				.addOptional(ResourceLocation.fromNamespaceAndPath("hexerei", "broom"))
				.addOptional(ResourceLocation.fromNamespaceAndPath("botania", "player_mover"));
	}
}
