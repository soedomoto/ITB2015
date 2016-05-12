from flask import Blueprint, request, render_template, \
                  flash, g, session, redirect, url_for, \
                  jsonify, Response

import json, requests
from app import app, db
from models import Komplain
        
class KomplainAktaLahir(Blueprint):
    def __init__(self):
        Blueprint.__init__(self, 'komplain', __name__, url_prefix='/komplain', template_folder='views', static_folder='static')
        self.web()
        self.api()
    
    def web(self):
        @self.route('/list/', methods=['GET', 'POST'])
        def list():
            url = '{}/api/komplain/list/'.format(app.config.get('BASE_URL'))
            resp = requests.get(url)
            return render_template("complaint.html", komplains=resp.json())
            
        @self.route('/add/', methods=['GET'])
        def add_complaint():
            qs = requests.args
            if qs.has_key("no-template"):
                return render_template("addComplaint.html", use_template=False)
            return render_template("addComplaint.html", use_template=True)
        
        @self.route('/add/savekomplain/', methods=['POST'])
        def add_save_complaint():
            form = request.form
            komplain = Komplain()
            komplain.id_komplain = None
            komplain.komplain = form['komentar']


    def api(self):
		@self.route('/api/list/', methods=['GET', 'POST'])
		def api_komplain_list():
			komplains = Komplain.query.all()
			return Response(json.dumps([i.serialize for i in komplains]), mimetype='application/json')
		
		@self.route('/api/getById/<id_komplain>', methods=['GET', 'POST'])
		def api_komplain_getById(id_komplain):
			komplain = Komplain.query.filter_by(id_komplain=id_komplain).first()
			return Response(json.dumps(komplain.serialize), mimetype='application/json')