from flask import Blueprint, request, render_template, \
                  flash, g, session, redirect, url_for, \
                  jsonify, json, Response

from app import app,db
import requests
from models import User,Session, AktaKematian,PermohonanAktaKematian, Notifikasi
from sqlalchemy import func 

__author__ = 'Lutfi RM'

class DeathCerticate(Blueprint):
    def __init__(self):
        Blueprint.__init__(self, 'death_certificate', __name__, url_prefix='/death_certificate', template_folder='views', static_folder='static')
        self.web() #untuk gui, nama 
        self.api() #untuk api, diawali dg api_nama
    
    def web(self):
        @self.route('/', methods=['GET'])
        def index():
            return render_template("list.html")

        @self.route('/list/', methods=['GET', 'POST'])
        def list():
            return render_template("list.html")
            
        @self.route('/add/', methods=['GET'])
        def add():
            return render_template("add.html")
            
        @self.route('/registrasi/', methods=['GET'])
        def registrasi():
            return render_template("registrasi.html")
        
        @self.route('/kirimregistrasi/', methods=['POST'])
        def kirimregistrasi():
            form = request.form
            notifikasi = Notifikasi()
            notifikasi.id          =None
            notifikasi.nik         =form['nik']  
            notifikasi.email       =form['email']
            notifikasi.status      ='silakan kunjungi {}' \
                                    '/death_certificate/registrasiUser/'\
                                    .format(app.config.get('BASE_URL'))
   
            db.session.add(notifikasi)
            db.session.commit()
            
            return jsonify({'success' : True})
        
        @self.route('/registrasiUser/', methods=['GET'])
        def registrasiUser():
            return render_template("registrasiUser.html")
        
        @self.route('/adduser', methods=['POST'])
        def adduser():
            form = request.form
            user = User()
            user.nik         =form['nik']  
            user.email       =form['email']
            user.password    =form['password'] 
   
            db.session.add(user)
            db.session.commit()
            
            return jsonify({'success' : True})
  
        @self.route('/modifyuser', methods=['POST'])
        def modifyuser():
            form = request.form
   
            user=User.query.filter_by(nik=form['nik']).first()
            user.password=form['password']
            user.email=form['email']
            db.session.commit()
  
        @self.route('/getuser/<nik>', methods=['GET'])
        def getuser(nik):
            user = User.query.filter_by(nik=form['nik']).first()
            return Response(json.dumps([i.serialize for i in user]), mimetype='application/json')
   
        @self.route('/deleteuser/<nik>', methods=['POST'])
        def deleteuser(nik):
            user=User.query.filter_by(nik=form['nik']).first()
            db.session.delete(user)
            db.session.commit()
   
        '''
        @self.route('/login/<nik>/<password>', methods=['GET', 'POST'])
        def login(nik,password):
            user=User.query.filter(and_(nik=form['nik'],password=form['password'])).first()
            return Response(json.dumps([i.serialize for i in user]), mimetype='application/json')
        '''
        @self.route('/add/saveaktakematian', methods=['POST'])
        def add_save_aktakematian():
            form = request.form
            aktaKematian = AktaKematian()
            aktaKematian.idAkta         =form['idAkta']  
            aktaKematian.idPermohonan   =form['idPermohonan']
            aktaKematian.idKtpPemohon   =form['idKtpPemohon']  
            aktaKematian.idKtpSaksi1    =form['idKtpSaksi1']  
            aktaKematian.idKtpSaksi2    =form['idKtpSaksi2']  
            aktaKematian.namaTerlapor   =form['namaTerlapor']
            aktaKematian.pekerjaan      =form['pekerjaan']   
            aktaKematian.idKtpTerlapor  =form['idKtpTerlapor']   
            aktaKematian.idKK           =form['idKK'] 
            aktaKematian.idAktaLahir    =form['idAktaLahir']
            aktaKematian.idAktaNikah    =form['idKAktaNikah']   
            aktaKematian.waktuMeninggal =form['waktuMeninggal']  
            aktaKematian.tempatMeninggal=form['tempatMeninggal']
            aktaKematian.hubPemohon     =form['hubPemohon']
            aktaKematian.sebabMeninggal =form['sebabMeninggal'] 
                        
            db.session.add(aktaKematian)
            db.session.commit()
            
            return jsonify({'success' : True})
        
        @self.route('/add/savepermohonanaktakematian', methods=['POST'])
        def add_save_permohonanaktakematian():
            form = request.form
            permohonanaktaKematian = PermohonanAktaKematian()
            permohonanaktaKematian.idPermohonan        =None
            permohonanaktaKematian.idKtpPemohon        =form['idKtpPemohon']  
            permohonanaktaKematian.idKtpSaksi1         =form['idKtpSaksi1']  
            permohonanaktaKematian.idKtpSaksi2         =form['idKtpSaksi2']  
            permohonanaktaKematian.namaTerlapor        =form['namaTerlapor']
            permohonanaktaKematian.pekerjaan           =form['pekerjaan']
            permohonanaktaKematian.idKtpTerlapor       =form['idKtpTerlapor']   
            permohonanaktaKematian.idKK                =form['idKK'] 
            permohonanaktaKematian.idAktaLahir         =form['idAktaLahir']
            permohonanaktaKematian.idAktaNikah         =form['idKAktaNikah']   
            permohonanaktaKematian.waktuMeninggal      =form['waktuMeninggal']  
            permohonanaktaKematian.tempatMeninggal     =form['tempatMeninggal']
            permohonanaktaKematian.hubPemohon          =form['hubPemohon']
            permohonanaktaKematian.sebabMeninggal      =form['sebabMeninggal'] 
            permohonanaktaKematian.status              =None
            permohonanaktaKematian.fileKtpPemohon      =form['fileKtpPemohon']  
            permohonanaktaKematian.fileKtpSaksi1       =form['fileKtpSaksi1']  
            permohonanaktaKematian.fileKtpSaksi2       =form['fileKtpSaksi2']   
            permohonanaktaKematian.fileKtpTerlapor     =form['fileKtpTerlapor']   
            permohonanaktaKematian.fileKK              =form['fileKK'] 
            permohonanaktaKematian.fileAktaLahir       =form['fileAktaLahir']
            permohonanaktaKematian.fileAktaNikah       =form['fileKAktaNikah']
            permohonanaktaKematian.suratLurah          =form['suratLurah']
            permohonanaktaKematian.fileSuratLurah      =form['fileSuratLurah']
            permohonanaktaKematian.suratMedis          =form['suratMedis']
            permohonanaktaKematian.fileSuratMedis      =form['fileSuratMedis']       
            db.session.add(permohonanaktaKematian)
            db.session.commit()
            return jsonify({'success' : True})
  
        @self.route('/add/updatestatuspermohonanaktakematian', methods=['POST'])
        def add_updatestatuspermohonanaktakematian():
            form = request.form
   
            mohon=PermohonanAktaKematian.query.filter_by(idPermohonan=form['idPermohonan']).first()
            mohon.status=form['status']
            db.session.commit()
  
        @self.route('/list/permohonanaktakematian', methods=['GET', 'POST'])
        def list_permohonanaktamati():
            PermohonanAktaKematians = PermohonanAktaKematian.query.all()
            return Response(json.dumps([i.serialize for i in PermohonanAktaKematians]), mimetype='application/json')
  
        @self.route('/list/permohonanaktakematianall', methods=['GET', 'POST'])
        def list_permohonanaktamatiall():
            print("pass")
            url = '{}/death_certificate/list/permohonanaktakematian'.format(app.config.get('BASE_URL'))
            resp = requests.get(url)
            
            return render_template("listAdmin.html", permohonanList=resp.json()) 
   
        @self.route('/list/permohonanaktakematian/<id>', methods=['GET', 'POST'])
        def list_permohonanaktamatibyid(id):
            PermohonanAktaKematians = PermohonanAktaKematian.query.filter_by(idPermohonan=id).all()
            return Response(json.dumps([i.serialize for i in PermohonanAktaKematians]), mimetype='application/json')
  
        @self.route('/list/permohonanaktakematianbypemohon/', methods=['GET','POST'])
        def list_permohonanaktakematianbypemohon():
            id = request.args.get('idPermohon')
            PermohonanAktaKematians = PermohonanAktaKematian.query.filter_by(idKtpPemohon=id).all()
            return Response(json.dumps([i.serialize for i in PermohonanAktaKematians]), mimetype='application/json')
            
        @self.route('/list/statuspermohonanaktakematian/', methods=['GET','POST'])
        def list_statuspermohonanaktamatibyid():
            id = request.args.get('noPermohonan')
            PermohonanAktaKematians = PermohonanAktaKematian.query.filter_by(idPermohonan=id).first()
            print PermohonanAktaKematians
            result={
                'status' : PermohonanAktaKematians.status
            }
            
            return Response(json.dumps(result), mimetype='application/json')
   
        @self.route('/list/permohonanaktakematianbystat/<stat>', methods=['GET', 'POST'])
        def list_permohonanaktamatibystatus(stat):
            PermohonanAktaKematians = PermohonanAktaKematian.query.filter_by(status=stat).all()
            return Response(json.dumps([i.serialize for i in PermohonanAktaKematians]), mimetype='application/json')
        
        @self.route('/list/permohonanaktakematianoperator', methods=['GET', 'POST'])
        def list_permohonanaktamatioperator():
            print("pass")
            url = '{}/death_certificate/list/permohonanaktakematianbystat/0'.format(app.config.get('BASE_URL'))
            resp = requests.get(url)
            
            return render_template("listAdminOperator.html", permohonanList=resp.json()) 
        
        @self.route('/list/permohonanaktakematiankasi', methods=['GET', 'POST'])
        def list_permohonanaktamatikasi():
            print("pass")
            url = '{}/death_certificate/list/permohonanaktakematianbystat/1'.format(app.config.get('BASE_URL'))
            resp = requests.get(url)
            
            return render_template("listAdminKasi.html", permohonanList=resp.json()) 
        
        @self.route('/list/permohonanaktakematiankabid', methods=['GET', 'POST'])
        def list_permohonanaktamatikabid():
            print("pass")
            url = '{}/death_certificate/list/permohonanaktakematianbystat/2'.format(app.config.get('BASE_URL'))
            resp = requests.get(url)
            
            return render_template("listAdminKabid.html", permohonanList=resp.json()) 
        
        @self.route('/getDataByKK/<id>', methods=['GET'])
        def getDataByKK(id):
            url = '{}/family/api/by-kk/{}/'.format(app.config.get('BASE_URL'), id)
            resp = requests.get(url)
            return resp.json()
        
        @self.route('/getDataByAktaLahir/<id>', methods=['GET'])
        def getDataByAktaLahir(id):
            url = '{}/aktalahir/api/getById/{}/'.format(app.config.get('BASE_URL'), id)
            resp = requests.get(url)
            return resp.json()
  
        @self.route('/getDataByAktaNikah/<id>', methods=['GET'])
        def getDataByAktaNikah(id):
            url = '{}/api/getDataByAktaNikah/{}/'.format(app.config.get('BASE_URL'), id)
            resp = requests.get(url)
            return resp.json()
   
        @self.route('/getDataByKTP/<id>', methods=['GET'])
        def getDataByNIK(id):
            url = '{}/api/getDataByKTP/{}/'.format(app.config.get('BASE_URL'), id)
            resp = requests.get(url)
            return resp.json()
   
        @self.route('/getNamaByKTP/<id>', methods=['GET'])
        def getNamaByNIK(id):
            url = '{}/api/getNamaByKTP/{}/'.format(app.config.get('BASE_URL'), id)
            resp = requests.get(url)
            return resp.json()
  
        @self.route('/getUmurByKTP/<id>', methods=['GET'])
        def getUmurByNIK(id):
            url = '{}/api/getUmurByKTP/{}/'.format(app.config.get('BASE_URL'), id)
            resp = requests.get(url)
            return resp.json()
   
        @self.route('/getRTByKTP/<id>', methods=['GET'])
        def getRTByNIK(id):
            url = '{}/api/getRTByKTP/{}/'.format(app.config.get('BASE_URL'), id)
            resp = requests.get(url)
            return resp.json()
  
        @self.route('/getUmurByKTP/<id>', methods=['GET'])
        def getRWByNIK(id):
            url = '{}/api/getRWByKTP/{}/'.format(app.config.get('BASE_URL'), id)
            resp = requests.get(url)
            return resp.json()
    
        @self.route('/getKelurahanByKTP/<id>', methods=['GET'])
        def getKelurahanByNIK(id):
            url = '{}/api/getKelurahanByKTP/{}/'.format(app.config.get('BASE_URL'), id)
            resp = requests.get(url)
            return resp.json()
   
        @self.route('/getKecamatanByKTP/<id>', methods=['GET'])
        def getKecamatanByNIK(id):
            url = '{}/api/getKecamatanByKTP/{}/'.format(app.config.get('BASE_URL'), id)
            resp = requests.get(url)
            return resp.json()
  
        @self.route('/getKabupatenByKTP/<id>', methods=['GET'])
        def getKabupatenByNIK(id):
            url = '{}/api/getKabupatenByKTP/{}/'.format(app.config.get('BASE_URL'), id)
            resp = requests.get(url)
            return resp.json()
  
        @self.route('/getProvinsiByKTP/<id>', methods=['GET'])
        def getProvinsiByNIK(id):
            url = '{}/api/getProvinsiByKTP/{}/'.format(app.config.get('BASE_URL'), id)
            resp = requests.get(url)
            return resp.json()
  
        @self.route('/getPekerjaanByKTP/<id>', methods=['GET'])
        def getPekerjaanByNIK(id):
            url = '{}/api/getPekerjaanByKTP/{}/'.format(app.config.get('BASE_URL'), id)
            resp = requests.get(url)
            return resp.json()
   
    def api(self):
        @self.route('/api/list/', methods=['GET', 'POST'])
        def api_list_aktamati():
            AktaKematians = AktaKematian.query.all()
            return Response(json.dumps([i.serialize for i in AktaKematians]), mimetype='application/json')
        
        @self.route('/api/by-aktakematian/<id>', methods=['GET', 'POST'])
        def api_list_aktamatibykk(id):
            AktaKematians = AktaKematian.query.filter_by(idAkta=id).all()
            return Response(json.dumps([i.serialize for i in AktaKematians]), mimetype='application/json')
            