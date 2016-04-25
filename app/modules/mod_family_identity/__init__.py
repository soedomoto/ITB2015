from flask import Blueprint, request, render_template, \
                  flash, g, session, redirect, url_for, \
                  jsonify, Response

import json, requests
from app import app, db
from models import Keluarga
        
class FamilyIdentity(Blueprint):
    def __init__(self):
        Blueprint.__init__(self, 'family_identity', __name__, url_prefix='/family', template_folder='views', static_folder='static')
        self.web()
        self.api()
    
    def web(self):
        @self.route('/', methods=['GET'])
        def list():
            url = '{}/api/family/list/'.format(app.config.get('BASE_URL'))
            resp = requests.get(url)
            
            return render_template("list.html", families=resp.json())
          
        @self.route('/view/<kd_kk>', methods=['GET'])
        def view(kd_kk):
            return render_template("add.html", use_template=False)
        
        @self.route('/edit/<kd_kk>', methods=['GET'])
        def edit(kd_kk):
            return render_template("add.html", use_template=False)
            
        @self.route('/add/', methods=['GET'])
        def add():
            qs = request.args
            if qs.has_key("no-template"):
                return render_template("add.html", use_template=False)
            
            return render_template("add.html", use_template=True)
        
        @self.route('/add/save', methods=['POST'])
        def add_save():
            form = request.form
            
            keluarga = Keluarga()
            keluarga.no_kk                  = '123'
            keluarga.nik_kk                 = form['nik-pemohon']
            keluarga.alamat                 = form['alamat-pemohon']
            keluarga.no_rt                  = form['rt-pemohon']
            keluarga.no_rw                  = form['rw-pemohon']
            keluarga.kelurahan              = form['kel-pemohon']
            keluarga.kecamatan              = form['kec-pemohon']
            keluarga.kabupaten              = form['kab-pemohon']
            keluarga.propinsi               = form['prop-pemohon']
            keluarga.kode_pos               = '1234' #form['pos-pemohon']
            
            db.session.add(keluarga)
            db.session.commit()
            
            return jsonify({'success' : True})
        
    def api(self):
        @self.route('/api/list/', methods=['GET', 'POST'])
        def api_list():
            keluargas = Keluarga.query.all()
            return Response(json.dumps([i.serialize for i in keluargas]), mimetype='application/json')
        
        @self.route('/api/by-kk/<kk>', methods=['GET', 'POST'])
        def api_list_kk(kk):
            keluargas = Keluarga.query.filter_by(no_kk=kk).all()
            return Response(json.dumps([i.serialize for i in keluargas]), mimetype='application/json')
            