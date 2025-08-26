package fr.qg.quests

import com.mojang.brigadier.arguments.BoolArgumentType
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import fr.qg.quests.commands.QuestListCommand
import fr.qg.quests.commands.QuestStartCommand
import fr.qg.quests.commands.type.QuestArgumentType
import fr.qg.quests.models.Quest
import fr.qg.quests.registries.QuestsRegistry
import fr.qg.quests.registries.TownsRegistry
import fr.qg.quests.utils.executeOnlyForPlayer
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import org.bukkit.plugin.java.JavaPlugin

class QuestsPlugin : JavaPlugin() {

    companion object {
        lateinit var instance: QuestsPlugin
    }

    override fun onEnable() {
        instance = this

        TownsRegistry.load()
        QuestsRegistry.load()

        Commands.literal("townquests")
            .then(
                Commands.literal("start")
                         .then(Commands.argument("quest", QuestArgumentType)
                         .executeOnlyForPlayer(QuestStartCommand))
            )
            .then(Commands.literal("list")
                .then(Commands.argument("started", BoolArgumentType.bool())
                .executeOnlyForPlayer(QuestListCommand)
            ))
    }
}
