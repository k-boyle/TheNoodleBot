package casino.noodle.handlers;

import discord4j.core.event.domain.Event;

public interface Handler<T extends Event> {
    void handleEvent(T event);
}
