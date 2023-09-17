package by.dragonsurvivalteam.dragonsurvival.data;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.registry.DSItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class DataItemTagProvider extends ItemTagsProvider {
	public DataItemTagProvider(final PackOutput output, final CompletableFuture<HolderLookup.Provider> lookupProvider, final CompletableFuture<TagLookup<Block>> blockTags, final String modId, @Nullable final ExistingFileHelper existingFileHelper) {
		super(output, lookupProvider, blockTags, modId, existingFileHelper);
	}

	@Override
	protected void addTags(@NotNull final HolderLookup.Provider provider) {
		tag(createForgeKey("raw_fishes")).add(Items.COD, Items.SALMON, Items.TROPICAL_FISH, Items.PUFFERFISH);
		tag(createForgeKey("raw_meats")).add(Items.BEEF, Items.CHICKEN, Items.MUTTON, Items.PORKCHOP, Items.RABBIT);

		tag(createKey("dragon_survival_food")).add(DSItems.seaDragonTreat, DSItems.forestDragonTreat, DSItems.caveDragonTreat, DSItems.hotDragonRod, DSItems.explosiveCopper, DSItems.doubleQuartz, DSItems.quartzExplosiveCopper, DSItems.charredMeat, DSItems.charredVegetable, DSItems.charredMushroom, DSItems.charredSeafood, DSItems.chargedCoal, DSItems.chargedSoup, DSItems.meatWildBerries, DSItems.smellyMeatPorridge, DSItems.sweetSourRabbit, DSItems.meatChorusMix, DSItems.diamondChorus, DSItems.luminousOintment, DSItems.frozenRawFish, DSItems.seasonedFish, DSItems.goldenCoralPufferfish, DSItems.goldenTurtleEgg);
		tag(createKey("sea_dragon_survival_food")).add(DSItems.seaDragonTreat, DSItems.frozenRawFish, DSItems.seasonedFish, DSItems.goldenCoralPufferfish, DSItems.goldenTurtleEgg);
		tag(createKey("cave_dragon_survival_food")).add(DSItems.caveDragonTreat, DSItems.hotDragonRod, DSItems.explosiveCopper, DSItems.doubleQuartz, DSItems.quartzExplosiveCopper, DSItems.charredMeat, DSItems.charredVegetable, DSItems.charredMushroom, DSItems.charredSeafood, DSItems.chargedCoal, DSItems.chargedSoup);
		tag(createKey("forest_survival_food")).add(DSItems.forestDragonTreat, DSItems.meatWildBerries, DSItems.smellyMeatPorridge, DSItems.sweetSourRabbit, DSItems.meatChorusMix, DSItems.diamondChorus);
		tag(createKey("charred_food")).add(DSItems.chargedCoal, DSItems.chargedSoup, DSItems.charredMeat, DSItems.charredMushroom, DSItems.charredSeafood, DSItems.charredVegetable);
		tag(createKey("copper")).addTag(createForgeKey("ingots/copper")).addOptional(new ResourceLocation("cavesandcliffs:raw_copper"));

		this.copy(DataBlockTagProvider.DRAGON_ALTARS, createKey("dragon_altars"));
		this.copy(DataBlockTagProvider.DRAGON_TREASURES, createKey("dragon_treasures"));
		this.copy(DataBlockTagProvider.WOODEN_DRAGON_DOORS, createKey("wooden_dragon_doors"));
	}

	private static TagKey<Item> createKey(@NotNull final String name) {
		return ItemTags.create(new ResourceLocation(DragonSurvivalMod.MODID, name));
	}

	private static TagKey<Item> createForgeKey(String name) {
		return ItemTags.create(new ResourceLocation("forge", name));
	}

	@Override
	public @NotNull String getName() {
		return "Dragon Survival Item tags";
	}
}