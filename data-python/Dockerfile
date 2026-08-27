FROM python:3.12-alpine
WORKDIR /app
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt
COPY aquasensus_data aquasensus_data
COPY app app
ENV PYTHONPATH=/app
EXPOSE 8090
HEALTHCHECK --interval=20s --timeout=5s --start-period=20s --retries=5 \
    CMD wget -qO- http://127.0.0.1:8090/internal/health || exit 1
USER nobody
CMD ["uvicorn", "app.main:app", "--host", "0.0.0.0", "--port", "8090"]
