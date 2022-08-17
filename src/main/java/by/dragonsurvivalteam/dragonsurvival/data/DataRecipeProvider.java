package by.dragonsurvivalteam.dragonsurvival.data;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;

import java.util.function.Consumer;

public class DataRecipeProvider extends RecipeProvider{
	public DataRecipeProvider(DataGenerator pGenerator){
		super(pGenerator);
	}

	@Override
	public String getName(){
		return "Dragon Survival Recipes";
	}

	@Override
	protected void buildCraftingRecipes(Consumer<FinishedRecipe> pFinishedRecipeConsumer){
//		ShapedRecipeBuilder builder = ShapedRecipeBuilder.shaped(Items.APPLE);
//		builder.pattern("xxx").pattern("uuu");
//		builder.define('x', Blocks.DIRT).define('u', Blocks.DIAMOND_BLOCK);
//		builder.save(pFinishedRecipeConsumer);

	}
}