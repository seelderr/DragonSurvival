package by.dragonsurvivalteam.dragonsurvival.registry.datagen;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;

public class DataLootTableProvider extends LootTableProvider {

	public DataLootTableProvider(PackOutput pOutput, Set<ResourceKey<LootTable>> pRequiredTables, List<SubProviderEntry> pSubProviders, CompletableFuture<HolderLookup.Provider> pRegistries) {
		super(pOutput, pRequiredTables, pSubProviders, pRegistries);
	}

	// All of this is no longer allowed, as the methods are all final. How to fix this?
	/*@Override
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

		@Override
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

				add(value, builder);
			});
		}

		@Override
		protected Iterable<Block> getKnownBlocks(){
			return DSBlocks.DS_BLOCKS.values();
		}
	}*/
}