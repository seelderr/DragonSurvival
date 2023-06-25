package by.dragonsurvivalteam.dragonsurvival.mixins;

import net.minecraftforge.fml.loading.LoadingModList;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class ApplyMixinPlugin implements IMixinConfigPlugin {
    @Override
    public void onLoad(final String mixinPackage) { /* Nothing to do */ }

    @Override
    public String getRefMapperConfig() {
        /* Nothing to do */
        return null;
    }

    @Override
    public boolean shouldApplyMixin(final String targetClassName, final String mixinClassName) {
        // TODO :: Could also check for version if there are future compatibility problems
        // `ModList.get()` is not available at this point in time
        if (mixinClassName.equals("by.dragonsurvivalteam.dragonsurvival.mixins.MixinJadeHarvestToolProvider")) {
            return LoadingModList.get().getModFileById("jade") != null;
        } else if (mixinClassName.equals("by.dragonsurvivalteam.dragonsurvival.mixins.MixinHarvestabilityWailaHandler")) {
            return LoadingModList.get().getModFileById("wthitharvestability") != null;
        }

        return true;
    }

    @Override
    public void acceptTargets(final Set<String> myTargets, final Set<String> otherTargets) { /* Nothing to do */ }

    @Override
    public List<String> getMixins() {
        /* Nothing to do */
        return null;
    }

    @Override
    public void preApply(final String targetClassName, final ClassNode targetClass, final String mixinClassName, final IMixinInfo mixinInfo) { /* Nothing to do */ }

    @Override
    public void postApply(final String targetClassName, final ClassNode targetClass, final String mixinClassName, final IMixinInfo mixinInfo) { /* Nothing to do */ }
}
