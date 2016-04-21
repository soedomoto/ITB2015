from app import db
from sqlalchemy import ForeignKey
    
class Keluarga(db.Model):
    __tablename__   = 'keluarga'
    
    '''
    no. kk, nama kk, alamat, no rt, no rw, kelurahan, kabupaten, kecamatan, kode pos, propinsi
    '''
    
    no_kk               = db.Column(db.String(100), primary_key=True)
    nik_kk              = db.Column(db.String(255), nullable=False)
    alamat              = db.Column(db.String(255), nullable=False)
    no_rt               = db.Column(db.Integer, nullable=False)
    no_rw               = db.Column(db.Integer, nullable=False)
    kelurahan           = db.Column(db.String(255), nullable=False)
    kecamatan           = db.Column(db.String(255), nullable=False)
    kabupaten           = db.Column(db.String(255), nullable=False)
    propinsi            = db.Column(db.String(255), nullable=False)
    kode_pos            = db.Column(db.Integer, nullable=False)
    
    