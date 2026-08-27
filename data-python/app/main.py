import asyncio
import logging
import os

from fastapi import FastAPI

from aquasensus_data.ordonnancement.pipeline import executer

app = FastAPI(title="AquaSensus data", docs_url=None, redoc_url=None)
LOG = logging.getLogger("aquasensus.data")


@app.get("/internal/health")
def sante() -> dict[str, str]:
    return {"statut": "ok", "service": "data"}


@app.post("/internal/run")
def lancer() -> dict:
    return executer()


@app.on_event("startup")
async def planifier() -> None:
    async def boucle() -> None:
        await asyncio.sleep(15)
        while True:
            try:
                executer()
            except Exception as ex:  # noqa: BLE001 — le cœur Java reste disponible (ENF-13)
                LOG.warning("Pipeline reporté : %s", ex)
            await asyncio.sleep(6 * 3600)

    asyncio.create_task(boucle())


if __name__ == "__main__":
    executer()
