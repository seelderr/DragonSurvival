package by.dragonsurvivalteam.dragonsurvival.mixins;

import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/mixins/AccessorLivingRenderer.java
@Mixin( LivingEntityRenderer.class)
public interface AccessorLivingRenderer {
    @Accessor("layers")
    List<RenderLayer> getRenderLayers();
    @Invoker("shouldShowName")
    boolean callShouldShowName(LivingEntity p_177070_1_);
}
=======
@Mixin( LivingRenderer.class )
public interface AccessorLivingRenderer{
	@Accessor( "layers" )
	List<LayerRenderer> getRenderLayers();
	@Invoker( "shouldShowName" )
	boolean callShouldShowName(LivingEntity p_177070_1_);
}
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/mixins/AccessorLivingRenderer.java
