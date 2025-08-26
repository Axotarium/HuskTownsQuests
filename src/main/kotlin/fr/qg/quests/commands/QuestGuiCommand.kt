package fr.qg.quests.commands

import com.mojang.brigadier.context.CommandContext
import fr.qg.quests.handler.GuiHandler
import fr.qg.quests.utils.PlayerOnlyCommand
import io.papermc.paper.command.brigadier.CommandSourceStack
import org.bukkit.entity.Player

object QuestGuiCommand : PlayerOnlyCommand {

    override fun execute(sender: Player, ctx: CommandContext<CommandSourceStack>) {
        val category = ctx.getArgument("category", String::class.java)

        sender.sendPlainMessage(category)
    }
}