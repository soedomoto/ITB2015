from app import app, db
import requests

class Penduduk(db.Model):
    __tablename__       = 'penduduk'
    
    '''
    Data di buku induk kependudukan (Formulir F1-01) :
    nik, no. kk, nama, jenkel, tmpt lahir, tgl lahir, gol darah, agama, status perkawinan, status hub keluarga, pddkn akhir, pekerjaan, penyandang cacat, nama ibu, nama ayah, nama kepala keluarga, alamat, no rt, no rw, no kel, nama kel, nama kec
    '''
    
    nik                 = db.Column(db.String(100), primary_key=True)
    no_kk               = db.Column(db.String(128), nullable=False)
    nama                = db.Column(db.String(255), nullable=False)
    jenis_kelamin       = db.Column(db.String(255), nullable=False)
    tempat_lahir        = db.Column(db.String(255), nullable=False)
    tanggal_lahir       = db.Column(db.String(255), nullable=False)
    golongan_darah      = db.Column(db.String(255), nullable=False)
    agama               = db.Column(db.String(255), nullable=False)
    status_perkawinan   = db.Column(db.String(255), nullable=False)
    hubungan_keluarga   = db.Column(db.String(255), nullable=False)
    pendidikan_akhir    = db.Column(db.String(255), nullable=False)
    pekerjaan           = db.Column(db.String(255), nullable=False)
    penyandang_cacat    = db.Column(db.String(255), nullable=False)
    nama_ibu            = db.Column(db.String(255), nullable=False)
    nama_ayah           = db.Column(db.String(255), nullable=False)
    alamat              = db.Column(db.String(255), nullable=False)
    no_rt               = db.Column(db.String(255), nullable=False)
    no_rw               = db.Column(db.String(255), nullable=False)
    kelurahan           = db.Column(db.String(255), nullable=False)
    kecamatan           = db.Column(db.String(255), nullable=False)
    
    @property
    def obj_kecamatan(self):
        url = '{}/api/master/subdistrict/prov/{}/regency/{}/id/{}/'.format(app.config.get('BASE_URL'), '32', '74', self.kecamatan)
        resp = requests.get(url)
        return resp.json()
    
    @property
    def obj_kelurahan(self):
        url = '{}/api/master/village/prov/{}/regency/{}/subdistrict/{}/id/{}/'.format(app.config.get('BASE_URL'), '32', '74', self.kecamatan, self.kelurahan)
        resp = requests.get(url)
        return resp.json()
    
    @property
    def obj_agama(self):
        url = '{}/api/master/religion/id/{}/'.format(app.config.get('BASE_URL'), self.agama)
        resp = requests.get(url)
        return resp.json()
    
    @property
    def obj_hubungan_keluarga(self):
        url = '{}/api/master/relationship/id/{}/'.format(app.config.get('BASE_URL'), self.hubungan_keluarga)
        resp = requests.get(url)
        return resp.json()
    
    @property
    def obj_status_perkawinan(self):
        url = '{}/api/master/marital/id/{}/'.format(app.config.get('BASE_URL'), self.status_perkawinan)
        resp = requests.get(url)
        return resp.json()
    
    @property
    def serialize(self):
        """Return object data in easily serializeable format"""
        return {
            'nik'                   : self.nik,
            'no_kk'                 : self.no_kk, 
            'nama'                  : self.nama, 
            'jenis_kelamin'         : self.jenis_kelamin, 
            'tempat_lahir'          : self.tempat_lahir, 
            'tanggal_lahir'         : self.tanggal_lahir, 
            'golongan_darah'        : self.golongan_darah, 
            'agama'                 : self.obj_agama, 
            'status_perkawinan'     : self.obj_status_perkawinan, 
            'hubungan_keluarga'     : self.obj_hubungan_keluarga, 
            'pendidikan_akhir'      : self.pendidikan_akhir, 
            'pekerjaan'             : self.pekerjaan, 
            'penyandang_cacat'      : self.penyandang_cacat, 
            'nama_ibu'              : self.nama_ibu, 
            'nama_ayah'             : self.nama_ayah,
            'alamat'                : self.alamat, 
            'no_rt'                 : self.no_rt, 
            'no_rw'                 : self.no_rw, 
            'kelurahan'             : self.obj_kelurahan,
            'kecamatan'             : self.obj_kecamatan
        }
    
    