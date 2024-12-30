// server.js
const express = require('express');
const http = require('http');
const { Server } = require('socket.io');

const app = express();
const server = http.createServer(app);
const io = new Server(server);

io.on('connection', (socket) => {
    console.log('사용자가 연결되었습니다.');

    socket.on('message', (msg) => {
        io.emit('message', msg); // 모든 클라이언트에게 메시지 전송
    });

    socket.on('disconnect', () => {
        console.log('사용자가 연결을 끊었습니다.');
    });
});

server.listen(4000, () => {
    console.log('서버가 http://localhost:4000 에서 실행 중입니다.');
});
