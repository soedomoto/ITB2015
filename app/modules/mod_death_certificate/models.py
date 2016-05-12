from app import db
from sqlalchemy import ForeignKey

class User(db.Model):
    __tablename__   = 'User'
    
    '''
    nik,email,password 
    '''
    
    nik         = db.Column(db.String(255), primary_key=True)
    email       = db.Column(db.String(255), nullable=False)
    password    = db.Column(db.String(255), nullable=True)
     
    @property
    def serialize(self):
        return{
            'nik'     : self.nik,         
            'email'   : self.email,    
            'password': self.password,   
           
        }   

class Notifikasi(db.Model):
    __tablename__   = 'Notifikasi'
    
    '''
    id,nik,email,status 
    '''
    
    id          = db.Column(db.Integer, primary_key=True,autoincrement=True)
    nik         = db.Column(db.String(255), nullable=True)
    email       = db.Column(db.String(255), nullable=True)
    status      = db.Column(db.String(255), nullable=True)
     
    @property
    def serialize(self):
        return{
            'id'    : self.id,         
            'nik'   : self.nik,
            'email' : self.email,
            'status': self.status,   
           
        }   
 
class Session(db.Model):
    __tablename__   = 'Session'
    
    '''
    nik, sesi 
    '''
    
    nik        = db.Column(db.String(255), primary_key=True)
    sesi       = db.Column(db.Integer, nullable=False)
     
    @property
    def serialize(self):
        return{
            'nik'     : self.nik,         
            'sesi'   : self.sesi,    
        }   

        
class AktaKematian(db.Model):
    __tablename__   = 'AktaKematian'
    
    '''
    idAkta, idPermohonan, idKtpPemohon, idKtpSaksi1, idKtpSaksi2, namaTerlapor, 
	pekerjaan,idKtpTerlapor, idKK, idAktaLahir, idKAktaNikah, waktuMeninggal, 
	tempatMeninggal, hubPemohon, sebabMeniggal 
    '''
    
    idAkta         = db.Column(db.String(255), primary_key=True)
    idPermohonan   = db.Column(db.Integer, nullable=False)
    idKtpPemohon   = db.Column(db.String(255), nullable=False)
    idKtpSaksi1    = db.Column(db.String(255), nullable=False)
    idKtpSaksi2    = db.Column(db.String(255), nullable=False)
    namaTerlapor   = db.Column(db.String(255), nullable=False)
    pekerjaan      = db.Column(db.String(255), nullable=True)
    idKtpTerlapor  = db.Column(db.String(255), nullable=True)
    idKK           = db.Column(db.String(255), nullable=False)
    idAktaLahir    = db.Column(db.String(255), nullable=False)
    idAktaNikah    = db.Column(db.String(255), nullable=True)
    waktuMeninggal = db.Column(db.String(255), nullable=False)
    tempatMeninggal= db.Column(db.String(255), nullable=False)
    hubPemohon     = db.Column(db.String(255), nullable=False)
    sebabMeninggal = db.Column(db.String(255), nullable=False)
 
    @property
    def serialize(self):
        return{
            'idAkta'         : self.idAkta,         
            'idPermohonan'   : self.idPermohonan,    
            'idKtpPemohon'   : self.idKtpPemohon,   
            'idKtpSaksi1'    : self.idKtpSaksi1,    
            'idKtpSaksi2'    : self.idKtpSaksi2,    
            'namaTerlapor'   : self.namaTerlapor,   
            'pekerjaan'      : self.pekerjaan,      
            'idKtpTerlapor'  : self.idKtpTerlapor,  
            'idKK'           : self.idKK,           
            'idAktaLahir'    : self.idAktaLahir,    
            'idAktaNikah'    : self.idAktaNikah,    
            'waktuMeninggal' : self.waktuMeninggal, 
            'tempatMeninggal': self.tempatMeninggal,
            'hubPemohon'     : self.hubPemohon,     
            'sebabMeninggal' : self.sebabMeninggal
        }   
 
  
