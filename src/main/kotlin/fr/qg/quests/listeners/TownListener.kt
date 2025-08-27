package fr.qg.quests.listeners

import fr.qg.quests.registries.TownsRegistry.disband
import fr.qg.quests.registries.TownsRegistry.register
import net.william278.husktowns.events.PostTownCreateEvent
import net.william278.husktowns.events.TownDisbandEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

object TownListener : Listener {

    @EventHandler
    fun onCreate(event: PostTownCreateEvent) {
        val town = event.town
        town.register()
    }

    @EventHandler
    fun onCreate(event: TownDisbandEvent) {
        event.town.disband()
    }
}