package fr.qg.quests.models

import fr.qg.quests.actions.BlockBreakAction
import fr.qg.quests.actions.CraftingAction
import fr.qg.quests.actions.FishingAction
import fr.qg.quests.actions.KillEntityAction
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.event.Listener

enum class QuestType(val listener: Listener) {

    BREAK_BLOCK(BlockBreakAction),
    KILL_ENTITY(KillEntityAction),
    FISHING(FishingAction),
    CRAFTING(CraftingAction)

}