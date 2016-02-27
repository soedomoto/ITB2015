var net = require('net');
var prompt = require('prompt');
var client = new net.Socket();
prompt.start();

function ask() {
	prompt.get(['data'], function (err, result) {	
		client.write(result.data);
		setTimeout(function() {
			ask();
		}, 500);
	});
}

prompt.get(['ipaddr', 'port'], function (err, result) {
	client.connect(result.port, result.ipaddr, function() {
		console.log('Connected');
		setTimeout(function() {
			ask();
		}, 500);
	});

	client.on('data', function(data) {
		console.log('\r\n' + data);
	});

	client.on('close', function() {
		console.log('Connection closed');
	});
});
