package org.aquasensus.maintenance.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class JournalEvenementsMaintenance {

    private static final Logger LOG = LoggerFactory.getLogger(JournalEvenementsMaintenance.class);

    @EventListener
    public void technicienAffecte(InterventionService.TechnicienAffecte event) {
        LOG.info("Événement TECHNICIEN_AFFECTE intervention={} technicien={}", event.interventionId(), event.technicienId());
    }

    @EventListener
    public void retablissement(InterventionService.RetablissementConfirme event) {
        LOG.info("Événement RETABLISSEMENT_CONFIRME intervention={} ouvrage={}", event.interventionId(), event.pointEauId());
    }
}
