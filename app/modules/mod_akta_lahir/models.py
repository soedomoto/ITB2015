import requests, json
from app import app, db
from sqlalchemy import ForeignKey
    
class Permohonan(db.Model):
    __tablename__   = 'permohonan'

    '''
    idPermohonan, nik pemohon, hubungan dengan bayi, no KK, no Akta Nikah, no surat lahir
    '''

    id_permohonan		= db.Column(db.Integer, primary_key=True, autoincrement=True)
    nik_pemohon			= db.Column(db.String(100), nullable=False)
    hubungan_bayi		= db.Column(db.String(100), nullable=False)
    no_kk				= db.Column(db.String(128), nullable=False)
    no_akta_nikah		= db.Column(db.String(128), nullable=False)
    no_surat_lahir		= db.Column(db.String(128), nullable=False)
    nama_bayi			= db.Column(db.String(100), nullable=False)
    ttl_bayi			= db.Column(db.String(100), nullable=False)
    jam_lahir			= db.Column(db.String(100), nullable=False)
    tempat_lahir		= db.Column(db.String(100), nullable=False)
    jns_kelamin			= db.Column(db.String(100), nullable=False)
    ank_ke				= db.Column(db.String(100), nullable=False)
    jns_kelahiran		= db.Column(db.String(100), nullable=False)
    nik_ayah			= db.Column(db.String(100), nullable=False)
    nik_ibu				= db.Column(db.String(100), nullable=False)
    nik_saksi1			= db.Column(db.String(100), nullable=False)
    nik_saksi2			= db.Column(db.String(100), nullable=False)
    status_verifikasi	= db.Column(db.String(128), nullable=False)
    
    @property
    def obj_populasi(self):
        url = '{}/api/population/id/{}/'.format(app.config.get('BASE_URL'), self.nik_pemohon)
        resp = requests.get(url)
        return resp.json()

    @property
    def obj_jenis_kelamin(self):
        url = '{}/api/master/jeniskelamin/id/{}/'.format(app.config.get('BASE_URL'), self.jns_kelamin)
        resp = requests.get(url)
        return resp.json()

    @property
    def obj_status_verifikasi(self):
        url = '{}/api/master/verifikasi/id/{}/'.format(app.config.get('BASE_URL'), self.status_verifikasi)
        resp = requests.get(url)
        return resp.json()

    @property
    def obj_populasi_ayah(self):
        url = '{}/api/population/id/{}/'.format(app.config.get('BASE_URL'), self.nik_ayah)
        resp = requests.get(url)
        return resp.json()
    
    @property
    def obj_populasi_ibu(self):
        url = '{}/api/population/id/{}/'.format(app.config.get('BASE_URL'), self.nik_ibu)
        resp = requests.get(url)
        return resp.json()

    @property
    def obj_saksi1(self):
        url = '{}/api/population/id/{}/'.format(app.config.get('BASE_URL'), self.nik_pemohon)
        resp = requests.get(url)
        return resp.json()
	
    @property
    def obj_saksi2(self):
        url = '{}/api/population/id/{}/'.format(app.config.get('BASE_URL'), self.nik_pemohon)
        resp = requests.get(url)
        return resp.json()

    @property
    def serialize(self):
        """Return object data in easily serializeable format"""
        return{
            'id_permohonan'     : self.id_permohonan,
            'nik_pemohon'       : self.obj_populasi,
            'hubungan_bayi'     : self.hubungan_bayi,
            'no_kk'             : self.no_kk,
            'no_akta_nikah'     : self.no_akta_nikah,
            'no_surat_lahir'    : self.no_surat_lahir,
			'nama_bayi'         : self.nama_bayi,
            'ttl_bayi'          : self.ttl_bayi,
            'tempat_lahir'      : self.tempat_lahir,
            'jns_kelamin'       : self.obj_jenis_kelamin,
            'anak_ke'           : self.ank_ke,
            'jns_kelahiran'     : self.jns_kelahiran,
            'nik_ayah'          : self.obj_populasi_ayah,
            'nik_ibu'           : self.obj_populasi_ibu,
			'nik_saksi1'		: self.obj_saksi1,
			'nik_saksi2'		: self.obj_saksi2,
            'status_verifikasi' : self.obj_status_verifikasi
        }
    
class Akta_Lahir(db.Model):
    __tablename__ = 'akta_lahir'

    '''
    no akta lahir, nama, ttl, pukul, tempat, jk, anakKe, jnsKelahiran, idPermohonan
    '''

    id_akta_lahir   = db.Column(db.Integer, primary_key=True, autoincrement=True)
    no_akta_lahir	= db.Column(db.String(100), nullable=True) 
    nama			= db.Column(db.String(100), nullable=False)
    ttl				= db.Column(db.String(100), nullable=False)
    pukul			= db.Column(db.String(100), nullable=False) 
    tempat			= db.Column(db.String(100), nullable=False)
    jns_kelamin		= db.Column(db.String(100), nullable=False)
    ank_ke			= db.Column(db.String(100), nullable=False)
    jns_kelahiran	= db.Column(db.String(100), nullable=False) 
    nik_ayah		= db.Column(db.String(100), nullable=False)
    nik_ibu			= db.Column(db.String(100), nullable=False)
    id_permohonan	= db.Column(db.Integer, ForeignKey("permohonan.id_permohonan"), nullable=False)

    @property
    def obj_jenis_kelamin(self):
        url = '{}/api/master/jeniskelamin/id/{}/'.format(app.config.get('BASE_URL'), self.jns_kelamin)
        resp = requests.get(url)
        return resp.json()
        
    @property
    def obj_jenis_kelahiran(self):
        url = '{}/api/jeniskelahiran/id/{}/'.format(app.config.get('BASE_URL'), self.jns_kelahiran)
        resp = requests.get(url)
        return resp.json()
    
    @property
    def obj_populasi_ayah(self):
	    url = '{}/api/population/id/{}/'.format(app.config.get('BASE_URL'), self.nik_ayah)
	    resp = requests.get(url)
	    return resp.json()
    
    @property
    def obj_populasi_ibu(self):
	    url = '{}/api/population/id/{}/'.format(app.config.get('BASE_URL'), self.nik_ibu)
	    resp = requests.get(url)
	    return resp.json()
    
    @property
    def serialize(self):
        """Return object data in easily serializeable format"""
        return{
            'id_akta_lahir'     : self.id_akta_lahir,
            'no_akta_lahir'     : self.no_akta_lahir,
            'nama'              : self.nama,
            'ttl'               : self.ttl,
            'tempat'            : self.tempat,
            'jns_kelamin'       : self.obj_jenis_kelamin,
            'anak_ke'           : self.ank_ke,
            'jns_kelahiran'     : self.jns_kelahiran,
            'nik_ayah'          : self.obj_populasi_ayah,
            'nik_ibu'           : self.obj_populasi_ibu,
            'id_permohonan'     : self.id_permohonan
        }