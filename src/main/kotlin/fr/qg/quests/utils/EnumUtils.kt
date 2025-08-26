package fr.qg.quests.utils

inline fun <reified T : Enum<T>> safeEnumValueOf(value: String): T? {
    return enumValues<T>().firstOrNull { it.name.equals(value, true) }
}