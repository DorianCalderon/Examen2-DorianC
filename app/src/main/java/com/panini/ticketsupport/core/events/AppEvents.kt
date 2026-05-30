package com.panini.ticketsupport.core.events

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object TicketEventBus {

    private val _events = MutableSharedFlow<TicketEvent>(
        replay = 0,
        extraBufferCapacity = 0,
    )

    val events: SharedFlow<TicketEvent> = _events.asSharedFlow()

    suspend fun emit(event: TicketEvent) {
        _events.emit(event)
    }
}
