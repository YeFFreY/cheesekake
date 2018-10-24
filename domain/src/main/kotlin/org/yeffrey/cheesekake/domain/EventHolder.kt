package org.yeffrey.cheesekake.domain

interface Event
interface EventHolder {
    fun publish(event: Event)
    fun publishedEvents(): List<Event>
}

class EventHolderImpl : EventHolder {
    private val events: MutableList<Event> = mutableListOf()

    override fun publish(event: Event) {
        this.events.add(event)
    }

    override fun publishedEvents(): List<Event> = this.events.toList()
}

open class Aggregate {
    internal val eventHolder = EventHolderImpl()
    fun publishedEvents(): List<Event> = eventHolder.publishedEvents()
}