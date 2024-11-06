package by.dragonsurvivalteam.dragonsurvival.registry.datagen.advancements;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvival;
import by.dragonsurvivalteam.dragonsurvival.common.criteria.BeDragonTrigger;
import by.dragonsurvivalteam.dragonsurvival.registry.DSAdvancementTriggers;
import by.dragonsurvivalteam.dragonsurvival.registry.DSItems;
import by.dragonsurvivalteam.dragonsurvival.registry.datagen.Translation;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Consumer;

public class DSAdvancements implements AdvancementProvider.AdvancementGenerator {
    @Translation(type = Translation.Type.MISC, comments = "Dragon Survival")
    private static final String ROOT = Translation.Type.ADVANCEMENT.wrap("root");

    @Translation(type = Translation.Type.MISC, comments = "Be Yourself")
    private static final String BE_DRAGON = Translation.Type.ADVANCEMENT.wrap("be_dragon");

    @Translation(type = Translation.Type.MISC, comments = "Unless you can be a dragon.")
    private static final String BE_DRAGON_DESCRIPTION = Translation.Type.ADVANCEMENT_DESCRIPTION.wrap("be_dragon");

    @Override
    public void generate(@NotNull HolderLookup.Provider registries, @NotNull Consumer<AdvancementHolder> saver, @NotNull ExistingFileHelper helper) {
        AdvancementHolder root = Advancement.Builder.advancement()
                .display(
                        DSItems.ELDER_DRAGON_BONE.value(),
                        Component.translatable(ROOT),
                        Component.empty(),
                        DragonSurvival.res("textures/block/stone_dragon_door_top.png"),
                        AdvancementType.GOAL,
                        false,
                        false,
                        false
                )
                .addCriterion("root", PlayerTrigger.TriggerInstance.tick())
                .save(saver, DragonSurvival.res("root"), helper);

        // --- Be dragon type --- //

        Advancement.Builder.advancement()
                .parent(root)
                .display(
                        DSItems.STAR_BONE.value(),
                        Component.translatable(BE_DRAGON),
                        Component.translatable(BE_DRAGON_DESCRIPTION),
                        null,
                        AdvancementType.GOAL,
                        false,
                        false,
                        false
                )
                .addCriterion("be_dragon", DSAdvancementTriggers.BE_DRAGON.get().createCriterion(new BeDragonTrigger.BeDragonInstance(Optional.empty(), Optional.empty(), Optional.empty())))
                .rewards(AdvancementRewards.Builder.experience(12))
                .save(saver, DragonSurvival.res("be_dragon"), helper);
    }
}
