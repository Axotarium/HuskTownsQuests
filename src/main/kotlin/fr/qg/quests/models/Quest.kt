package fr.qg.quests.models

data class Quest(
    val id: String,

    val type: QuestType,
    val goal: Int,
    val data: String,
    val prerequisites: List<String>,

    val view: QuestView
)