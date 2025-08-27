package fr.qg.quests.actions

import fr.qg.quests.models.QuestType
import fr.qg.quests.registries.TownsRegistry.getStartedQuests
import fr.qg.quests.registries.TownsRegistry.increaseQuestProgress
import fr.qg.quests.utils.town
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockBreakEvent

object BlockBreakAction : QuestAction {

    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        val player = event.player

        val town = player.town ?: return

        val block = event.block
        val material = block.type

        town.getStartedQuests(QuestType.BREAK_BLOCK).forEach { quest ->
            if (quest.data.equals(material.name, true)) {
                town.increaseQuestProgress(1, quest)
            }
        }
    }
}