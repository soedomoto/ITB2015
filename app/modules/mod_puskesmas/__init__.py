from flask import Blueprint, request, render_template, \
                  flash, g, session, redirect, url_for, \
                  jsonify, Response

import json, requests
from datetime import datetime
from app import app, db
from models import SuratLahir, SuratSehat

__author__ = 'Neti SS'

class Puskesmas(Blueprint):
    def __init__(self):
        Blueprint.__init__(self, 'puskesmas', __name__,
                           url_prefix='/puskesmas', template_folder='views',
                           static_folder='static')
        self.web()
        self.admin()
        self.api()

    def web(self):
        '''
        GUI Methods
        '''

        pass

    def admin(self):
        '''
        Admin method
        '''

        pass


    def api(self):
        '''
        API Methods
        '''

        @self.route('/api/surat-lahir/list/', methods=['GET'])
        def api_surat_lahir_list():
            surat_lahirs = SuratLahir.query.all()
            return Response(json.dumps([i.serialize for i in surat_lahirs]),
                            mimetype='application/json')

        @self.route('/api/surat-lahir/no/<no_surat>', methods=['GET'])
        def api_surat_lahir_by_no(no_surat):
            surat_lahir = SuratLahir.query.filter_by(no_surat=no_surat).first()
            return Response(json.dumps(surat_lahir.serialize if surat_lahir
                            else None), mimetype='application/json')

        @self.route('/api/surat-lahir/tanggal/<tanggal_surat>', methods=[
            'GET'])
        def api_surat_lahir_by_tanggal(tanggal_surat):
            surat_lahir = SuratLahir.query.filter_by(
                    tanggal_surat=datetime.strptime(tanggal_surat, '%Y-%m-%d')
                ).first()
            return Response(json.dumps(surat_lahir.serialize if surat_lahir
                            else None), mimetype='application/json')

        @self.route('/api/surat-sehat/list/', methods=['GET'])
        def api_surat_sehat_list():
            surat_sehats = SuratSehat.query.all()
            return Response(json.dumps([i.serialize for i in surat_sehats]),
                            mimetype='application/json')

        @self.route('/api/surat-sehat/no/<no_surat>', methods=['GET'])
        def api_surat_sehat_by_no(no_surat):
            surat_sehat = SuratSehat.query.filter_by(no_surat=no_surat).first()
            return Response(json.dumps(surat_sehat.serialize if surat_sehat
                            else None), mimetype='application/json')

        @self.route('/api/surat-sehat/tanggal/<tanggal_surat>', methods=[
            'GET'])
        def api_surat_sehat_by_tanggal(tanggal_surat):
            surat_sehat = SuratSehat.query.filter_by(
                    tanggal_surat=datetime.strptime(tanggal_surat, '%Y-%m-%d')
                ).first()
            return Response(json.dumps(surat_sehat.serialize if surat_sehat
                            else None), mimetype='application/json')
