package fr.qg.quests.actions

import fr.qg.quests.models.QuestType
import fr.qg.quests.registries.TownsRegistry.getStartedQuests
import fr.qg.quests.registries.TownsRegistry.increaseQuestProgress
import fr.qg.quests.utils.town
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.CraftItemEvent

object CraftingAction : QuestAction {

    @EventHandler
    fun onCraft(event: CraftItemEvent) {

        val player = event.whoClicked as Player
        val town = player.town ?: return

        val item = event.currentItem?.type?.name ?: return

        town.getStartedQuests(QuestType.CRAFTING).filter { it.data.equals(item, true) }.forEach {
            town.increaseQuestProgress(1, it)
        }
    }
}