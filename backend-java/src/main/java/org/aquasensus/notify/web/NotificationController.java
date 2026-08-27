package org.aquasensus.notify.web;

import java.util.List;
import org.aquasensus.identity.web.UtilisateurCourant;
import org.aquasensus.notify.application.NotificationService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationService notifications;

    public NotificationController(NotificationService notifications) {
        this.notifications = notifications;
    }

    @GetMapping
    public List<NotificationService.NotificationVue> miennes(
            @AuthenticationPrincipal UtilisateurCourant courant) {
        return notifications.pourUtilisateur(courant.id());
    }
}
