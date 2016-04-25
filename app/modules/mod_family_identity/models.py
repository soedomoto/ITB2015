import requests, json
from app import app, db
from sqlalchemy import ForeignKey
    
class Keluarga(db.Model):
    __tablename__   = 'keluarga'
    
    '''
    no. kk, nama kk, alamat, no rt, no rw, kelurahan, kabupaten, kecamatan, kode pos, propinsi
    '''
    
    no_kk               = db.Column(db.String(100), primary_key=True)
    alamat              = db.Column(db.String(255), nullable=False)
    no_rt               = db.Column(db.Integer, nullable=False)
    no_rw               = db.Column(db.Integer, nullable=False)
    kelurahan           = db.Column(db.String(255), nullable=False)
    kecamatan           = db.Column(db.String(255), nullable=False)
    kabupaten           = db.Column(db.String(255), nullable=False)
    propinsi            = db.Column(db.String(255), nullable=False)
    kode_pos            = db.Column(db.Integer, nullable=False)
    
    @property
    def obj_propinsi(self):
        url = '{}/api/master/province/id/{}/'.format(app.config.get('BASE_URL'), self.propinsi)
        resp = requests.get(url)
        return resp.json()
    
    @property
    def obj_kabupaten(self):
        url = '{}/api/master/regency/prov/{}/id/{}/'.format(app.config.get('BASE_URL'), self.propinsi, self.kabupaten)
        resp = requests.get(url)
        return resp.json()
    
    @property
    def obj_kecamatan(self):
        url = '{}/api/master/subdistrict/prov/{}/regency/{}/id/{}/'.format(app.config.get('BASE_URL'), self.propinsi, self.kabupaten, self.kecamatan)
        resp = requests.get(url)
        return resp.json()
    
    @property
    def obj_kelurahan(self):
        url = '{}/api/master/village/prov/{}/regency/{}/subdistrict/{}/id/{}/'.format(app.config.get('BASE_URL'), self.propinsi, self.kabupaten, self.kecamatan, self.kelurahan)
        resp = requests.get(url)
        return resp.json()
    
    @property
    def anggota_keluarga(self):
        url = '{}/api/population/list/kk/{}/'.format(app.config.get('BASE_URL'), self.no_kk)
        resp = requests.get(url)
        return resp.json()
    
    @property
    def serialize(self):
        """Return object data in easily serializeable format"""
        krt = None
        arts = self.anggota_keluarga
        for art in arts:
            if art['hubungan_keluarga']['kd_hubungan_keluarga'] == 1:
                krt = art
        
        return {
            'no_kk'         : self.no_kk,
            'alamat'        : self.alamat, 
            'no_rt'         : self.no_rt, 
            'no_rw'         : self.no_rw, 
            'kelurahan'     : self.obj_kelurahan, 
            'kecamatan'     : self.obj_kecamatan, 
            'kabupaten'     : self.obj_kabupaten, 
            'propinsi'      : self.obj_propinsi, 
            'kode_pos'      : self.kode_pos, 
            'art'           : arts, 
            'krt'           : krt
        }
    
    