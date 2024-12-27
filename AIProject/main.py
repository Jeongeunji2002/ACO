import torch
from transformers import pipeline

sentiment_pipeline = pipeline("sentiment-analysis")

@app.websocket("/ws")
async def websocket_endpoint(websocket: WebSocket):
    await websocket.accept()
    while True:
        data = await websocket.receive_text()
        sentiment = sentiment_pipeline(data)
        await websocket.send_text(f"메시지: {data}, 감정: {sentiment}")
