package haimfeng.landrop.log;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import haimfeng.landrop.config.AppConstants;
import haimfeng.landrop.event.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EventLogger {
    private final EventBus eventBus;
    private static final Logger logger = LogManager.getLogger(EventLogger.class);

    public EventLogger(EventBus eventBus) {
        this.eventBus = eventBus;
        eventBus.register(this);

        logger.info("\n{}", AppConstants.getAppConstants());
    }

    @Subscribe
    public void logEvent(Event event) {
        switch (event) {
            case AppStopEvent stopEvent -> {
                logger.info("App stopped.");
                if (eventBus != null) {
                    eventBus.unregister(this);
                }
            }
            case BroadcastReceivedEvent broadcastReceivedEvent -> {
                logger.info("{}: {}: {}",
                        event.EventName,
                        ((AppEvent) event).EventData,
                        broadcastReceivedEvent.broadcastPacket.toString()
                );
            }
            case StartBroadcastEvent startBroadcastEvent -> {
                logger.info("{}: {}",
                        event.EventName,
                        ((AppEvent) event).EventData
                );
            }
            case UpdateReceivedListEvent updateReceivedListEvent -> {
                logger.info("{}: {}: {}",
                        event.EventName,
                        "size: " + updateReceivedListEvent.receivedPackets.size(),
                        updateReceivedListEvent.receivedPackets.toString()
                );
            }
            case SendConnectionRequestEvent sendConnectionRequestEvent -> {
                logger.info("{}: {}: {}",
                        event.EventName,
                        ((AppEvent) event).EventData,
                        sendConnectionRequestEvent.broadcastPacket.toString()
                );
            }
            case AppStartEvent appStartEvent -> {
                logger.info("{}: {}",
                        event.EventName,
                        ((AppEvent) event).EventData
                );
            }
            case ConnectionEstablishedEvent connectionEstablishedEvent -> {
                logger.info("{}: {}: {}",
                        event.EventName,
                        ((AppEvent) event).EventData,
                        connectionEstablishedEvent.broadcastPacket.toString()
                );
            }
            case ReceivedConnectionRequestEvent receivedConnectionRequestEvent -> {
                logger.info("{}: {}: {}",
                        event.EventName,
                        ((AppEvent) event).EventData,
                        receivedConnectionRequestEvent.receivedPacket.toString()
                );
            }
            case RequestOutOfTimeEvent requestOutOfTimeEvent -> {
                logger.info("{}: {}",
                        event.EventName,
                        ((AppEvent) event).EventData
                );
            }
            case AppEvent appEvent -> {
                logger.info("{}: {}",
                        event.EventName,
                        appEvent.EventData
                );
            }
            case ExceptionEvent exceptionEvent -> {
                if (exceptionEvent.exception instanceof RuntimeException) {
                    logger.error("{}: {}: {}",
                            exceptionEvent.exceptionSubmitter,
                            exceptionEvent.exceptionMessage,
                            exceptionEvent.exception
                    );
                } else {
                    logger.warn("{}: {}",
                            exceptionEvent.exceptionSubmitter,
                            exceptionEvent.exceptionMessage
                    );
                }
            }
            default -> {}
        }
    }
}