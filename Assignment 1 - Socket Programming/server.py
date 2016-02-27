#!/usr/bin/python

import socket, threading, sys

# Use argument to define port
args = sys.argv
if not len(args) == 2:
    print 'Usage : python server.py <port>'
    exit()
else:
    # Define addr where socket server is listening
    host = '0.0.0.0'
    port = int(args[1])

# Create thread class to handle each connected client
class ClientThread(threading.Thread):
    def __init__(self, ip, port, socket):
        threading.Thread.__init__(self)
        self.ip = ip
        self.port = port
        self.socket = socket
        print "[+] %s:%s - New thread is started" % (ip, str(port))

    def run(self):
        # Define send and recv handler for each thread
        self.socket.send('Welcome, Thank you for connecting...')
        msg = self.socket.recv(1024)
        print "[+] %s:%s - Received message : %s" % (ip, str(port), msg)

# Create socket server
sock_server = socket.socket()
sock_server.bind((host, port))
sock_server.listen(5)
print 'Socket server is listening at %s:%s' % (host, port)

# Wait for client
while True:
    (sock, (ip, port)) = sock_server.accept()
    thread = ClientThread(ip, port, sock)
    thread.start()
