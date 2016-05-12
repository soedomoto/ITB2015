from flask import Blueprint, request, render_template, \
                  flash, g, session, redirect, url_for, \
                  jsonify, Response

import json
from app import db
from models import UserManagementRequestor, UserManagementAdmin
        
class ManajemenUser(Blueprint):
    def __init__(self):
        Blueprint.__init__(self, 'manajemen_user', __name__, url_prefix='/user', template_folder='views', static_folder='static')
        self.web()
        self.api()
    
    def web(self):
        @self.route('/list/', methods=['GET', 'POST'])
        def list():
            return render_template("list.html")
            
        @self.route('/add/', methods=['GET'])
        def add():
            return render_template("add.html")


    def api(self):
		@self.route('/api/requestor/list/', methods=['GET', 'POST'])
		def api_userRequestor_list():
			users = UserManagementRequestor.query.all()
			return Response(json.dumps([i.serialize for i in users]), mimetype='application/json')
		
		@self.route('/api/requestor/getById/<nik>', methods=['GET', 'POST'])
		def api_userRequestor_getById(nik):
			user = UserManagementRequestor.query.filter_by(nik=nik).first()
			return Response(json.dumps(user.serialize), mimetype='application/json')
		
		@self.route('/api/admin/list/', methods=['GET', 'POST'])
		def api_userAdmin_list():
			userAdmins = UserManagementAdmin.query.all()
			return Response(json.dumps([i.serialize for i in userAdmins]), mimetype='application/json')
		
		@self.route('/api/admin/getById/<username>', methods=['GET', 'POST'])
		def api_userAdmin_getById(username):
			user = UserManagementAdmin.query.filter_by(username=username).first()
			return Response(json.dumps(user.serialize), mimetype='application/json')