class PermohonanAktaKematian(db.Model):
    __tablename__   = 'Permohonan'
    
    '''
    idPermohonan, idKtpPemohon, idKtpSaksi1, idKtpSaksi2, namaTerlapor, pekerjaan,
	idKtpTerlapor, idKK, idAktaLahir, idKAktaNikah, waktuMeninggal, tempatMeninggal, 
	hubPemohon, sebabMeninggal,status,fileKtpPemohon, fileKtpSaksi1, fileKtpSaksi2, 
	fileKtpTerlapor, fileKK, fileAktaLahir, fileKAktaNikah
    '''
    
    idPermohonan          = db.Column(db.Integer, primary_key=True,autoincrement=True)
    idKtpPemohon          = db.Column(db.String(255), nullable=False)
    idKtpSaksi1           = db.Column(db.String(255), nullable=False)
    idKtpSaksi2           = db.Column(db.String(255), nullable=False)
    namaTerlapor          = db.Column(db.String(255), nullable=False)
    pekerjaan             = db.Column(db.String(255), nullable=True)
    idKtpTerlapor         = db.Column(db.String(255), nullable=True)
    idKK                  = db.Column(db.String(255), nullable=False)
    idAktaLahir           = db.Column(db.String(255), nullable=False)
    idAktaNikah           = db.Column(db.String(255), nullable=True)
    waktuMeninggal        = db.Column(db.String(255), nullable=False)
    tempatMeninggal       = db.Column(db.String(255), nullable=False)
    hubPemohon            = db.Column(db.String(255), nullable=False)
    sebabMeninggal        = db.Column(db.String(255), nullable=False)
    status                = db.Column(db.String(255), nullable=True)
    fileKtpPemohon        = db.Column(db.String(255), nullable=False)
    fileKtpSaksi1         = db.Column(db.String(255), nullable=False)
    fileKtpSaksi2         = db.Column(db.String(255), nullable=False)
    fileKtpTerlapor       = db.Column(db.String(255), nullable=True)
    fileKK                = db.Column(db.String(255), nullable=False)
    fileAktaLahir         = db.Column(db.String(255), nullable=False)
    fileAktaNikah         = db.Column(db.String(255), nullable=True)
    suratLurah            = db.Column(db.String(255), nullable=True)
    fileSuratLurah        = db.Column(db.String(255), nullable=True)
    suratMedis            = db.Column(db.String(255), nullable=True)
    fileSuratMedis        = db.Column(db.String(255), nullable=True)
    
    @property
    def serialize(self):
        return{  
            'idPermohonan'     : self.idPermohonan,    
            'idKtpPemohon'     : self.idKtpPemohon,   
            'idKtpSaksi1'      : self.idKtpSaksi1,    
            'idKtpSaksi2'      : self.idKtpSaksi2,    
            'namaTerlapor'     : self.namaTerlapor,   
            'pekerjaan'        : self.pekerjaan,      
            'idKtpTerlapor'    : self.idKtpTerlapor,  
            'idKK'             : self.idKK,           
            'idAktaLahir'      : self.idAktaLahir,    
            'idAktaNikah'      : self.idAktaNikah,    
            'waktuMeninggal'   : self.waktuMeninggal, 
            'tempatMeninggal'  : self.tempatMeninggal,
            'hubPemohon'       : self.hubPemohon,     
            'sebabMeninggal'   : self.sebabMeninggal,
            'status'           : self.status,
            'fileKtpPemohon'   : self.fileKtpPemohon,   
            'fileKtpSaksi1'    : self.fileKtpSaksi1,    
            'fileKtpSaksi2'    : self.fileKtpSaksi2,      
            'fileKtpTerlapor'  : self.fileKtpTerlapor,  
            'fileKK'           : self.fileKK,           
            'fileAktaLahir'    : self.fileAktaLahir,    
            'fileAktaNikah'    : self.fileAktaNikah,
            'suratLurah'       : self.suratLurah,
            'fileSuratLurah'   : self.fileSuratLurah,
            'suratMedis'       : self.suratMedis,
            'fileSuratMedis'   : self.fileSuratMedis,
        } 