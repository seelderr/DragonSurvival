package by.dragonsurvivalteam.dragonsurvival.client;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.client.gui.DragonScreen;
import by.dragonsurvivalteam.dragonsurvival.client.gui.SourceOfMagicScreen;
import by.dragonsurvivalteam.dragonsurvival.client.handlers.DragonSkins;
import by.dragonsurvivalteam.dragonsurvival.client.handlers.KeyInputHandler;
import by.dragonsurvivalteam.dragonsurvival.client.models.creatures.KnightModel;
import by.dragonsurvivalteam.dragonsurvival.client.models.creatures.PrinceModel;
import by.dragonsurvivalteam.dragonsurvival.client.models.creatures.PrincessHorseModel;
import by.dragonsurvivalteam.dragonsurvival.client.models.projectiles.FireballModel;
import by.dragonsurvivalteam.dragonsurvival.client.models.projectiles.LightningBallModel;
import by.dragonsurvivalteam.dragonsurvival.client.models.projectiles.StormBreathEffectModel;
import by.dragonsurvivalteam.dragonsurvival.client.particles.BeaconParticle;
import by.dragonsurvivalteam.dragonsurvival.client.particles.DSParticles;
import by.dragonsurvivalteam.dragonsurvival.client.particles.SeaSweepParticle;
import by.dragonsurvivalteam.dragonsurvival.client.render.ClientDragonRender;
import by.dragonsurvivalteam.dragonsurvival.client.render.blocks.DragonBeaconRenderer;
import by.dragonsurvivalteam.dragonsurvival.client.render.blocks.HelmetEntityRenderer;
import by.dragonsurvivalteam.dragonsurvival.client.render.blocks.PredatorStarTESR;
import by.dragonsurvivalteam.dragonsurvival.client.render.entity.creatures.*;
import by.dragonsurvivalteam.dragonsurvival.client.render.entity.dragon.DragonHitboxRender;
import by.dragonsurvivalteam.dragonsurvival.client.render.entity.dragon.DragonRenderer;
import by.dragonsurvivalteam.dragonsurvival.client.render.entity.projectiles.BallLightningRenderer;
import by.dragonsurvivalteam.dragonsurvival.client.render.entity.projectiles.DragonSpikeRenderer;
import by.dragonsurvivalteam.dragonsurvival.client.render.entity.projectiles.FireBallRenderer;
import by.dragonsurvivalteam.dragonsurvival.client.render.entity.projectiles.StormBreathRender;
import by.dragonsurvivalteam.dragonsurvival.client.shader.ShaderHelper;
import by.dragonsurvivalteam.dragonsurvival.common.blocks.DSBlocks;
import by.dragonsurvivalteam.dragonsurvival.common.entity.DSEntities;
import by.dragonsurvivalteam.dragonsurvival.server.containers.DSContainers;
import by.dragonsurvivalteam.dragonsurvival.server.tileentity.DSTileEntities;
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

@Mod.EventBusSubscriber( bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT )
@SuppressWarnings( "unused" )
public class ClientModEvents{

