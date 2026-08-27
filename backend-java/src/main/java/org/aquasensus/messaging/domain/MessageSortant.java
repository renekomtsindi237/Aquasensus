package org.aquasensus.messaging.domain;

public record MessageSortant(CanalMessage canal, String destinataire, String contenu, java.util.UUID sessionId) {}
