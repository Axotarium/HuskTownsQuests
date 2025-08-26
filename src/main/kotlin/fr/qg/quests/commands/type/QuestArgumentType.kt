package fr.qg.quests.commands.type

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import fr.qg.quests.models.Quest
import fr.qg.quests.registries.QuestsRegistry
import io.papermc.paper.command.brigadier.argument.CustomArgumentType
import java.util.concurrent.CompletableFuture

object QuestArgumentType : CustomArgumentType<Quest, String> {

    override fun parse(reader: StringReader): Quest =
        QuestsRegistry.quests.first { it.id == reader.readUnquotedString() }

    override fun getNativeType(): ArgumentType<String> = StringArgumentType.word()

    override fun <S : Any> listSuggestions(context: CommandContext<S>, builder: SuggestionsBuilder): CompletableFuture<Suggestions> {
        QuestsRegistry.quests.filter { it.id.startsWith(builder.remaining) }
            .forEach { builder.suggest(it.id) }
        return builder.buildFuture()
    }

}
