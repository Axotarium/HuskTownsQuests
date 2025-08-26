package fr.qg.quests.models

class TownData(
    val completed: MutableList<String>,
    val inProgress: MutableMap<String, Int>
)