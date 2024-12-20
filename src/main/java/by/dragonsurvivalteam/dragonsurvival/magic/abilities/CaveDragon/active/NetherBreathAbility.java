/*package by.dragonsurvivalteam.dragonsurvival.magic.abilities.CaveDragon.active;


@Translation(type = Translation.Type.ABILITY_DESCRIPTION, comments = {
        "■ Elemental breath: a stream of fire that ignites enemies and blocks. Range depends on age of the dragon.\n",
        "■ Is able to destroy some blocks. Cannot be used under water, and during rain."
})
@Translation(type = Translation.Type.ABILITY, comments = "Nether Breath") // TODO :: rename things from nether to fire? or from fire to nether?
@RegisterDragonAbility
public class NetherBreathAbility extends BreathAbility {
    @Translation(key = "fire_breath", type = Translation.Type.CONFIGURATION, comments = "Enable / Disable the fire breath ability")
    @ConfigOption(side = ConfigSide.SERVER, category = {"cave_dragon", "magic", "abilities", "active", "fire_breath"}, key = "fire_breath")
    public static Boolean isEnabled = true;

    @ConfigRange(min = 0, max = 100.0)
    @Translation(key = "fire_breath_damage", type = Translation.Type.CONFIGURATION, comments = "Amount of damage (multiplied by the ability level)")
    @ConfigOption(side = ConfigSide.SERVER, category = {"cave_dragon", "magic", "abilities", "active", "fire_breath"}, key = "fire_breath_damage")
    public static Double damage = 3.0;

    @ConfigRange(min = 0, max = 100)
    @Translation(key = "fire_breath_initial_mana_cost", type = Translation.Type.CONFIGURATION, comments = "Mana cost for starting the cast")
    @ConfigOption(side = ConfigSide.SERVER, category = {"cave_dragon", "magic", "abilities", "active", "fire_breath"}, key = "fire_breath_initial_mana_cost")
    public static Integer initialManaCost = 2;

    @ConfigRange(min = 0, max = 100)
    @Translation(key = "fire_breath_sustaining_mana_cost", type = Translation.Type.CONFIGURATION, comments = "Mana cost for sustaining the ability")
    @ConfigOption(side = ConfigSide.SERVER, category = {"cave_dragon", "magic", "abilities", "active", "fire_breath"}, key = "fire_breath_sustaining_mana_cost")
    public static Integer sustainedManaCost = 1;

    @ConfigRange(min = 0.5, max = 100.0)
    @Translation(key = "fire_breath_mana_cost_tick_rate", type = Translation.Type.CONFIGURATION, comments = "Time (in seconds) between ticks of the sustained mana cost being applied")
    @ConfigOption(side = ConfigSide.SERVER, category = {"cave_dragon", "magic", "abilities", "active", "fire_breath"}, key = "fire_breath_mana_cost_tick_rate")
    public static Double sustainedManaCostTickRate = 2.0;

    @Translation(key = "fire_breath_spreads_fire", type = Translation.Type.CONFIGURATION, comments = "Fire breath will spread actual fire if enabled")
    @ConfigOption(side = ConfigSide.SERVER, category = {"cave_dragon", "magic", "abilities", "active", "fire_breath"}, key = "fire_breath_spreads_fire")
    public static Boolean spreadsFire = true;

    @ConfigRange(min = 0.05, max = 10_000.0)
    @Translation(key = "fire_breath_cooldown", type = Translation.Type.CONFIGURATION, comments = "Cooldown (in seconds) after using the ability")
    @ConfigOption(side = ConfigSide.SERVER, category = {"cave_dragon", "magic", "abilities", "active", "fire_breath"}, key = "fire_breath_cooldown")
    public static Double cooldown = 5.0;

    @ConfigRange(min = 0.05, max = 10_000.0)
    @Translation(key = "fire_breath_cast_time", type = Translation.Type.CONFIGURATION, comments = "Cast time (in seconds)")
    @ConfigOption(side = ConfigSide.SERVER, category = {"cave_dragon", "magic", "abilities", "active", "fire_breath"}, key = "fire_breath_cast_time")
    public static Double castTime = 1.0;

    @Override
    public String getName() {
        return "nether_breath";
    }

    @Override
    public AbstractDragonType getDragonType() {
        return DragonTypes.CAVE;
    }

    @Override
    public ResourceLocation[] getSkillTextures() {
        return new ResourceLocation[]{
                ResourceLocation.fromNamespaceAndPath(MODID, "abilities/cave/nether_breath_0"),
                ResourceLocation.fromNamespaceAndPath(MODID, "abilities/cave/nether_breath_1"),
                ResourceLocation.fromNamespaceAndPath(MODID, "abilities/cave/nether_breath_2"),
                ResourceLocation.fromNamespaceAndPath(MODID, "abilities/cave/nether_breath_3"),
                ResourceLocation.fromNamespaceAndPath(MODID, "abilities/cave/nether_breath_4")
        };
    }

    @Override
    public ArrayList<Component> getLevelUpInfo() {
        ArrayList<Component> list = super.getLevelUpInfo();
        list.add(Component.translatable(LangKey.ABILITY_DAMAGE, "+" + damage));
        return list;
    }

    @Override
    public int getMaxLevel() {
        return 4;
    }

    @Override
    public int getMinLevel() {
        return 1;
    }

    @Override
    public boolean isDisabled() {
        return super.isDisabled() || !isEnabled;
    }

    @Override
    public int getManaCost() {
        return sustainedManaCost;
    }

    @Override
    public Integer[] getRequiredLevels() {
        return new Integer[]{0, 10, 30, 50};
    }

    @Override
    public int getSkillCooldown() {
        return Functions.secondsToTicks(cooldown);
    }

    @Override
    public void onBlock(final BlockPos blockPosition, final BlockState blockState, final Direction direction) {
        if (!player.level().isClientSide) {
            if (spreadsFire) {
                BlockPos firePosition = blockPosition.relative(direction);
                Block block = blockState.getBlock();

                if (block instanceof TntBlock tnt) {
                    tnt.onCaughtFire(blockState, player.level(), blockPosition, direction, player);
                    player.level().setBlock(blockPosition, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL_IMMEDIATE);
                } else if (block instanceof CampfireBlock && !blockState.getValue(CampfireBlock.LIT)) {
                    player.level().setBlock(blockPosition, blockState.setValue(CampfireBlock.LIT, true), Block.UPDATE_ALL_IMMEDIATE);
                } else if (FireBlock.canBePlacedAt(player.level(), firePosition, direction) && player.getRandom().nextInt(100) < 50) {
                    BlockState fireBlockState = FireBlock.getState(player.level(), firePosition);
                    player.level().setBlock(firePosition, fireBlockState, Block.UPDATE_ALL_IMMEDIATE);
                    blockState.onCaughtFire(player.level(), blockPosition, direction, player);
                }
            }

            int level = DragonAbilities.getAbility(player, BurnAbility.class).map(DragonAbility::getLevel).orElse(0);

            if (player.getRandom().nextInt(100) < level * 15) {
                BlockState blockAbove = player.level().getBlockState(blockPosition.above());

                if (blockAbove.getBlock() == Blocks.AIR) {
                    AreaEffectCloud entity = new AreaEffectCloud(EntityType.AREA_EFFECT_CLOUD, player.level());
                    entity.setWaitTime(0);
                    entity.setPos(blockPosition.above().getX(), blockPosition.above().getY(), blockPosition.above().getZ());
                    entity.setPotionContents(new PotionContents(CAVE_BREATH));
                    entity.setDuration(Functions.secondsToTicks(2));
                    entity.setRadius(1);
                    entity.setParticle(new SmallFireParticleOption(37, false));
                    entity.setOwner(player);
                    player.level().addFreshEntity(entity);
                }
            }
        } else {
            for (int i = 0; i < 4; i++) {
                if (player.getRandom().nextInt(100) < 20) {
                    player.level().addParticle(ParticleTypes.LAVA, blockPosition.above().getX(), blockPosition.above().getY(), blockPosition.above().getZ(), 0, 0.05, 0);
                }
            }

            if (blockState.getBlock() == Blocks.WATER) {
                for (int i = 0; i < 4; i++) {
                    if (player.getRandom().nextInt(100) < 90) {
                        player.level().addParticle(ParticleTypes.BUBBLE_COLUMN_UP, blockPosition.above().getX(), blockPosition.above().getY(), blockPosition.above().getZ(), 0, 0.05, 0);
                    }
                }
            }
        }
    }

    private static DragonAbilityInstance testInstance;

    @Override
    public void onChanneling(Player player, int castDuration) {
        super.onChanneling(player, castDuration);

        if(!player.level().isClientSide)
        {
            Holder<by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.DragonAbility> ability = player.registryAccess().holderOrThrow(by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.DragonAbilities.NETHER_BREATH);
            if(testInstance == null) {
                testInstance = new DragonAbilityInstance(ability);
            }
            testInstance.apply((ServerPlayer) player);
        }
       /* if (player.isInWaterRainOrBubble() || player.level().isRainingAt(player.blockPosition())) {
            if (player.level().isClientSide()) {
                if (player.tickCount % 10 == 0) {
                    player.playSound(SoundEvents.LAVA_EXTINGUISH, 0.25F, 1F);
                }

                for (int i = 0; i < 12; i++) {
                    double xSpeed = speed * 1f * xComp;
                    double ySpeed = speed * 1f * yComp;
                    double zSpeed = speed * 1f * zComp;
                    player.level().addParticle(ParticleTypes.SMOKE, dx, dy, dz, xSpeed, ySpeed, zSpeed);
                }
            }
            return;
        }

        if (player.level().isClientSide() && castDuration <= 0 && FMLEnvironment.dist.isClient()) {
            sound();
        }

        if (player.level().isClientSide()) {
            for (int i = 0; i < calculateNumberOfParticles(DragonStateProvider.getData(player).getSize()); i++) {
                double xSpeed = speed * 1f * xComp;
                double ySpeed = speed * 1f * yComp;
                double zSpeed = speed * 1f * zComp;
                player.level().addParticle(new SmallFireParticleOption(37, true), dx, dy, dz, xSpeed, ySpeed, zSpeed);
            }

            for (int i = 0; i < calculateNumberOfParticles(DragonStateProvider.getData(player).getSize()) / 2; i++) {
                double xSpeed = speed * xComp + spread * 0.7 * (player.getRandom().nextFloat() * 2 - 1) * Math.sqrt(1 - xComp * xComp);
                double ySpeed = speed * yComp + spread * 0.7 * (player.getRandom().nextFloat() * 2 - 1) * Math.sqrt(1 - yComp * yComp);
                double zSpeed = speed * zComp + spread * 0.7 * (player.getRandom().nextFloat() * 2 - 1) * Math.sqrt(1 - zComp * zComp);
                player.level().addParticle(new LargeFireParticleOption(37, false), dx, dy, dz, xSpeed, ySpeed, zSpeed);
            }
        }

        hitEntities();

        if (player.tickCount % 10 == 0) {
            hitBlocks();
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void sound() {
        Vec3 pos = player.getEyePosition(1.0F);
        SimpleSoundInstance startingSound = new SimpleSoundInstance(
                DSSounds.FIRE_BREATH_START.get(),
                SoundSource.PLAYERS,
                1.0F, 1.0F,
                SoundInstance.createUnseededRandom(),
                pos.x, pos.y, pos.z
        );
        Minecraft.getInstance().getSoundManager().playDelayed(startingSound, 0);
        Minecraft.getInstance().getSoundManager().stop(ResourceLocation.fromNamespaceAndPath(MODID, "fire_breath_loop"), SoundSource.PLAYERS);
        Minecraft.getInstance().getSoundManager().queueTickingSound(new FireBreathSound(this));
    }

    @OnlyIn(Dist.CLIENT)
    public void stopSound() {
        if (DSSounds.FIRE_BREATH_END != null) {
            Vec3 pos = player.getEyePosition(1.0F);
            SimpleSoundInstance endSound = new SimpleSoundInstance(
                    DSSounds.FIRE_BREATH_END.get(),
                    SoundSource.PLAYERS,
                    1.0F, 1.0F,
                    SoundInstance.createUnseededRandom(),
                    pos.x, pos.y, pos.z
            );

            Minecraft.getInstance().getSoundManager().playDelayed(endSound, 0);
        }

        Minecraft.getInstance().getSoundManager().stop(ResourceLocation.fromNamespaceAndPath(MODID, "fire_breath_loop"), SoundSource.PLAYERS);
    }

    @Override
    public boolean canHitEntity(LivingEntity entity) {
        return (!(entity instanceof Player) || player.canHarmPlayer((Player) entity)) && !entity.fireImmune();
    }

    @Override
    public void onEntityHit(LivingEntity entityHit) {
        if (!entityHit.isOnFire()) {
            // Short enough fire duration to not cause fire damage but still drop cooked items
            entityHit.setRemainingFireTicks(1);
        }

        super.onEntityHit(entityHit);

        if (!entityHit.level().isClientSide()) {
            int level = DragonAbilities.getAbility(player, BurnAbility.class).map(DragonAbility::getLevel).orElse(0);

            if (entityHit.getRandom().nextInt(100) < level * 15) {
                entityHit.getData(DSDataAttachments.ENTITY_HANDLER).lastAfflicted = player != null ? player.getId() : -1;
                entityHit.addEffect(new MobEffectInstance(DSEffects.BURN, Functions.secondsToTicks(10), 0, false, true));
            }
        }
    }

    @Override
    public void onDamage(LivingEntity entity) {
        entity.setRemainingFireTicks(Functions.secondsToTicks(30));
    }

    public static float getDamage(int level) {
        return (float) (damage * level);
    }

    @Override
    public float getDamage() {
        return getDamage(getLevel());
    }

    @Override
    public int getSkillChargeTime() {
        return Functions.secondsToTicks(castTime);
    }

    @Override
    public int getContinuousManaCostTime() {
        return Functions.secondsToTicks(sustainedManaCostTickRate);
    }

    @Override
    public int getInitManaCost() {
        return initialManaCost;
    }

    @Override
    public void castComplete(Player player) {
        if(!player.level().isClientSide) {
            Holder<by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.DragonAbility> ability = player.registryAccess().holderOrThrow(by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.DragonAbilities.NETHER_BREATH);
            testInstance = new DragonAbilityInstance(ability);
        }
        if (player.level().isClientSide() && FMLEnvironment.dist.isClient()) {
            stopSound();
        }
    }

    @Override
    @SuppressWarnings("RedundantMethodOverride")
    public boolean requiresStationaryCasting() {
        return false;
    }
}*/