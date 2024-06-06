package by.dragonsurvivalteam.dragonsurvival.data;

import java.util.List;
import java.util.Set;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;

public class DataLootTableProvider extends LootTableProvider {
	public DataLootTableProvider(final PackOutput output, final Set<ResourceLocation> requiredTables, final List<SubProviderEntry> subProviders) {
		super(output, requiredTables, subProviders);
	}
}