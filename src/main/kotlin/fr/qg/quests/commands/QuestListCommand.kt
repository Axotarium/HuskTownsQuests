package fr.qg.quests.commands

import com.mojang.brigadier.context.CommandContext
import fr.qg.quests.registries.QuestsRegistry
import fr.qg.quests.registries.TownsRegistry
import fr.qg.quests.registries.TownsRegistry.data
import fr.qg.quests.utils.PlayerOnlyCommand
import fr.qg.quests.utils.town
import io.papermc.paper.command.brigadier.CommandSourceStack
import org.bukkit.entity.Player

object QuestListCommand : PlayerOnlyCommand {

    override fun execute(sender: Player, ctx: CommandContext<CommandSourceStack>) {

        val started = ctx.getArgument("started", Boolean::class.java) ?: false

        if (started) {
            val data = sender.town?.data ?: return sender.sendPlainMessage("You are not in a town !")

            data.inProgress.forEach {
                sender.sendPlainMessage("${it.key} : ${it.value}")
            }
        } else {
            QuestsRegistry.quests.forEach {
                sender.sendPlainMessage("${it.id}")
            }
        }
    }
}
