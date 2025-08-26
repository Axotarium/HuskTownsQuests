package fr.qg.quests.actions

import fr.qg.quests.models.QuestType
import fr.qg.quests.registries.TownsRegistry.getStartedQuests
import fr.qg.quests.registries.TownsRegistry.increaseQuestProgress
import fr.qg.quests.utils.town
import org.bukkit.entity.Item
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerFishEvent

object FishingAction : QuestAction {

    @EventHandler
    fun onFishing(event: PlayerFishEvent) {
        val player = event.player
        val town = player.town ?: return

        val item = (event.caught as? Item)?.itemStack?.type?.name ?: return

        town.getStartedQuests(QuestType.FISHING).filter { it.data.equals(item, true) }.forEach {
            town.increaseQuestProgress(1, it)
        }
    }
}