package by.dragonsurvivalteam.dragonsurvival.input;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.IKeyConflictContext;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.common.util.Lazy;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public enum Keybinds {

    // Implementation inspired by Create

    TOGGLE_WINGS("ds.keybind.wings", KeyConflictContext.IN_GAME, GLFW.GLFW_KEY_G),
    DRAGON_INVENTORY("ds.keybind.dragon_inv", KeyConflictContext.IN_GAME, GLFW.GLFW_KEY_UNKNOWN),

    NEXT_ABILITY("ds.keybind.use_ability", KeyConflictContext.IN_GAME, GLFW.GLFW_KEY_C),
    PREV_ABILITY("ds.keybind.toggle_abilities", KeyConflictContext.IN_GAME, GLFW.GLFW_KEY_X),

    USE_ABILITY("ds.keybind.next_ability", KeyConflictContext.IN_GAME, GLFW.GLFW_KEY_R),
    TOGGLE_ABILITIES("ds.keybind.prev_ability", KeyConflictContext.IN_GAME, GLFW.GLFW_KEY_F),

    ABILITY1("ds.keybind.ability1", KeyConflictContext.IN_GAME, GLFW.GLFW_KEY_KP_1),
    ABILITY2("ds.keybind.ability2", KeyConflictContext.IN_GAME, GLFW.GLFW_KEY_KP_2),
    ABILITY3("ds.keybind.ability3", KeyConflictContext.IN_GAME, GLFW.GLFW_KEY_KP_3),
    ABILITY4("ds.keybind.ability4", KeyConflictContext.IN_GAME, GLFW.GLFW_KEY_KP_4),

    // TODO: Verify that this should use GUI?
    SPIN_ABILITY("ds.keybind.spin", KeyConflictContext.GUI, GLFW.GLFW_KEY_V),
    FREE_LOOK("ds.keybind.free_look", KeyConflictContext.IN_GAME, GLFW.GLFW_KEY_LEFT_ALT),
    ;

    private Lazy<KeyMapping> keyMapping;

    private Keybinds(String description, IKeyConflictContext keyConflictContext, int defaultKey) {
        this(description, keyConflictContext, defaultKey, "ds.keybind.category");
    }

    private Keybinds(String description, IKeyConflictContext keyConflictContext, int defaultKey, String category) {
        keyMapping = Lazy.of(() -> new KeyMapping(description, keyConflictContext, InputConstants.Type.KEYSYM, defaultKey, category));
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public static void registerAllKeys(RegisterKeyMappingsEvent evt) {
        for (Keybinds keybind : values()) {
            evt.register(keybind.get());
        }
    }

    public KeyMapping get() {
        return keyMapping.get();
    }

    /**
     * Mirror for {@link KeyMapping#consumeClick()}
     *
     * @return True if the key was clicked
     */
    public boolean consumeClick() {
        return keyMapping.get().consumeClick();
    }

    /**
     * Mirror for {@link KeyMapping#isDown()}
     *
     * @return True if the key is down
     */
    public boolean isDown() {
        return keyMapping.get().isDown();
    }

    /**
     * Mirror for {@link KeyMapping#consumeClick()}
     *
     * @return Key for this KeyMapping
     */
    public InputConstants.Key getKey() {
        return keyMapping.get().getKey();
    }
}
