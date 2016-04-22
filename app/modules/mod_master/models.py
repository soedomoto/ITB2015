from app import db
from sqlalchemy import ForeignKey

class Propinsi(db.Model):
    __tablename__   = 'propinsi'
    
    '''
    kode, nama
    '''
    
    kd_propinsi         = db.Column(db.String(2), nullable=False, primary_key=True)
    nama_propinsi       = db.Column(db.String(255), nullable=False)
    
    @property
    def serialize(self):
        """Return object data in easily serializeable format"""
        return {
            'kd_propinsi'     : self.kd_propinsi,
            'nama_propinsi'   : self.nama_propinsi
        }
    
class Kabupaten(db.Model):
    __tablename__   = 'kabupaten'
    
    '''
    prop, kode, nama
    '''
    
    kd_propinsi         = db.Column(db.String(2), ForeignKey("propinsi.kd_propinsi"), nullable=False, primary_key=True)
    kd_kabupaten        = db.Column(db.String(2), nullable=False, primary_key=True)
    nama_kabupaten      = db.Column(db.String(255), nullable=False)
    
    @property
    def serialize(self):
        """Return object data in easily serializeable format"""
        return {
            'kd_propinsi'       : self.kd_propinsi,
            'kd_kabupaten'      : self.kd_kabupaten, 
            'nama_kabupaten'    : self.nama_kabupaten
        }
    
class Kecamatan(db.Model):
    __tablename__   = 'kecamatan'
    
    '''
    prop, kab, kode, nama
    '''
    
    kd_propinsi         = db.Column(db.String(2), ForeignKey("kabupaten.kd_propinsi"), nullable=False, primary_key=True)
    kd_kabupaten        = db.Column(db.String(2), ForeignKey("kabupaten.kd_kabupaten"), nullable=False, primary_key=True)
    kd_kecamatan        = db.Column(db.String(2), nullable=False, primary_key=True)
    nama_kecamatan      = db.Column(db.String(255), nullable=False)
    
    @property
    def serialize(self):
        """Return object data in easily serializeable format"""
        return {
            'kd_propinsi'       : self.kd_propinsi,
            'kd_kabupaten'      : self.kd_kabupaten, 
            'kd_kecamatan'      : self.kd_kecamatan, 
            'nama_kecamatan'    : self.nama_kecamatan
        }

class Kelurahan(db.Model):
    __tablename__   = 'kelurahan'
    
    '''
    prop, kab, kec, kode, nama
    '''
    
    kd_propinsi         = db.Column(db.String(2), ForeignKey("kecamatan.kd_propinsi"), nullable=False, primary_key=True)
    kd_kabupaten        = db.Column(db.String(2), ForeignKey("kecamatan.kd_kabupaten"), nullable=False, primary_key=True)
    kd_kecamatan        = db.Column(db.String(2), ForeignKey("kecamatan.kd_kecamatan"), nullable=False, primary_key=True)
    kd_kelurahan        = db.Column(db.String(4), nullable=False, primary_key=True)
    nama_kelurahan      = db.Column(db.String(255), nullable=False)
    
    @property
    def serialize(self):
        """Return object data in easily serializeable format"""
        return {
            'kd_propinsi'       : self.kd_propinsi,
            'kd_kabupaten'      : self.kd_kabupaten, 
            'kd_kecamatan'      : self.kd_kecamatan, 
            'kd_kelurahan'      : self.kd_kelurahan, 
            'nama_kelurahan'    : self.nama_kelurahan
        }
    
    