package haimfeng.landrop.log;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import haimfeng.landrop.config.AppConstants;
import haimfeng.landrop.event.*;

import java.util.Arrays;
import java.util.logging.Logger;

public class EventLogger {
    private final EventBus eventBus; // 事件总线
    private static final Logger logger = Logger.getLogger("Logger"); // 日志记录器

    public EventLogger(EventBus eventBus) {
        this.eventBus = eventBus;
        eventBus.register(this);

        logger.info("AppConstants: {\n" + AppConstants.getAppConstants() + "}");
    }

    /**
     * 监听事件
     * @param event
     */
    @Subscribe
    public void logEvent(Event event) {
        StringBuilder sb = new StringBuilder();
        sb.append(event.EventName);
        sb.append(": ");

        switch (event) {
            // 监听App停止事件
            case AppStopEvent stopEvent -> {
                logger.info("App stopped.");
                if (eventBus != null) {
                    eventBus.unregister(this);
                }
                return;
            }
            // 监听广播接收事件
            case BroadcastReceivedEvent broadcastReceivedEvent -> {
                sb.append(((AppEvent) event).EventData);
                sb.append(": ");
                sb.append(broadcastReceivedEvent.broadcastPacket.toString());
                logger.info(sb.toString());
            }
            // 监听广播发送事件
            case StartBroadcastEvent startBroadcastEvent -> {
                sb.append(((AppEvent) event).EventData);
                logger.info(sb.toString());
            }
            // 监听App启动事件
            case AppEvent appEvent -> {
                sb.append(appEvent.EventData);
                logger.info(sb.toString());
            }
            // 监听异常事件
            case ExceptionEvent exceptionEvent -> {
                sb.append(exceptionEvent.exceptionSubmitter);
                sb.append(": ");
                sb.append(exceptionEvent.exceptionMessage);

                Throwable exception = exceptionEvent.exception;

                if (exception instanceof RuntimeException) {
                    sb.append(": ");
                    sb.append(Arrays.toString(exception.getStackTrace()));
                    logger.severe(sb.toString());
                } else {
                    logger.warning(sb.toString());
                }
            }
            default -> {
            }
        }
    }

}
