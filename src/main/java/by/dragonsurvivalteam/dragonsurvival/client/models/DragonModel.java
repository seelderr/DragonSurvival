package by.dragonsurvivalteam.dragonsurvival.client.models;

import by.dragonsurvivalteam.dragonsurvival.client.render.ClientDragonRenderer;
import by.dragonsurvivalteam.dragonsurvival.client.skin_editor_system.DragonEditorHandler;
import by.dragonsurvivalteam.dragonsurvival.client.skin_editor_system.objects.SkinPreset.SkinAgeGroup;
import by.dragonsurvivalteam.dragonsurvival.client.util.RenderingUtils;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.capability.objects.DragonMovementData;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonBody;
import by.dragonsurvivalteam.dragonsurvival.common.entity.DragonEntity;
import by.dragonsurvivalteam.dragonsurvival.config.ClientConfig;
import by.dragonsurvivalteam.dragonsurvival.util.Functions;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.loading.math.MathParser;
import software.bernie.geckolib.model.GeoModel;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod.MODID;

public class DragonModel extends GeoModel<DragonEntity> {
    private final ResourceLocation defaultTexture = ResourceLocation.fromNamespaceAndPath(MODID, "textures/dragon/cave_newborn.png");
    private final ResourceLocation model = ResourceLocation.fromNamespaceAndPath(MODID, "geo/dragon_model.geo.json");
    private ResourceLocation overrideTexture;
    private CompletableFuture<Void> textureRegisterFuture = CompletableFuture.completedFuture(null);

    /**
     * TODO Body Types Update
     * Required:
     * - tips for body types like for magic abilities
     * <p>
     * Extras:
     * - customization.json - Ability to disallow some details in the editor for some Body Types (for example, wing details are not required for wingless).
     * - emotes.json - Ability to disallow some emotions for certain Body Types.
     */

    @Override
    public void applyMolangQueries(final AnimationState<DragonEntity> animationState, double currentTick) {
        super.applyMolangQueries(animationState, currentTick);

        DragonEntity dragon = animationState.getAnimatable();

        // In case the Integer (id of the player) is null
        if (dragon.playerId == null || dragon.getPlayer() == null) {
            return;
        }

        float deltaTick = Minecraft.getInstance().getTimer().getRealtimeDeltaTicks();
        Player player = dragon.getPlayer();
        DragonStateHandler handler = DragonStateProvider.getData(player);
        DragonMovementData md = handler.getMovementData();

        MathParser.setVariable("query.head_yaw", () -> md.headYaw);
        MathParser.setVariable("query.head_pitch", () -> md.headPitch);

        double gravity = player.getAttribute(Attributes.GRAVITY).getValue();
        MathParser.setVariable("query.gravity", () -> gravity);

        double verticalVelocity = md.deltaMovement.y * 10;

        double bodyYawTarget;
        double headYawTarget;
        double headPitchTarget;
        if (!ClientDragonRenderer.isOverridingMovementData) {
            bodyYawTarget = Functions.angleDifference(md.bodyYaw, md.bodyYawLastFrame) / deltaTick * 0.2;
            headYawTarget = Functions.angleDifference(md.headYaw, md.headYawLastFrame) / deltaTick * 0.2;
            headPitchTarget = Functions.angleDifference(md.headPitch, md.headPitchLastFrame) / deltaTick * 0.2;
        } else {
            bodyYawTarget = 0;
            headYawTarget = 0;
            headPitchTarget = 0;
        }

        double currentBodyYawChange = MathParser.getVariableFor("query.body_yaw_change").get();
        double currentHeadPitchChange = MathParser.getVariableFor("query.head_pitch_change").get();
        double currentHeadYawChange = MathParser.getVariableFor("query.head_yaw_change").get();
        double currentTailMotionUp = MathParser.getVariableFor("query.tail_motion_up").get();

        MathParser.setVariable("query.body_yaw_change", () -> Mth.lerp(deltaTick, currentBodyYawChange, bodyYawTarget));
        MathParser.setVariable("query.head_yaw_change", () -> Mth.lerp(deltaTick, currentHeadPitchChange, headYawTarget));
        MathParser.setVariable("query.head_pitch_change", () -> Mth.lerp(deltaTick, currentHeadYawChange, headPitchTarget));
        // TODO: Why does this instantly snap?
        MathParser.setVariable("query.tail_motion_up", () -> Mth.lerp(deltaTick * 0.1, -verticalVelocity, currentTailMotionUp));
    }

