var net = require('net');
var prompt = require('prompt');
var glob = require("glob");
var fs = require("fs");

prompt.start();
var server = net.createServer(function(socket) {
	socket.name = socket.remoteAddress + ":" + socket.remotePort;
	console.log(socket.name + "> connected");
	
	socket.write("Welcome " + socket.name);
	listFiles(socket);
	
	socket.on('data', function (data) {
		console.log(socket.name + "> " + data);
		
		if(isNaN(data)) {
			if(String(data).toUpperCase() === "Y") {
				listFiles(socket);
			} else if(String(data).toUpperCase() === "N") {
				socket.destroy();
			}
		} else {
			fs.readFile(socket.files[parseInt(data)-1], function (err, result) {
				socket.write(
					"Below contents of file " + socket.files[parseInt(data)-1] + " :\r\n" + 
					result
				);
				socket.write("Over again? (Y/N)");
		});		
		}
	});
	
	socket.on('end', function () {
		console.log(socket.name + "> disconnected");
	});
});

function listFiles(socket) {
	glob("**/files/*", {}, function (err, files) {
		socket.files = files;
		
		var strLstFiles = "To read file contents, select a number below : \r\n";
		for(var index in files) {
			strLstFiles += ((parseInt(index)+1) + ". " + files[index] + "\r\n");
		}
		socket.write(strLstFiles);
	});
}

prompt.get(['port'], function (err, result) {
	server.listen(result.port, '127.0.0.1', function () {
		console.log('Socket server listening on *:' + result.port);	
	});
});
