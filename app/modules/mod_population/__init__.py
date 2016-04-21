from flask import Blueprint, request, render_template, \
                  flash, g, session, redirect, url_for, \
                  jsonify

from models import Penduduk
        
class Population(Blueprint):
    def __init__(self):
        Blueprint.__init__(self, 'population', __name__, url_prefix='/penduduk', template_folder='templates')
        self.web()
        self.api()
    
    def web(self):
        @self.route('/list/', methods=['GET', 'POST'])
        def list():
            return jsonify({"asdf" : "eerewr"})
        
    def api(self):
        @self.route('/api/list/', methods=['GET', 'POST'])
        def api_list():
            penduduks = Penduduk.query.all()
            return jsonify(penduduks)
        
        @self.route('/api/by-nik/<nik>', methods=['GET', 'POST'])
        def api_list_nik(nik):
            penduduks = Penduduk.query.filter_by(nik=nik).all()
            return jsonify(penduduks)
        
        @self.route('/api/by-kk/<kk>', methods=['GET', 'POST'])
        def api_list_kk(kk):
            penduduks = Penduduk.query.filter_by(no_kk=kk).all()
            return jsonify(penduduks)
        
        