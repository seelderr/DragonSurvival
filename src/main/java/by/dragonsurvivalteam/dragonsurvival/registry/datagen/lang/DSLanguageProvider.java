package by.dragonsurvivalteam.dragonsurvival.registry.datagen.lang;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvival;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigOption;
import by.dragonsurvivalteam.dragonsurvival.registry.datagen.Translation;
import net.minecraft.core.Holder;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.modscan.ModAnnotation;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.neoforged.neoforgespi.language.ModFileScanData;
import org.apache.commons.lang3.text.WordUtils;
import org.objectweb.asm.Type;

import java.lang.annotation.ElementType;
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
        // This list contains a separate entry for each annotation - therefor we don't need to check for the Translations list element
        Set<ModFileScanData.AnnotationData> annotationDataSet = ModList.get().getModFileById(DragonSurvival.MODID).getFile().getScanResult().getAnnotations();

        handleTranslationAnnotations(annotationDataSet);
        handleConfigCategories(annotationDataSet);
    }

    private void handleTranslationAnnotations(final Set<ModFileScanData.AnnotationData> annotationDataSet) {
        Type translationType = Type.getType(Translation.class);

        for (ModFileScanData.AnnotationData annotationData : annotationDataSet) {
            if (!annotationData.annotationType().equals(translationType)) {
                continue;
            }

            // Default values of annotations are not stored in the annotation data map
            String locale = (String) annotationData.annotationData().get("locale");

            if (locale != null && !locale.equals(this.locale)) {
                continue;
            }

            String key = (String) annotationData.annotationData().get("key");
            Translation.Type type = Translation.Type.valueOf(((ModAnnotation.EnumHolder) annotationData.annotationData().get("type")).value());
            //noinspection unchecked -> type is correct
            List<String> comments = (List<String>) annotationData.annotationData().get("comments");

            if (annotationData.targetType() == ElementType.FIELD) {
                try {
                    Field field = Class.forName(annotationData.clazz().getClassName()).getDeclaredField(annotationData.memberName());
                    field.setAccessible(true);

                    if (key == null) {
                        if (Holder.class.isAssignableFrom(field.getType())) {
                            Holder<?> holder = (Holder<?>) field.get(null);

                            //noinspection DataFlowIssue -> only a problem if we work with Holder$Direct which should not be the case here
                            key = type.wrap(holder.getKey().location().getPath());
                            add(key, format(comments));

                            continue;
                        }

                        if (ResourceKey.class.isAssignableFrom(field.getType())) {
                            ResourceKey<?> resourceKey = (ResourceKey<?>) field.get(null);

                            key = type.wrap(resourceKey.location().getPath());
                            add(key, format(comments));

                            continue;
                        }

                        if (type == Translation.Type.MISC && String.class.isAssignableFrom(field.getType())) {
                            String translationKey = (String) field.get(null);
                            add(translationKey, format(comments));

                            continue;
                        }
                    }
                } catch (ReflectiveOperationException exception) {
                    throw new RuntimeException("An error occurred while trying to get the translations from [" + annotationData + "]", exception);
                }
            }

            if (key == null || key.isEmpty()) {
                throw new IllegalStateException("Key should not be empty if annotated on a non-holder field - annotation data: [" + annotationData + "]");
            }

            try {
                add(type.wrap(key), format(comments));
            } catch (IllegalStateException exception) {
                // Log extra information to make debugging easier
                DragonSurvival.LOGGER.error("Invalid translation entry due to a duplicate key issue [{}]", annotationData);
                throw exception;
            }

            if (type == Translation.Type.CONFIGURATION) {
                String capitalized = capitalize(key.split("_"));

                if (capitalized.length() > 25) {
                    DragonSurvival.LOGGER.warn("Translation [{}] for the key [{}] might be too long for the configuration screen", capitalized, key);
                }

                add(type.prefix + key, capitalized);
            }
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

    private String format(final List<String> comments) {
        return format(comments.toArray(new String[0]));
    }

    /** Separates the comment elements by a new line */
    private String format(final String... comments) {
        StringBuilder comment = new StringBuilder();

        for (int line = 0; line < comments.length; line++) {
            comment.append(comments[line]);

            // Don't add a new line to the last line
            if (line != comments.length - 1) {
                comment.append("\n");
            }
        }

        return comment.toString();
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
