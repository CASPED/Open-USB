var io = require('socket.io'),
    express = require('express'),
    http = require('http');

var app = express();
var server = http.createServer(app);
var io = require('socket.io').listen(server);


var PORT = 80;
server.listen(PORT);
console.log('Listening on port '+PORT);

app.use(express.bodyParser());
app.use(express.static(__dirname + '/frontend'));


//Sockets
io.sockets.on('connection', function (socket) {
    socket.emit('trace', { server: 'Connected to server' });
    // socket.on('my other event', function (data) {
    //     console.log(data);
    // });
});

// routing
app.get('/', function (req, res) {
    res.sendfile(__dirname + '/frontend/index.html');
});

app.post('/trace', function (req, res) {
    var msgObj = {};
    console.log(req.body);
    msgObj[req.body.action] = req.body.msg;
    io.sockets.emit('trace', msgObj);
    res.send(200);
});

app.post('/draw', function (req, res) {
    var msgObj = {};
    console.log(req.body);
    msgObj[req.body.action] = req.body.msg;
    io.sockets.emit('draw', msgObj);
    res.send(200);
});


