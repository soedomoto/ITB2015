from flask import Blueprint, request, render_template, \
                  flash, g, session, redirect, url_for, \
                  jsonify, Response

import json, requests
from app import app, db
from models import Permohonan, Akta_Lahir
        
class AktaLahir(Blueprint):
    def __init__(self):
        Blueprint.__init__(self, 'akta_lahir', __name__, url_prefix='/aktalahir', template_folder='views', static_folder='static')
        self.web()
        self.api()
    
    def web(self):
        @self.route('/status/', methods=['GET'])
        def list():
            url = '{}/api/aktalahir/permohonan/list/'.format(app.config.get('BASE_URL'))
            resp = requests.get(url)
            
            return render_template("status.html", permohonans=resp.json())
        
        @self.route('/admin/verify/', methods=['GET'])
        def admin_verify():
            url = '{}/api/aktalahir/permohonan/list/'.format(app.config.get('BASE_URL'))
            resp = requests.get(url)
            
            return render_template("verifikasi.html", permohonans=resp.json())
        
        @self.route('/view/<id_permohonan>', methods=['GET'])
        def view(id_permohonan):
            url = '{}/api/aktalahir/permohonan/getById/{}'.format(app.config.get('BASE_URL'), id_permohonan)
            return render_template("view.html", use_template=True)
        
        @self.route('/edit/<kd_akta>', methods=['GET'])
        def edit(kd_akta):
            return render_template("add.html", use_template=False)
        
        @self.route('/add/', methods=['GET'])
        def add():
            qs = request.args
            if qs.has_key("no-template"):
                return render_template("add.html", use_template=False)
            return render_template("add.html", use_template=True)

        @self.route('/add/savepermohonan', methods=['POST'])
        def add_save_permohonan():
            form = request.form

            permohonan = Permohonan()
            permohonan.id_permohonan = None
            permohonan.nik_pemohon = form['nik_pemohon']
            permohonan.no_surat_lahir = form['ket_lahir']
            permohonan.no_kk = form['no_kk']
            permohonan.hubungan_bayi = form['hubungan_bayi']
            permohonan.no_akta_nikah = form['no_nikah']
            permohonan.nama_bayi = form['nama_bayi']
            permohonan.ttl_bayi = form['tanggal']
            permohonan.jam_lahir = form['pukul']
            permohonan.tempat_lahir = form['tempat']
            permohonan.jns_kelamin = form['jk_bayi']
            permohonan.ank_ke = form['anak_ke']
            permohonan.jns_kelahiran = form['jns_kelahiran']
            permohonan.nik_ayah = form['nik_ayah']
            permohonan.nik_ibu = form['nik_ibu']
            permohonan.nik_saksi1 = form['nik_saksi1']
            permohonan.nik_saksi2 = form['nik_saksi2']
            permohonan.status_verifikasi = None

            db.session.add(permohonan)
            db.session.commit()

            return jsonify({'success' : True})

            #permohonan.nik_pemohon            = form['nik-pemohon']
            
            #db.session.add(keluarga)
            #db.session.commit()
            
            #return jsonify({'success' : True})

    def api(self):
        @self.route('/api/permohonan/list/', methods=['GET', 'POST'])
        def api_permohonan_list():
            permohonans = Permohonan.query.all()
            return Response(json.dumps([i.serialize for i in permohonans]), mimetype='application/json')

        @self.route('/api/permohonan/getById/<idPermohonan>', methods=['GET', 'POST'])
        def api_permohonan_getById(idPermohonan):
            permohonan = Permohonan.query.filter_by(id_permohonan=idPermohonan).first()
            return Response(json.dumps(permohonan.serialize), mimetype='application/json')

        @self.route('/api/permohonan/getByNik/<nik_pemohon>', methods=['GET', 'POST'])
        def api_permohonan_getByNik(nik_pemohon):
            permohonans_nik = Permohonan.query.filter_by(nik_pemohon=nik_pemohon).all()
            return Response(json.dumps([i.serialize for i in permohonans_nik]), mimetype='application/json')

        @self.route('/api/list/', methods=['GET', 'POST'])
        def api_akta_lahir_list():
            akta_lahirs = Akta_Lahir.query.all()
            return Response(json.dumps([i.serialize for i in akta_lahirs]), mimetype='application/json')

        @self.route('/api/getById/<no_akta_lahir>', methods=['GET', 'POST'])
        def api_akta_lahir_getById(no_akta_lahir):
            akta_lahir = Akta_Lahir.query.filter_by(no_akta_lahir=no_akta_lahir).first()
            return Response(json.dumps(akta_lahir.serialize), mimetype='application/json')
