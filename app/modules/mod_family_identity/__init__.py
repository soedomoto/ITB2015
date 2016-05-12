from flask import Blueprint, request, render_template, \
                  flash, g, session, redirect, url_for, \
                  jsonify, Response

import json, requests
from datetime import datetime
from app import app, db
from models import Keluarga, PermohonanKK

__author__ = 'Aris Prawisudatama'

class FamilyIdentity(Blueprint):
    def __init__(self):
        Blueprint.__init__(self, 'family_identity', __name__,
                           url_prefix='/family', template_folder='views',
                           static_folder='static')
        self.web()
        self.admin()
        self.api()
    
    def web(self):
        '''
        GUI Methods
        '''

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

        # KK Request ======================================================

        @self.route('/add/', methods=['GET'])
        def add():
            qs = request.args
            if qs.has_key("no-template"):
                return render_template("add.html", use_template=False)
            
            return render_template("add.html", use_template=True)
        
        @self.route('/add/save', methods=['POST'])
        def add_save():
            form = request.form
            data = dict((key, form.getlist(key) if len(form.getlist(
                key)) > 1 else form.getlist(key)[0]) for key in form.keys())
            
            permohonan                = PermohonanKK()
            permohonan.json_data      = json.dumps(data)
            permohonan.tanggal_entry  = datetime.now()
            
            db.session.add(permohonan)
            db.session.commit()
            
            return jsonify({'success' : True})

        @self.route('/requests/detail/<id>', methods=['GET'])
        def request_detail(id):
            url = '{}/api/family/requests/id/{}/'.format(app.config.get(
                'BASE_URL'), id)
            resp = requests.get(url)

            qs = request.args
            if qs.has_key("no-template"):
                return render_template("requests_detail.html",
                                       use_template=False,
                                       request=resp.json())

            return render_template("requests_detail.html", use_template=True,
                                   request=resp.json())

    def admin(self):
        '''
        Admin method
        '''

        # Verify KK Request ===================================================

        @self.route('/admin/requests/verify/', methods=['GET'])
        def admin_verify():
            url = '{}/api/family/requests/list/'.format(app.config.get(
                'BASE_URL'))
            resp = requests.get(url)

            qs = request.args
            if qs.has_key("no-template"):
                return render_template("requests_verify_list.html",
                                       use_template=False, requests =
                                       resp.json())

            return render_template("requests_verify_list.html", use_template=True,
                                   requests = resp.json())

        @self.route('/admin/requests/verify/<id>/notes/', methods=['POST'])
        def admin_verify_add_notes(id):
            form = request.form

            permohonan = PermohonanKK.query.filter(
                PermohonanKK.id_permohonan==id).first()
            permohonan.catatan = str(form.getlist('catatan')[0])

            db.session.commit()
            return Response(json.dumps({'result' : True}),
                            mimetype='application/json')

        
    def api(self):
        '''
        API Methods
        '''

        # Keluarga ============================================================

        @self.route('/api/list/', methods=['GET'])
        def api_list():
            keluargas = Keluarga.query.all()
            return Response(json.dumps([i.serialize for i in keluargas]),
                            mimetype='application/json')
        
        @self.route('/api/by-kk/<kk>', methods=['GET', 'POST'])
        def api_list_kk(kk):
            keluargas = Keluarga.query.filter_by(no_kk=kk).all()
            return Response(json.dumps([i.serialize for i in keluargas]),
                            mimetype='application/json')

        # Permohonan KK =======================================================

        @self.route('/api/requests/list/', methods=['GET'])
        @self.route('/api/requests/list/verified/<verified>/', methods=[
            'GET'])
        def api_requests_list(verified=0):
            permohonans = PermohonanKK.query.filter(
                PermohonanKK.verified==int(verified)).all()
            return Response(json.dumps([i.serialize for i in permohonans]),
                            mimetype='application/json')

        @self.route('/api/requests/id/<id>', methods=['GET'])
        def api_requests_by_id(id):
            permohonan = PermohonanKK.query.filter_by(id_permohonan=id).first()
            return Response(json.dumps(permohonan.serialize),
                            mimetype='application/json')

        @self.route('/api/requests/set-verified/<id>/', methods=['GET'])
        def api_requests_set_verified(id):
            permohonan = PermohonanKK.query.filter(
                PermohonanKK.id_permohonan==id).first()
            permohonan.verified = True

            db.session.commit()

            return Response(json.dumps(permohonan.serialize),
                            mimetype='application/json')
