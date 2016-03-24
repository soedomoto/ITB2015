import json
from dejavu import Dejavu
from dejavu.recognize import FileRecognizer, MicrophoneRecognizer

import dejavu_sqlite

config = {
    "database_type": "sqlite", 
    "database": {
        "db": "/home/soedomoto/Documents/dejavu.db"
    }
}

if __name__ == '__main__':
    # create a Dejavu instance
    djv = Dejavu(config)

    # Fingerprint all the mp3's in the directory we give it
    djv.fingerprint_file('mp3/Sean-Fournier--Falling-For-You.mp3')

    # Fingerprint all the mp3's in the directory we give it
    djv.fingerprint_directory("mp3", [".mp3"])

    # Recognize audio from a file
    song = djv.recognize(FileRecognizer, "test/test1.mp3")
    print "From file we recognized: %s\n" % song
    
    song = djv.recognize(FileRecognizer, "test/test2.mp3")
    print "From file we recognized: %s\n" % song
