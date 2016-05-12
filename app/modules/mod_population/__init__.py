from flask import Blueprint, request, render_template, \
                  flash, g, session, redirect, url_for, \
                  jsonify, Response

import json
from models import Penduduk
        
class Population(Blueprint):
    def __init__(self):
        Blueprint.__init__(self, 'population', __name__, url_prefix='/population', template_folder='templates')
        self.web()
        self.api()
    
    def web(self):
        @self.route('/list/', methods=['GET', 'POST'])
        def list():
            return jsonify({"asdf" : "eerewr"})
        
    def api(self):
        @self.route('/api/list/', methods=['GET'])
        def api_pop_list():
            populations = Penduduk.query.all()
            return Response(json.dumps([i.serialize for i in populations]), mimetype='application/json')
        
        @self.route('/api/id/<nik>', methods=['GET'])
        def api_pop_by_nik(nik):
            population = Penduduk.query.filter_by(nik=nik).first()
            return Response(json.dumps(population.serialize), mimetype='application/json')
        
        @self.route('/api/list/kk/<kk>', methods=['GET'])
        def api_pop_list_by_kk(kk):
            populations = Penduduk.query.filter_by(no_kk=kk).all()
            return Response(json.dumps([i.serialize for i in populations]), mimetype='application/json')
        
        