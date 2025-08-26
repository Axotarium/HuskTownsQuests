package fr.qg.quests.actions

import fr.qg.quests.models.QuestType
import fr.qg.quests.registries.TownsRegistry.getStartedQuests
import fr.qg.quests.registries.TownsRegistry.increaseQuestProgress
import fr.qg.quests.utils.town
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDeathEvent

object KillEntityAction : QuestAction {

    @EventHandler
    fun onEntityDeath(event: EntityDeathEvent) {
        val entity = event.entity
        val killer = entity.killer ?: return

        val town = killer.town ?: return

        town.getStartedQuests(QuestType.KILL_ENTITY).filter { it.data.equals(entity.type.name, true) }.forEach {
            town.increaseQuestProgress(1, it)
        }
    }
}