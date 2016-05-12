from flask import Blueprint, request, render_template, \
                  flash, g, session, redirect, url_for, \
                  jsonify, Response

import json
from app import db
from models import Laporan
        
class Lapor(Blueprint):
    def __init__(self):
        Blueprint.__init__(self, 'lapor', __name__, url_prefix='/lapor',
                           template_folder='views', static_folder='static')
        self.web()
        self.api()
    
    def web(self):
        @self.route('/list/', methods=['GET', 'POST'])
        def list():
            return render_template("list.html")
            
        @self.route('/add/', methods=['GET'])
        def add():
            return render_template("add.html")
        
        @self.route('/add/save', methods=['POST'])
        def add_save():
            form = request.form
            
            l=Laporan()
            l.tipe_laporan = form["tipe-laporan"]
            l.subjek_laporan = form["subjek-laporan"]
            l.isi_laporan = form["isi-laporan"]
            
            db.session.add(l)
            db.session.commit()
            
            
            return Response(json.dumps(l.serialize),
                            mimetype='application/json')
        
    def api(self):
        @self.route('/api/list/', methods=['GET', 'POST'])
        def api_list():
            keluargas = Keluarga.query.all()
            return jsonify(keluargas)
        
        @self.route('/api/by-kk/<kk>', methods=['GET', 'POST'])
        def api_list_kk(kk):
            keluargas = Keluarga.query.filter_by(no_kk=kk).all()
            return jsonify(keluargas)
            