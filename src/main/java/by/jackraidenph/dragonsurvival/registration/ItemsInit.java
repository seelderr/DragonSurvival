package by.jackraidenph.dragonsurvival.registration;

import by.jackraidenph.dragonsurvival.DragonSurvivalMod;
import by.jackraidenph.dragonsurvival.Functions;
import by.jackraidenph.dragonsurvival.capability.DragonStateHandler;
import by.jackraidenph.dragonsurvival.capability.DragonStateProvider;
import by.jackraidenph.dragonsurvival.config.ConfigHandler;
import by.jackraidenph.dragonsurvival.handlers.ServerSide.NetworkHandler;
import by.jackraidenph.dragonsurvival.network.SyncGrowthState;
import by.jackraidenph.dragonsurvival.network.SyncSize;
import by.jackraidenph.dragonsurvival.network.SynchronizeDragonCap;
import by.jackraidenph.dragonsurvival.util.DragonLevel;
import by.jackraidenph.dragonsurvival.util.DragonType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SSetPassengersPacket;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.Collection;
import java.util.Collections;

@Mod.EventBusSubscriber(modid = DragonSurvivalMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ItemsInit {
    public static Item starBone, elderDragonBone, elderDragonDust;
    public static ItemGroup items = new ItemGroup("dragon.survival.blocks") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(elderDragonBone);
        }
    };
    public static Item charredMeat, charredVegetable, charredMushroom, charredSeafood, chargedCoal, chargedSoup;
    public static Item seaDragonTreat, caveDragonTreat, forestDragonTreat;
    public static Item huntingNet;
    public static Item passiveFireBeacon, passiveMagicBeacon, passivePeaceBeacon;
    public static Item starHeart;
    
    public static Item lightningTextureItem;

    @SubscribeEvent
    public static void register(final RegistryEvent.Register<Item> event) {
        starBone = new Item(new Item.Properties().tab(items)) {
            @Override
            public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
                LazyOptional<DragonStateHandler> playerStateProvider = playerIn.getCapability(DragonStateProvider.DRAGON_CAPABILITY);
                if (playerStateProvider.isPresent()) {
                    DragonStateHandler dragonStateHandler = playerStateProvider.orElse(null);
                    if (dragonStateHandler.isDragon()) {
                        double size = dragonStateHandler.getSize();
                        if (size > 14) {
                        	size -= 2;
                            size = Math.max(size, DragonLevel.BABY.size);
                        	dragonStateHandler.setSize(size, playerIn);
                            
                            
                            if(!playerIn.isCreative()) {
                                playerIn.getItemInHand(handIn).shrink(1);
                            }
                            
                            if (!worldIn.isClientSide){
                                NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> playerIn), new SyncSize(playerIn.getId(), size));
                                if (dragonStateHandler.getPassengerId() != 0){
                                    Entity mount = worldIn.getEntity(dragonStateHandler.getPassengerId());
                                    if (mount != null){
                                        mount.stopRiding();
                                        ((ServerPlayerEntity)playerIn).connection.send(new SSetPassengersPacket(playerIn));
                                        NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) playerIn), new SynchronizeDragonCap(playerIn.getId(), dragonStateHandler.isHiding(), dragonStateHandler.getType(), dragonStateHandler.getSize(), dragonStateHandler.hasWings(), dragonStateHandler.getLavaAirSupply(), 0));
                                    }
                                }
                            }

                            playerIn.refreshDimensions();
                            return ActionResult.consume(playerIn.getItemInHand(handIn));
                        }
                    }
                }

                return super.use(worldIn, playerIn, handIn);
            }
        }.setRegistryName(DragonSurvivalMod.MODID, "star_bone");

        elderDragonDust = new Item(new Item.Properties().tab(items)).setRegistryName(DragonSurvivalMod.MODID, "elder_dragon_dust");
        elderDragonBone = new Item(new Item.Properties().tab(items)).setRegistryName(DragonSurvivalMod.MODID, "elder_dragon_bone");

        chargedCoal = new Item(new Item.Properties().tab(items)) {
        	@Override
        	public int getBurnTime(ItemStack itemStack) {
        		return 3200;
        	}
        }.setRegistryName(DragonSurvivalMod.MODID, "charged_coal");
        charredMeat = new Item(new Item.Properties().tab(items).food(new Food.Builder().nutrition(1).saturationMod(0.4F).meat()
        		.effect(() -> new EffectInstance(Effects.HUNGER, 20 * 15, 0), 1.0F)
        		.build())).setRegistryName(DragonSurvivalMod.MODID, "charred_meat");
        charredVegetable = new Item(new Item.Properties().tab(items).food(new Food.Builder().nutrition(1).saturationMod(0.4F).meat()
                .effect(() -> new EffectInstance(Effects.HUNGER, 20 * 15, 0), 1.0F)
                .build())).setRegistryName(DragonSurvivalMod.MODID, "charred_vegetable");
        charredMushroom = new Item(new Item.Properties().tab(items).food(new Food.Builder().nutrition(1).saturationMod(0.4F).meat()
                .effect(() -> new EffectInstance(Effects.HUNGER, 20 * 15, 0), 1.0F)
                .build())).setRegistryName(DragonSurvivalMod.MODID, "charred_mushroom");
        charredSeafood = new Item(new Item.Properties().tab(items).food(new Food.Builder().nutrition(1).saturationMod(0.4F).meat()
                .effect(() -> new EffectInstance(Effects.HUNGER, 20 * 15, 0), 1.0F)
                .build())).setRegistryName(DragonSurvivalMod.MODID, "charred_seafood");
        chargedSoup = new Item(new Item.Properties().tab(items).food(new Food.Builder().nutrition(1).saturationMod(0.4F).meat().alwaysEat()
                .effect(() -> new EffectInstance(Effects.POISON, 20 * 15, 0), 1.0F)
                .effect(() -> new EffectInstance(DragonEffects.FIRE, Functions.secondsToTicks(ConfigHandler.SERVER.chargedSoupBuffDuration.get()), 0), 1.0F)
                .build())).setRegistryName(DragonSurvivalMod.MODID, "charged_soup");
        IForgeRegistry<Item> registry = event.getRegistry();
        registry.registerAll(starBone, elderDragonBone, chargedCoal, charredMeat, charredVegetable, charredMushroom, chargedSoup, charredSeafood, elderDragonDust);
    
        seaDragonTreat = new Item(new Item.Properties().tab(items).food(new Food.Builder().nutrition(1).alwaysEat().saturationMod(0.4F).meat()
                                                                                   .effect(() -> new EffectInstance(Effects.HUNGER, 20 * 15, 0), 1.0F)
                                                                                   .build())){
            public ItemStack finishUsingItem(ItemStack p_77654_1_, World p_77654_2_, LivingEntity entity) {
                if(entity instanceof PlayerEntity){
                    PlayerEntity player = (PlayerEntity)entity;
                
                    if( DragonStateProvider.getCap(player).map((cap) -> cap.getType()).get() == DragonType.SEA) {
                        DragonStateProvider.replenishMana(player, DragonStateProvider.getMaxMana(player));
                    }
                }
            
                return this.isEdible() ? entity.eat(p_77654_2_, p_77654_1_) : p_77654_1_;
            }
        }.setRegistryName(DragonSurvivalMod.MODID, "sea_dragon_treat");
    
        caveDragonTreat = new Item(new Item.Properties().tab(items).food(new Food.Builder().nutrition(1).alwaysEat().saturationMod(0.4F).meat()
                                                                                   .effect(() -> new EffectInstance(Effects.HUNGER, 20 * 15, 0), 1.0F)
                                                                                   .build())){
            public ItemStack finishUsingItem(ItemStack p_77654_1_, World p_77654_2_, LivingEntity entity) {
                if(entity instanceof PlayerEntity){
                    PlayerEntity player = (PlayerEntity)entity;
                
                    if( DragonStateProvider.getCap(player).map((cap) -> cap.getType()).get() == DragonType.CAVE) {
                        DragonStateProvider.replenishMana(player, DragonStateProvider.getMaxMana(player));
                    }
                }
            
                return this.isEdible() ? entity.eat(p_77654_2_, p_77654_1_) : p_77654_1_;
            }
        }.setRegistryName(DragonSurvivalMod.MODID, "cave_dragon_treat");
    
        forestDragonTreat = new Item(new Item.Properties().tab(items).food(new Food.Builder().nutrition(1).alwaysEat().saturationMod(0.4F).meat()
                    .effect(() -> new EffectInstance(Effects.HUNGER, 20 * 15, 0), 1.0F)
                    .build())){
            public ItemStack finishUsingItem(ItemStack p_77654_1_, World p_77654_2_, LivingEntity entity) {
                if(entity instanceof PlayerEntity){
                    PlayerEntity player = (PlayerEntity)entity;
                    
                    if( DragonStateProvider.getCap(player).map((cap) -> cap.getType()).get() == DragonType.FOREST) {
                        DragonStateProvider.replenishMana(player, DragonStateProvider.getMaxMana(player));
                    }
                }
                
                return this.isEdible() ? entity.eat(p_77654_2_, p_77654_1_) : p_77654_1_;
            }
        }.setRegistryName(DragonSurvivalMod.MODID, "forest_dragon_treat");
    
        registry.registerAll(seaDragonTreat, caveDragonTreat, forestDragonTreat);
    
        starHeart = new Item(new Item.Properties().tab(items)){
            @Override
            public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand p_77659_3_)
            {
                if(!world.isClientSide) {
                    DragonStateHandler handler = DragonStateProvider.getCap(player).orElse(null);
    
                    if (handler != null) {
                        handler.growing = !handler.growing;
                        player.sendMessage(new TranslationTextComponent(handler.growing ? "ds.growth.now_growing" : "ds.growth.no_growth"), player.getUUID());
                        
                        if(!player.isCreative()) {
                            player.getItemInHand(p_77659_3_).shrink(1);
                        }
                        
                        NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity)player), new SyncGrowthState(handler.growing));
                        
                        return ActionResult.consume(player.getItemInHand(p_77659_3_));
                    }
                }
                
                return super.use(world, player, p_77659_3_);
            }
        }.setRegistryName(DragonSurvivalMod.MODID, "star_heart");
        registry.register(starHeart);
        
        
        
        huntingNet = new Item(new Item.Properties()).setRegistryName(DragonSurvivalMod.MODID, "dragon_hunting_mesh");
        registry.register(huntingNet);
        passiveMagicBeacon = new Item(new Item.Properties()).setRegistryName(DragonSurvivalMod.MODID, "beacon_magic_1");
        registry.register(passiveMagicBeacon);
        passivePeaceBeacon = new Item(new Item.Properties()).setRegistryName(DragonSurvivalMod.MODID, "beacon_peace_1");
        registry.register(passivePeaceBeacon);
        passiveFireBeacon = new Item(new Item.Properties()).setRegistryName(DragonSurvivalMod.MODID, "beacon_fire_1");
        registry.register(passiveFireBeacon);
        
        lightningTextureItem = new Item(new Item.Properties()){
            @Override
            public Collection<ItemGroup> getCreativeTabs()
            {
                return Collections.emptyList();
            }
        }.setRegistryName(DragonSurvivalMod.MODID, "lightning");
        registry.register(lightningTextureItem);
    }
}
