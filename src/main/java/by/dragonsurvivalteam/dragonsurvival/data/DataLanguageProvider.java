package by.dragonsurvivalteam.dragonsurvival.data;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.LanguageProvider;

public class DataLanguageProvider extends LanguageProvider{
	public DataLanguageProvider(DataGenerator gen, String modid, String locale){
		super(gen, modid, locale);
	}

	@Override
	protected void addTranslations(){
//		this.addBlock(() -> DSBlocks.acaciaDoor, "This is a door");
//		this.addItem(() -> DSItems.caveDragonTreat, "Cave treats");
	}
}