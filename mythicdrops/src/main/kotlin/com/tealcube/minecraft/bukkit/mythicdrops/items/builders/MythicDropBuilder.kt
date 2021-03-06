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
package com.tealcube.minecraft.bukkit.mythicdrops.items.builders

import com.google.common.base.Joiner
import com.tealcube.minecraft.bukkit.mythicdrops.api.MythicDrops
import com.tealcube.minecraft.bukkit.mythicdrops.api.enchantments.MythicEnchantment
import com.tealcube.minecraft.bukkit.mythicdrops.api.items.ItemGenerationReason
import com.tealcube.minecraft.bukkit.mythicdrops.api.items.builders.DropBuilder
import com.tealcube.minecraft.bukkit.mythicdrops.api.names.NameType
import com.tealcube.minecraft.bukkit.mythicdrops.api.relations.RelationManager
import com.tealcube.minecraft.bukkit.mythicdrops.api.settings.SettingsManager
import com.tealcube.minecraft.bukkit.mythicdrops.api.tiers.Tier
import com.tealcube.minecraft.bukkit.mythicdrops.api.tiers.TierManager
import com.tealcube.minecraft.bukkit.mythicdrops.chatColorize
import com.tealcube.minecraft.bukkit.mythicdrops.events.RandomItemGenerationEvent
import com.tealcube.minecraft.bukkit.mythicdrops.items.getThenSetItemMetaAsDamageable
import com.tealcube.minecraft.bukkit.mythicdrops.items.setDisplayNameChatColorized
import com.tealcube.minecraft.bukkit.mythicdrops.items.setLoreChatColorized
import com.tealcube.minecraft.bukkit.mythicdrops.items.setRepairCost
import com.tealcube.minecraft.bukkit.mythicdrops.logging.JulLoggerFactory
import com.tealcube.minecraft.bukkit.mythicdrops.names.NameMap
import com.tealcube.minecraft.bukkit.mythicdrops.replaceArgs
import com.tealcube.minecraft.bukkit.mythicdrops.replaceWithCollections
import com.tealcube.minecraft.bukkit.mythicdrops.unChatColorize
import com.tealcube.minecraft.bukkit.mythicdrops.utils.ItemStackUtil
import com.tealcube.minecraft.bukkit.mythicdrops.utils.ItemUtil
import com.tealcube.minecraft.bukkit.mythicdrops.utils.LeatherArmorUtil
import com.tealcube.minecraft.bukkit.mythicdrops.utils.SkullUtil
import com.tealcube.minecraft.bukkit.mythicdrops.utils.TemplatingUtil
import io.pixeloutlaw.minecraft.spigot.hilt.getDisplayName
import io.pixeloutlaw.minecraft.spigot.hilt.setUnbreakable
import java.util.ArrayList
import java.util.logging.Logger
import kotlin.math.max
import kotlin.math.min
import org.apache.commons.text.WordUtils
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack

