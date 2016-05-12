from flask import Blueprint, request, render_template, \
                  flash, g, session, redirect, url_for, \
                  jsonify, Response
import json
from app import db
from models import  Propinsi, Kabupaten, Kecamatan, Kelurahan, \
                    Agama, HubunganKeluarga, StatusPerkawinan, \
                    HubunganBayi, StatusVerifikasiAktaLahir, \
                    JenisKelamin, JenisKelahiran, LevelOtorisasi
        
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
            return Response(json.dumps(subdistrict.serialize), mimetype='application/json')
        
        @self.route('/api/village/list/', methods=['GET'])
        def api_village_list():
            villages = Kelurahan.query.all()
            return Response(json.dumps([i.serialize for i in villages]), mimetype='application/json')
        
        @self.route('/api/village/list/prov/<prop_id>/regency/<regency_id>/subdistrict/<subdistrict_id>', methods=['GET'])
        def api_village_list_by_prov_regency_subdistrict(prop_id, regency_id, subdistrict_id):
            villages = Kelurahan.query.filter_by(kd_propinsi=prop_id, kd_kabupaten=regency_id, kd_kecamatan=subdistrict_id).all()
            return Response(json.dumps([i.serialize for i in villages]), mimetype='application/json')
        
        @self.route('/api/village/prov/<prop_id>/regency/<regency_id>/subdistrict/<subdistrict_id>/id/<village_id>', methods=['GET'])
        def api_village_by_id(prop_id, regency_id, subdistrict_id, village_id):
            village = Kelurahan.query.filter_by(kd_propinsi=prop_id, kd_kabupaten=regency_id, kd_kecamatan=subdistrict_id, kd_kelurahan=village_id).first()
            return Response(json.dumps(village.serialize), mimetype='application/json')
        
        @self.route('/api/religion/list/', methods=['GET'])
        def api_religion_list():
            religions = Agama.query.all()
            return Response(json.dumps([i.serialize for i in religions]), mimetype='application/json')
        
        @self.route('/api/religion/id/<kd_agama>', methods=['GET'])
        def api_religion_by_id(kd_agama):
            religion = Agama.query.filter_by(kd_agama=kd_agama).first()
            return Response(json.dumps(religion.serialize), mimetype='application/json')
        
        @self.route('/api/relationship/list/', methods=['GET'])
        def api_relationship_list():
            relationships = HubunganKeluarga.query.all()
            return Response(json.dumps([i.serialize for i in relationships]), mimetype='application/json')
        
        @self.route('/api/relationship/id/<kd_hubungan_art>', methods=['GET'])
        def api_relationship_by_id(kd_hubungan_art):
            relationship = HubunganKeluarga.query.filter_by(kd_hubungan_keluarga=kd_hubungan_art).first()
            return Response(json.dumps(relationship.serialize), mimetype='application/json')
        
        @self.route('/api/marital/list/', methods=['GET'])
        def api_marital_list():
            maritals = StatusPerkawinan.query.all()
            return Response(json.dumps([i.serialize for i in maritals]), mimetype='application/json')
        
        @self.route('/api/marital/id/<kd_status_kawin>', methods=['GET'])
        def api_marital_by_id(kd_status_kawin):
            marital = StatusPerkawinan.query.filter_by(kd_status_perkawinan=kd_status_kawin).first()
            return Response(json.dumps(marital.serialize), mimetype='application/json')
        
        @self.route('/api/babyrelation/list/', methods=['GET'])
        def api_baby_relation_list():
            baby_relations = HubunganBayi.query.all()
            return Response(json.dumps([i.serialize for i in baby_relations]), mimetype='application/json')
        
        @self.route('/api/babyrelation/id/<kd_hubungan_bayi>', methods=['GET'])
        def api_baby_relation_by_id(kd_hubungan_bayi):
            baby_relation = HubunganBayi.query.filter_by(kd_hubungan_bayi=kd_hubungan_bayi).first()
            return Response(json.dumps(baby_relation.serialize), mimetype='application/json')
        
        @self.route('/api/verifikasi/list/', methods=['GET'])
        def api_verifikasi_akta_list():
            verifikasi_aktas = StatusVerifikasiAktaLahir.query.all()
            return Response(json.dumps([i.serialize for i in verifikasi_aktas]), mimetype='application/json')
        
        @self.route('/api/verifikasi/id/<kd_verifikasi_akta>', methods=['GET'])
        def api_verifikasi_akta_by_id(kd_verifikasi_akta):
            verifikasi_akta = StatusVerifikasiAktaLahir.query.filter_by(kd_verifikasi_akta=kd_verifikasi_akta).first()
            return Response(json.dumps(verifikasi_akta.serialize), mimetype='application/json')
        
        @self.route('/api/jeniskelamin/list/', methods=['GET'])
        def api_jns_kelamin_list():
            jns_kelamins = JenisKelamin.query.all()
            return Response(json.dumps([i.serialize for i in jns_kelamins]), mimetype='application/json')
        
        @self.route('/api/jeniskelamin/id/<kd_jns_kelamin>', methods=['GET'])
        def api_jns_kelamin_by_id(kd_jns_kelamin):
            jns_kelamin = JenisKelamin.query.filter_by(kd_jns_kelamin=kd_jns_kelamin).first()
            return Response(json.dumps(jns_kelamin.serialize), mimetype='application/json')
        
        @self.route('/api/jeniskelahiran/list/', methods=['GET'])
        def api_jns_kelahiran_list():
            jns_kelahirans = JenisKelahiran.query.all()
            return Response(json.dumps([i.serialize for i in jns_kelahirans]), mimetype='application/json')
        
        @self.route('/api/jeniskelahiran/id/<kd_jns_kelahiran>', methods=['GET'])
        def api_jns_kelahiran_by_id(kd_jns_kelahiran):
            jns_kelahiran = JenisKelahiran.query.filter_by(kd_jns_kelahiran=kd_jns_kelahiran).first()
            return Response(json.dumps(jns_kelahiran.serialize), mimetype='application/json')

        @self.route('/api/levelotorisasi/list/', methods=['GET'])
        def api_level_otorisasi_list():
            level_otorisasis = LevelOtorisasi.query.all()
            return Response(json.dumps([i.serialize for i in level_otorisasis]), mimetype='application/json')

        @self.route('/api/levelotorisasi/id/<kd_level_otorisasi>/', methods=['GET'])
        def api_level_otorisasi_by_id(kd_level_otorisasi):
            level_otorisasi = LevelOtorisasi.query.filter_by(kd_level_otorisasi=kd_level_otorisasi).first()
            return Response(json.dumps(level_otorisasi.serialize), mimetype='application/json')