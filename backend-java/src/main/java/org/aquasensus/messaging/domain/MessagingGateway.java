package org.aquasensus.messaging.domain;

public interface MessagingGateway {

    void envoyer(MessageSortant message);

    boolean supporte(CanalMessage canal);
}
