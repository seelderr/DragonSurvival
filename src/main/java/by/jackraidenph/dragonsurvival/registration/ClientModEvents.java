package by.jackraidenph.dragonsurvival.registration;

import by.jackraidenph.dragonsurvival.BeaconParticle;
import by.jackraidenph.dragonsurvival.DragonSurvivalMod;
import by.jackraidenph.dragonsurvival.gecko.renderer.DragonRenderer;
import by.jackraidenph.dragonsurvival.gecko.renderer.KnightRenderer;
import by.jackraidenph.dragonsurvival.gecko.renderer.PrinceRenderer;
import by.jackraidenph.dragonsurvival.gecko.model.*;
import by.jackraidenph.dragonsurvival.gui.magic.DragonScreen;
import by.jackraidenph.dragonsurvival.handlers.ClientSide.ClientDragonRender;
import by.jackraidenph.dragonsurvival.handlers.ClientSide.KeyInputHandler;
import by.jackraidenph.dragonsurvival.models.magic.FireballModel;
import by.jackraidenph.dragonsurvival.models.magic.LightningBallModel;
import by.jackraidenph.dragonsurvival.nest.NestScreen;
import by.jackraidenph.dragonsurvival.renderer.PrincessRenderer;
import by.jackraidenph.dragonsurvival.renderer.*;
import by.jackraidenph.dragonsurvival.renderer.magic.BallLightningRenderer;
import by.jackraidenph.dragonsurvival.renderer.magic.DragonSpikeRenderer;
import by.jackraidenph.dragonsurvival.renderer.magic.FireBallRenderer;
import by.jackraidenph.dragonsurvival.shader.ShaderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.entity.SpriteRenderer;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
@SuppressWarnings("unused")
public class ClientModEvents {
    
    @SubscribeEvent
    public static void onTextureStitchEvent(TextureStitchEvent.Pre event) {
        if(event.getMap().location() == AtlasTexture.LOCATION_BLOCKS) {
            event.addSprite(new ResourceLocation(DragonSurvivalMod.MODID, "te/star/cage"));
            event.addSprite(new ResourceLocation(DragonSurvivalMod.MODID, "te/star/wind"));
            event.addSprite(new ResourceLocation(DragonSurvivalMod.MODID, "te/star/open_eye"));
            event.addSprite(new ResourceLocation(DragonSurvivalMod.MODID, "te/star/wind_vertical"));
    
            event.addSprite(new ResourceLocation(DragonSurvivalMod.MODID, "gui/dragon_claws_axe"));
            event.addSprite(new ResourceLocation(DragonSurvivalMod.MODID, "gui/dragon_claws_pickaxe"));
            event.addSprite(new ResourceLocation(DragonSurvivalMod.MODID, "gui/dragon_claws_shovel"));
            event.addSprite(new ResourceLocation(DragonSurvivalMod.MODID, "gui/dragon_claws_sword"));
        }
    
        DragonSurvivalMod.LOGGER.info("Successfully added sprites!");
    }

