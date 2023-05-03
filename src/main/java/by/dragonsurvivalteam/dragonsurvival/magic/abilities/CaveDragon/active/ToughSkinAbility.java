package by.dragonsurvivalteam.dragonsurvival.magic.abilities.CaveDragon.active;


import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.client.handlers.KeyInputHandler;
import by.dragonsurvivalteam.dragonsurvival.client.particles.DSParticles;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonType;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.DragonTypes;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigOption;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigRange;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigSide;
import by.dragonsurvivalteam.dragonsurvival.magic.common.RegisterDragonAbility;
import by.dragonsurvivalteam.dragonsurvival.magic.common.active.AoeBuffAbility;
import by.dragonsurvivalteam.dragonsurvival.registry.DragonEffects;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.Locale;

@RegisterDragonAbility
public class ToughSkinAbility extends AoeBuffAbility{

	@ConfigOption( side = ConfigSide.SERVER, category = {"magic", "abilities", "cave_dragon", "actives", "tough_skin"}, key = "toughSkin", comment = "Whether the tough skin ability should be enabled" )
	public static Boolean toughSkin = true;
	@ConfigRange( min = 0, max = 10000 )
	@ConfigOption( side = ConfigSide.SERVER, category = {"magic", "abilities", "cave_dragon", "actives", "tough_skin"}, key = "toughSkinDuration", comment = "The duration in seconds of the tough skin effect given when the ability is used" )
	public static Integer toughSkinDuration = 3600;
	@ConfigRange( min = 1, max = 10000 )
	@ConfigOption( side = ConfigSide.SERVER, category = {"magic", "abilities", "cave_dragon", "actives", "tough_skin"}, key = "toughSkinCooldown", comment = "The cooldown in ticks of the tough skin ability" )
	public static Integer toughSkinCooldown = 600;
	@ConfigRange( min = 1, max = 10000 )
	@ConfigOption( side = ConfigSide.SERVER, category = {"magic", "abilities", "cave_dragon", "actives", "tough_skin"}, key = "toughSkinCasttime", comment = "The cast time in ticks of the tough skin ability" )
	public static Integer toughSkinCasttime = 60;
	@ConfigRange( min = 0, max = 100 )
	@ConfigOption( side = ConfigSide.SERVER, category = {"magic", "abilities", "cave_dragon", "actives", "tough_skin"}, key = "toughSkinManaCost", comment = "The mana cost for using the tough skin ability" )
	public static Integer toughSkinManaCost = 1;
	@ConfigRange( min = 0, max = 10000 )
	@ConfigOption( side = ConfigSide.SERVER, category = {"magic", "abilities", "cave_dragon", "actives", "tough_skin"}, key = "toughSkinArmorValue", comment = "The amount of extra armor given per level of tough skin effect" )
	public static Double toughSkinArmorValue = 3.0;

	@Override
	public int getSortOrder(){
		return 3;
	}

	@Override
	public int getSkillCastingTime(){
		return toughSkinCasttime;
	}

	@Override
	public ArrayList<Component> getInfo(){
		ArrayList<Component> components = super.getInfo();

		if(!KeyInputHandler.ABILITY3.isUnbound()){
			components = new ArrayList<>(components.subList(0, components.size() - 1));
		}

		components.add(Component.translatable("ds.skill.duration.seconds", toughSkinDuration));

		if(!KeyInputHandler.ABILITY3.isUnbound()){

			String key = KeyInputHandler.ABILITY3.getKey().getDisplayName().getString().toUpperCase(Locale.ROOT);

			if(key.isEmpty()){
				key = KeyInputHandler.ABILITY3.getKey().getDisplayName().getString();
			}
			components.add(Component.translatable("ds.skill.keybind", key));
		}

		return components;
	}

	@Override
	public int getRange(){
		return 5;
	}

	@Override
	public ParticleOptions getParticleEffect(){
		return DSParticles.peaceBeaconParticle;
	}

	@Override
	public int getManaCost(){
		return toughSkinManaCost;
	}

	@Override
	public Integer[] getRequiredLevels(){
		return new Integer[]{0, 15, 35};
	}

	@Override
	public int getSkillCooldown(){
		return toughSkinCooldown;
	}

	@Override
	public MobEffectInstance getEffect(){
		return new MobEffectInstance(DragonEffects.STRONG_LEATHER, toughSkinDuration, getLevel() - 1);
	}

	@Override
	public Component getDescription(){
		return Component.translatable("ds.skill.description." + getName(), toughSkinDuration, getDefence(getLevel()));
	}

	@Override
	public String getName(){
		return "strong_leather";
	}

	@Override
	public AbstractDragonType getDragonType(){
		return DragonTypes.CAVE;
	}

	@Override
	public ResourceLocation[] getSkillTextures(){
		return new ResourceLocation[]{new ResourceLocation(DragonSurvivalMod.MODID, "textures/skills/cave/strong_leather_0.png"),
		                              new ResourceLocation(DragonSurvivalMod.MODID, "textures/skills/cave/strong_leather_1.png"),
		                              new ResourceLocation(DragonSurvivalMod.MODID, "textures/skills/cave/strong_leather_2.png"),
		                              new ResourceLocation(DragonSurvivalMod.MODID, "textures/skills/cave/strong_leather_3.png")};
	}


	public static double getDefence(int level){
		return level * toughSkinArmorValue;
	}

	@Override
	@OnlyIn( Dist.CLIENT )
	public ArrayList<Component> getLevelUpInfo(){
		ArrayList<Component> list = super.getLevelUpInfo();
		list.add(Component.translatable("ds.skill.defence", "+" + toughSkinArmorValue));
		return list;
	}

	@Override
	public int getMaxLevel(){
		return 3;
	}

	@Override
	public int getMinLevel(){
		return 0;
	}

	@Override
	public boolean isDisabled(){
		return super.isDisabled() || !toughSkin;
	}
}