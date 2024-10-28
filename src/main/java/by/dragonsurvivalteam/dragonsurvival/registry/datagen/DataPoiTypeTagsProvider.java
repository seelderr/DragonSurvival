package by.dragonsurvivalteam.dragonsurvival.registry.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.PoiTypeTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.PoiTypeTags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class DataPoiTypeTagsProvider extends PoiTypeTagsProvider {
	public DataPoiTypeTagsProvider(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pProvider, String modId, @Nullable ExistingFileHelper existingFileHelper) {
		super(pOutput, pProvider, modId, existingFileHelper);
	}

	@Override
	protected void addTags(HolderLookup.@NotNull Provider provider) {
		tag(PoiTypeTags.ACQUIRABLE_JOB_SITE)
				.addOptional(ResourceLocation.fromNamespaceAndPath(this.modId, "dragon_rider_poi"));
	}
}
