package casino.noodle.handlers;

import com.google.common.eventbus.Subscribe;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class ReadyHandler implements Handler<ReadyEvent> {
    private final Logger logger = LogManager.getLogger(ReadyHandler.class);

    @Subscribe
    public void handleEvent(ReadyEvent event) {
        logger.info("Noodle bot ready!");
    }
}
