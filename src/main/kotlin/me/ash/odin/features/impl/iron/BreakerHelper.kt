package me.ash.odin.features.impl.iron

import com.odtheking.odin.clickgui.settings.impl.BooleanSetting
import com.odtheking.odin.events.core.onReceive
import com.odtheking.odin.features.Module
import com.odtheking.odin.utils.Colors
import com.odtheking.odin.utils.equalsOneOf
import com.odtheking.odin.utils.itemId
import com.odtheking.odin.utils.loreString
import com.odtheking.odin.utils.render.textDim
import com.odtheking.odin.utils.skyblock.dungeon.DungeonUtils
import com.odtheking.odin.utils.skyblock.dungeon.tiles.RoomType
import net.minecraft.core.BlockPos
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.level.block.Blocks
import me.ash.odin.utils.Skit

object BreakerHelper : Module(
    name = "Breaker Helper",
    description = "Utilities for Dungeon Breaker.",
    category = Skit.IRON
) {
    private val chargesRegex = Regex("Charges: (\\d+)/(\\d+)⸕")
    private var charges = 0
    private var max = 0
    private val blacklistedBlocks = listOf(
        Blocks.BARRIER,
        Blocks.BEDROCK,
        Blocks.COMMAND_BLOCK,
        Blocks.CHAIN_COMMAND_BLOCK,
        Blocks.REPEATING_COMMAND_BLOCK,
        Blocks.SKELETON_SKULL,
        Blocks.SKELETON_WALL_SKULL,
        Blocks.WITHER_SKELETON_SKULL,
        Blocks.WITHER_SKELETON_WALL_SKULL,
        Blocks.TNT,
        Blocks.CHEST,
        Blocks.TRAPPED_CHEST,
        Blocks.END_PORTAL_FRAME,
        Blocks.END_PORTAL,
        Blocks.PISTON,
        Blocks.PISTON_HEAD,
        Blocks.STICKY_PISTON,
        Blocks.MOVING_PISTON,
        Blocks.LEVER,
        Blocks.STONE_BUTTON
    )

    private val onlyWhenFatigue by BooleanSetting("Insta-mine when fatigue", true, desc = "Insta-mine blocks (ZPDB) when mining fatigue is applied.")
    private val hud by HUD("Display charges", "Shows the amount of charges left in your Dungeon Breaker.", true) {
        if (!it && (max == 0 || !DungeonUtils.inDungeons)) 0 to 0
        else textDim("§e${if (it) 17 else charges}§7/§e${if (it) 20 else max}§c⸕", 0, 0, Colors.WHITE)
    }

    init {
        onReceive<ClientboundContainerSetSlotPacket> {
            if (!DungeonUtils.inDungeons || item?.itemId != "DUNGEONBREAKER") return@onReceive
            item?.loreString?.firstNotNullOfOrNull { chargesRegex.find(it) }?.let { match ->
                charges = match.groupValues[1].toIntOrNull() ?: 0
                max = match.groupValues[2].toIntOrNull() ?: 0
            }
        }
    }

    @JvmStatic
    fun onHitBlock(pos: BlockPos) {
        val player = mc.player ?: return
        val level = mc.level ?: return

        if (!enabled || !onlyWhenFatigue || !DungeonUtils.inDungeons || charges == 0) return
        if (player.mainHandItem.itemId != "DUNGEONBREAKER") return

        if ((DungeonUtils.inBoss && !DungeonUtils.isFloor(7)) || DungeonUtils.currentRoom.equalsOneOf(RoomType.PUZZLE, RoomType.FAIRY)) return

        if (!player.hasEffect(MobEffects.MINING_FATIGUE)) return
        if (level.getBlockState(pos).block in blacklistedBlocks) return

        level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3)
    }
}