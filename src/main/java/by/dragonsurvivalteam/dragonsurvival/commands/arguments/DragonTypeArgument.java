package by.dragonsurvivalteam.dragonsurvival.commands.arguments;

import by.dragonsurvivalteam.dragonsurvival.registry.dragon.DragonType;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.ResourceLocationException;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;

public class DragonTypeArgument implements ArgumentType<Holder<DragonType>> {
    public static final String ID = "dragon_type";

    private final HolderLookup.RegistryLookup<DragonType> lookup;

    public DragonTypeArgument(final CommandBuildContext context) {
        lookup = context.lookupOrThrow(DragonType.REGISTRY);
    }

    @Override
    public @Nullable Holder<DragonType> parse(final StringReader reader) throws CommandSyntaxException {
        try {
            int start = reader.getCursor();
            ResourceLocation dragonType = ResourceLocation.read(reader);
            Optional<Holder.Reference<DragonType>> optional = lookup.get(ResourceKey.create(DragonType.REGISTRY, dragonType));

            if (optional.isEmpty()) { // TODO :: do the same (error handling) for stage and body
                reader.setCursor(start);
                throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument().createWithContext(reader);
            }

            return optional.get();
        } catch (ResourceLocationException exception) {
            // ResourceLocation#read already resets the cursor
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument().createWithContext(reader);
        }
    }

    public static Holder<DragonType> get(final CommandContext<?> context) {
        //noinspection unchecked -> type is valid
        return (Holder<DragonType>) context.getArgument(ID, Holder.class);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
        List<String> suggestions = new ArrayList<>();
        lookup.listElementIds().forEach(element -> suggestions.add(element.location().toString()));
        return SharedSuggestionProvider.suggest(suggestions, builder);
    }
}
