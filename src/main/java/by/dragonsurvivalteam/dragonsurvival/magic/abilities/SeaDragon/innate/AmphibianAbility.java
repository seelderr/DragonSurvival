/*package by.dragonsurvivalteam.dragonsurvival.magic.abilities.SeaDragon.innate;


@Translation(type = Translation.Type.ABILITY_DESCRIPTION, comments = {
        "■ Drying out under the harsh sun is a major concern for sea dragons. If they are outside of the water for too long, they will dehydrate and suffer damage.\n",
        "■ The skill «Water», effect «Sea Peace», rain, ice, snow and water bottles §7could make your life easier.§r\n",
        "■ Damage when dehydrated: §c%s per %ss§r"
})
@Translation(type = Translation.Type.ABILITY, comments = "Thin Skin")
@RegisterDragonAbility
public class AmphibianAbility extends InnateDragonAbility {
    @Override
    public Component getDescription() {
        return Component.translatable(Translation.Type.ABILITY_DESCRIPTION.wrap(getName()), SeaDragonConfig.seaDehydrationDamage, 2);
    }

    @Override
    public int getSortOrder() {
        return 4;
    }

    @Override
    public String getName() {
        return "amphibian";
    }

    @Override
    public AbstractDragonType getDragonType() {
        return DragonTypes.SEA;
    }

    @Override
    public ResourceLocation[] getSkillTextures() {
        return new ResourceLocation[]{
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/sea/amphibian_0.png"),
                ResourceLocation.fromNamespaceAndPath(MODID, "textures/skills/sea/amphibian_1.png")
        };
    }

    @Override
    public int getLevel() {
        return ServerConfig.penaltiesEnabled && SeaDragonConfig.seaTicksWithoutWater != 0 ? 1 : 0;
    }

    @Override
    public boolean isDisabled() {
        return super.isDisabled() || !ServerConfig.penaltiesEnabled || SeaDragonConfig.seaTicksWithoutWater == 0;
    }
}*/