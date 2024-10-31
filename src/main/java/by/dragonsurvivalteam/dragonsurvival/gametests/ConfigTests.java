package by.dragonsurvivalteam.dragonsurvival.gametests;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvival;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonType;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.DragonTypes;
import by.dragonsurvivalteam.dragonsurvival.common.handlers.DragonFoodHandler;
import by.dragonsurvivalteam.dragonsurvival.config.ConfigHandler;
import by.dragonsurvivalteam.dragonsurvival.config.ConfigUtils;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigSide;
import by.dragonsurvivalteam.dragonsurvival.config.types.FoodConfigCollector;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.core.component.DataComponents;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterGameTestsEvent;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import java.lang.reflect.Field;
import java.util.List;

@PrefixGameTestTemplate(false)
@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class ConfigTests {
    @SubscribeEvent // Was used due to potential requirement of having the test run in a specific order - keeping setup for commit history
    public static void registerTests(final RegisterGameTestsEvent event) throws NoSuchMethodException {
        // These are only registered if the template namespace is specified in the test
        event.register(ConfigTests.class.getMethod("testConfig", GameTestHelper.class));
        event.register(ConfigTests.class.getMethod("testInvalidConfig", GameTestHelper.class));
    }

    @GameTest(templateNamespace = DragonSurvival.MODID, template = TestUtils.AIR_CUBE_1X)
    public static void testConfig(final GameTestHelper helper) {
        ConfigHandler.resetConfigValues(ConfigSide.SERVER);

        String configKey = "caveDragonFoods";
        List<Item> foodItems = List.of(Items.RAIL, Items.POWERED_RAIL);
        List<Pair<Integer, Float>> foodData = List.of(Pair.of(5, 3f), Pair.of(2, 1f));

        List<FoodConfigCollector> foodsToSet = List.of(
                FoodConfigCollector.of(ConfigUtils.location(foodItems.get(0)), foodData.get(0).first(), foodData.get(0).second()),
                FoodConfigCollector.of(ConfigUtils.location(foodItems.get(1)), foodData.get(1).first(), foodData.get(1).second())
        );

        AbstractDragonType dragonType = DragonTypes.CAVE;

        // Check that the items we set as edible are correctly set
        TestUtils.setAndCheckConfig(helper, configKey, foodsToSet);
        List<Item> foods = DragonFoodHandler.getEdibleFoods(dragonType);

        helper.assertTrue(
                foods.size() == foodsToSet.size(),
                String.format("[%s] dragon food list has [" + foods.size() + "] entries - expected [" + foodsToSet.size() + "]", dragonType)
        );
        //noinspection SlowListContainsAll -> ignore performance
        helper.assertTrue(
                foods.containsAll(foodItems),
                String.format("[%s] dragon food list items were [" + foods + "] - expected [" + foodItems + "]", dragonType)
        );

        // Check that the specified food properties are correctly set
        for (int i = 0; i < foodItems.size(); i++) {
            FoodProperties properties = DragonFoodHandler.getDragonFoodProperties(foodItems.get(i), dragonType);

            //noinspection DataFlowIssue -> ignore potential null value
            helper.assertTrue(
                    properties.nutrition() == foodData.get(i).first(),
                    String.format("[%s] has a nutrition value of [%d] - expected [%d]", foodItems.get(i), properties.nutrition(), foodData.get(i).first())
            );
            helper.assertTrue(
                    properties.saturation() == foodData.get(i).second(),
                    String.format("[%s] has a nutrition value of [%f] - expected [%f]", foodItems.get(i), properties.saturation(), foodData.get(i).second())
            );
        }

        // Check that pufferfish retains the confusion effect and its default food properties (since none were specified)
        foodsToSet = List.of(FoodConfigCollector.of(ConfigUtils.location(Items.PUFFERFISH)));
        TestUtils.setAndCheckConfig(helper, configKey, foodsToSet);

        FoodProperties properties = DragonFoodHandler.getDragonFoodProperties(Items.PUFFERFISH, dragonType);
        FoodProperties pufferfishProperties = Items.PUFFERFISH.components().get(DataComponents.FOOD);

        //noinspection DataFlowIssue -> ignore potential null value
        helper.assertTrue(
                properties.nutrition() == pufferfishProperties.nutrition(),
                String.format("[%s] has a nutrition value of [%d] - expected [%d]", Items.PUFFERFISH, properties.nutrition(), pufferfishProperties.nutrition())
        );
        helper.assertTrue(
                properties.saturation() == pufferfishProperties.saturation(),
                String.format("[%s] has a nutrition value of [%f] - expected [%f]", Items.PUFFERFISH, properties.saturation(), pufferfishProperties.saturation())
        );
        helper.assertTrue(
                properties.effects().stream().noneMatch(possibleEffect -> possibleEffect.effect().getEffect().is(MobEffects.CONFUSION)),
                String.format("[%s] effects are [%s] - expected [%s] to not be present", Items.PUFFERFISH, properties.effects(), MobEffects.CONFUSION.getRegisteredName())
        );

        helper.succeed();
    }

    @GameTest(templateNamespace = DragonSurvival.MODID, template = TestUtils.AIR_CUBE_1X)
    public static void testInvalidConfig(final GameTestHelper helper) {
        ConfigHandler.resetConfigValues(ConfigSide.SERVER);

        String configKey = "bonusBreakSpeed";
        float valueNotInRange = 0f;
        float defaultValue = 2f;

        TestUtils.setAndCheckConfig(helper, configKey, defaultValue);
        ConfigHandler.updateConfigValue(configKey, valueNotInRange);
        Field field = ConfigHandler.getField(configKey);

        try {
            Object fieldValue = field.get(null);
            helper.assertTrue(fieldValue.equals(defaultValue), "The field value [" + fieldValue + "] did not match the expected value [" + defaultValue + "] (config should not have been updated)");
        } catch (IllegalAccessException exception) {
            helper.fail("Failed trying to access a field for the config [" + configKey + "] to validate the config: [" + exception.getMessage() + "]");
        }

        helper.succeed();
    }
}
