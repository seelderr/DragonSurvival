package by.dragonsurvivalteam.dragonsurvival.data;

import by.dragonsurvivalteam.dragonsurvival.common.blocks.DragonDoor;
import by.dragonsurvivalteam.dragonsurvival.common.blocks.DragonDoor.Part;
import by.dragonsurvivalteam.dragonsurvival.common.blocks.SourceOfMagicBlock;
import by.dragonsurvivalteam.dragonsurvival.common.blocks.TreasureBlock;
import by.dragonsurvivalteam.dragonsurvival.registry.DSBlocks;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.*;
import net.minecraft.world.level.storage.loot.LootTable.Builder;
import net.minecraft.world.level.storage.loot.entries.AlternativesEntry;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
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
			DSBlocks.DS_BLOCKS.forEach((key, value) -> {
				Function<Block, Builder> builder = b -> {
					if(b instanceof DragonDoor){
						return createSinglePropConditionTable(b, DragonDoor.PART, Part.BOTTOM);
					}else if(b instanceof SourceOfMagicBlock){
						return LootTable.lootTable().withPool(applyExplosionCondition(b, LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(b).when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(b).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SourceOfMagicBlock.PRIMARY_BLOCK, true))))));
					}else if(b instanceof TreasureBlock){
						ArrayList<LootPoolSingletonContainer.Builder> list = new ArrayList<>();

						for(Integer possibleValue : TreasureBlock.LAYERS.getPossibleValues()){
							LootPoolSingletonContainer.Builder entry = LootItem.lootTableItem(b)
                            .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(b)
                            .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(TreasureBlock.LAYERS, possibleValue)))
                            .apply(SetItemCountFunction.setCount(ConstantValue.exactly(possibleValue)));

							list.add(entry);
						}

						LootPoolSingletonContainer.Builder[] arr = list.toArray(new LootPoolSingletonContainer.Builder[0]);

						return LootTable.lootTable()
						.withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
				          .when(LootItemEntityPropertyCondition.entityPresent(LootContext.EntityTarget.THIS))
				          .add(AlternativesEntry.alternatives(AlternativesEntry.alternatives(arr))
			               .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(b))));
					}

					return createSingleItemTable(value);
				};

				this.add(value, builder);
			});
		}

		@Override
		protected Iterable<Block> getKnownBlocks(){
			return DSBlocks.DS_BLOCKS.values();
		}
	}
}