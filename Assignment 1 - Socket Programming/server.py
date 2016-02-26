#!/usr/bin/python

import socket

# Define addr where socket server is listening
host = socket.gethostname()
port = 12345

# Create socket server
s = socket.socket()
s.bind((host, port))
s.listen(5)

# Wait for client
while True:
    c, addr = s.accept()
    print 'Got connection from', addr
    c.send('Thank you for connecting')
    c.close()
