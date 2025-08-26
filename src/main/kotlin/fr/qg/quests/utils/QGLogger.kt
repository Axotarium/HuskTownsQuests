package fr.qg.quests.utils

import org.bukkit.Bukkit
import java.util.logging.Level

object QGLogger {

    fun log(message: String, level: Level = Level.INFO) {
        Bukkit.getLogger().log( level, "[${level.name}]: $message")
    }
}