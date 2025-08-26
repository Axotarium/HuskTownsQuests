package fr.qg.quests.models

class ItemConfiguration(
    val name: String,
    val lore: List<String>,
    val type: String,
    val enchanted: Boolean,
    val modeldata: Int
)