package me.ash.odin.features.impl.iron

import com.odtheking.odin.clickgui.settings.Setting.Companion.withDependency
import com.odtheking.odin.clickgui.settings.impl.*
import com.odtheking.odin.events.TickEvent
import com.odtheking.odin.events.core.on
import com.odtheking.odin.features.Module
import com.odtheking.odin.utils.equalsOneOf
import com.odtheking.odin.utils.skyblock.dungeon.DungeonUtils
import com.odtheking.odin.utils.skyblock.dungeon.M7Phases
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.Blocks
import me.ash.odin.utils.Skit

object FuckDiorite : Module(
    name = "Fuck Diorite",
    description = "Replaces the pillars in the storm fight with glass.",
    category = Skit.IRON
) {
    private val GLASS_STATE = Blocks.GLASS.defaultBlockState()

    private val STAINED_GLASS_BLOCKS = arrayOf(
        Blocks.WHITE_STAINED_GLASS,
        Blocks.ORANGE_STAINED_GLASS,
        Blocks.MAGENTA_STAINED_GLASS,
        Blocks.LIGHT_BLUE_STAINED_GLASS,
        Blocks.YELLOW_STAINED_GLASS,
        Blocks.LIME_STAINED_GLASS,
        Blocks.PINK_STAINED_GLASS,
        Blocks.GRAY_STAINED_GLASS,
        Blocks.LIGHT_GRAY_STAINED_GLASS,
        Blocks.CYAN_STAINED_GLASS,
        Blocks.PURPLE_STAINED_GLASS,
        Blocks.BLUE_STAINED_GLASS,
        Blocks.BROWN_STAINED_GLASS,
        Blocks.GREEN_STAINED_GLASS,
        Blocks.RED_STAINED_GLASS,
        Blocks.BLACK_STAINED_GLASS
    )

    private val pillarBasedColor by BooleanSetting("Pillar Based", true, desc = "Swaps the diorite in the pillar to a corresponding color.").withDependency { !schitzo }
    private val colorIndex by SelectorSetting("Color", "None", arrayListOf(
        "NONE", "WHITE", "ORANGE", "MAGENTA", "LIGHT_BLUE", "YELLOW", "LIME", "PINK",
        "GRAY", "LIGHT_GRAY", "CYAN", "PURPLE", "BLUE", "BROWN", "GREEN", "RED", "BLACK"
    ), desc = "Color for the stained glass.").withDependency { !pillarBasedColor && !schitzo }

    private val schitzo by BooleanSetting("Schitzo mode", false, desc = "Schtizoing.")

    private val pillars = arrayOf(BlockPos(46, 169, 41), BlockPos(46, 169, 65), BlockPos(100, 169, 65), BlockPos(100, 169, 41))
    private val pillarColors = intArrayOf(5, 4, 10, 14)

    private val coordinates: Array<Set<BlockPos>> = Array(4) { pillarIndex ->
        val pillar = pillars[pillarIndex]
        buildSet {
            for (dx in (pillar.x - 3)..(pillar.x + 3))
                for (dy in pillar.y..(pillar.y + 37))
                    for (dz in (pillar.z - 3)..(pillar.z + 3))
                        add(BlockPos(dx, dy, dz))
        }
    }

    init {
        on<TickEvent.End> {
            if (DungeonUtils.getF7Phase() == M7Phases.P2) replaceDiorite()
        }
    }

    private fun replaceDiorite() {
        val level = mc.level ?: return

        for ((index, coordinateSet) in coordinates.withIndex()) {
            for (pos in coordinateSet) {
                val state = level.getBlockState(pos) ?: return

                if (state.block.equalsOneOf(Blocks.DIORITE, Blocks.POLISHED_DIORITE)) {
                    setGlass(pos, index)
                }
            }
        }
    }

    private fun setGlass(pos: BlockPos, pillarIndex: Int) {
        val newState = when {
            schitzo -> STAINED_GLASS_BLOCKS.random().defaultBlockState()
            pillarBasedColor -> STAINED_GLASS_BLOCKS[pillarColors[pillarIndex]].defaultBlockState()
            colorIndex != 0 -> STAINED_GLASS_BLOCKS[colorIndex - 1].defaultBlockState()
            else -> GLASS_STATE
        }

        mc.level?.setBlock(pos, newState, 3)
    }
}