    @SubscribeEvent
    public static void setupClient(final FMLClientSetupEvent event) {
        Minecraft minecraft = event.getMinecraftSupplier().get();
    
        KeyInputHandler.setupKeybinds();

        RenderTypeLookup.setRenderLayer(BlockInit.dragon_altar_stone, RenderType.cutout());
        RenderTypeLookup.setRenderLayer(BlockInit.dragon_altar_sandstone, RenderType.cutout());
        RenderTypeLookup.setRenderLayer(BlockInit.dragon_altar_red_sandstone, RenderType.cutout());
        RenderTypeLookup.setRenderLayer(BlockInit.dragon_altar_purpur_block, RenderType.cutout());
        RenderTypeLookup.setRenderLayer(BlockInit.dragon_altar_oak_log, RenderType.cutout());
        RenderTypeLookup.setRenderLayer(BlockInit.dragon_altar_nether_bricks, RenderType.cutout());
        RenderTypeLookup.setRenderLayer(BlockInit.dragon_altar_mossy_cobblestone, RenderType.cutout());
        RenderTypeLookup.setRenderLayer(BlockInit.dragon_altar_blackstone, RenderType.cutout());
        RenderTypeLookup.setRenderLayer(BlockInit.mediumCaveNest, RenderType.cutout());
        RenderTypeLookup.setRenderLayer(BlockInit.mediumForestNest, RenderType.cutout());
        RenderTypeLookup.setRenderLayer(BlockInit.mediumSeaNest, RenderType.cutout());
        RenderTypeLookup.setRenderLayer(BlockInit.bigCaveNest, RenderType.cutout());
        RenderTypeLookup.setRenderLayer(BlockInit.bigForestNest, RenderType.cutout());
        RenderTypeLookup.setRenderLayer(BlockInit.bigSeaNest, RenderType.cutout());
        RenderTypeLookup.setRenderLayer(BlockInit.birchDoor, RenderType.cutout());
        RenderTypeLookup.setRenderLayer(BlockInit.acaciaDoor, RenderType.cutout());
        RenderTypeLookup.setRenderLayer(BlockInit.peaceDragonBeacon, RenderType.cutout());
        RenderTypeLookup.setRenderLayer(BlockInit.fireDragonBeacon, RenderType.cutout());
        RenderTypeLookup.setRenderLayer(BlockInit.magicDragonBeacon, RenderType.cutout());
    
        RenderingRegistry.registerEntityRenderingHandler(EntityTypesInit.DRAGON_SPIKE, DragonSpikeRenderer::new);
    
        RenderingRegistry.registerEntityRenderingHandler(EntityTypesInit.MAGICAL_BEAST, MagicalPredatorRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityTypesInit.BOLAS_ENTITY, manager -> new SpriteRenderer<>(manager, minecraft.getItemRenderer()));

        RenderingRegistry.registerEntityRenderingHandler(EntityTypesInit.PRINCESS, manager -> new PrincessRenderer(manager, (IReloadableResourceManager) minecraft.getResourceManager()));
        RenderingRegistry.registerEntityRenderingHandler(EntityTypesInit.HUNTER_HOUND, by.jackraidenph.dragonsurvival.renderer.HunterHoundRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityTypesInit.SHOOTER_HUNTER, by.jackraidenph.dragonsurvival.renderer.ShooterHunterRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityTypesInit.SQUIRE_HUNTER, by.jackraidenph.dragonsurvival.renderer.SquireHunterRenderer::new);

        ClientRegistry.bindTileEntityRenderer(TileEntityTypesInit.PREDATOR_STAR_TILE_ENTITY_TYPE, PredatorStarTESR::new);
        ClientRegistry.bindTileEntityRenderer(TileEntityTypesInit.helmetTile, HelmetEntityRenderer::new);
        ClientRegistry.bindTileEntityRenderer(TileEntityTypesInit.dragonBeacon, DragonBeaconRenderer::new);
        ShaderHelper.initShaders();

        ScreenManager.register(Containers.nestContainer, NestScreen::new);
        ScreenManager.register(Containers.dragonContainer, DragonScreen::new);
        
        //Gecko renderers
        DragonModel dragonModel = new DragonModel();
        RenderingRegistry.registerEntityRenderingHandler(EntityTypesInit.BALL_LIGHTNING, manager -> new BallLightningRenderer(manager, new LightningBallModel()));
        RenderingRegistry.registerEntityRenderingHandler(EntityTypesInit.FIREBALL, manager -> new FireBallRenderer(manager, new FireballModel()));
    
        RenderingRegistry.registerEntityRenderingHandler(EntityTypesInit.DRAGON, manager -> new DragonRenderer(manager, ClientDragonRender.dragonModel = dragonModel));
        RenderingRegistry.registerEntityRenderingHandler(EntityTypesInit.DRAGON_ARMOR, manager -> new DragonRenderer(manager, ClientDragonRender.dragonArmorModel = new DragonArmorModel(dragonModel)));
        RenderingRegistry.registerEntityRenderingHandler(EntityTypesInit.KNIGHT, manager -> new KnightRenderer(manager, new KnightModel()));
        RenderingRegistry.registerEntityRenderingHandler(EntityTypesInit.PRINCESS_ON_HORSE, manager -> new by.jackraidenph.dragonsurvival.gecko.renderer.PrincessRenderer(manager, new PrincessModel()));
        RenderingRegistry.registerEntityRenderingHandler(EntityTypesInit.PRINCE_ON_HORSE, manager -> new PrinceRenderer(manager, new PrinceModel()));
    }

    @SubscribeEvent
    public static void registerParticleFactories(ParticleFactoryRegisterEvent factoryRegisterEvent) {
        ParticleManager particleManager = Minecraft.getInstance().particleEngine;
        particleManager.register(ParticleRegistry.fireBeaconParticle, p_create_1_ -> new IParticleFactory<BasicParticleType>() {
            @Nullable
            @Override
            public Particle createParticle(BasicParticleType p_199234_1_, ClientWorld clientWorld, double v, double v1, double v2, double v3, double v4, double v5) {
                BeaconParticle beaconParticle = new BeaconParticle(clientWorld, v, v1, v2, v3, v4, v5);
                beaconParticle.pickSprite(p_create_1_);
                return beaconParticle;
            }
        });
        particleManager.register(ParticleRegistry.magicBeaconParticle, p_create_1_ -> new IParticleFactory<BasicParticleType>() {
            @Nullable
            @Override
            public Particle createParticle(BasicParticleType p_199234_1_, ClientWorld clientWorld, double v, double v1, double v2, double v3, double v4, double v5) {
                BeaconParticle beaconParticle = new BeaconParticle(clientWorld, v, v1, v2, v3, v4, v5);
                beaconParticle.pickSprite(p_create_1_);
                return beaconParticle;
            }
        });
        particleManager.register(ParticleRegistry.peaceBeaconParticle, p_create_1_ -> new IParticleFactory<BasicParticleType>() {
            @Nullable
            @Override
            public Particle createParticle(BasicParticleType p_199234_1_, ClientWorld clientWorld, double v, double v1, double v2, double v3, double v4, double v5) {
                BeaconParticle beaconParticle = new BeaconParticle(clientWorld, v, v1, v2, v3, v4, v5);
                beaconParticle.pickSprite(p_create_1_);
                return beaconParticle;
            }
        });
    }
    
}
