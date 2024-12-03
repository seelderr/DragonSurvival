package by.dragonsurvivalteam.dragonsurvival.common.capability;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvival;
import by.dragonsurvivalteam.dragonsurvival.commands.DragonCommand;
import by.dragonsurvivalteam.dragonsurvival.common.capability.subcapabilities.EmoteCap;
import by.dragonsurvivalteam.dragonsurvival.common.capability.subcapabilities.SkinCap;
import by.dragonsurvivalteam.dragonsurvival.common.capability.subcapabilities.SubCap;
import by.dragonsurvivalteam.dragonsurvival.common.items.growth.StarHeartItem;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import by.dragonsurvivalteam.dragonsurvival.network.client.ClientProxy;
import by.dragonsurvivalteam.dragonsurvival.network.player.SyncSize;
import by.dragonsurvivalteam.dragonsurvival.registry.DSAdvancementTriggers;
import by.dragonsurvivalteam.dragonsurvival.registry.DSModifiers;
import by.dragonsurvivalteam.dragonsurvival.registry.attachments.*;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.DragonType;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.body.DragonBodies;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.body.DragonBody;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.stage.DragonStage;
import by.dragonsurvivalteam.dragonsurvival.util.DragonUtils;
import by.dragonsurvivalteam.dragonsurvival.util.Functions;
import by.dragonsurvivalteam.dragonsurvival.util.ResourceHelper;
import by.dragonsurvivalteam.dragonsurvival.util.ToolUtils;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import javax.annotation.Nullable;

public class DragonStateHandler extends EntityStateHandler {
    public static final int NO_SIZE = -1;

    @SuppressWarnings("unchecked")
    public final Supplier<SubCap>[] caps = new Supplier[]{this::getSkinData, this::getEmoteData};

    public record SavedDragonStage(@Nullable Holder<DragonStage> dragonStage, @Nullable Holder<DragonStage> previousStage, double size) { /* Nothing to do */ }

    // --- Other --- //

    /** Translucent rendering in the inventory screen leads to issues (invisible model) */
    public boolean isBeingRenderedInInventory;

    /** Only needs to be updated on effect removal (server -> client) */
    private int hunterStacks;

    public boolean isGrowing = true;
    public StarHeartItem.State starHeartState = StarHeartItem.State.INACTIVE;

    public boolean refreshBody;

    /** Last timestamp the server synchronized the player */
    public int lastSync;
    private final EmoteCap emoteData = new EmoteCap(this);
    private final SkinCap skinData = new SkinCap(this);
    private final Map<ResourceKey<DragonType>, SavedDragonStage> savedDragonStages = new ConcurrentHashMap<>();

    private Holder<DragonType> dragonType;
    private Holder<DragonBody> dragonBody;
    private Holder<DragonStage> dragonStage;
    public Holder<DragonStage> previousStage;

    private int passengerId = -1;
    private boolean hasFlight;
    private boolean areWingsSpread;
    private double size = NO_SIZE;
    private boolean destructionEnabled;

    // Needed to calculate collision damage correctly when flying. See ServerFlightHandler.
    public Vec3 preCollisionDeltaMovement = Vec3.ZERO;

    /** Sets the size and retains the current stage */
    public void setClientSize(double size) {
        setClientSize(dragonStage, size);
    }

    /** Sets the stage and retains the current size */
    public void setClientSize(@Nullable final Holder<DragonStage> dragonStage) {
        setClientSize(dragonStage, size);
    }

