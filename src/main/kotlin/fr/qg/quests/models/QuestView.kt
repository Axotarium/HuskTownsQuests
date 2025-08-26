package fr.qg.quests.models

import org.bukkit.Material

class QuestView(
    val name: String,
    val lore: List<String>,
    val type: Material,
    val category: String,
    val modeldata: Int?
)
