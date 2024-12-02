package by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons;

import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.generic.PlusMinusButton;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.registry.datagen.Translation;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.DragonAbilityInstance;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

public class IncreaseLevelButton extends PlusMinusButton {
    @Translation(type = Translation.Type.MISC, comments = "§aUpgrade the skill for§r %s §alevels worth of experience points§r")
    private static final String LEVEL_UP = Translation.Type.GUI.wrap("level_up");

    public int upgradeCost;

    private DragonAbilityInstance ability;
    private final int slot;

    public IncreaseLevelButton(int x, int y, int slot) {
        super(x, y, 16, 16, true, Button::onPress);
        this.slot = slot;
    }

    @Override
    public void onPress() {
        // FIXME
        /*LocalPlayer player = Objects.requireNonNull(Minecraft.getInstance().player);
        DragonStateHandler data = DragonStateProvider.getData(player);

        ability = data.getMagicData().getPassiveAbilityFromSlot(slot);

        if (ability != null) {
            int newLevel = ability.getLevel() + 1;

            if (newLevel <= ability.getMaxLevel() && (player.experienceLevel >= ability.getLevelCost(1) || player.isCreative())) {
                PacketDistributor.sendToServer(new SyncSkillLevelChangeCost(ability.getName(), newLevel, 1));
                DragonAbilities.setAbilityLevel(player, ability.getClass(), newLevel);
            }
        }*/
    }

    @Override
    public void renderWidget(@NotNull final GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.renderWidget(graphics, mouseX, mouseY, partialTick);
        // FIXME
        /*if (isHovered()) {
            //noinspection DataFlowIssue -> player is present
            DragonStateHandler data = DragonStateProvider.getData(Minecraft.getInstance().player);
            ability = data.getMagicData().getPassiveAbilityFromSlot(slot);

            if (ability != null && ability.getLevel() < ability.getMaxLevel()) {
                upgradeCost = ability.getLevelCost(1);

                ArrayList<Component> description = new ArrayList<>();
                description.add(Component.translatable(LEVEL_UP, upgradeCost));

                if (!ability.getLevelUpInfo().isEmpty()) {
                    description.add(Component.empty());
                    description.addAll(ability.getLevelUpInfo());
                }

                graphics.renderComponentTooltip(Minecraft.getInstance().font, description, mouseX, mouseY);
            } else {
                upgradeCost = 0;
            }
        }*/
    }
}