    public void setClientSize(@Nullable final Holder<DragonStage> dragonStage, double size) {
        Holder<DragonStage> oldStage = this.dragonStage;
        updateSizeAndStage(null, dragonStage, size);

        if (this.dragonStage == null) {
            return;
        }

        if (oldStage == null || !this.dragonStage.is(oldStage)) {
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

        if (this.dragonStage == null) {
            DSModifiers.updateSizeModifiers(player, this);
            return;
        }

        if (oldSize != this.size || oldStage == null || !this.dragonStage.is(oldStage)) {
            PacketDistributor.sendToPlayersTrackingEntityAndSelf(serverPlayer, new SyncSize(serverPlayer.getId(), getStage(), getSize()));
            DSAdvancementTriggers.BE_DRAGON.get().trigger(serverPlayer);
            serverPlayer.refreshDimensions();

            setSavedDragonStage(dragonType.getKey(), new SavedDragonStage(dragonStage, previousStage, size));
            DSModifiers.updateSizeModifiers(player, this);
        }
    }

    private void updateSizeAndStage(@Nullable final HolderLookup.Provider provider, @Nullable final Holder<DragonStage> dragonStage, double size) {
        if (dragonStage == null || size == NO_SIZE) {
            this.dragonStage = null;
            this.size = NO_SIZE;
            return;
        }

        double newSize = DragonStage.getValidSize(size);

        if (!dragonStage.value().sizeRange().matches(newSize)) {
            if (newSize > dragonStage.value().sizeRange().max()) {
                Optional<Holder.Reference<DragonStage>> nextStage = DragonStage.getNextStage(provider, dragonStage.value());

                // Find the next dragon stage in the chain that matches with the given size
                while (nextStage.isPresent()) {
                    this.dragonStage = nextStage.get();

                    if (!this.dragonStage.value().sizeRange().matches(newSize)) {
                        nextStage = DragonStage.getNextStage(provider, dragonStage.value());
                    } else {
                        nextStage = Optional.empty();
                    }
                }
            } else {
                this.dragonStage = Objects.requireNonNullElseGet(previousStage, () -> DragonStage.get(provider, newSize));
            }

        } else {
            this.dragonStage = dragonStage;
        }

        this.size = this.dragonStage.value().getBoundedSize(newSize);
    }

    public @Nullable SavedDragonStage getSavedDragonStage(final ResourceKey<DragonType> dragonType) {
        return savedDragonStages.get(dragonType);
    }

    private void setSavedDragonStage(final ResourceKey<DragonType> dragonType, final SavedDragonStage savedDragonStage) {
        if (savedDragonStage == null) {
            savedDragonStages.remove(dragonType);
            return;
        }

        if (savedDragonStage.dragonStage() == null || savedDragonStage.size() == NO_SIZE) {
            return;
        }

        savedDragonStages.put(dragonType, savedDragonStage);
    }

    private void setSavedDragonStage(final ResourceKey<DragonType> dragonType) {
        setSavedDragonStage(dragonType, new SavedDragonStage(dragonStage, previousStage, size));
    }

    // TODO :: use optional for these?
    public Holder<DragonType> getType() {
        return dragonType;
    }

    public Holder<DragonStage> getStage() {
        return dragonStage;
    }

    public Holder<DragonType> getDragonType() {
        return dragonType;
    }

    public Holder<DragonBody> getBody() {
        return dragonBody;
    }

    public void setType(final Holder<DragonType> type, Player player) {
        Holder<DragonType> oldType = dragonType;
        setType(type);

        MagicData magicData = MagicData.getData(player);
        if (type != null) {
            if (oldType == null || !oldType.is(type.getKey())) {
                DSModifiers.updateTypeModifiers(player, this);
                skinData.skinPreset.initDefaults(dragonType.getKey());
                magicData.refresh(dragonType);
            }
        } else {
            DSModifiers.clearModifiers(player);
        }
    }

    /** Only used for rendering related code - to properly set the type (and update modifiers) use {@link DragonStateHandler#setType(Holder, Player)} */
    public void setType(final Holder<DragonType> type) {
        dragonType = type;
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

    /** Determines if the current dragon type can harvest the supplied block (with or without tools) (configured harvest bonuses are taken into account) */
    public boolean canHarvestWithPaw(final Player player, final BlockState state) {
        if (isDragon() && ClawInventoryData.getData(player).hasValidClawTool(state)) {
            return true;
        }

        return canHarvestWithPawNoTools(player, state);
    }

    /** Determines if the current dragon type can harvest the supplied block without a tool (configured harvest bonuses are taken into account) */
    public boolean canHarvestWithPawNoTools(final Player player, final BlockState state) {
        if (!isDragon()) {
            return false;
        }

        return getDragonHarvestLevel(player, state) >= ToolUtils.getRequiredHarvestLevel(state);
    }

    /**
     * Returns the calculated harvest level (based on the unlocked bonuses) (-1 for non-dragons) <br>
     * The unlockable harvest level bonus is only considered if the supplied state is part of {@link AbstractDragonType#harvestableBlocks()} <br>
     * If the supplied state is 'null' then the unlockable bonuses are also considered
     */
    public int getDragonHarvestLevel(final Player player, @Nullable final BlockState state) {
        if (!isDragon()) {
            return -1;
        }

        int harvestLevel = 0;

        // TODO
        /*if (state == null || state.is(getType().harvestableBlocks())) {
            harvestLevel = getStage().value().harvestLevelBonus();
        }*/

        if (state != null) {
            harvestLevel += player.getExistingData(DSDataAttachments.HARVEST_BONUSES).map(data -> data.get(state)).orElse(0);
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

    public CompoundTag serializeNBT(HolderLookup.Provider provider, boolean isSavingForSoul) {
        CompoundTag tag = new CompoundTag();
        tag.putString(DRAGON_BODY, dragonBody != null ? Objects.requireNonNull(dragonBody.getKey()).location().toString() : "none");
        tag.putString(DRAGON_STAGE, dragonStage != null ? Objects.requireNonNull(dragonStage.getKey()).location().toString() : "none");
        tag.putString(DRAGON_TYPE, dragonType != null ? Objects.requireNonNull(dragonType.getKey()).location().toString() : "none");

        if (isDragon()) {
            tag.putDouble("size", getSize());
            tag.putBoolean("destructionEnabled", getDestructionEnabled());
            tag.putBoolean(IS_GROWING, isGrowing);
            tag.putInt(STAR_HEART_STATE, starHeartState.ordinal());

            tag.putBoolean("isFlying", isWingsSpread());
        }

        if (isDragon() || ServerConfig.saveAllAbilities) {
            tag.putBoolean("hasWings", hasFlight());
        }

        if (isSavingForSoul && getType() != null) {
            // Only store the size of the dragon the player is currently in if we are saving for the soul
            storeSavedStage(provider, getStage(), tag);
        } else if (!isSavingForSoul) {
            for (ResourceKey<DragonType> type : ResourceHelper.keys(provider, DragonType.REGISTRY)) {
                DragonStateHandler.SavedDragonStage savedStage = getSavedDragonStage(type);
                if(savedStage != null) {
                    storeSavedStage(provider, savedStage.dragonStage, tag);
                }
            }
        }

        for (int i = 0; i < caps.length; i++) {
            if (isSavingForSoul) {
                if (/* Emote Data */ i == 1) {
                    continue;
                }
            }

            tag.put("cap_" + i, caps[i].get().serializeNBT(provider));
        }

        tag.put(ENTITY_STATE, super.serializeNBT(provider));

        return tag;
    }

    @Override
    public CompoundTag serializeNBT(@NotNull final HolderLookup.Provider provider) {
        return serializeNBT(provider, false);
    }

    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag, boolean isLoadingForSoul) { // TODO :: make a different method for soul
        String storedDragonType = tag.getString(DRAGON_TYPE);

        if(!storedDragonType.isEmpty()) {
            provider.holder(by.dragonsurvivalteam.dragonsurvival.registry.dragon.DragonTypes.key(ResourceLocation.parse(storedDragonType)))
                    .ifPresentOrElse(realDragonType -> this.dragonType = realDragonType,
                            () -> DragonSurvival.LOGGER.warn("Cannot set dragon type [{}] while deserializing NBT of [{}] due to the dragon type not existing", storedDragonType, tag));
        }


        String storedDragonBody = tag.getString(DRAGON_BODY);

        if (!storedDragonBody.isEmpty()) {
            provider.holder(DragonBodies.key(ResourceLocation.parse(storedDragonBody)))
                    .ifPresentOrElse(dragonBody -> this.dragonBody = dragonBody,
                            () -> DragonSurvival.LOGGER.warn("Cannot set dragon body [{}] while deserializing NBT of [{}] due to the dragon body not existing", storedDragonBody, tag));
        }

        double size = tag.getDouble(SIZE);
        Holder<DragonStage> dragonStage = null;

        var loadedDragonStageKey = ResourceKey.codec(DragonStage.REGISTRY).parse(provider.createSerializationContext(NbtOps.INSTANCE), tag.get(DRAGON_STAGE));
        if(loadedDragonStageKey.isSuccess()) {
            var loadedDragonStage = provider.lookup(DragonStage.REGISTRY).get().get(loadedDragonStageKey.getOrThrow());
            if(loadedDragonStage.isPresent()) {
                dragonStage = loadedDragonStage.get();
            } else if(size != NO_SIZE) {
                dragonStage = DragonStage.get(provider, size);
                DragonSurvival.LOGGER.warn("Cannot set dragon stage [{}] while deserializing NBT of [{}] due to the dragon stage not existing - setting [{}] as fallback", tag.get(DRAGON_STAGE), tag, dragonStage);
            }
        }

        var loadedPreviousDragonStageKey = ResourceKey.codec(DragonStage.REGISTRY).parse(provider.createSerializationContext(NbtOps.INSTANCE), tag.get(PREVIOUS_DRAGON_STAGE));
        if(loadedPreviousDragonStageKey.isSuccess()) {
            var loadedPreviousDragonStage = provider.lookup(DragonStage.REGISTRY).get().get(loadedPreviousDragonStageKey.getOrThrow());
            if(loadedPreviousDragonStage.isPresent()) {
                this.previousStage = loadedPreviousDragonStage.get();
            } else {
                DragonSurvival.LOGGER.warn("Cannot set previous dragon stage [{}] while deserializing NBT of [{}] due to the previous dragon stage not existing", tag.get(PREVIOUS_DRAGON_STAGE), tag);
            }
        }

        if (dragonType != null) {
            if (dragonBody == null) {
                // This can happen if a dragon body gets removed
                dragonBody = DragonBody.random(provider);
            }

            setWingsSpread(tag.getBoolean("isFlying"));

            // Make sure a stage is set if the player was deserialized as a dragon
            // It could be missing here if the NBT is loaded from an old save
            setClientSize(dragonStage != null ? dragonStage : DragonStage.get(provider, size), size);

            setDestructionEnabled(tag.getBoolean("destructionEnabled"));
            isGrowing = !tag.contains(IS_GROWING) || tag.getBoolean(IS_GROWING);
            starHeartState = StarHeartItem.State.values()[tag.getInt(STAR_HEART_STATE)];
        }

        if (isDragon() || ServerConfig.saveAllAbilities) {
            // TODO: How do we replicate this behavior in DragonSpinData?
            //getMovementData().spinLearned = tag.getBoolean("spinLearned");
            setHasFlight(tag.getBoolean("hasWings"));
        }

        if (isLoadingForSoul && getType() != null) {
            // Only load the size of the dragon the player is currently in if we are loading for the soul
            setSavedDragonStage(dragonType.getKey(), loadSavedStage(provider, dragonType.getKey(), tag));
        } else if (!isLoadingForSoul) {
            for (ResourceKey<DragonType> type : ResourceHelper.keys(provider, DragonType.REGISTRY)) {
                setSavedDragonStage(type, loadSavedStage(provider, type, tag));
            }
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

        super.deserializeNBT(provider, tag.getCompound(ENTITY_STATE));

        if (isDragon()) {
            refreshBody = true;
            getSkinData().compileSkin(getStage());
        }
    }

    private @Nullable SavedDragonStage loadSavedStage(final HolderLookup.Provider provider, final ResourceKey<DragonType> dragonType, final CompoundTag tag) {
        CompoundTag compound = tag.getCompound(dragonType.toString() + SAVED_STAGE_SUFFIX);

        if (compound.isEmpty()) {
            return null;
        }

        Holder<DragonStage> dragonStage = provider.lookup(DragonStage.REGISTRY).get().getOrThrow(ResourceKey.codec(DragonStage.REGISTRY).parse(provider.createSerializationContext(NbtOps.INSTANCE), tag.get(DRAGON_STAGE)).getOrThrow());
        Holder<DragonStage> previousDragonStage;
        if(tag.contains(PREVIOUS_DRAGON_STAGE)) {
            previousDragonStage = provider.lookup(DragonStage.REGISTRY).get().getOrThrow(ResourceKey.codec(DragonStage.REGISTRY).parse(provider.createSerializationContext(NbtOps.INSTANCE), tag.get(PREVIOUS_DRAGON_STAGE)).getOrThrow());
        } else {
            previousDragonStage = null;
        }
        double size = compound.getDouble(SIZE);

        return new SavedDragonStage(dragonStage, previousDragonStage, size);
    }

    private void storeSavedStage(final HolderLookup.Provider provider, final Holder<DragonStage> dragonStage, final CompoundTag tag) {
        SavedDragonStage savedDragonStage = savedDragonStages.get(dragonType.getKey());

        if (savedDragonStage != null) {
            CompoundTag savedStageTag = new CompoundTag();
            savedStageTag.put(DRAGON_STAGE, ResourceKey.codec(DragonStage.REGISTRY).encodeStart(provider.createSerializationContext(NbtOps.INSTANCE), dragonStage.getKey()).getOrThrow());
            savedStageTag.put(PREVIOUS_DRAGON_STAGE, ResourceKey.codec(DragonStage.REGISTRY).encodeStart(provider.createSerializationContext(NbtOps.INSTANCE), dragonStage.getKey()).getOrThrow());
            savedStageTag.putDouble(SIZE, size);

            tag.put(dragonType.getKey().toString() + SAVED_STAGE_SUFFIX, savedStageTag);
        }
    }

    @Override
    public void deserializeNBT(@NotNull final HolderLookup.Provider provider, final CompoundTag tag) {
        deserializeNBT(provider, tag, false);
    }

    public void revertToHumanForm(Player player, boolean isRevertingFromSoul) {
        // Don't set the saved dragon size if we are reverting from a soul, as we already are storing the size of the dragon in the soul
        if (ServerConfig.saveGrowthStage && !isRevertingFromSoul) {
            this.setSavedDragonStage(dragonType.getKey());
        }

        // Drop everything in your claw slots
        DragonCommand.reInsertClawTools(player);

        setType(null);
        setBody(null, player);
        setSize(player, null, NO_SIZE);

        if (!ServerConfig.saveAllAbilities) {
            SpinData.getData(player).hasSpin = false;
            this.setHasFlight(false);
        }

        AltarData altarData = AltarData.getData(player);
        altarData.altarCooldown = Functions.secondsToTicks(ServerConfig.altarUsageCooldown);
        altarData.hasUsedAltar = true;
    }

    // --- Hunter handler --- //

    public void modifyHunterStacks(int modification) {
        // FIXME
        //hunterStacks = Math.clamp(hunterStacks + modification, 0, HunterHandler.MAX_HUNTER_STACKS);
    }

    public boolean hasMaxHunterStacks() {
        // FIXME
        return false;
        //return hunterStacks == HunterHandler.MAX_HUNTER_STACKS;
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

    public String getTypeNameLowerCase() {
        return ResourceHelper.getNameLowercase(dragonType);
    }

    public ResourceLocation getFoodIcons() {
        return dragonType.value().miscResources().foodSprites();
    }

    public static final String DRAGON_TYPE = "dragon_type";
    public static final String DRAGON_BODY = "dragon_body";
    public static final String DRAGON_STAGE = "dragon_stage";
    public static final String PREVIOUS_DRAGON_STAGE = "previous_dragon_stage";
    public static final String ENTITY_STATE = "entity_state";

    public static final String SIZE = "size";
    public static final String SAVED_STAGE_SUFFIX = "_saved_stage";

    public static final String STAR_HEART_STATE = "star_heart_state";
    public static final String IS_GROWING = "is_growing";
}