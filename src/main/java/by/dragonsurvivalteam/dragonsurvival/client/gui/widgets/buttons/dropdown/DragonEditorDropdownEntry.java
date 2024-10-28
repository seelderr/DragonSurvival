/*
package by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.dropdown;


public class DragonEditorDropdownEntry extends DropdownEntry {
    public static final ResourceLocation BACKGROUND_TEXTURE = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/textbox.png");
    public final List<EditorPartButton> children = new ArrayList<>();
    public int num;
    public DropDownButton source;

    public DragonEditorDropdownEntry(DropDownButton source, int num){
        this.num = num;
        this.source = source;
    }

    @Override
    public void render(@NotNull final GuiGraphics guiGraphics, int pIndex, int pTop, int pLeft, int pWidth, int pHeight, int pMouseX, int pMouseY, boolean pIsMouseOver, float pPartialTicks){
        children.forEach(button -> {
            button.setY(pTop);
            button.visible = source.visible;
            button.active = !Objects.equals(source.current, button.value);
            button.render(guiGraphics, pMouseX, pMouseY, pPartialTicks);
        });
    }

    @Override
    public @NotNull List<? extends GuiEventListener> children(){
        return children;
    }

    @Override
    public @NotNull List<? extends NarratableEntry> narratables(){
        return children;
    }
}*/
