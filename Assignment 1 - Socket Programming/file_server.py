#!/usr/bin/python

import socket, threading, sys, select, os

# Use argument to define port
args = sys.argv
if not len(args) == 2:
    print 'Usage : python file_server.py <port>'
    exit()
else:
    # Define addr where socket server is listening
    host = '0.0.0.0'
    port = int(args[1])

sockets = []
last_data = {}
recv_buffer = 4096

def column(matrix, i):
    return [row[i] for row in matrix]

def list_file(sock, inpdir):
    cip, cport = sock.getpeername()
    data = []
    
    data.append(['dir ', os.pardir])
    for path, subdirs, files in os.walk(inpdir, topdown=True):
        for name in subdirs:
            data.append(['dir ', name])
        for name in files:
            data.append(['file', name])
        break
    
    last_data[cip + ':' + str(cport)] = [inpdir, column(data, 1)]
    
    counter = 1
    str_list = 'Please select folder/file below :\n'
    for f in data:
        str_list = str_list + "[{}] {}".format(counter, '{}  {}\n'.format(f[0], f[1]))
        counter = counter + 1
        
    sock.send(str_list)

if __name__ == "__main__":
    try:
        # Create socket server
        sock_server = socket.socket()
        sock_server.bind((host, port))
        sock_server.listen(5)
        print 'File server is listening at %s:%s' % (host, port)
        
        sockets.append(sock_server)
        
        while True:
            read_sockets, write_sockets, error_sockets = select.select(sockets, [], [])
            for sock in read_sockets:
                # a new connection request recieved
                if sock == sock_server: 
                    new_sock, addr = sock_server.accept()
                    sockets.append(new_sock)
                    print "[+] Client %s:%s connected" % addr
                     
                    list_file(new_sock, os.getcwd())
                    
                # a message from a client, not a new connection
                else:
                    cip, cport = sock.getpeername()
                    # process data recieved from client, 
                    try:
                        # receiving data from the socket.
                        data = sock.recv(recv_buffer)
                        if data:
                            # convert to integer
                            try:
                                data = int(data)
                            except:
                                sock.send('Your selection must be an integer\n')
                                continue
                            
                            # there is something in the socket
                            if cip + ':' + str(cport) in last_data:
                                l_data = last_data[cip + ':' + str(cport)]
                                try:
                                    f = os.path.join(l_data[0], l_data[1][data-1])
                                    if os.path.isdir(f):
                                        f = os.path.abspath(f)
                                        list_file(sock, f)
                                    elif os.path.isfile(f):
                                        with open(f, 'r') as handler:
                                            content = handler.read()
                                            content =   '===============================================================================\n' + \
                                                        'Content of file : ' + l_data[1][data-1] + '\n' + \
                                                        '-------------------------------------------------------------------------------\n' + \
                                                        content + '\n' + \
                                                        '-------------------------------------------------------------------------------\n'
                                            sock.send(content)
                                            
                                            list_file(sock, l_data[0])
                                except:
                                    list_file(sock, l_data[0])
                        else:
                            # remove the socket that's broken    
                            if sock in sockets:
                                sockets.remove(sock)

                    # exception 
                    except:
                        continue
    except KeyboardInterrupt:
        print "File server is stopping..."
        sock_server.close()
        print "File server is stopped"