class MythicDropBuilder(
    private val relationManager: RelationManager,
    private val settingsManager: SettingsManager,
    private val tierManager: TierManager
) : DropBuilder {
    companion object {
        private val logger: Logger = JulLoggerFactory.getLogger(MythicDropBuilder::class)
    }

    constructor(mythicDrops: MythicDrops) : this(
        mythicDrops.relationManager,
        mythicDrops.settingsManager,
        mythicDrops.tierManager
    )

    private var tier: Tier? = null
    private var material: Material? = null
    private var itemGenerationReason: ItemGenerationReason = ItemGenerationReason.DEFAULT
    private var useDurability: Boolean = false

    override fun withTier(tier: Tier?): DropBuilder {
        this.tier = tier
        return this
    }

    override fun withTier(tierName: String?): DropBuilder {
        this.tier =
            tierName?.let { tierManager.getById(tierName) ?: tierManager.getById(tierName.replace(" ", "_")) } ?: null
        return this
    }

    override fun withMaterial(material: Material?): DropBuilder {
        this.material = material
        return this
    }

    override fun withItemGenerationReason(reason: ItemGenerationReason?): DropBuilder {
        this.itemGenerationReason = reason ?: ItemGenerationReason.DEFAULT
        return this
    }

    override fun useDurability(b: Boolean): DropBuilder {
        this.useDurability = b
        return this
    }

    override fun build(): ItemStack? {
        val chosenTier = tier ?: tierManager.randomByWeight() ?: return null
        val chosenMat = material ?: ItemUtil.getMaterialsFromTier(chosenTier).random() ?: return null

        val itemStack = ItemStack(chosenMat, 1)
        if (!settingsManager.configSettings.options.isAllowItemsToBeRepairedByAnvil) {
            itemStack.setRepairCost()
        }

        val baseEnchantments = getBaseEnchantments(itemStack, chosenTier)
        val bonusEnchantments = getBonusEnchantments(itemStack, chosenTier)

        itemStack.addUnsafeEnchantments(baseEnchantments + bonusEnchantments)

        if (useDurability) {
            val minimumDurability = (chosenMat.maxDurability - (chosenMat.maxDurability * max(
                chosenTier.minimumDurabilityPercentage,
                chosenTier.minimumDurabilityPercentage
            ))).toInt()
            val maximumDurability = (chosenMat.maxDurability - (chosenMat.maxDurability * min(
                chosenTier.minimumDurabilityPercentage,
                chosenTier.minimumDurabilityPercentage
            ))).toInt()
            val durability = (minimumDurability..maximumDurability).random()
            itemStack.getThenSetItemMetaAsDamageable(
                { this.damage = durability },
                { this.durability = durability.toShort() })
        }

        itemStack.setUnbreakable(chosenTier.isUnbreakable)

        val enchantmentName = getEnchantmentTypeName(itemStack)

        val name = generateName(itemStack, chosenTier, enchantmentName)
        itemStack.setDisplayNameChatColorized(name)
        val lore = generateLore(itemStack, chosenTier, enchantmentName)
        itemStack.setLoreChatColorized(lore)

        if (settingsManager.configSettings.options.isRandomizeLeatherColors) {
            LeatherArmorUtil.setRandomizedColor(itemStack)
        }
        SkullUtil.setSkullOwner(itemStack)

        val randomItemGenerationEvent = RandomItemGenerationEvent(chosenTier, itemStack, itemGenerationReason)
        Bukkit.getPluginManager().callEvent(randomItemGenerationEvent)
        if (randomItemGenerationEvent.isCancelled) {
            return null
        }
        return randomItemGenerationEvent.itemStack
    }

    private fun generateLore(itemStack: ItemStack, chosenTier: Tier, enchantmentName: String): List<String> {
        val tooltipFormat = settingsManager.configSettings.display.tooltipFormat

        val minecraftName = getMinecraftMaterialName(itemStack.type)
        val mythicName = getMythicMaterialName(itemStack.type)
        val generalLoreString = NameMap.getInstance().getRandom(NameType.GENERAL_LORE, "")
        val materialLoreString = NameMap.getInstance()
            .getRandom(NameType.MATERIAL_LORE, itemStack.type.name.toLowerCase())
        val tierLoreString = NameMap.getInstance().getRandom(NameType.TIER_LORE, chosenTier.name.toLowerCase())
        val enchantmentLoreString = NameMap.getInstance()
            .getRandom(
                NameType.ENCHANTMENT_LORE, enchantmentName.toLowerCase() ?: ""
            )

        val generalLore =
            generalLoreString.split("/n".toRegex()).dropLastWhile { it.isEmpty() }
        val materialLore =
            materialLoreString.split("/n".toRegex()).dropLastWhile { it.isEmpty() }
        val tierLore =
            tierLoreString.split("/n".toRegex()).dropLastWhile { it.isEmpty() }
        val enchantmentLore =
            enchantmentLoreString.split("/n".toRegex()).dropLastWhile { it.isEmpty() }

        val baseLore = chosenTier.baseLore.flatMap { lineOfLore ->
            lineOfLore.split("/n".toRegex()).dropLastWhile { it.isEmpty() }
        }

        val numOfBonusLore = (chosenTier.minimumBonusLore..chosenTier.maximumBonusLore).random()
        val bonusLore = mutableListOf<String>()
        repeat(numOfBonusLore) {
            val availableBonusLore = chosenTier.bonusLore.filter { !bonusLore.contains(it) }
            if (availableBonusLore.isNotEmpty()) {
                val selectedBonusLore = availableBonusLore.random()
                bonusLore.addAll(selectedBonusLore.split("/n".toRegex()).dropLastWhile { it.isEmpty() })
            }
        }

        val socketGemLore = ArrayList<String>()
        val socketableLore = ArrayList<String>()
        if (settingsManager.configSettings.components.isSocketingEnabled && Math.random() < chosenTier.chanceToHaveSockets) {
            val numberOfSockets = (chosenTier.minimumSockets..chosenTier.maximumSockets).random()
            if (numberOfSockets > 0) {
                for (i in 0 until numberOfSockets) {
                    val line = settingsManager.socketingSettings.items.socketedItem.socket
                    socketGemLore.add(line)
                }
                socketableLore.addAll(
                    settingsManager.socketingSettings.items.socketedItem.lore
                )
            }
        }

        val socketLore = ArrayList(socketGemLore)
        socketLore.addAll(socketableLore)

        val displayName = itemStack.getDisplayName()
        val relationLore = displayName?.let { name ->
            name.unChatColorize().split(" ".toRegex()).dropLastWhile { it.isEmpty() }
                .mapNotNull { relationManager.getById(it) }.flatMap { it.lore }
        } ?: emptyList()

        val args = listOf(
            "%basematerial%" to minecraftName,
            "%mythicmaterial%" to mythicName,
            "%tiername%" to chosenTier.displayName,
            "%enchantment%" to enchantmentName,
            "%tiercolor%" to "${chosenTier.displayColor}"
        )

        return tooltipFormat.replaceWithCollections(
            "%baselore%" to baseLore,
            "%bonuslore%" to bonusLore,
            "%socketgemlore%" to socketGemLore,
            "%socketablelore%" to socketableLore,
            "%socketlore%" to socketLore,
            "%generallore%" to generalLore,
            "%tierlore%" to tierLore,
            "%materiallore%" to materialLore,
            "%enchantmentlore%" to enchantmentLore,
            "%relationlore%" to relationLore
        ).replaceArgs(args).map { TemplatingUtil.template(it) }
    }

    private fun generateName(itemStack: ItemStack, chosenTier: Tier, enchantmentName: String): String {
        val format = settingsManager.configSettings.display.itemDisplayNameFormat
        if (format == null || format.isEmpty()) {
            return "Mythic Item"
        }
        val minecraftName = getMinecraftMaterialName(itemStack.type)
        val mythicName = getMythicMaterialName(itemStack.type)
        val generalPrefix = NameMap.getInstance().getRandom(NameType.GENERAL_PREFIX, "")
        val generalSuffix = NameMap.getInstance().getRandom(NameType.GENERAL_SUFFIX, "")
        val materialPrefix = NameMap.getInstance()
            .getRandom(NameType.MATERIAL_PREFIX, itemStack.type.name.toLowerCase())
        val materialSuffix = NameMap.getInstance()
            .getRandom(NameType.MATERIAL_SUFFIX, itemStack.type.name.toLowerCase())
        val tierPrefix = NameMap.getInstance().getRandom(NameType.TIER_PREFIX, chosenTier.name.toLowerCase())
        val tierSuffix = NameMap.getInstance().getRandom(NameType.TIER_SUFFIX, chosenTier.name.toLowerCase())
        val highestEnch = ItemStackUtil.getHighestEnchantment(itemStack)
        val enchantmentPrefix = NameMap.getInstance()
            .getRandom(
                NameType.ENCHANTMENT_PREFIX,
                highestEnch?.name?.toLowerCase() ?: ""
            )
        val enchantmentSuffix = NameMap.getInstance()
            .getRandom(
                NameType.ENCHANTMENT_SUFFIX,
                highestEnch?.name?.toLowerCase() ?: ""
            )

        val args = listOf(
            "%basematerial%" to minecraftName,
            "%mythicmaterial%" to mythicName,
            "%generalprefix%" to generalPrefix,
            "%generalsuffix%" to generalSuffix,
            "%materialprefix%" to materialPrefix,
            "%materialsuffix%" to materialSuffix,
            "%tierprefix%" to tierPrefix,
            "%tiersuffix%" to tierSuffix,
            "%tiername%" to chosenTier.displayName,
            "%enchantment%" to enchantmentName,
            "%enchantmentprefix%" to enchantmentPrefix,
            "%enchantmentsuffix%" to enchantmentSuffix
        )

        return "${chosenTier.displayColor}${format.replaceArgs(args).trim()}${chosenTier.identifierColor}"
    }

    private fun getMythicMaterialName(type: Material): String {
        val comb = type.name
        var mythicMatName: String? = settingsManager.languageSettings.displayNames[comb]
        if (mythicMatName == null || mythicMatName == comb) {
            mythicMatName = getMinecraftMaterialName(type)
        }
        return WordUtils.capitalize(mythicMatName)
    }

    private fun getMinecraftMaterialName(type: Material): String {
        val matName = type.name
        val split = matName.split("_".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val prettyMaterialName = Joiner.on(" ").skipNulls().join(split)
        return WordUtils.capitalizeFully(prettyMaterialName)
    }

    private fun getEnchantmentTypeName(itemStack: ItemStack): String {
        val enchantment = ItemStackUtil.getHighestEnchantment(itemStack)
            ?: return settingsManager.languageSettings.displayNames
                .getOrDefault("Ordinary", "Ordinary")
                .chatColorize()
        @Suppress("DEPRECATION")
        val ench = try {
            settingsManager.languageSettings.displayNames[enchantment.key.key]
                ?: settingsManager.languageSettings.displayNames[enchantment.name]
        } catch (throwable: Throwable) {
            settingsManager.languageSettings.displayNames[enchantment.name]
        }
        return ench ?: "Ordinary"
    }

    private fun getBonusEnchantments(itemStack: ItemStack, tier: Tier): Map<Enchantment, Int> {
        if (tier.bonusEnchantments.isEmpty()) {
            return emptyMap()
        }
        val bonusEnchantmentsToAdd = (tier.minimumBonusEnchantments..tier.maximumBonusEnchantments).random()
        val tierBonusEnchantments = getSafeEnchantments(tier.isSafeBonusEnchantments, tier.bonusEnchantments, itemStack)
        if (tierBonusEnchantments.isEmpty()) {
            return emptyMap()
        }
        val bonusEnchantments = mutableMapOf<Enchantment, Int>()
        repeat(bonusEnchantmentsToAdd) {
            val mythicEnchantment = tierBonusEnchantments.random()
            val enchantment = mythicEnchantment.enchantment
            val randomizedLevelOfEnchantment =
                bonusEnchantments[enchantment]?.let {
                    min(
                        mythicEnchantment.maximumLevel,
                        it + mythicEnchantment.getRandomLevel()
                    )
                }
                    ?: mythicEnchantment.getRandomLevel()
            val trimmedLevel = if (!tier.isAllowHighBonusEnchantments) {
                getAcceptableEnchantmentLevel(enchantment, randomizedLevelOfEnchantment)
            } else {
                randomizedLevelOfEnchantment
            }
            bonusEnchantments[enchantment] = trimmedLevel
        }
        return bonusEnchantments
    }

    private fun getAcceptableEnchantmentLevel(enchantment: Enchantment, level: Int): Int {
        return max(min(level, enchantment.maxLevel), enchantment.startLevel)
    }

    private fun getBaseEnchantments(itemStack: ItemStack, tier: Tier): Map<Enchantment, Int> {
        if (tier.baseEnchantments.isEmpty()) {
            return emptyMap()
        }
        val safeEnchantments = getSafeEnchantments(tier.isSafeBaseEnchantments, tier.baseEnchantments, itemStack)
        return safeEnchantments.map { mythicEnchantment ->
            val enchantment = mythicEnchantment.enchantment
            val minimumLevel = min(mythicEnchantment.minimumLevel, enchantment.startLevel)
            val maximumLevel = max(mythicEnchantment.maximumLevel, enchantment.maxLevel)
            when {
                !tier.isSafeBaseEnchantments -> enchantment to (minimumLevel..maximumLevel).random()
                tier.isAllowHighBaseEnchantments -> enchantment to (minimumLevel..mythicEnchantment.maximumLevel).random()
                else -> enchantment to getAcceptableEnchantmentLevel(
                    enchantment,
                    (minimumLevel..maximumLevel).random()
                )
            }
        }.toMap()
    }

    private fun getSafeEnchantments(
        isSafe: Boolean,
        enchantments: Collection<MythicEnchantment>,
        itemStack: ItemStack
    ): Collection<MythicEnchantment> {
        return enchantments.filter {
            if (isSafe) {
                it.enchantment.canEnchantItem(itemStack)
            } else {
                true
            }
        }
    }
}
