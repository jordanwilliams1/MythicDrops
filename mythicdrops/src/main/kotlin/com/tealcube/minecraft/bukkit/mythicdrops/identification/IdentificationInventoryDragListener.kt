/*
 * This file is part of MythicDrops, licensed under the MIT License.
 *
 * Copyright (C) 2019 Richard Harrah
 *
 * Permission is hereby granted, free of charge,
 * to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS
 * OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.tealcube.minecraft.bukkit.mythicdrops.identification

import com.tealcube.minecraft.bukkit.mythicdrops.api.choices.IdentityWeightedChoice
import com.tealcube.minecraft.bukkit.mythicdrops.api.items.ItemGenerationReason
import com.tealcube.minecraft.bukkit.mythicdrops.api.relations.RelationManager
import com.tealcube.minecraft.bukkit.mythicdrops.api.settings.SettingsManager
import com.tealcube.minecraft.bukkit.mythicdrops.api.tiers.TierManager
import com.tealcube.minecraft.bukkit.mythicdrops.chatColorize
import com.tealcube.minecraft.bukkit.mythicdrops.getTargetItemAndCursorAndPlayer
import com.tealcube.minecraft.bukkit.mythicdrops.items.builders.MythicDropBuilder
import com.tealcube.minecraft.bukkit.mythicdrops.items.getFromItemMetaAsDamageable
import com.tealcube.minecraft.bukkit.mythicdrops.items.getThenSetItemMetaAsDamageable
import com.tealcube.minecraft.bukkit.mythicdrops.logging.JulLoggerFactory
import com.tealcube.minecraft.bukkit.mythicdrops.updateCurrentItemAndSubtractFromCursor
import com.tealcube.minecraft.bukkit.mythicdrops.utils.BroadcastMessageUtil
import com.tealcube.minecraft.bukkit.mythicdrops.utils.ItemUtil
import com.tealcube.minecraft.bukkit.mythicdrops.utils.TierUtil
import io.pixeloutlaw.minecraft.spigot.hilt.getDisplayName
import io.pixeloutlaw.minecraft.spigot.hilt.getLore
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent

class IdentificationInventoryDragListener(
    private val relationManager: RelationManager,
    private val settingsManager: SettingsManager,
    private val tierManager: TierManager
) : Listener {
    private val logger = JulLoggerFactory.getLogger(IdentificationInventoryDragListener::class)

    @EventHandler(priority = EventPriority.LOWEST)
    fun onInventoryClickEvent(event: InventoryClickEvent) {
        val targetItemAndCursorAndPlayer = event.getTargetItemAndCursorAndPlayer(logger) ?: return
        val (targetItem, cursor, player) = targetItemAndCursorAndPlayer
        val cursorName = cursor.getDisplayName() ?: ""
        val targetItemName = targetItem.getDisplayName() ?: ""

        // Check if the cursor is an Identity Tome
        val identityTome = IdentityTome(settingsManager.identifyingSettings.items.identityTome)
        if (cursorName != identityTome.getDisplayName()) {
            logger.fine("cursorName != identifyingSettings.identityTomeName.chatColorize()")
            return
        }

        // Check if the target item is an Unidentified Item
        val unidentifiedVersionOfTargetItem =
            UnidentifiedItem(targetItem.type, settingsManager.identifyingSettings.items.unidentifiedItem)
        if (targetItemName != unidentifiedVersionOfTargetItem.getDisplayName()) {
            logger.fine("targetItemName != unidentifiedVersionOfTargetItem.getDisplayName()")
            player.sendMessage(settingsManager.languageSettings.identification.notUnidentifiedItem.chatColorize())
            return
        }

        // Get potential tier from last line of lore
        val targetItemLore = targetItem.getLore()
        val potentialTierString = if (targetItemLore.isNotEmpty()) {
            targetItemLore.last()
        } else {
            ""
        }

        // Identify the item
        val tier = TierUtil.getTier(potentialTierString)
            ?: IdentityWeightedChoice.between(ItemUtil.getTiersFromMaterial(targetItem.type)).choose()
        if (tier == null) {
            logger.fine("tier == null")
            player.sendMessage(settingsManager.languageSettings.identification.failure.chatColorize())
            return
        }

        val newTargetItem = MythicDropBuilder(relationManager, settingsManager, tierManager)
            .withItemGenerationReason(ItemGenerationReason.DEFAULT)
            .withMaterial(targetItem.type)
            .withTier(tier)
            .useDurability(false)
            .build()

        if (newTargetItem == null) {
            logger.fine("newTargetItem == null")
            player.sendMessage(settingsManager.languageSettings.identification.failure.chatColorize())
            return
        }

        newTargetItem.getThenSetItemMetaAsDamageable({
            damage = targetItem.getFromItemMetaAsDamageable({ damage }) ?: 0
        }, { this.durability = targetItem.durability })

        val identificationEvent = IdentificationEvent(newTargetItem, player)
        Bukkit.getPluginManager().callEvent(identificationEvent)

        if (identificationEvent.isCancelled) {
            logger.fine("identificationEvent.isCancelled")
            player.sendMessage(settingsManager.languageSettings.identification.failure.chatColorize())
            return
        }

        event.updateCurrentItemAndSubtractFromCursor(identificationEvent.result)
        player.sendMessage(settingsManager.languageSettings.identification.success.chatColorize())
        if (tier.isBroadcastOnFind) {
            BroadcastMessageUtil.broadcastItem(settingsManager.languageSettings, player, identificationEvent.result)
        }
    }
}
