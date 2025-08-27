package fr.qg.quests.handler

import fr.qg.menu.common.actions.ClickScript
import fr.qg.menu.common.asMenu
import fr.qg.menu.common.models.OpenedMenuData
import fr.qg.menu.common.models.QGMenu
import fr.qg.menu.common.open
import fr.qg.menu.common.utils.mapSlot
import fr.qg.quests.QuestsPlugin
import fr.qg.quests.models.ItemConfiguration
import fr.qg.quests.models.Quest
import fr.qg.quests.registries.QuestsRegistry
import fr.qg.quests.registries.TownsRegistry.data
import fr.qg.quests.registries.TownsRegistry.startQuest
import fr.qg.quests.utils.QGLogger
import fr.qg.quests.utils.safeEnumValueOf
import fr.qg.quests.utils.town
import org.bukkit.Material
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import java.util.logging.Level

object GuiHandler {

    lateinit var questMenu: QGMenu

    lateinit var alreadyCompleted: ItemConfiguration
    lateinit var completing: ItemConfiguration
    lateinit var notstarted: ItemConfiguration

    const val QUESTS_SLOT_KEY = '$'

    fun load() {
        val config = QuestsPlugin.instance.config
        val guiSection = config.getConfigurationSection("gui")

        if (guiSection == null) {
            QGLogger.log("No 'gui' section found in config; GUI will not be available", Level.WARNING)
            return
        }

        // Load items
        val itemsSection = guiSection.getConfigurationSection("items")
        if (itemsSection == null) {
            QGLogger.log("No 'gui.items' section found in config", Level.WARNING)
        } else {
            alreadyCompleted = itemsSection.getConfigurationSection("already_completed")?.asItemConfiguration()
                ?: defaultItemConfig("&a{questname}", listOf("&7Terminé"), "quest-type")
            completing = itemsSection.getConfigurationSection("completing")?.asItemConfiguration()
                ?: defaultItemConfig("&e{questname}", listOf("&7Progression: &e{progression}&7/&e{goal}", "{description}"), "quest-type", enchanted = true)
            notstarted = itemsSection.getConfigurationSection("not_started")?.asItemConfiguration()
                ?: defaultItemConfig("&c{questname}", listOf("&7Clique pour démarrer", "{description}"), "quest-type")
        }

        // Load menu
        val menuSection = guiSection.getConfigurationSection("menu")
        if (menuSection == null) {
            QGLogger.log("No 'gui.menu' section found in config; GUI will not be available", Level.WARNING)
            return
        }

        try {
            questMenu = menuSection.asMenu()
            questMenu.scripts[QUESTS_SLOT_KEY] = questsClickScript
        } catch (e: Throwable) {
            QGLogger.log("Failed to load GUI menu from config: ${e.message}", Level.SEVERE)
            throw e
        }
    }

    private fun defaultItemConfig(
        name: String,
        lore: List<String>,
        type: String,
        enchanted: Boolean = false,
        modeldata: Int = -1
    ) = ItemConfiguration(name, lore, type, enchanted, modeldata)

    private fun ConfigurationSection.asItemConfiguration(): ItemConfiguration {
        val name = getString("name") ?: "&fItem"
        val lore = getStringList("lore")
        val type = getString("type") ?: "quest-type"
        val enchanted = getBoolean("enchanted", false)
        val modeldata = getInt("modeldata", -1)
        return ItemConfiguration(name, lore, type, enchanted, modeldata)
    }

    val questsClickScript: ClickScript
        get() = object : ClickScript {
            override fun action(data: OpenedMenuData, player: Player, slot: Int, event: InventoryClickEvent) {
                val quests = data.information as? List<Quest> ?: return

                val quest = quests[data.menu.mapSlot(QUESTS_SLOT_KEY, slot)]

                val town = player.town ?: return
                val data = town.data

                if (data.completed.contains(quest.id) || data.inProgress.contains(quest.id)) {
                    return
                }

                town.startQuest(quest)
                player.closeInventory()
                player.sendPlainMessage("Quest started !")
            }
        }


    fun ItemConfiguration.build(quest: Quest, progression: Int) : ItemStack {

        val view = quest.view

        val material = if (type.equals("quest-type", true)) view.type else safeEnumValueOf(type) ?: Material.BARRIER
        val itemName = this.name.replace("&", "§").replace("{questname}", view.name)

        val resultLore = mutableListOf<String>()
        this.lore.forEach {
            val returnStr = it.replace("{questname}", view.name)
                .replace("{progression}", "$progression")
                .replace("{goal}", "${quest.goal}")
                .replace("&","§")

            if (it.contains("{description}")) {
                view.lore.forEach {
                    resultLore.add(returnStr.replace("{description}", it))
                }
            } else resultLore.add(returnStr)
        }

        val item = ItemStack(material, 1)

        item.itemMeta = item.itemMeta?.apply {
            setDisplayName(itemName)
            lore = resultLore

            if (this@build.modeldata == -1 && view.modeldata != null) {
                setCustomModelData(view.modeldata)
            } else if (this@build.modeldata > 0) {
                setCustomModelData(this@build.modeldata)
            }

            if (this@build.enchanted) {
                addEnchant(Enchantment.UNBREAKING, 1, true)
            }

            addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ADDITIONAL_TOOLTIP)
        }

        return item
    }

    fun Player.openQuestMenu(type: String?) : Boolean {
        val quests = if (type.equals("all", true)) QuestsRegistry.quests
            else QuestsRegistry.quests.filter { it.view.category.equals(type, true) }
        return this.openQuestMenu(quests)
    }

    fun Player.openQuestMenu(quests: List<Quest>) : Boolean {
        println("a")
        val data = this.town?.data ?: return false
        println("b")
        questMenu.open(this, { inv ->
            val slots = questMenu.pattern.withIndex().filter { it.value == QUESTS_SLOT_KEY }.indices.toList()
            quests.subList(0, slots.size.coerceAtMost(quests.size)).onEachIndexed { i, q ->
                val slot = slots[i]

                if (data.completed.contains(q.id))
                    inv.setItem(slot, alreadyCompleted.build(q, q.goal))
                else if (data.inProgress.contains(q.id))
                    inv.setItem(slot, completing.build(q, data.inProgress[q.id] ?: 0))
                else
                    inv.setItem(slot, notstarted.build(q, 0))
            }
        }, {}, quests)

        return true
    }
}