#!/usr/bin/python

import socket

# Define addr where socket server is listening
host = socket.gethostname()
port = 12345

# Create socket client
s = socket.socket()
s.connect((host, port))

# Print incoming messages
print s.recv(1024)
s.close
