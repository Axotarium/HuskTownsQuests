package fr.qg.quests.registries

import com.google.common.reflect.TypeToken
import com.google.gson.GsonBuilder
import fr.qg.quests.QuestsPlugin
import fr.qg.quests.models.Quest
import fr.qg.quests.models.QuestType
import fr.qg.quests.models.TownData
import net.william278.husktowns.api.HuskTownsAPI
import net.william278.husktowns.town.Town
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import kotlin.collections.mutableMapOf
import kotlin.jvm.optionals.getOrNull

object TownsRegistry {

    private val towns = ConcurrentHashMap<Town, MutableMap<QuestType, MutableList<Quest>>>()
    private val storageTowns = ConcurrentHashMap<Town, TownData>()

    private val dataFile: File by lazy {
        val plugin = JavaPlugin.getProvidingPlugin(QuestsPlugin::class.java)
        val folder = plugin.dataFolder
        if (!folder.exists()) folder.mkdirs()
        File(folder, "towns.json")
    }

    private val gson = GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()

    fun load() {
        storageTowns.clear()
        storageTowns.putAll(loadTowns())

        towns.clear()
        storageTowns.forEach { (town, data) ->
            towns[town] = mutableMapOf()

            data.completed.forEach { questId ->
                val quest = QuestsRegistry.quests.first { it.id == questId }
                towns[town]?.getOrPut(quest.type) { mutableListOf() }?.add(quest)
            }
        }
    }

    fun loadTowns(): Map<Town, TownData> {
        if (!dataFile.exists()) return mutableMapOf()

        val json = dataFile.readText()
        val type = object : TypeToken<MutableMap<Int, TownData>>() {}.type
        return gson.fromJson<MutableMap<Int, TownData>>(json, type)
            .mapKeys { (k, _) -> HuskTownsAPI.getInstance().getTown(k).getOrNull() }
            .filterKeys { it != null }.mapKeys { it.key!! }
    }

    fun save() {
        val storage = storageTowns.mapKeys { it.key.id }
        val json = gson.toJson(storage)
        dataFile.writeText(json)
    }

    fun Town.register() {
        towns[this] = towns.getOrDefault(this, mutableMapOf())
        storageTowns[this] = TownData(mutableListOf(), mutableMapOf())
    }

    fun Town.disband() {
        towns.remove(this)
        storageTowns.remove(this)
    }

    fun Town.getStartedQuests(questType: QuestType): List<Quest> = towns[this]?.get(questType) ?: emptyList()

    fun Town.startQuest(quest: Quest) {
        val data = storageTowns[this]!!
        data.inProgress[quest.id] = 0
        towns[this]?.get(quest.type)?.add(quest)
    }

    val Town.data: TownData?
        get() = storageTowns[this]

    fun Town.increaseQuestProgress(value: Int, quest: Quest) {
        val data = storageTowns[this]!!

        data.inProgress[quest.id] = data.inProgress.getOrDefault(quest.id, 0) + value

        verifyCompletion(quest, data, this)
    }

    private fun verifyCompletion(quest: Quest, data: TownData, town: Town) {
        if (data.inProgress[quest.id]!! >= quest.goal) {
            data.completed.add(quest.id)
            data.inProgress.remove(quest.id)

            towns[town]?.let {
                it[quest.type]?.remove(quest)
            }

            town.members.forEach {
                Bukkit.getPlayer(it.key)?.sendMessage("La quête ${quest.id} a été terminée ! GG")
            }
        }
    }
}