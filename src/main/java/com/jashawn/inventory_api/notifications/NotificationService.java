package com.jashawn.inventory_api.notifications;

import com.jashawn.inventory_api.stockItem.dto.StockAvailability;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@Slf4j
public class NotificationService {

//    CopyOnWriteArrayList = thread safe by creating copy of each time it is updated
//    which bypasses ConcurrentModificationException
    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

//    Create HTTP pipeline
    public SseEmitter subscribe() {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        this.emitters.add(emitter);

        log.info("New client subscribed to Server Sent Event stream. Total clients: {}", emitters.size());

//        Clean up emitters when they close
        emitter.onCompletion(() -> {
            log.info("Server Sent Event connection completed normally");
            this.emitters.remove(emitter);
        });

        emitter.onTimeout(() -> {
            log.warn("Server Sent Event connection timed out");
            this.emitters.remove(emitter);
        });

        emitter.onError((ex) -> {
            log.error("Server Sent Event connection encountered an error: {}", ex.getMessage());
            this.emitters.remove(emitter);
        });

        return emitter;
    }


//    Return alerts asynchronously
    public void sendLowStockAlert(StockAvailability stockAvailability) {
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event()
                        .name("LOW_STOCK_ALERT")
                        .data(stockAvailability) // Automatically converts object to JSON
                );
            } catch (IOException e) {
                log.debug("Failed to send Server Side Event to emitter, removing dead connection: {}", e.getMessage());
                emitters.remove(emitter);
            }
        }
    }

}
