package fr.qg.quests.utils

import net.william278.husktowns.api.HuskTownsAPI
import net.william278.husktowns.town.Town
import org.bukkit.entity.Player
import kotlin.jvm.optionals.getOrNull

val Player.town: Town?
    get() = HuskTownsAPI.getInstance().getUserTown(
        HuskTownsAPI.getInstance().getOnlineUser(this.uniqueId)
    ).getOrNull()?.town