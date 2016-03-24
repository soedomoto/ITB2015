from __future__ import absolute_import
from itertools import izip_longest
import Queue
import binascii

import sqlite3 as sqlite
from dejavu.database import Database


class SqliteDatabase(Database):
    type = "sqlite"

    # tables
    FINGERPRINTS_TABLENAME = "fingerprints"
    SONGS_TABLENAME = "songs"

    # fields
    FIELD_FINGERPRINTED = "fingerprinted"

    # creates
    CREATE_FINGERPRINTS_TABLE = """
        CREATE TABLE IF NOT EXISTS `{}` (
             `{}` binary(10) not null,
             `{}` unsigned mediumint not null,
             `{}` unsigned int not null, 
             UNIQUE ({}, {}, {})
    );""".format(
        FINGERPRINTS_TABLENAME, 
        Database.FIELD_HASH, Database.FIELD_SONG_ID, Database.FIELD_OFFSET, 
        Database.FIELD_HASH, Database.FIELD_SONG_ID, Database.FIELD_OFFSET
    )

    CREATE_SONGS_TABLE = """
        CREATE TABLE IF NOT EXISTS `{}` (
            `{}` INTEGER PRIMARY KEY AUTOINCREMENT,
            `{}` varchar(250) not null,
            `{}` tinyint default 0,
            `{}` binary(20) not null
    );""".format(
        SONGS_TABLENAME, 
        Database.FIELD_SONG_ID, Database.FIELD_SONGNAME, FIELD_FINGERPRINTED, Database.FIELD_FILE_SHA1
    )

    # inserts (ignores duplicates)
    INSERT_FINGERPRINT = """
        INSERT INTO {} ({}, {}, {}) values (?, ?, ?);
    """.format(FINGERPRINTS_TABLENAME, Database.FIELD_HASH, Database.FIELD_SONG_ID, Database.FIELD_OFFSET)

    INSERT_SONG = """
        INSERT INTO {} ({}, {}) values (?, ?);
    """.format(SONGS_TABLENAME, Database.FIELD_SONGNAME, Database.FIELD_FILE_SHA1)

    # selects
    SELECT = """
        SELECT {}, {} FROM {} WHERE {} = ?;
    """.format(Database.FIELD_SONG_ID, Database.FIELD_OFFSET, FINGERPRINTS_TABLENAME, Database.FIELD_HASH)

    SELECT_MULTIPLE = """
        SELECT {}, {}, {} FROM {} WHERE {} IN (%s);
    """.format(Database.FIELD_HASH, Database.FIELD_SONG_ID, Database.FIELD_OFFSET,
           FINGERPRINTS_TABLENAME, Database.FIELD_HASH)

    SELECT_ALL = """
        SELECT {}, {} FROM {};
    """.format(Database.FIELD_SONG_ID, Database.FIELD_OFFSET, FINGERPRINTS_TABLENAME)

    SELECT_SONG = """
        SELECT {}, {} as {} FROM {} WHERE {} = ?;
    """.format(Database.FIELD_SONGNAME, Database.FIELD_FILE_SHA1, Database.FIELD_FILE_SHA1, 
          SONGS_TABLENAME, Database.FIELD_SONG_ID)

    SELECT_NUM_FINGERPRINTS = """
        SELECT COUNT(*) as n FROM {}
    """.format(FINGERPRINTS_TABLENAME)

    SELECT_UNIQUE_SONG_IDS = """
        SELECT COUNT(DISTINCT {}) as n FROM {} WHERE {} = 1;
    """.format(Database.FIELD_SONG_ID, SONGS_TABLENAME, FIELD_FINGERPRINTED)

    SELECT_SONGS = """
        SELECT {}, {}, {} as {} FROM {} WHERE {} = 1;
    """.format(Database.FIELD_SONG_ID, Database.FIELD_SONGNAME, Database.FIELD_FILE_SHA1, Database.FIELD_FILE_SHA1, 
          SONGS_TABLENAME, FIELD_FINGERPRINTED)

    # drops
    DROP_FINGERPRINTS = "DROP TABLE IF EXISTS {};".format(FINGERPRINTS_TABLENAME)
    DROP_SONGS = "DROP TABLE IF EXISTS {};".format(SONGS_TABLENAME)

    # update
    UPDATE_SONG_FINGERPRINTED = """
        UPDATE {} SET {} = 1 WHERE {} = ?
    """.format(SONGS_TABLENAME, FIELD_FINGERPRINTED, Database.FIELD_SONG_ID)

    # delete
    DELETE_UNFINGERPRINTED = """
        DELETE FROM {} WHERE {} = 0;
    """.format(SONGS_TABLENAME, FIELD_FINGERPRINTED)

    def __init__(self, **options):
        super(SqliteDatabase, self).__init__()
        self.conn = sqlite.connect(options["db"])
        self.conn.text_factory = str
        print "database opened successfully";
        self.cursor = self.conn.cursor()
        self._options = options

    def after_fork(self):
        # Clear the cursor cache, we don't want any stale connections from
        # the previous process.
        Cursor.clear_cache()

    def setup(self):
        """
        Creates any non-existing tables required for dejavu to function.

        This also removes all songs that have been added but have no
        fingerprints associated with them.
        """
        self.cursor.execute(self.CREATE_SONGS_TABLE)
        self.cursor.execute(self.CREATE_FINGERPRINTS_TABLE)
        self.conn.commit()
        
        self.delete_unfingerprinted_songs()

    def empty(self):
        """
        Drops tables created by dejavu and then creates them again
        by calling `SqliteDatabase.setup`.

        .. warning:
            This will result in a loss of data
        """
        self.cursor.execute(self.DROP_FINGERPRINTS)
        self.cursor.execute(self.DROP_SONGS)

        self.setup()

    def delete_unfingerprinted_songs(self):
        """
        Removes all songs that have no fingerprints associated with them.
        """
        self.cursor.execute(self.DELETE_UNFINGERPRINTED)
        self.conn.commit()

    def get_num_songs(self):
        """
        Returns number of songs the database has fingerprinted.
        """
        self.cursor.execute(self.SELECT_UNIQUE_SONG_IDS)

        for count, in cur:
            return count
        return 0

    def get_num_fingerprints(self):
        """
        Returns number of fingerprints the database has fingerprinted.
        """
        self.cursor.execute(self.SELECT_NUM_FINGERPRINTS)

        for count, in cur:
            return count
        return 0

    def set_song_fingerprinted(self, sid):
        """
        Set the fingerprinted flag to TRUE (1) once a song has been completely
        fingerprinted in the database.
        """
        self.cursor.execute(self.UPDATE_SONG_FINGERPRINTED, (sid,))
        self.conn.commit()

    def get_songs(self):
        """
        Return songs that have the fingerprinted flag set TRUE (1).
        """
        rs = self.cursor.execute(self.SELECT_SONGS)
        for id, name, sha1 in rs:
            sha1 = sha1.encode("hex")
            yield {Database.FIELD_SONG_ID : id, Database.FIELD_SONGNAME : name, Database.FIELD_FILE_SHA1 : sha1}

    def get_song_by_id(self, sid):
        """
        Returns song by its ID.
        """
        self.cursor.execute(self.SELECT_SONG, (sid,))
        name, sha1 = self.cursor.fetchone()
        return {Database.FIELD_SONGNAME : name, Database.FIELD_FILE_SHA1 : sha1.encode("hex")}

    def insert(self, hash, sid, offset):
        """
        Insert a (sha1, song_id, offset) row into database.
        """
        self.cursor.execute(self.INSERT_FINGERPRINT, (hash.decode("hex"), sid, offset))
        self.conn.commit()

    def insert_song(self, songname, file_hash):
        """
        Inserts song in the database and returns the ID of the inserted record.
        """
        self.cursor.execute(self.INSERT_SONG, (songname, file_hash.decode("hex")))
        self.conn.commit()
        return self.cursor.lastrowid

    def query(self, hash):
        """
        Return all tuples associated with hash.

        If hash is None, returns all entries in the
        database (be careful with that one!).
        """
        # select all if no key
        query = self.SELECT_ALL if hash is None else self.SELECT
        
        self.cursor.execute(query)
        for sid, offset in cur:
            yield (sid, offset)

    def get_iterable_kv_pairs(self):
        """
        Returns all tuples in database.
        """
        return self.query(None)

    def insert_hashes(self, sid, hashes):
        """
        Insert series of hash => song_id, offset
        values into the database.
        """
        values = []
        for hash, offset in hashes:
            values.append((hash.decode("hex"), sid, offset))
        
        for split_values in grouper(values, 1000):
            self.cursor.executemany(self.INSERT_FINGERPRINT, split_values)
        self.conn.commit()

    def return_matches(self, hashes):
        """
        Return the (song_id, offset_diff) tuples associated with
        a list of (sha1, sample_offset) values.
        """
        # Create a dictionary of hash => offset pairs for later lookups
        mapper = {}
        for hash, offset in hashes:
            mapper[hash.lower().decode("hex")] = offset

        # Get an iteratable of all the hashes we need
        values = mapper.keys()
        
        for split_values in grouper(values, 1000):
            # Create our IN part of the query
            query = self.SELECT_MULTIPLE
            query = query % ','.join(['?'] * len(split_values))

            rs = self.cursor.execute(query, split_values)

            for hash, sid, offset in rs:
                # (sid, db_offset - song_sampled_offset)
                yield (sid, offset - mapper[hash])

    def __getstate__(self):
        return (self._options,)

    def __setstate__(self, state):
        self._options, = state
        self.cursor = cursor_factory(**self._options)
        
def grouper(iterable, n, fillvalue=None):
    args = [iter(iterable)] * n
    return (filter(None, values) for values
            in izip_longest(fillvalue=fillvalue, *args))

