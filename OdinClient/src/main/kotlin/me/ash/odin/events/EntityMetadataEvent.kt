package me.ash.odin.events

import com.odtheking.odin.events.core.CancellableEvent
import net.minecraft.network.protocol.Packet
import net.minecraft.world.entity.Entity

class EntityMetadataEvent(val entity: Entity, packet: Packet<*>) : CancellableEvent()