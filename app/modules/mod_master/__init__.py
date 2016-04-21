from flask import Blueprint, request, render_template, \
                  flash, g, session, redirect, url_for, \
                  jsonify, Response
import json
from app import db
from models import Propinsi, Kabupaten, Kecamatan, Kelurahan
        
class Master(Blueprint):
    def __init__(self):
        Blueprint.__init__(self, 'master', __name__, url_prefix='/master', template_folder='views', static_folder='static')
        self.web()
        self.api()
    
    def web(self):
        pass
        
    def api(self):
        @self.route('/api/province/list/', methods=['GET'])
        def api_prov_list():
            provinces = Propinsi.query.all()
            return Response(json.dumps([i.serialize for i in provinces]), mimetype='application/json')
        
        @self.route('/api/province/id/<prop_id>', methods=['GET'])
        def api_prov_by_id(prop_id):
            province = Propinsi.query.filter_by(kd_propinsi=prop_id).first()
            return Response(json.dumps(province.serialize), mimetype='application/json')
        
        @self.route('/api/regency/list/', methods=['GET'])
        def api_regency_list():
            regencies = Kabupaten.query.all()
            return Response(json.dumps([i.serialize for i in regencies]), mimetype='application/json')
        
        @self.route('/api/regency/list/prov/<prop_id>', methods=['GET'])
        def api_regency_list_by_prov_id(prop_id):
            regencies = Kabupaten.query.filter_by(kd_propinsi=prop_id).all()
            return Response(json.dumps([i.serialize for i in regencies]), mimetype='application/json')
        
        @self.route('/api/regency/prov/<prop_id>/id/<regency_id>', methods=['GET'])
        def api_regency_by_id(prop_id, regency_id):
            regency = Kabupaten.query.filter_by(kd_propinsi=prop_id, kd_kabupaten=regency_id).first()
            return Response(json.dumps(regency.serialize), mimetype='application/json')
        
        @self.route('/api/subdistrict/list/', methods=['GET'])
        def api_subdistrict_list():
            subdistricts = Kecamatan.query.all()
            return Response(json.dumps([i.serialize for i in subdistricts]), mimetype='application/json')
        
        @self.route('/api/subdistrict/list/prov/<prop_id>/regency/<regency_id>', methods=['GET'])
        def api_subdistrict_list_by_prov_regency(prop_id, regency_id):
            subdistricts = Kecamatan.query.filter_by(kd_propinsi=prop_id, kd_kabupaten=regency_id).all()
            return Response(json.dumps([i.serialize for i in subdistricts]), mimetype='application/json')
        
        @self.route('/api/subdistrict/prov/<prop_id>/regency/<regency_id>/id/<subdistrict_id>', methods=['GET'])
        def api_subdistrict_by_id(prop_id, regency_id, subdistrict_id):
            subdistrict = Kecamatan.query.filter_by(kd_propinsi=prop_id, kd_kabupaten=regency_id, kd_kecamatan=subdistrict_id).first()
            return Response(json.dumps(subdistrict), mimetype='application/json')
            