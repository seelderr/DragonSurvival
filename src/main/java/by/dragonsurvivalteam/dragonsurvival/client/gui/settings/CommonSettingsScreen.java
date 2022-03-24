package by.dragonsurvivalteam.dragonsurvival.client.gui.settings;

import by.dragonsurvivalteam.dragonsurvival.config.ConfigHandler;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.ForgeConfigSpec;

public class CommonSettingsScreen extends ClientSettingsScreen{
	public CommonSettingsScreen(Screen p_i225930_1_, GameSettings p_i225930_2_, ITextComponent p_i225930_3_){
		super(p_i225930_1_, p_i225930_2_, p_i225930_3_);
	}

	@Override
	public void tick(){
		super.tick();

		if(!Minecraft.getInstance().player.hasPermissions(2)){
			this.minecraft.setScreen(this.lastScreen);
		}
	}

	@Override
	public String getConfigName(){
		return "common";
	}

	@Override
	public ForgeConfigSpec getSpec(){
		return ConfigHandler.commonSpec;
	}
}