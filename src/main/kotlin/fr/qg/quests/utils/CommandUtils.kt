package fr.qg.quests.utils

import com.mojang.brigadier.Command
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import io.papermc.paper.command.brigadier.CommandSourceStack
import org.bukkit.entity.Player


fun <T : ArgumentBuilder<CommandSourceStack, T>> ArgumentBuilder<CommandSourceStack, T>
        .executeOnlyForPlayer(cmd: PlayerOnlyCommand): T? =
    executes { ctx ->
        val sender = ctx.getSource().sender
        val executor = ctx.getSource().executor

        if (executor !is Player) {
            sender.sendPlainMessage("The command is player only !")
            return@executes Command.SINGLE_SUCCESS
        }

        cmd.execute(executor, ctx)

        return@executes Command.SINGLE_SUCCESS
    }

interface PlayerOnlyCommand {
    fun execute(sender: Player, ctx: CommandContext<CommandSourceStack>)
}
