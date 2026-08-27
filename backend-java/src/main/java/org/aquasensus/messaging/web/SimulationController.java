package org.aquasensus.messaging.web;

import java.util.List;
import java.util.UUID;
import org.aquasensus.messaging.application.SessionUssdService;
import org.aquasensus.messaging.application.SimulationMessagerieService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/simulation")
@PreAuthorize("hasRole('ADMIN')")
public class SimulationController {

    private final SimulationMessagerieService sms;
    private final SessionUssdService ussd;

    public SimulationController(SimulationMessagerieService sms, SessionUssdService ussd) {
        this.sms = sms;
        this.ussd = ussd;
    }

    @PostMapping("/sms/inbound")
    public SmsReponse sms(@RequestBody SmsInbound corps) {
        String reponse = sms.recevoirSms(corps.numeroFictif(), corps.contenu());
        return new SmsReponse(reponse, reponse.length() <= 160);
    }

    @PostMapping("/ussd/session")
    public SessionUssdService.ReponseUssd ussd(@RequestBody UssdInbound corps) {
        return ussd.traiter(corps.sessionId(), corps.numeroFictif(), corps.saisie());
    }

    @GetMapping("/messages")
    public List<SimulationMessagerieService.MessageJournal> journal() {
        return sms.lister();
    }

    public record SmsInbound(String numeroFictif, String contenu) {}

    public record SmsReponse(String reponse, boolean gsm7) {}

    public record UssdInbound(UUID sessionId, String numeroFictif, String saisie) {}
}
