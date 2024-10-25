const http = require('http');

const PORT = 15808;
const HOST = 'localhost';

const server = http.createServer((req, res) => {
    console.log(req.rawHeaders[0])
    res.statusCode = 200;
    res.setHeader('Content-Type', 'text/plain');
    res.end('Hello, this is a response from port 15808!');
});

server.listen(PORT, HOST, () => {
    console.log(`Server running at http://${HOST}:${PORT}/api/v1/resource`);
});
