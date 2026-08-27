# AquaSensus — raccourcis autour de docker compose (fichier compose.yml à la racine).
# Docker Compose v2 requis. Lancer depuis la racine du dépôt.

ENV_FILE     ?= .env
COMPOSE      := docker compose --env-file $(ENV_FILE)

.DEFAULT_GOAL := help

.PHONY: help env config build up up-fg down stop restart ps logs \
	logs-core logs-data logs-web logs-db logs-proxy \
	core data web db \
	health clean

help:
	@echo "AquaSensus — commandes d'orchestration"
	@echo ""
	@echo "  make env        Copie .env.example vers .env s'il manque"
	@echo "  make build      Construit les images (core Java, data Python, web Angular)"
	@echo "  make up         Démarre la pile en arrière-plan (db, core, data, web, proxy)"
	@echo "  make up-fg      Idem, journaux au premier plan"
	@echo "  make down       Arrête les conteneurs (conserve le volume PostgreSQL)"
	@echo "  make stop       Stoppe sans supprimer les conteneurs"
	@echo "  make restart    Recrée la pile"
	@echo "  make ps         État des services"
	@echo "  make logs       Journaux suivis (tous services)"
	@echo "  make logs-core  Journaux du backend Java"
	@echo "  make logs-data  Journaux du service Python (non exposé publiquement)"
	@echo "  make logs-web   Journaux de la PWA Nginx"
	@echo "  make core       Java + PostgreSQL uniquement"
	@echo "  make data       Pipeline Python (+ dépendances Java/db)"
	@echo "  make web        PWA + proxy (+ cœur)"
	@echo "  make db         PostgreSQL seul"
	@echo "  make health     Vérifie HTTP : PWA, API, OpenAPI"
	@echo "  make config     Valide compose.yml"
	@echo "  make clean      down + suppression du volume Postgres (données perdues)"
	@echo ""
	@echo "Sans Make, à la racine : docker compose --env-file .env up --build -d"

env:
	@if [ ! -f $(ENV_FILE) ]; then cp .env.example $(ENV_FILE); echo "Créé $(ENV_FILE) — remplacer les secrets."; else echo "$(ENV_FILE) déjà présent."; fi

config: env
	$(COMPOSE) config

build: env
	$(COMPOSE) build core data web

up: env
	$(COMPOSE) up --build -d
	@echo "PWA : http://localhost/   API : http://localhost/api/v1/health"

up-fg: env
	$(COMPOSE) up --build

down:
	$(COMPOSE) down

stop:
	$(COMPOSE) stop

restart: env
	$(COMPOSE) up --build -d --force-recreate

ps:
	$(COMPOSE) ps

logs:
	$(COMPOSE) logs -f --tail=80

logs-core:
	$(COMPOSE) logs -f --tail=80 core

logs-data:
	$(COMPOSE) logs -f --tail=80 data

logs-web:
	$(COMPOSE) logs -f --tail=80 web

logs-db:
	$(COMPOSE) logs -f --tail=80 db

logs-proxy:
	$(COMPOSE) logs -f --tail=80 proxy

db: env
	$(COMPOSE) up -d db

core: env
	$(COMPOSE) up --build -d db core

data: env
	$(COMPOSE) up --build -d db core data

web: env
	$(COMPOSE) up --build -d db core web proxy

health:
	@echo "== PWA ==" && curl -sf -o /dev/null -w "http://localhost/ -> %{http_code}\n" http://localhost/ || echo "PWA indisponible"
	@echo "== API ==" && curl -sf http://localhost/api/v1/health && echo "" || echo "API indisponible"
	@echo "== Java direct ==" && curl -sf http://localhost:8080/api/v1/health && echo "" || echo "core:8080 indisponible"

clean:
	$(COMPOSE) down -v
	@echo "Volume PostgreSQL supprimé."
