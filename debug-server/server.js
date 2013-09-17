var io = require('socket.io'),
    express = require('express'),
    http = require('http');

var app = express();
var server = http.createServer(app);
var io = require('socket.io').listen(server);


var PORT = 80;
server.listen(PORT);
console.log('Listening on port '+PORT);

app.use(express.static(__dirname + '/frontend'));

// routing
app.get('/', function (req, res) {
    res.sendfile(__dirname + '/frontend/index.html');
});


io.sockets.on('connection', function (socket) {
    socket.emit('news', { hello: 'world' });
    socket.on('my other event', function (data) {
        console.log(data);
    });
});