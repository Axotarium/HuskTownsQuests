package fr.qg.quests.commands

import com.mojang.brigadier.context.CommandContext
import fr.qg.quests.models.Quest
import fr.qg.quests.registries.TownsRegistry.startQuest
import fr.qg.quests.utils.PlayerOnlyCommand
import fr.qg.quests.utils.town
import io.papermc.paper.command.brigadier.CommandSourceStack
import org.bukkit.entity.Player


object QuestStartCommand : PlayerOnlyCommand {

    override fun execute(sender: Player, ctx: CommandContext<CommandSourceStack>) {

        val quest = ctx.getArgument("quest", Quest::class.java)
        val town = sender.town ?: return sender.sendPlainMessage("You are not in a town !")

        town.startQuest(quest)
        sender.sendPlainMessage("Quest started !")
    }
}
