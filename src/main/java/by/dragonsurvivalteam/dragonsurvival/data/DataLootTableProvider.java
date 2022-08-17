package by.dragonsurvivalteam.dragonsurvival.data;

import by.dragonsurvivalteam.dragonsurvival.registry.DSBlocks;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTable.Builder;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class DataLootTableProvider extends LootTableProvider{
	ExistingFileHelper existingFileHelper;
	public DataLootTableProvider(DataGenerator pGenerator, ExistingFileHelper existingFileHelper){
		super(pGenerator);
		this.existingFileHelper = existingFileHelper;
	}

	@Override
	public String getName(){
		return "Dragon Survival Loot tables";
	}

	@Override
	protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, Builder>>>, LootContextParamSet>> getTables(){
		return ImmutableList.of(
			Pair.of(() -> new DragonSurvivalBlockLoot(existingFileHelper), LootContextParamSets.BLOCK)
		);
	}

	@Override
	protected void validate(Map<ResourceLocation, LootTable> map, ValidationContext validationtracker){
		map.forEach((name, table) -> LootTables.validate(validationtracker, name, table));
	}

	public class DragonSurvivalBlockLoot extends BlockLoot {
		private final Map<ResourceLocation, LootTable.Builder> map = Maps.newHashMap();
		ExistingFileHelper existingFileHelper;
		public DragonSurvivalBlockLoot(ExistingFileHelper existingFileHelper){
			this.existingFileHelper = existingFileHelper;
		}

		public void addTables(){
			DSBlocks.DS_BLOCKS.forEach((key, value) -> this.add(value, createSingleItemTable(value)));
		}

		@Override
		protected Iterable<Block> getKnownBlocks(){
			return DSBlocks.DS_BLOCKS.values();
		}
	}
}