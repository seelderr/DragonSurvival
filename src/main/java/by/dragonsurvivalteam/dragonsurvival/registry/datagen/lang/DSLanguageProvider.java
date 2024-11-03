package by.dragonsurvivalteam.dragonsurvival.registry.datagen.lang;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvival;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigOption;
import by.dragonsurvivalteam.dragonsurvival.registry.datagen.Translation;
import net.minecraft.data.PackOutput;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.neoforged.neoforgespi.language.ModFileScanData;
import org.apache.commons.lang3.text.WordUtils;
import org.objectweb.asm.Type;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DSLanguageProvider extends LanguageProvider {
    private final String locale;

    public DSLanguageProvider(final PackOutput output, final String locale) {
        super(output, DragonSurvival.MODID, locale);
        this.locale = locale;
    }

    @Override
    protected void addTranslations() {
        Set<ModFileScanData.AnnotationData> annotationDataSet = ModList.get().getModFileById(DragonSurvival.MODID).getFile().getScanResult().getAnnotations();
        handleTranslations(annotationDataSet);
        handleConfigCategories(annotationDataSet);
    }

    private void handleTranslations(final Set<ModFileScanData.AnnotationData> annotationDataSet) {
        Type translationType = Type.getType(Translation.class);
        Type translationListType = Type.getType(Translation.Translations.class);

        for (ModFileScanData.AnnotationData annotationData : annotationDataSet) {
            if (!annotationData.annotationType().equals(translationType) && !annotationData.annotationType().equals(translationListType)) {
                continue;
            }

            List<Translation> translations;

            try {
                // Technically 'AnnotationData#annotationData' contains the information
                // To keep things simple though we use the same method as in 'ConfigHandler#createConfigEntries'
                translations = getTranslations(Class.forName(annotationData.clazz().getClassName()).getDeclaredField(annotationData.memberName()));
            } catch (ReflectiveOperationException exception) {
                throw new RuntimeException("An error occurred while trying to get the translations from [" + annotationData + "]", exception);
            }

            translations.forEach(translation -> {
                if (!locale.equals(translation.locale())) {
                    return;
                }

                StringBuilder comment = new StringBuilder();

                for (int line = 0; line < translation.comments().length; line++) {
                    comment.append(translation.comments()[line]);

                    // Don't add a new line to the last line
                    if (line != translation.comments().length - 1) {
                        comment.append("\n");
                    }
                }

                try {
                    add(translation.type().prefix + translation.key() + translation.type().suffix, comment.toString());
                } catch (IllegalStateException exception) {
                    DragonSurvival.LOGGER.error("Invalid translation entry due to a duplicate key issue [{}]", translation);
                    throw exception;
                }

                if (translation.type() == Translation.Type.CONFIGURATION) {
                    String capitalized = capitalize(translation.key().split("_"));

                    if (capitalized.length() > 25) {
                        DragonSurvival.LOGGER.warn("Translation [{}] for the key [{}] might be too long for the configuration screen", capitalized, translation.key());
                    }

                    add(translation.type().prefix + translation.key(), capitalized);
                }
            });
        }
    }

    private void handleConfigCategories(final Set<ModFileScanData.AnnotationData> annotationDataSet) {
        Type configOptionType = Type.getType(ConfigOption.class);
        List<String> categoriesAdded = new ArrayList<>();

        for (ModFileScanData.AnnotationData annotationData : annotationDataSet) {
            if (!annotationData.annotationType().equals(configOptionType)) {
                continue;
            }

            //noinspection unchecked -> it is the correct type
            List<String> categories = (List<String>) annotationData.annotationData().get("category");

            if (categories == null || categories.isEmpty()) {
                categories = List.of("general");
            }

            categories.forEach(category -> {
                if (categoriesAdded.contains(category)) {
                    return;
                }

                categoriesAdded.add(category);
                String key = LangKey.CATEGORY_PREFIX + category;
                add(key, capitalize(category.split("_")));
            });
        }
    }

    public static List<Translation> getTranslations(final Field field) {
        Translation translation = field.getAnnotation(Translation.class);

        if (translation != null) {
            return List.of(translation);
        }

        Translation.Translations translations = field.getAnnotation(Translation.Translations.class);

        if (translations != null) {
            return List.of(translations.value());
        }

        return List.of();
    }

    @SuppressWarnings("deprecation") // ignore
    private String capitalize(final String... components) {
        if (components.length == 1) {
            return WordUtils.capitalize(components[0]);
        }

        StringBuilder capitalized = new StringBuilder();

        for (int i = 0; i < components.length; i++) {
            capitalized.append(WordUtils.capitalize(components[i]));

            // Don't add a white space to the last element
            if (i != components.length - 1) {
                capitalized.append(" ");
            }
        }

        return capitalized.toString();
    }
}
