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
import net.minecraft.client.util.ITooltipFlag;
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
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nullable;
import java.util.List;

@Mod.EventBusSubscriber(modid = DragonSurvivalMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ItemsInit {
    
    public static ItemGroup items = new ItemGroup("dragon.survival.blocks") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(elderDragonBone);
        }
    };
    public static Item dragonHeartShard, weakDragonHeart, elderDragonHeart;
    public static Item starBone, elderDragonBone, elderDragonDust;
    public static Item charredMeat, charredVegetable, charredMushroom, charredSeafood, chargedCoal, chargedSoup;
    public static Item seaDragonTreat, caveDragonTreat, forestDragonTreat;
    public static Item huntingNet;
    public static Item passiveFireBeacon, passiveMagicBeacon, passivePeaceBeacon;
    public static Item starHeart;
    
    public static Item lightningTextureItem;

    @SubscribeEvent
    public static void register(final RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> registry = event.getRegistry();
    
        starBone = registerItem(registry, new Item(new Item.Properties().tab(items)) {
            @Override
            public void appendHoverText(ItemStack p_77624_1_,
                    @Nullable
                            World p_77624_2_, List<ITextComponent> p_77624_3_, ITooltipFlag p_77624_4_)
            {
                super.appendHoverText(p_77624_1_, p_77624_2_, p_77624_3_, p_77624_4_);
                p_77624_3_.add(new TranslationTextComponent("ds.description.starBone"));
            }
            
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
        }, "star_bone");
    
        starHeart = registerItem(registry, new Item(new Item.Properties().tab(items)){
            @Override
            public void appendHoverText(ItemStack p_77624_1_,
                    @Nullable
                            World p_77624_2_, List<ITextComponent> p_77624_3_, ITooltipFlag p_77624_4_)
            {
                super.appendHoverText(p_77624_1_, p_77624_2_, p_77624_3_, p_77624_4_);
                p_77624_3_.add(new TranslationTextComponent("ds.description.starHeart"));
            }
            
            @Override
            public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand p_77659_3_)
            {
                if(!world.isClientSide) {
                    DragonStateHandler handler = DragonStateProvider.getCap(player).orElse(null);
                
                    if (handler != null) {
                        handler.growing = !handler.growing;
                        player.sendMessage(new TranslationTextComponent(handler.growing ? "ds.growth.now_growing" : "ds.growth.no_growth"), player.getUUID());
                        
                        NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity)player), new SyncGrowthState(handler.growing));
                        return ActionResult.success(player.getItemInHand(p_77659_3_));
                    }
                }
            
                return super.use(world, player, p_77659_3_);
            }
        }, "star_heart");

        elderDragonDust = registerItem(registry, "elder_dragon_dust", "ds.description.elderDragonDust");
        elderDragonBone = registerItem(registry, "elder_dragon_bone", "ds.description.elderDragonBone");
    
        dragonHeartShard = registerItem(registry,  "heart_element", "ds.description.heartElement");
        weakDragonHeart = registerItem(registry,  "weak_dragon_heart", "ds.description.weakDragonHeart");
        elderDragonHeart = registerItem(registry,  "elder_dragon_heart", "ds.description.elderDragonHeart");
        
        chargedCoal = registerItem(registry, new Item(new Item.Properties().tab(items)) {
        	@Override
        	public int getBurnTime(ItemStack itemStack) {
        		return 3200;
        	}
        }, "charged_coal");
        
        charredMeat = registerItem(registry, new Item(new Item.Properties().tab(items).food(new Food.Builder().nutrition(1).saturationMod(0.4F).meat()
        		.effect(() -> new EffectInstance(Effects.HUNGER, 20 * 15, 0), 1.0F)
        		.build())), "charred_meat");
        
        charredVegetable = registerItem(registry, new Item(new Item.Properties().tab(items).food(new Food.Builder().nutrition(1).saturationMod(0.4F).meat()
                .effect(() -> new EffectInstance(Effects.HUNGER, 20 * 15, 0), 1.0F)
                .build())), "charred_vegetable");
        
        charredMushroom = registerItem(registry, new Item(new Item.Properties().tab(items).food(new Food.Builder().nutrition(1).saturationMod(0.4F).meat()
                .effect(() -> new EffectInstance(Effects.HUNGER, 20 * 15, 0), 1.0F)
                .build())), "charred_mushroom");
        
        charredSeafood = registerItem(registry, new Item(new Item.Properties().tab(items).food(new Food.Builder().nutrition(1).saturationMod(0.4F).meat()
                .effect(() -> new EffectInstance(Effects.HUNGER, 20 * 15, 0), 1.0F)
                .build())), "charred_seafood");
        
        chargedSoup = registerItem(registry, new Item(new Item.Properties().tab(items).food(new Food.Builder().nutrition(1).saturationMod(0.4F).meat().alwaysEat()
                .effect(() -> new EffectInstance(Effects.POISON, 20 * 15, 0), 1.0F)
                .effect(() -> new EffectInstance(DragonEffects.FIRE, Functions.secondsToTicks(ConfigHandler.SERVER.chargedSoupBuffDuration.get()), 0), 1.0F)
                .build())){
            @Override
            public void appendHoverText(ItemStack p_77624_1_,
                    @Nullable
                            World p_77624_2_, List<ITextComponent> p_77624_3_, ITooltipFlag p_77624_4_)
            {
                super.appendHoverText(p_77624_1_, p_77624_2_, p_77624_3_, p_77624_4_);
                p_77624_3_.add(new TranslationTextComponent("ds.description.chargedSoup"));
            }
        }, "charged_soup");
        
        seaDragonTreat = registerItem(registry, new Item(new Item.Properties().tab(items).food(new Food.Builder().nutrition(1).alwaysEat().saturationMod(0.4F).meat()
               .effect(() -> new EffectInstance(Effects.HUNGER, 20 * 15, 0), 1.0F)
               .build())){
            @Override
            public void appendHoverText(ItemStack p_77624_1_,
                    @Nullable
                            World p_77624_2_, List<ITextComponent> p_77624_3_, ITooltipFlag p_77624_4_)
            {
                super.appendHoverText(p_77624_1_, p_77624_2_, p_77624_3_, p_77624_4_);
                p_77624_3_.add(new TranslationTextComponent("ds.description.seaDragonTreat"));
            }
            
            public ItemStack finishUsingItem(ItemStack p_77654_1_, World p_77654_2_, LivingEntity entity) {
                if(entity instanceof PlayerEntity){
                    PlayerEntity player = (PlayerEntity)entity;
                
                    if( DragonStateProvider.getCap(player).map((cap) -> cap.getType()).get() == DragonType.SEA) {
                        DragonStateProvider.replenishMana(player, DragonStateProvider.getMaxMana(player));
                    }
                }
            
                return this.isEdible() ? entity.eat(p_77654_2_, p_77654_1_) : p_77654_1_;
            }
        }, "sea_dragon_treat");
    
        caveDragonTreat = registerItem(registry, new Item(new Item.Properties().tab(items).food(new Food.Builder().nutrition(1).alwaysEat().saturationMod(0.4F).meat()
                                                                                   .effect(() -> new EffectInstance(Effects.HUNGER, 20 * 15, 0), 1.0F)
                                                                                   .build())){
            @Override
            public void appendHoverText(ItemStack p_77624_1_,
                    @Nullable
                            World p_77624_2_, List<ITextComponent> p_77624_3_, ITooltipFlag p_77624_4_)
            {
                super.appendHoverText(p_77624_1_, p_77624_2_, p_77624_3_, p_77624_4_);
                p_77624_3_.add(new TranslationTextComponent("ds.description.caveDragonTreat"));
            }
            
            public ItemStack finishUsingItem(ItemStack p_77654_1_, World p_77654_2_, LivingEntity entity) {
                if(entity instanceof PlayerEntity){
                    PlayerEntity player = (PlayerEntity)entity;
                
                    if( DragonStateProvider.getCap(player).map((cap) -> cap.getType()).get() == DragonType.CAVE) {
                        DragonStateProvider.replenishMana(player, DragonStateProvider.getMaxMana(player));
                    }
                }
            
                return this.isEdible() ? entity.eat(p_77654_2_, p_77654_1_) : p_77654_1_;
            }
        }, "cave_dragon_treat");
    
        forestDragonTreat = registerItem(registry, new Item(new Item.Properties().tab(items).food(new Food.Builder().nutrition(1).alwaysEat().saturationMod(0.4F).meat()
                    .effect(() -> new EffectInstance(Effects.HUNGER, 20 * 15, 0), 1.0F)
                    .build())){
            @Override
            public void appendHoverText(ItemStack p_77624_1_,
                    @Nullable
                            World p_77624_2_, List<ITextComponent> p_77624_3_, ITooltipFlag p_77624_4_)
            {
                super.appendHoverText(p_77624_1_, p_77624_2_, p_77624_3_, p_77624_4_);
                p_77624_3_.add(new TranslationTextComponent("ds.description.forestDragonTreat"));
            }
            
            public ItemStack finishUsingItem(ItemStack p_77654_1_, World p_77654_2_, LivingEntity entity) {
                if(entity instanceof PlayerEntity){
                    PlayerEntity player = (PlayerEntity)entity;
                    
                    if( DragonStateProvider.getCap(player).map((cap) -> cap.getType()).get() == DragonType.FOREST) {
                        DragonStateProvider.replenishMana(player, DragonStateProvider.getMaxMana(player));
                    }
                }
                
                return this.isEdible() ? entity.eat(p_77654_2_, p_77654_1_) : p_77654_1_;
            }
        }, "forest_dragon_treat");

        
        
        
        huntingNet = new Item(new Item.Properties()).setRegistryName(DragonSurvivalMod.MODID, "dragon_hunting_mesh");
        registry.register(huntingNet);
        passiveMagicBeacon = new Item(new Item.Properties()).setRegistryName(DragonSurvivalMod.MODID, "beacon_magic_1");
        registry.register(passiveMagicBeacon);
        passivePeaceBeacon = new Item(new Item.Properties()).setRegistryName(DragonSurvivalMod.MODID, "beacon_peace_1");
        registry.register(passivePeaceBeacon);
        passiveFireBeacon = new Item(new Item.Properties()).setRegistryName(DragonSurvivalMod.MODID, "beacon_fire_1");
        registry.register(passiveFireBeacon);
        lightningTextureItem = new Item(new Item.Properties()).setRegistryName(DragonSurvivalMod.MODID, "lightning");
        registry.register(lightningTextureItem);
    }
    
    public static Item registerItem(IForgeRegistry<Item> registry, String name, String description){
        Item item = new Item(new Item.Properties().tab(items)){
            @Override
            public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> list, ITooltipFlag tooltipFlag)
            {
                super.appendHoverText(stack, world, list, tooltipFlag);
                list.add(new TranslationTextComponent(description));
            }
        };
        item.setRegistryName(DragonSurvivalMod.MODID, name);
        registry.register(item);
        return item;
    }
    
    public static Item registerItem(IForgeRegistry<Item> registry, Item item, String name){
        item.setRegistryName(DragonSurvivalMod.MODID, name);
        registry.register(item);
        return item;
    }
}
