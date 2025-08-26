package fr.qg.quests.registries

import fr.qg.quests.QuestsPlugin
import fr.qg.quests.models.Quest
import fr.qg.quests.models.QuestType
import fr.qg.quests.models.QuestView
import fr.qg.quests.utils.QGLogger
import fr.qg.quests.utils.safeEnumValueOf
import org.bukkit.Material
import org.bukkit.configuration.ConfigurationSection
import java.util.logging.Level

object QuestsRegistry {

    val quests: MutableList<Quest> = mutableListOf()

    fun load() {
        quests.clear()
        quests.addAll(loadQuests())
    }

    fun loadQuests(): List<Quest> {

        val result = mutableListOf<Quest>()
        val config = QuestsPlugin.instance.config.getConfigurationSection("quests") ?: return result

        config.getKeys(false).forEach { id ->
            config.getQuest(id)?.let { result.add(it) }
        }

        return result
    }

    fun ConfigurationSection.getQuest(id: String): Quest? {
        val type = safeEnumValueOf<QuestType>(getString("type", "BREAK_BLOCK")!!) ?: run {
            QGLogger.log( "Unknown quest type for quest $id", Level.WARNING)
            return null
        }
        val goal = getInt("goal")
        val data = getString("data") ?: run {
            QGLogger.log( "No data for quest $id", Level.WARNING)
            return null
        }
        val prerequisites = getStringList("prerequisites")

        val viewsection = getConfigurationSection("view") ?: run {
            QGLogger.log( "No view for quest $id", Level.WARNING)
            return null
        }

        return Quest(id, type, goal, data, prerequisites, viewsection.asQuestView())
    }

    fun ConfigurationSection.asQuestView() : QuestView {
        val title = getString("title") ?: "error"
        val material = safeEnumValueOf<Material>(getString("material")!!) ?: Material.STONE
        val lore = getStringList("lore")
        val modeldata = getInt("modeldata", -1)

        val category = getString("category") ?: ""

        return QuestView(title, lore, material, category, if(modeldata == -1) null else modeldata )
    }
}