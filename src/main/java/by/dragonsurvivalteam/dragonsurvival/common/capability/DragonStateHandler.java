package by.dragonsurvivalteam.dragonsurvival.common.capability;

import by.dragonsurvivalteam.dragonsurvival.commands.DragonCommand;
import by.dragonsurvivalteam.dragonsurvival.common.capability.objects.DragonMovementData;
import by.dragonsurvivalteam.dragonsurvival.common.capability.subcapabilities.*;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonType;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.DragonTypes;
import by.dragonsurvivalteam.dragonsurvival.common.handlers.magic.HunterHandler;
import by.dragonsurvivalteam.dragonsurvival.common.items.growth.StarHeartItem;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import by.dragonsurvivalteam.dragonsurvival.mixins.PlayerEndMixin;
import by.dragonsurvivalteam.dragonsurvival.mixins.PlayerStartMixin;
import by.dragonsurvivalteam.dragonsurvival.network.client.ClientProxy;
import by.dragonsurvivalteam.dragonsurvival.network.player.SyncSize;
import by.dragonsurvivalteam.dragonsurvival.registry.DSAdvancementTriggers;
import by.dragonsurvivalteam.dragonsurvival.registry.DSModifiers;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.DragonBodies;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.DragonBody;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.DragonStage;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.DragonStages;
import by.dragonsurvivalteam.dragonsurvival.util.DragonUtils;
import by.dragonsurvivalteam.dragonsurvival.util.Functions;
import by.dragonsurvivalteam.dragonsurvival.util.ToolUtils;
import net.minecraft.ResourceLocationException;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import javax.annotation.Nullable;

public class DragonStateHandler extends EntityStateHandler {
    public static final int NO_SIZE = -1;

    @SuppressWarnings("unchecked")
    public final Supplier<SubCap>[] caps = new Supplier[]{this::getSkinData, this::getMagicData, this::getEmoteData, this::getClawToolData};

    // --- Tool swap --- //

    /** Used in {@link PlayerStartMixin} and {@link PlayerEndMixin} */
    public ItemStack storedMainHandWeapon = ItemStack.EMPTY;
    public boolean switchedWeapon;

    public ItemStack storedMainHandTool = ItemStack.EMPTY;
    public boolean switchedTool;
    public int switchedToolSlot = -1;

    /** To track the state if a tool swap is triggered within a tool swap (should only swap back if the last tool swap finishes) */
    public int toolSwapLayer;

    // --- Other --- //

    /** Translucent rendering in the inventory screen leads to issues (invisible model) */
    public boolean isBeingRenderedInInventory;

    /** Only needs to be updated on effect removal (server -> client) */
    private int hunterStacks;

    public boolean isGrowing = true;
    public StarHeartItem.State starHeartState = StarHeartItem.State.INACTIVE;

    public boolean treasureResting;
    public int treasureRestTimer;
    public int treasureSleepTimer;

    public int altarCooldown;
    public boolean hasUsedAltar;
    public boolean isInAltar;
    public boolean refreshBody;

    /** Last timestamp the server synchronized the player */
    public int lastSync;

    private final DragonMovementData movementData = new DragonMovementData();
    private final ClawInventory clawToolData = new ClawInventory(this);
    private final EmoteCap emoteData = new EmoteCap(this);
    private final MagicCap magicData = new MagicCap(this);
    private final SkinCap skinData = new SkinCap(this);
    private final Map<String, Double> savedDragonSize = new ConcurrentHashMap<>();

    private AbstractDragonType dragonType;
    private Holder<DragonBody> dragonBody;
    private Holder<DragonStage> dragonStage;

    private int passengerId = -1;
    private boolean hasFlight;
    private boolean areWingsSpread;
    private double size = NO_SIZE;
    private boolean destructionEnabled;

    // Needed to calculate collision damage correctly when flying. See ServerFlightHandler.
    public Vec3 preCollisionDeltaMovement = Vec3.ZERO;

    public void setFreeLook(boolean isFreeLook) {
        movementData.wasFreeLook = movementData.isFreeLook;
        movementData.isFreeLook = isFreeLook;
    }

    public void setFirstPerson(boolean isFirstPerson) {
        movementData.isFirstPerson = isFirstPerson;
    }

    public void setBite(boolean bite) {
        movementData.bite = bite;
    }

    public void setDesiredMoveVec(Vec2 desiredMoveVec) {
        movementData.desiredMoveVec = desiredMoveVec;
    }

