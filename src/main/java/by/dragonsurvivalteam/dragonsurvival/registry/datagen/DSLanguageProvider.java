package by.dragonsurvivalteam.dragonsurvival.registry.datagen;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvival;
import net.minecraft.data.PackOutput;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.neoforged.neoforgespi.language.ModFileScanData;
import org.apache.commons.lang3.text.WordUtils;
import org.objectweb.asm.Type;

import java.lang.annotation.ElementType;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public class DSLanguageProvider extends LanguageProvider {
    private final String locale;

    public DSLanguageProvider(final PackOutput output, final String locale) {
        super(output, DragonSurvival.MODID, locale);
        this.locale = locale;
    }

    @Override
    protected void addTranslations() {
        Set<ModFileScanData.AnnotationData> annotationDataSet = ModList.get().getModFileById(DragonSurvival.MODID).getFile().getScanResult().getAnnotations();
        Predicate<Type> typePredicate = type -> type.equals(Type.getType(Translation.class)) || type.equals(Type.getType(Translation.Translations.class));

        for (ModFileScanData.AnnotationData annotationData : annotationDataSet) {
            if (annotationData.targetType() != ElementType.FIELD || !typePredicate.test(annotationData.annotationType())) {
                continue;
            }

            List<Translation> translations;

            try {
                // Technically 'AnnotationData#annotationData' contains the information
                // To keep things simple though we use the same method as in 'ConfigHandler#createConfigEntries'
                translations = getTranslations(annotationData.clazz().getClass().getField(annotationData.memberName()));
            } catch (NoSuchFieldException exception) {
                throw new RuntimeException(exception);
            }

            translations.forEach(translation -> {
                if (locale.equals(translation.locale())) {
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

                add(translation.type().prefix + translation.key() + translation.type().suffix, comment.toString());

                if (translation.type() == Translation.Type.CONFIGURATION) {
                    // If no translation key for the configuration key is supplied it will default to '<mod_id>.configuration.<key>'
                    // Currently we just make the key by turning sth. like 'sea_vision_mana_cost' into 'Sea Vision Mana Cost'
                    String[] keyComponents = translation.key().split("_");
                    StringBuilder translatedKey = new StringBuilder();

                    for (int i = 0; i < keyComponents.length; i++) {
                        //noinspection deprecation -> ignore
                        translatedKey.append(WordUtils.capitalize(keyComponents[i]));

                        // Don't add a white space to the last element
                        if (i != keyComponents.length - 1) {
                            translatedKey.append(" ");
                        }
                    }

                    add(translation.type().prefix + translation.key(), translatedKey.toString());
                }
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
}
