from app import db
from sqlalchemy import ForeignKey
import requests

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
    
class Agama(db.Model):
    __tablename__   = 'agama'
    
    '''
    kode, nama
    '''
    
    kd_agama    = db.Column(db.Integer, nullable=False, primary_key=True)
    nama_agama  = db.Column(db.String(100), nullable=False)
    
    @property
    def serialize(self):
        """Return object data in easily serializeable format"""
        return {
            'kd_agama'      : self.kd_agama,
            'nama_agama'    : self.nama_agama
        }
    
class HubunganKeluarga(db.Model):
    __tablename__   = 'hubungan_keluarga'
    
    '''
    kode, nama
    '''
    
    kd_hubungan_keluarga    = db.Column(db.Integer, nullable=False, primary_key=True)
    nama_hubungan_keluarga  = db.Column(db.String(100), nullable=False)
    
    @property
    def serialize(self):
        """Return object data in easily serializeable format"""
        return {
            'kd_hubungan_keluarga'      : self.kd_hubungan_keluarga,
            'nama_hubungan_keluarga'    : self.nama_hubungan_keluarga
        }
    
class StatusPerkawinan(db.Model):
    __tablename__   = 'status_perkawinan'
    
    '''
    kode, nama
    '''
    
    kd_status_perkawinan    = db.Column(db.Integer, nullable=False, primary_key=True)
    nama_status_perkawinan  = db.Column(db.String(100), nullable=False)
    
    @property
    def serialize(self):
        """Return object data in easily serializeable format"""
        return {
            'kd_status_perkawinan'      : self.kd_status_perkawinan,
            'nama_status_perkawinan'    : self.nama_status_perkawinan
        }

class HubunganBayi(db.Model):
    __tablename__ = 'bayi_pemohon'
    
    '''
    kode, nama
    '''
    
    kd_hubungan_bayi = db.Column(db.Integer, primary_key=True)
    nama_hubungan_bayi = db.Column(db.String(100), nullable=False)
    
    @property
    def serialize(self):
        """Return object data in easily serializeable format"""
        return {
            'kd_hubungan_bayi'      : self.kd_hubungan_bayi,
            'nama_hubungan_bayi'    : self.nama_hubungan_bayi
        }
        
class StatusVerifikasiAktaLahir(db.Model):
    __tablename__ = 'verifikasi_akta_lahir'

    '''
    kode, nama
    '''

    kd_verifikasi_akta = db.Column(db.Integer, primary_key=True)
    nama_verifikasi_akta = db.Column(db.String(100), nullable=False)
    
    @property
    def serialize(self):
        """Return object data in easily serializeable format"""
        return {
            'kd_verifikasi_akta'    : self.kd_verifikasi_akta,
            'nama_verifikasi_akta'  : self.nama_verifikasi_akta
        }
    
class JenisKelamin(db.Model):
    __tablename__ = 'jenis_kelamin'
    
    '''
    kode, nama
    '''
    
    kd_jns_kelamin = db.Column(db.Integer, primary_key=True)
    nama_jns_kelamin = db.Column(db.String(100), nullable=False)
    
    @property
    def serialize(self):
        """Return object data in easily serializeable format"""
        return{
            'kd_jns_kelamin'    : self.kd_jns_kelamin,
            'nama_jns_kelamin'  : self.nama_jns_kelamin
        }

class JenisKelahiran(db.Model):
    __tablename__ = 'jenis_kelahiran'
    
    '''
    kode, nama
    '''
    
    kd_jns_kelahiran = db.Column(db.Integer, primary_key=True)
    nama_jns_kelahiran = db.Column(db.String(100), nullable=False)
    
    @property
    def serialize(self):
        """Return object data in easily serializeable format"""
        return{
            'kd_jns_kelahiran'    : self.kd_jns_kelahiran,
            'nama_jns_kelahiran'  : self.kd_jns_kelahiran
        }

class LevelOtorisasi(db.Model):
	__tablename__ = 'level_otorisasi'
	
	'''
	kode, nama
	'''
	
	kd_level_otorisasi = db.Column(db.Integer, primary_key=True)
	nama_level_otorisasi = db.Column(db.String(255), nullable=False)
	
	@property
	def serialize(self):
		"""Return object data in easily serializeable format"""
		return{
			'kd_level_otorisasi'	: self.kd_level_otorisasi,
			'nama_level_otorisasi'	: self.nama_level_otorisasi
		}