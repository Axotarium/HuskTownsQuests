package fr.qg.quests.commands

import com.mojang.brigadier.context.CommandContext
import fr.qg.quests.QuestsPlugin
import fr.qg.quests.handler.GuiHandler
import fr.qg.quests.registries.QuestsRegistry
import fr.qg.quests.registries.TownsRegistry
import fr.qg.quests.utils.PlayerOnlyCommand
import io.papermc.paper.command.brigadier.CommandSourceStack
import org.bukkit.entity.Player

object QuestReloadCommand : PlayerOnlyCommand {
    override fun execute(
        sender: Player,
        ctx: CommandContext<CommandSourceStack>
    ) {
        QuestsPlugin.instance.reloadConfig()
        GuiHandler.load()
        QuestsRegistry.load()
        TownsRegistry.load()
        sender.sendPlainMessage("Quests reloaded !")
    }

}