	@SubscribeEvent
	public static void onTextureStitchEvent(TextureStitchEvent.Pre event){
		if(event.getMap().location() == AtlasTexture.LOCATION_BLOCKS){
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
	public static void setupClient(final FMLClientSetupEvent event){
		Minecraft minecraft = event.getMinecraftSupplier().get();

		DragonSkins.init();

		KeyInputHandler.setupKeybinds();

		RenderTypeLookup.setRenderLayer(DSBlocks.dragon_altar_stone, RenderType.cutout());
		RenderTypeLookup.setRenderLayer(DSBlocks.dragon_altar_sandstone, RenderType.cutout());
		RenderTypeLookup.setRenderLayer(DSBlocks.dragon_altar_red_sandstone, RenderType.cutout());
		RenderTypeLookup.setRenderLayer(DSBlocks.dragon_altar_purpur_block, RenderType.cutout());
		RenderTypeLookup.setRenderLayer(DSBlocks.dragon_altar_oak_log, RenderType.cutout());
		RenderTypeLookup.setRenderLayer(DSBlocks.dragon_altar_nether_bricks, RenderType.cutout());
		RenderTypeLookup.setRenderLayer(DSBlocks.dragon_altar_mossy_cobblestone, RenderType.cutout());
		RenderTypeLookup.setRenderLayer(DSBlocks.dragon_altar_blackstone, RenderType.cutout());
		RenderTypeLookup.setRenderLayer(DSBlocks.dragon_altar_birch_log, RenderType.cutout());
		RenderTypeLookup.setRenderLayer(DSBlocks.birchDoor, RenderType.cutout());
		RenderTypeLookup.setRenderLayer(DSBlocks.acaciaDoor, RenderType.cutout());
		RenderTypeLookup.setRenderLayer(DSBlocks.peaceDragonBeacon, RenderType.cutout());
		RenderTypeLookup.setRenderLayer(DSBlocks.fireDragonBeacon, RenderType.cutout());
		RenderTypeLookup.setRenderLayer(DSBlocks.magicDragonBeacon, RenderType.cutout());

		// enable transparency for certain small doors
		RenderTypeLookup.setRenderLayer(DSBlocks.birchSmallDoor, RenderType.cutout());
		RenderTypeLookup.setRenderLayer(DSBlocks.acaciaSmallDoor, RenderType.cutout());

		RenderingRegistry.registerEntityRenderingHandler(DSEntities.DRAGON_SPIKE, DragonSpikeRenderer::new);

		RenderingRegistry.registerEntityRenderingHandler(DSEntities.MAGICAL_BEAST, MagicalPredatorRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(DSEntities.BOLAS_ENTITY, manager -> new SpriteRenderer<>(manager, minecraft.getItemRenderer()));
		RenderingRegistry.registerEntityRenderingHandler(DSEntities.DRAGON_HITBOX, DragonHitboxRender::new);

		RenderingRegistry.registerEntityRenderingHandler(DSEntities.PRINCESS, manager -> new PrincessRenderer(manager, (IReloadableResourceManager)minecraft.getResourceManager()));
		RenderingRegistry.registerEntityRenderingHandler(DSEntities.HUNTER_HOUND, HunterHoundRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(DSEntities.SHOOTER_HUNTER, ShooterHunterRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(DSEntities.SQUIRE_HUNTER, SquireHunterRenderer::new);

		ClientRegistry.bindTileEntityRenderer(DSTileEntities.PREDATOR_STAR_TILE_ENTITY_TYPE, PredatorStarTESR::new);
		ClientRegistry.bindTileEntityRenderer(DSTileEntities.helmetTile, HelmetEntityRenderer::new);
		ClientRegistry.bindTileEntityRenderer(DSTileEntities.dragonBeacon, DragonBeaconRenderer::new);
		ShaderHelper.initShaders();

		ScreenManager.register(DSContainers.nestContainer, SourceOfMagicScreen::new);
		ScreenManager.register(DSContainers.dragonContainer, DragonScreen::new);

		//Gecko renderers
		RenderingRegistry.registerEntityRenderingHandler(DSEntities.BALL_LIGHTNING, manager -> new BallLightningRenderer(manager, new LightningBallModel()));
		RenderingRegistry.registerEntityRenderingHandler(DSEntities.FIREBALL, manager -> new FireBallRenderer(manager, new FireballModel()));
		RenderingRegistry.registerEntityRenderingHandler(DSEntities.STORM_BREATH_EFFECT, manager -> new StormBreathRender(manager, new StormBreathEffectModel()));

		RenderingRegistry.registerEntityRenderingHandler(DSEntities.DRAGON, manager -> new DragonRenderer(manager, ClientDragonRender.dragonModel));
		RenderingRegistry.registerEntityRenderingHandler(DSEntities.DRAGON_ARMOR, manager -> new DragonRenderer(manager, ClientDragonRender.dragonArmorModel));
		RenderingRegistry.registerEntityRenderingHandler(DSEntities.KNIGHT, manager -> new KnightRenderer(manager, new KnightModel()));
		RenderingRegistry.registerEntityRenderingHandler(DSEntities.PRINCESS_ON_HORSE, manager -> new PrincessHorseRenderer(manager, new PrincessHorseModel()));
		RenderingRegistry.registerEntityRenderingHandler(DSEntities.PRINCE_ON_HORSE, manager -> new PrinceHorseRenderer(manager, new PrinceModel()));
	}

	@SubscribeEvent
	public static void registerParticleFactories(ParticleFactoryRegisterEvent factoryRegisterEvent){
		ParticleManager particleManager = Minecraft.getInstance().particleEngine;
		particleManager.register(DSParticles.fireBeaconParticle, p_create_1_ -> new IParticleFactory<BasicParticleType>(){
			@Nullable
			@Override
			public Particle createParticle(BasicParticleType p_199234_1_, ClientWorld clientWorld, double v, double v1, double v2, double v3, double v4, double v5){
				BeaconParticle beaconParticle = new BeaconParticle(clientWorld, v, v1, v2, v3, v4, v5);
				beaconParticle.pickSprite(p_create_1_);
				return beaconParticle;
			}
		});
		particleManager.register(DSParticles.magicBeaconParticle, p_create_1_ -> new IParticleFactory<BasicParticleType>(){
			@Nullable
			@Override
			public Particle createParticle(BasicParticleType p_199234_1_, ClientWorld clientWorld, double v, double v1, double v2, double v3, double v4, double v5){
				BeaconParticle beaconParticle = new BeaconParticle(clientWorld, v, v1, v2, v3, v4, v5);
				beaconParticle.pickSprite(p_create_1_);
				return beaconParticle;
			}
		});
		particleManager.register(DSParticles.peaceBeaconParticle, p_create_1_ -> new IParticleFactory<BasicParticleType>(){
			@Nullable
			@Override
			public Particle createParticle(BasicParticleType p_199234_1_, ClientWorld clientWorld, double v, double v1, double v2, double v3, double v4, double v5){
				BeaconParticle beaconParticle = new BeaconParticle(clientWorld, v, v1, v2, v3, v4, v5);
				beaconParticle.pickSprite(p_create_1_);
				return beaconParticle;
			}
		});

		particleManager.register(DSParticles.seaSweep, p_create_1_ -> new IParticleFactory<BasicParticleType>(){
			@Nullable
			@Override
			public Particle createParticle(BasicParticleType p_199234_1_, ClientWorld clientWorld, double v, double v1, double v2, double v3, double v4, double v5){
				SeaSweepParticle beaconParticle = new SeaSweepParticle(clientWorld, v, v1, v2, v3, p_create_1_);
				beaconParticle.pickSprite(p_create_1_);
				return beaconParticle;
			}
		});
	}
}