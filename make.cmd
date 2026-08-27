@echo off
setlocal EnableExtensions
cd /d "%~dp0"

if not defined ENV_FILE set "ENV_FILE=.env"
set "COMPOSE=docker compose --env-file %ENV_FILE%"

if "%~1"=="" goto help

if /I "%~1"=="help" goto help
if /I "%~1"=="env" goto env
if /I "%~1"=="config" goto config
if /I "%~1"=="build" goto build
if /I "%~1"=="up" goto up
if /I "%~1"=="up-fg" goto up_fg
if /I "%~1"=="down" goto down
if /I "%~1"=="stop" goto stop
if /I "%~1"=="restart" goto restart
if /I "%~1"=="ps" goto ps
if /I "%~1"=="logs" goto logs
if /I "%~1"=="logs-core" goto logs_core
if /I "%~1"=="logs-data" goto logs_data
if /I "%~1"=="logs-web" goto logs_web
if /I "%~1"=="logs-db" goto logs_db
if /I "%~1"=="logs-proxy" goto logs_proxy
if /I "%~1"=="db" goto db
if /I "%~1"=="core" goto core
if /I "%~1"=="data" goto data
if /I "%~1"=="web" goto web
if /I "%~1"=="health" goto health
if /I "%~1"=="clean" goto clean

echo Cible inconnue : %~1
echo.
goto help

:ensure_env
if not exist "%ENV_FILE%" (
  copy /Y .env.example "%ENV_FILE%" >nul
  echo Cree %ENV_FILE% — remplacer les secrets.
)
exit /b 0

:env
call :ensure_env
if exist "%ENV_FILE%" echo %ENV_FILE% deja present.
goto :eof

:config
call :ensure_env
%COMPOSE% config
goto :eof

:build
call :ensure_env
%COMPOSE% build core data web
goto :eof

:up
call :ensure_env
%COMPOSE% up --build -d
echo PWA : http://localhost/   API : http://localhost/api/v1/health
goto :eof

:up_fg
call :ensure_env
%COMPOSE% up --build
goto :eof

:down
%COMPOSE% down
goto :eof

:stop
%COMPOSE% stop
goto :eof

:restart
call :ensure_env
%COMPOSE% up --build -d --force-recreate
goto :eof

:ps
%COMPOSE% ps
goto :eof

:logs
%COMPOSE% logs -f --tail=80
goto :eof

:logs_core
%COMPOSE% logs -f --tail=80 core
goto :eof

:logs_data
%COMPOSE% logs -f --tail=80 data
goto :eof

:logs_web
%COMPOSE% logs -f --tail=80 web
goto :eof

:logs_db
%COMPOSE% logs -f --tail=80 db
goto :eof

:logs_proxy
%COMPOSE% logs -f --tail=80 proxy
goto :eof

:db
call :ensure_env
%COMPOSE% up -d db
goto :eof

:core
call :ensure_env
%COMPOSE% up --build -d db core
goto :eof

:data
call :ensure_env
%COMPOSE% up --build -d db core data
goto :eof

:web
call :ensure_env
%COMPOSE% up --build -d db core web proxy
goto :eof

:health
echo == PWA ==
curl.exe -sf -o NUL -w "http://localhost/ -^> %%{http_code}\n" http://localhost/ 2>nul || echo PWA indisponible
echo == API ==
curl.exe -sf http://localhost/api/v1/health && echo. || echo API indisponible
echo == Java direct ==
curl.exe -sf http://localhost:8080/api/v1/health && echo. || echo core:8080 indisponible
goto :eof

:clean
%COMPOSE% down -v
echo Volume PostgreSQL supprime.
goto :eof

:help
echo AquaSensus — commandes d'orchestration (Windows, sans GNU Make)
echo.
echo   make env        Copie .env.example vers .env s'il manque
echo   make build      Construit les images (core Java, data Python, web Angular)
echo   make up         Demarre la pile en arriere-plan
echo   make up-fg      Idem, journaux au premier plan
echo   make down       Arrete les conteneurs (conserve le volume PostgreSQL)
echo   make stop       Stoppe sans supprimer les conteneurs
echo   make restart    Recree la pile
echo   make ps         Etat des services
echo   make logs       Journaux suivis (tous services)
echo   make logs-core  Journaux du backend Java
echo   make logs-data  Journaux du service Python
echo   make logs-web   Journaux de la PWA Nginx
echo   make core       Java + PostgreSQL uniquement
echo   make data       Pipeline Python
echo   make web        PWA + proxy
echo   make db         PostgreSQL seul
echo   make health     Verifie HTTP
echo   make config     Valide compose.yml
echo   make clean      down + suppression du volume Postgres
echo.
echo Equivalent : docker compose --env-file .env up --build -d
goto :eof
