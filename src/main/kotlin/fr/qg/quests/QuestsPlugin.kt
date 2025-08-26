package fr.qg.quests

import com.mojang.brigadier.arguments.BoolArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import fr.qg.menu.MenuAPI
import fr.qg.quests.commands.QuestGuiCommand
import fr.qg.quests.commands.QuestListCommand
import fr.qg.quests.commands.QuestReloadCommand
import fr.qg.quests.commands.QuestStartCommand
import fr.qg.quests.commands.type.QuestArgumentType
import fr.qg.quests.handler.GuiHandler
import fr.qg.quests.listeners.TownListener
import fr.qg.quests.models.Quest
import fr.qg.quests.models.QuestType
import fr.qg.quests.registries.QuestsRegistry
import fr.qg.quests.registries.TownsRegistry
import fr.qg.quests.utils.executeOnlyForPlayer
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class QuestsPlugin : JavaPlugin() {

    companion object {
        lateinit var instance: QuestsPlugin
    }

    override fun onEnable() {
        saveDefaultConfig()

        instance = this

        TownsRegistry.load()
        QuestsRegistry.load()

        MenuAPI.register(this)
        GuiHandler.load()

        server.pluginManager.registerEvents(TownListener, this)

        QuestType.entries.forEach { server.pluginManager.registerEvents(it.listener, this) }

        Bukkit.getScheduler().runTaskTimer(this, Runnable {
            TownsRegistry.save()
            println("towns' quests saved !")
        }, 20*60*3, 20*60*3)

        Commands.literal("townquests")
            .then(
                Commands.literal("reload")
                    .executeOnlyForPlayer(QuestReloadCommand)
            )
            .then(
                Commands.literal("start")
                         .then(Commands.argument("quest", QuestArgumentType)
                            .executeOnlyForPlayer(QuestStartCommand))
            )
            .then(Commands.literal("list")
                .then(Commands.argument("started", BoolArgumentType.bool())
                    .executeOnlyForPlayer(QuestListCommand)
            ))
            .then(
                Commands.literal("gui")
                    .then(Commands.argument("category", StringArgumentType.word())
                        .executeOnlyForPlayer(QuestGuiCommand)
            ))

    }
}