#!/usr/bin/python

import socket, threading, sys, select

recv_buffer = 4096

def prompt():
    sys.stdout.write('[Me]\t\t\t: '); 
    sys.stdout.flush()

if __name__ == "__main__":
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
    try:
        sock.connect((host, port))
    except socket.error:
        print 'Unable to connect'
        exit()
    
    print 'Connected to remote host. You can start sending messages'
    prompt()
    
    while 1:
        socket_list = [sys.stdin, sock]
        # Get the list sockets which are readable
        read_sockets, write_sockets, error_sockets = select.select(socket_list , [], [])
         
        for s in read_sockets:             
            if s == sock:
                # incoming message from remote server, s
                data = s.recv(recv_buffer)
                if not data :
                    print '\nDisconnected from chat server'
                    sys.exit()
                else :
                    #print data
                    sys.stdout.write('\r' + data)
                    prompt()
            
            else :
                # user entered a message
                msg = sys.stdin.readline()
                sock.send(msg)
                prompt()
                
