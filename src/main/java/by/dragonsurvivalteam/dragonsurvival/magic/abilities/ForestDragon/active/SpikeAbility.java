/*package by.dragonsurvivalteam.dragonsurvival.magic.abilities.ForestDragon.active;


@Translation(type = Translation.Type.ABILITY_DESCRIPTION, comments = "■ Ranged attack: shoots out sharp §cdarts§r, which fly a large distance to pierce your target. Less effective underwater.")
@Translation(type = Translation.Type.ABILITY, comments = "Spike")
@RegisterDragonAbility
public class SpikeAbility extends InstantCastAbility {
    @Translation(key = "spike", type = Translation.Type.CONFIGURATION, comments = "Enable / Disable the spike ability")
    @ConfigOption(side = ConfigSide.SERVER, category = {"forest_dragon", "magic", "abilities", "active", "spike"}, key = "spike")
    public static Boolean spikeEnabled = true;

    @ConfigRange(min = 0.0, max = 100.0)
    @Translation(key = "spike_spread", type = Translation.Type.CONFIGURATION, comments = "The amount of spread each additionally fired spike will have - spikes will have no spread if set to 0")
    @ConfigOption(side = ConfigSide.SERVER, category = {"forest_dragon", "magic", "abilities", "active", "spike"}, key = "spike_spread")
    public static Float spikeSpread = 1.5F;

    @Translation(key = "spike_additional_projectiles", type = Translation.Type.CONFIGURATION, comments = "The spike ability will fire additional projectiles based on the ability level if enabled")
    @ConfigOption(side = ConfigSide.SERVER, category = {"forest_dragon", "magic", "abilities", "active", "spike"}, key = "spike_additional_projectiles")
    public static Boolean spikeMultishot = true;

    @ConfigRange(min = 0.05, max = 10_000.0)
    @Translation(key = "spike_cooldown", type = Translation.Type.CONFIGURATION, comments = "Cooldown (in seconds) after using the ability")
    @ConfigOption(side = ConfigSide.SERVER, category = {"forest_dragon", "magic", "abilities", "active", "spike"}, key = "spike_cooldown")
    public static Double spikeCooldown = 3.0;

    @ConfigRange(min = 0, max = 100.0)
    @Translation(key = "spike_damage", type = Translation.Type.CONFIGURATION, comments = "Amount of damage (multiplied by the ability level)")
    @ConfigOption(side = ConfigSide.SERVER, category = {"forest_dragon", "magic", "abilities", "active", "spike"}, key = "spike_damage")
    public static Double spikeDamage = 2.0;

    @ConfigRange(min = 0, max = 100)
    @Translation(key = "spike_mana_cost", type = Translation.Type.CONFIGURATION, comments = "Mana cost")
    @ConfigOption(side = ConfigSide.SERVER, category = {"forest_dragon", "magic", "abilities", "active", "spike"}, key = "spike_mana_cost")
    public static Integer spikeManaCost = 1;

    @Override
    public Component getDescription() {
        return Component.translatable(Translation.Type.ABILITY_DESCRIPTION.wrap(getName()), getDamage());
    }

    @Override
    public int getSortOrder() {
        return 2;
    }

    @Override
    public String getName() {
        return "spike";
    }

    @Override
    public AbstractDragonType getDragonType() {
        return DragonTypes.FOREST;
    }

    @Override
    public ResourceLocation[] getSkillTextures() {
        return new ResourceLocation[]{
                ResourceLocation.fromNamespaceAndPath(MODID, "abilities/forest/spike_0"),
                ResourceLocation.fromNamespaceAndPath(MODID, "abilities/forest/spike_1"),
                ResourceLocation.fromNamespaceAndPath(MODID, "abilities/forest/spike_2"),
                ResourceLocation.fromNamespaceAndPath(MODID, "abilities/forest/spike_3"),
                ResourceLocation.fromNamespaceAndPath(MODID, "abilities/forest/spike_4")
        };
    }


    public float getDamage() {
        return (float) (spikeDamage * getLevel());
    }

    @Override
    public ArrayList<Component> getLevelUpInfo() {
        ArrayList<Component> list = super.getLevelUpInfo();
        list.add(Component.translatable(LangKey.ABILITY_DAMAGE, "+" + spikeDamage));
        return list;
    }

    @Override
    public int getMaxLevel() {
        return 4;
    }

    @Override
    public int getMinLevel() {
        return 0;
    }

    @Override
    public boolean isDisabled() {
        return super.isDisabled() || !spikeEnabled;
    }

    @Override
    public int getManaCost() {
        return spikeManaCost;
    }

    @Override
    public Integer[] getRequiredLevels() {
        return new Integer[]{0, 20, 30, 40};
    }

    @Override
    public int getSkillCooldown() {
        return Functions.secondsToTicks(spikeCooldown);
    }

    @Override
    public ArrayList<Component> getInfo() {
        ArrayList<Component> components = super.getInfo();
        components.add(Component.translatable(LangKey.ABILITY_DAMAGE, getDamage()));

        if (!Keybind.ABILITY2.get().isUnbound()) {

            String key = Keybind.ABILITY2.getKey().getDisplayName().getString().toUpperCase(Locale.ROOT);

            if (key.isEmpty()) {
                key = Keybind.ABILITY2.getKey().getDisplayName().getString();
            }
            components.add(Component.translatable(LangKey.ABILITY_KEYBIND, key));
        }

        return components;
    }

    @Override
    public boolean requiresStationaryCasting() {
        return false;
    }

    @Override
    public void onCast(Player player) {
        if(player.level().isClientSide) {
            return;
        }

        Holder<DragonAbility> ability = player.registryAccess().holderOrThrow(DragonAbilities.SPIKE_TEST);
        DragonAbilityInstance instance = new DragonAbilityInstance(ability);
        instance.apply((ServerPlayer) player);
        /*float speed = 1;
        // Copied from AbstractArrow.java constructor
        Vec3 launchPos = new Vec3(player.getX(), player.getEyeY() - 0.1F, player.getZ());
        for (int i = 0; i < getLevel(); i++) {
            DragonSpikeEntity entity = new DragonSpikeEntity(DSEntities.DRAGON_SPIKE.get(), player.level());
            entity.setPos(launchPos);
            entity.setOwner(player);
            entity.setArrow_level(getLevel());
            entity.setBaseDamage(getDamage());
            entity.pickup = AbstractArrow.Pickup.DISALLOWED;
            entity.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, speed, i * spikeSpread);
            player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ARROW_SHOOT, entity.getSoundSource(), 1.0F, 2.0F);
            player.level().addFreshEntity(entity);

            if (!spikeMultishot) {
                break;
            }
        }
    }
}*/