    public void setMovementData(double bodyYaw, double headYaw, double headPitch, Vec3 deltaMovement) {
        movementData.headYawLastFrame = movementData.headYaw;
        movementData.bodyYawLastFrame = movementData.bodyYaw;
        movementData.headPitchLastFrame = movementData.headPitch;
        movementData.deltaMovementLastFrame = movementData.deltaMovement;

        movementData.bodyYaw = bodyYaw;
        movementData.headYaw = headYaw;
        movementData.headPitch = headPitch;
        movementData.deltaMovement = deltaMovement;
    }

    /** Sets the size and retains the current stage */
    public void setClientSize(double size) {
        setClientSize(dragonStage, size);
    }

    /** Sets the stage and retains the current size */
    public void setClientSize(@Nullable final Holder<DragonStage> dragonStage) {
        setClientSize(dragonStage, size);
    }

    public void setClientSize(@Nullable final Holder<DragonStage> dragonStage, double size) {
        Holder<DragonStage> oldLevel = this.dragonStage;
        updateSizeAndStage(null, dragonStage, size);

        if (oldLevel == null || this.dragonStage != null && !this.dragonStage.is(oldLevel)) {
            if (FMLEnvironment.dist.isClient()) { // When deserializing nbt there is no player context
                // Only need to update when the level changes (for the skin)
                ClientProxy.sendClientData();
            }
        }
    }

    /** Sets the size and retains the current stage */
    public void setSize(final Player player, double size) {
        setSize(player, dragonStage, size);
    }

    /** Sets the stage and retains the current size */
    public void setSize(final Player player, @Nullable final Holder<DragonStage> dragonStage) {
        setSize(player, dragonStage, size);
    }

