package com.jashawn.inventory_api.notifications;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/v1/notifications")
@Tag(name = "Notifications", description = "Server-sent event notification stream.")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "Subscribe to low-stock notifications",
            description = "Opens a server-sent events stream. Inventory operations publish low-stock alerts to active subscribers when a low-stock condition is detected.")
    @ApiResponse(responseCode = "200", description = "SSE stream opened.", content = @Content(mediaType = MediaType.TEXT_EVENT_STREAM_VALUE))
    public SseEmitter streamNotifications() {
        return notificationService.subscribe();
    }
}
