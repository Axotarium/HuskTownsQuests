package fr.qg.quests.listeners

import fr.qg.quests.QuestsPlugin
import fr.qg.quests.registries.TownsRegistry.disband
import fr.qg.quests.registries.TownsRegistry.register
import net.william278.husktowns.HuskTowns
import net.william278.husktowns.api.HuskTownsAPI
import net.william278.husktowns.events.TownCreateEvent
import net.william278.husktowns.events.TownDisbandEvent
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import kotlin.jvm.optionals.getOrNull

object TownListener : Listener {

    @EventHandler
    fun onCreate(event: TownCreateEvent) {
        Bukkit.getScheduler().runTaskLater(QuestsPlugin.instance, Runnable {
            val town = HuskTownsAPI.getInstance().getTown(event.townName).getOrNull() ?: return@Runnable
            town.register()
        }, 2)
    }

    @EventHandler
    fun onCreate(event: TownDisbandEvent) {
        event.town.disband()
    }

}