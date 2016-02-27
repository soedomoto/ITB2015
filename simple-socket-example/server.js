var net = require('net');
var prompt = require('prompt');
prompt.start();

var server = net.createServer(function(socket) {
	socket.name = socket.remoteAddress + ":" + socket.remotePort;
	console.log(socket.name + "> connected");
	socket.write("Welcome " + socket.name);
	
	socket.on('data', function (data) {
		console.log(socket.name + "> " + data);
	});
	
	socket.on('end', function () {
		console.log(socket.name + "> disconnected");
	});
});

prompt.get(['port'], function (err, result) {
	server.listen(result.port, '127.0.0.1', function () {
		console.log('Socket server listening on *:' + result.port);	
	});
});
