#!/usr/bin/python

import socket, sys

# Use argument to define port
args = sys.argv
if not len(args) == 3:
    print 'Usage : python client.py <host> <port>'
    exit()
else:
    # Define addr where socket server is listening
    host = args[1]
    port = int(args[2])

# Create socket client
sock = socket.socket()
sock.connect((host, port))

# Say hello to server
sock.send("Hai server...")

# Print incoming messages
msg = sock.recv(1024)
print "[+] %s:%s - Received message : %s" % (host, port, msg)

sock.close