    public void setSize(final Player player, @Nullable final Holder<DragonStage> dragonStage, double size) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            setClientSize(dragonStage, size);
            return;
        }

        double oldSize = this.size;
        Holder<DragonStage> oldStage = this.dragonStage;
        updateSizeAndStage(serverPlayer.registryAccess(), dragonStage, size);

        if (oldSize != this.size || oldStage == null || this.dragonStage != null && !this.dragonStage.is(oldStage)) {
            PacketDistributor.sendToPlayersTrackingEntityAndSelf(serverPlayer, new SyncSize(serverPlayer.getId(), getStage(), getSize()));
            DSAdvancementTriggers.BE_DRAGON.get().trigger(serverPlayer);
            serverPlayer.refreshDimensions();

            setSavedDragonSize(dragonType.getTypeNameLowerCase(), size);
            DSModifiers.updateSizeModifiers(player, this);
        }
    }

    private void updateSizeAndStage(@Nullable final HolderLookup.Provider provider, @Nullable final Holder<DragonStage> dragonStage, double size) {
        if (dragonStage == null || size == NO_SIZE) {
            this.dragonStage = null;
            this.size = NO_SIZE;
        } else if (!dragonStage.value().sizeRange().matches(size)) {
            DragonStage.getNextStage(provider, dragonStage.value()).ifPresent(nextStage -> this.dragonStage = nextStage);
            this.size = this.dragonStage.value().getBoundedSize(size);
        } else {
            this.dragonStage = dragonStage;
            this.size = this.dragonStage.value().getBoundedSize(size);
        }
    }

    public double getSavedDragonSize(final String type) {
        Double value = savedDragonSize.get(type);
        value = value == null ? DragonStateHandler.NO_SIZE : value;

        return value;
    }

    public void setSavedDragonSize(final String type, double size) {
        Double value = savedDragonSize.get(type);

        if (size == 0 || (value != null && value == size)) {
            return;
        }

        savedDragonSize.put(type, size);
    }

    // TODO :: use optional for these?
    public AbstractDragonType getType() {
        return dragonType;
    }

    public Holder<DragonStage> getStage() {
        return dragonStage;
    }

    public Holder<DragonBody> getBody() {
        return dragonBody;
    }

    public String getTypeName() {
        if (dragonType == null) {
            return "human";
        }

        return dragonType.getTypeName();
    }

    public String getTypeNameLowerCase() {
        if (dragonType == null) {
            return "human";
        }

        return dragonType.getTypeNameLowerCase();
    }

    public String getSubtypeName() {
        if (dragonType == null) {
            return "human";
        }

        return dragonType.getSubtypeName();
    }

    public void setType(final AbstractDragonType type, Player player) {
        AbstractDragonType oldType = dragonType;
        setType(type);

        if (!DragonUtils.isType(oldType, dragonType)) {
            DSModifiers.updateTypeModifiers(player, this);
            skinData.skinPreset.initDefaults(dragonType);
        }
    }

    /** Only used for rendering related code - to properly set the type (and update modifiers) use {@link DragonStateHandler#setType(AbstractDragonType, Player)} */
    public void setType(final AbstractDragonType type) {
        if (type == null) {
            dragonType = null;
            return;
        }

        if (DragonUtils.isType(dragonType, type)) {
            return;
        }

        getMagicData().initAbilities(type);
        dragonType = DragonTypes.newDragonTypeInstance(type.getSubtypeName());
    }

    public void setBody(final Holder<DragonBody> body, Player player) {
        Holder<DragonBody> oldBody = dragonBody;
        setBody(body);

        if (!DragonUtils.isBody(oldBody, dragonBody)) {
            DSModifiers.updateBodyModifiers(player, this);
        }
    }

    /** Only used for rendering (does not update modifiers) */
    public void setBody(final Holder<DragonBody> body) {
        if (body == null) {
            dragonType = null;
            return;
        }

        if (dragonBody == null || !DragonUtils.isBody(body, dragonBody)) {
            dragonBody = body;
            refreshBody = true;
        }
    }

    public boolean hasValidClawTool(final BlockState state) {
        if (!isDragon()) {
            return false;
        }

        for (int i = 0; i < 4; i++) {
            ItemStack stack = getClawToolData().getClawsInventory().getItem(i);

            if (stack.isCorrectToolForDrops(state) || stack.getDestroySpeed(state) > 1) {
                return true;
            }
        }

        return false;
    }

    /** Determines if the current dragon type can harvest the supplied block (with or without tools) (configured harvest bonuses are taken into account) */
    public boolean canHarvestWithPaw(final BlockState state) {
        if (hasValidClawTool(state)) {
            return true;
        }

        return canHarvestWithPawNoTools(state);
    }

    /** Determines if the current dragon type can harvest the supplied block without a tool (configured harvest bonuses are taken into account) */
    public boolean canHarvestWithPawNoTools(final BlockState state) {
        if (!isDragon()) {
            return false;
        }

        return getDragonHarvestLevel(state) >= ToolUtils.getRequiredHarvestLevel(state);
    }

    /**
     * Returns the calculated harvest level (based on the unlocked bonuses) (-1 for non-dragons) <br>
     * The unlockable harvest level bonus is only considered if the supplied state is part of {@link AbstractDragonType#harvestableBlocks()} <br>
     * If the supplied state is 'null' then the unlockable bonuses are also considered
     */
    public int getDragonHarvestLevel(@Nullable final BlockState state) {
        if (!isDragon()) {
            return -1;
        }

        int harvestLevel = 0;

        if (state == null || state.is(getType().harvestableBlocks())) {
            harvestLevel = getStage().value().harvestLevelBonus();
        }

        return harvestLevel;
    }

    public void setPassengerId(int passengerId) {
        this.passengerId = passengerId;
    }

    public void setWingsSpread(boolean areWingsSpread) {
        this.areWingsSpread = areWingsSpread;
    }

    public void setDestructionEnabled(boolean destructionEnabled) {
        this.destructionEnabled = destructionEnabled;
    }

    public void setHasFlight(boolean hasFlight) {
        this.hasFlight = hasFlight;
    }

    public MagicCap getMagicData() {
        return magicData;
    }

    public DragonMovementData getMovementData() {
        return movementData;
    }

    public double getSize() {
        return size;
    }

    public boolean getDestructionEnabled() {
        return destructionEnabled;
    }

    public boolean isDragon() {
        return dragonType != null && dragonBody != null && dragonStage != null;
    }

    public int getPassengerId() {
        return passengerId;
    }

    public EmoteCap getEmoteData() {
        return emoteData;
    }

    public SkinCap getSkinData() {
        return skinData;
    }

    public boolean hasFlight() {
        return hasFlight;
    }

    public boolean isWingsSpread() {
        return hasFlight && areWingsSpread;
    }

    public ClawInventory getClawToolData() {
        return clawToolData;
    }

    public CompoundTag serializeNBT(HolderLookup.Provider provider, boolean isSavingForSoul) {
        CompoundTag tag = new CompoundTag();
        tag.putString("type", dragonType != null ? dragonType.getTypeName() : "none");
        tag.putString("subtype", dragonType != null ? dragonType.getSubtypeName() : "none");
        tag.putString(DRAGON_BODY, dragonBody != null ? Objects.requireNonNull(dragonBody.getKey()).location().toString() : "none");
        tag.putString(DRAGON_STAGE, dragonStage != null ? Objects.requireNonNull(dragonStage.getKey()).location().toString() : "none");

        if (isDragon()) {
            tag.put("typeData", dragonType.writeNBT());

            //Rendering
            DragonMovementData movementData = getMovementData();
            tag.putBoolean("bite", movementData.bite);
            tag.putBoolean("dig", movementData.dig);

            //Spin attack
            tag.putInt("spinCooldown", movementData.spinCooldown);
            tag.putInt("spinAttack", movementData.spinAttack);

            tag.putDouble("size", getSize());
            tag.putBoolean("destructionEnabled", getDestructionEnabled());
            tag.putBoolean(IS_GROWING, isGrowing);
            tag.putInt(STAR_HEART_STATE, starHeartState.ordinal());

            tag.putBoolean("isFlying", isWingsSpread());

            tag.putBoolean("resting", treasureResting);
            tag.putInt("restingTimer", treasureRestTimer);
        }

        if (isDragon() || ServerConfig.saveAllAbilities) {
            tag.putBoolean("spinLearned", getMovementData().spinLearned);
            tag.putBoolean("hasWings", hasFlight());
        }

        // Only store the size of the dragon the player is currently in if we are saving for the soul
        if (isSavingForSoul) {
            switch (getTypeName()) {
                case "sea" -> tag.putDouble("seaSize", getSavedDragonSize(DragonTypes.SEA.getTypeName()));
                case "cave" -> tag.putDouble("caveSize", getSavedDragonSize(DragonTypes.CAVE.getTypeName()));
                case "forest" -> tag.putDouble("forestSize", getSavedDragonSize(DragonTypes.FOREST.getTypeName()));
            }
        } else {
            tag.putDouble("seaSize", getSavedDragonSize(DragonTypes.SEA.getTypeName()));
            tag.putDouble("caveSize", getSavedDragonSize(DragonTypes.CAVE.getTypeName()));
            tag.putDouble("forestSize", getSavedDragonSize(DragonTypes.FOREST.getTypeName()));
        }

        for (int i = 0; i < caps.length; i++) {
            if (isSavingForSoul) {
                if (/* Emote Data */ i == 2 || /* Claw Tool Data */ i == 3) {
                    continue;
                }
            }

            tag.put("cap_" + i, caps[i].get().serializeNBT(provider));
        }

        tag.putInt("altarCooldown", altarCooldown);
        tag.putBoolean("usedAltar", hasUsedAltar);

        tag.put(ENTITY_STATE, super.serializeNBT(provider));

        return tag;
    }

    @Override
    public CompoundTag serializeNBT(@NotNull final HolderLookup.Provider provider) {
        return serializeNBT(provider, false);
    }

    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag, boolean isLoadingForSoul) {
        if (tag.getAllKeys().contains("subtype")) {
            dragonType = DragonTypes.newDragonTypeInstance(tag.getString("subtype"));
        } else {
            dragonType = DragonTypes.newDragonTypeInstance(tag.getString("type"));
        }

        if (dragonType != null && tag.contains("typeData")) {
            dragonType.readNBT(tag.getCompound("typeData"));
        }

        try {
            ResourceLocation bodyLocation = ResourceLocation.parse(tag.getString(DRAGON_BODY));
            Optional<Holder.Reference<DragonBody>> optionalBody = provider.holder(DragonBodies.key(bodyLocation));
            optionalBody.ifPresent(body -> dragonBody = body);
        } catch (ResourceLocationException ignored) {}

        try {
            ResourceLocation levelLocation = ResourceLocation.parse(tag.getString(DRAGON_STAGE));
            Optional<Holder.Reference<DragonStage>> optionalLevel = provider.holder(DragonStages.key(levelLocation));
            optionalLevel.ifPresent(level -> dragonStage = level);
        } catch (ResourceLocationException ignored) {}

        if (dragonType != null) {
            if (dragonBody == null) {
                // This can happen if a dragon body gets removed
                dragonBody = DragonBody.random(provider);
            }

            setBite(tag.getBoolean("bite"));
            getMovementData().headYawLastFrame = getMovementData().headYaw;
            getMovementData().bodyYawLastFrame = getMovementData().bodyYaw;
            getMovementData().headPitchLastFrame = getMovementData().headPitch;
            getMovementData().dig = tag.getBoolean("dig");

            setWingsSpread(tag.getBoolean("isFlying"));

            getMovementData().spinCooldown = tag.getInt("spinCooldown");
            getMovementData().spinAttack = tag.getInt("spinAttack");

            if (dragonStage == null) {
                setClientSize(tag.getDouble("size"));
            } else {
                setClientSize(dragonStage, tag.getDouble("size"));
            }

            setDestructionEnabled(tag.getBoolean("destructionEnabled"));
            isGrowing = !tag.contains(IS_GROWING) || tag.getBoolean(IS_GROWING);
            starHeartState = StarHeartItem.State.values()[tag.getInt(STAR_HEART_STATE)];

            treasureResting = tag.getBoolean("resting");
            treasureRestTimer = tag.getInt("restingTimer");
        }

        if (isDragon() || ServerConfig.saveAllAbilities) {
            getMovementData().spinLearned = tag.getBoolean("spinLearned");
            setHasFlight(tag.getBoolean("hasWings"));
        }

        // Only load the size of the dragon the player is currently in if we are loading for the soul
        if (isLoadingForSoul) {
            switch (getTypeName()) {
                case "sea" -> setSavedDragonSize(DragonTypes.SEA.getTypeName(), tag.getDouble("seaSize"));
                case "cave" -> setSavedDragonSize(DragonTypes.CAVE.getTypeName(), tag.getDouble("caveSize"));
                case "forest" -> setSavedDragonSize(DragonTypes.FOREST.getTypeName(), tag.getDouble("forestSize"));
            }
        } else {
            setSavedDragonSize(DragonTypes.SEA.getTypeName(), tag.getDouble("seaSize"));
            setSavedDragonSize(DragonTypes.CAVE.getTypeName(), tag.getDouble("caveSize"));
            setSavedDragonSize(DragonTypes.FOREST.getTypeName(), tag.getDouble("forestSize"));
        }

        for (int i = 0; i < caps.length; i++) {
            if (isLoadingForSoul) {
                if (i == 2) {
                    continue;
                }
            }

            if (isLoadingForSoul) {
                if (i == 3) {
                    continue;
                }
            }

            if (tag.contains("cap_" + i)) {
                caps[i].get().deserializeNBT(provider, (CompoundTag) tag.get("cap_" + i));
            }
        }

        altarCooldown = tag.getInt("altarCooldown");
        hasUsedAltar = tag.getBoolean("usedAltar");

        super.deserializeNBT(provider, tag.getCompound(ENTITY_STATE));

        if (isDragon()) {
            refreshBody = true;
            getSkinData().compileSkin(getStage());
        }
    }

    @Override
    public void deserializeNBT(@NotNull final HolderLookup.Provider provider, final CompoundTag tag) {
        deserializeNBT(provider, tag, false);
    }

    public void revertToHumanForm(Player player, boolean isRevertingFromSoul) {
        // Don't set the saved dragon size if we are reverting from a soul, as we already are storing the size of the dragon in the soul
        if (ServerConfig.saveGrowthStage && !isRevertingFromSoul) {
            this.setSavedDragonSize(this.getTypeName(), this.getSize());
        }

        // Drop everything in your claw slots
        DragonCommand.reInsertClawTools(player, this);

        setType(null);
        setBody(null, player);
        setSize(player, null, NO_SIZE);

        if (!ServerConfig.saveAllAbilities) {
            this.getMovementData().spinLearned = false;
            this.setHasFlight(false);
        }

        altarCooldown = Functions.secondsToTicks(ServerConfig.altarUsageCooldown);
        hasUsedAltar = true;
    }

    // --- Hunter handler --- //

    public void modifyHunterStacks(int modification) {
        hunterStacks = Math.clamp(hunterStacks + modification, 0, HunterHandler.MAX_HUNTER_STACKS);
    }

    public boolean hasMaxHunterStacks() {
        return hunterStacks == HunterHandler.MAX_HUNTER_STACKS;
    }

    public boolean hasHunterStacks() {
        return hunterStacks > 0;
    }

    public void clearHunterStacks() {
        hunterStacks = 0;
    }

    public int getHunterStacks() {
        return hunterStacks;
    }

    public static final String DRAGON_BODY = "dragon_body";
    public static final String DRAGON_STAGE = "dragon_stage";
    public static final String ENTITY_STATE = "entity_state";

    public static final String STAR_HEART_STATE = "star_heart_state";
    public static final String IS_GROWING = "is_growing";
}