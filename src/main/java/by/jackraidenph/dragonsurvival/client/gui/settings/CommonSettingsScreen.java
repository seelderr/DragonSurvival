package by.jackraidenph.dragonsurvival.client.gui.settings;

import by.jackraidenph.dragonsurvival.config.ConfigHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.BaseComponent;
import net.minecraftforge.common.ForgeConfigSpec;

public class CommonSettingsScreen extends ClientSettingsScreen
{
	public CommonSettingsScreen(Screen p_i225930_1_, Options p_i225930_2_, BaseComponent p_i225930_3_)
	{
		super(p_i225930_1_, p_i225930_2_, p_i225930_3_);
	}
	
	@Override
	public void tick()
	{
		super.tick();
		
		if(!Minecraft.getInstance().player.hasPermissions(2)){
			this.minecraft.setScreen(this.lastScreen);
		}
	}
	
	@Override
	public ForgeConfigSpec getSpec()
	{
		return ConfigHandler.commonSpec;
	}
	
	@Override
	public String getConfigName()
	{
		return "common";
	}
}
