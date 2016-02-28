#!/usr/bin/python

import socket, threading, sys, select

# Use argument to define port
args = sys.argv
if not len(args) == 2:
    print 'Usage : python chat_server.py <port>'
    exit()
else:
    # Define addr where socket server is listening
    host = '0.0.0.0'
    port = int(args[1])

sockets = []
recv_buffer = 4096

# broadcast chat messages to all connected clients
def broadcast (sock_server, sock, message):
    for socket in sockets:
        # send the message only to peer
        if socket != sock_server and socket != sock :
            try :
                socket.send(message)
            except :
                # broken socket connection
                socket.close()
                # broken socket, remove it
                if socket in sockets:
                    sockets.remove(socket)

if __name__ == "__main__":
    try:
        # Create socket server
        sock_server = socket.socket()
        sock_server.bind((host, port))
        sock_server.listen(5)
        print 'Chat server is listening at %s:%s' % (host, port)

        sockets.append(sock_server)

        while True:
            read_sockets, write_sockets, error_sockets = select.select(sockets, [], [])
            for sock in read_sockets:
                # a new connection request recieved
                if sock == sock_server: 
                    new_sock, addr = sock_server.accept()
                    sockets.append(new_sock)
                    print "[+] Client %s:%s connected" % addr
                     
                    broadcast(sock_server, new_sock, "[%s:%s]\t: Entered our chatting room\n" % addr)
                    
                # a message from a client, not a new connection
                else:
                    cip, cport = sock.getpeername()
                    # process data recieved from client, 
                    try:
                        # receiving data from the socket.
                        data = sock.recv(recv_buffer)
                        if data:
                            # there is something in the socket
                            broadcast(sock_server, sock, "[%s:%s]\t: %s" % (cip, cport, data)) 
                        else:
                            # remove the socket that's broken    
                            if sock in sockets:
                                sockets.remove(sock)
                            # at this stage, no data means probably the connection has been broken
                            broadcast(sock_server, sock, "[%s:%s]\t: Now offline\n" % (cip, cport)) 

                    # exception 
                    except:
                        broadcast(sock_server, sock, "[%s:%s]\t: Now offline\n" % (cip, cport))
                        continue
    except KeyboardInterrupt:
        print "Chat server is stopping..."
        sock_server.close()
        print "Chat server is stopped"
                    