    @Override
    public ResourceLocation getModelResource(final DragonEntity dragon) {
        return model;
    }

    public ResourceLocation getTextureResource(final DragonEntity dragon) {
        if (overrideTexture != null) {
            return overrideTexture;
        }

        Player player;

        if (dragon.overrideUUIDWithLocalPlayerForTextureFetch) {
            player = Minecraft.getInstance().player;
        } else {
            player = dragon.getPlayer();
        }

        if (player == null) {
            return defaultTexture;
        }

        DragonStateHandler handler = DragonStateProvider.getData(player);
        SkinAgeGroup ageGroup = handler.getSkinData().skinPreset.skinAges.get(handler.getLevel()).get();

        if (handler.getSkinData().blankSkin) {
            return ResourceLocation.fromNamespaceAndPath(MODID, "textures/dragon/blank_skin_" + handler.getTypeNameLowerCase() + ".png");
        }

        if (handler.getSkinData().recompileSkin.get(handler.getLevel())) {
            if (ClientConfig.forceCPUSkinGeneration) {
                if (textureRegisterFuture.isDone()) {
                    CompletableFuture<List<Pair<NativeImage, ResourceLocation>>> imageGenerationFuture = DragonEditorHandler.generateSkinTextures(dragon);
                    textureRegisterFuture = imageGenerationFuture.thenRunAsync(() -> {
                        handler.getSkinData().isCompiled.put(handler.getLevel(), true);
                        handler.getSkinData().recompileSkin.put(handler.getLevel(), false);
                        for (Pair<NativeImage, ResourceLocation> pair : imageGenerationFuture.join()) {
                            RenderingUtils.uploadTexture(pair.getFirst(), pair.getSecond());
                        }
                    }, Minecraft.getInstance());
                }
            } else {
                DragonEditorHandler.generateSkinTexturesGPU(dragon);
                handler.getSkinData().isCompiled.put(handler.getLevel(), true);
                handler.getSkinData().recompileSkin.put(handler.getLevel(), false);
            }
        }

        // Show the default skin while we are compiling if we haven't already compiled the skin
        if (ageGroup.defaultSkin || !handler.getSkinData().isCompiled.get(handler.getLevel())) {
            return ResourceLocation.fromNamespaceAndPath(MODID, "textures/dragon/" + handler.getTypeNameLowerCase() + "_" + handler.getLevel().getRawName() + ".png");
        }

        String uuid = player.getStringUUID();
        return ResourceLocation.fromNamespaceAndPath(MODID, "dynamic_normal_" + uuid + "_" + handler.getLevel().name);
    }

    public void setOverrideTexture(final ResourceLocation overrideTexture) {
        this.overrideTexture = overrideTexture;
    }

    @Override
    public ResourceLocation getAnimationResource(final DragonEntity dragon) {
        Player player = dragon.getPlayer();

        if (player != null) {
            DragonStateHandler handler = DragonStateProvider.getData(player);
            AbstractDragonBody body = handler.getBody();

            if (body != null) {
                return ResourceLocation.fromNamespaceAndPath(MODID, String.format("animations/dragon_%s.json", body.getBodyNameLowerCase()));
            }
        }

        return ResourceLocation.fromNamespaceAndPath(MODID, "animations/dragon_center.json");
    }

    @Override
    public RenderType getRenderType(final DragonEntity animatable, final ResourceLocation texture) {
        Player player = animatable.getPlayer();

        if (player != null) {
            DragonStateHandler data = DragonStateProvider.getData(player);

            if (data.hasHunterStacks() && !data.isBeingRenderedInInventory) {
                // Required type to make other entities and water visible through the translucent dragon
                return RenderType.itemEntityTranslucentCull(texture);
            }
        }

        return RenderType.entityCutout(texture);
    }
}