import datetime, json

import os
from flask import Blueprint, request, render_template, \
                  flash, g, session, redirect, url_for, \
                  jsonify, Response

from models import Service
from app import app, db
        
class CService(Blueprint):
    def __init__(self):
        '''
        Initialize module with provided template and static directory location
        '''
        
        Blueprint.__init__(self, 'service', __name__, url_prefix='/service', \
                           template_folder=os.path.join(os.path.dirname(__file__), 'views'), \
                           static_folder=os.path.join(os.path.dirname(__file__), 'static'))
        self.web()
        self.api()
    
    def web(self):
        '''
        Web GUI placeholder
        '''
        
        @self.route('/', methods=['GET'])
        def index():
            '''
            Main page GUI for ticket module
            '''
            services = Service.query.all()
            return render_template("index.html", services=services)

        @self.route('/save/', methods=['POST'])
        def save():
            '''
            Save selected service choosen by customer
            '''
            form = request.form

            ticket = Ticket()
            ticket.service          = int(form['service'])
            ticket.service_date     = datetime.datetime.now()
            ticket.ticket_time      = datetime.datetime.now()

            db.session.add(ticket)
            db.session.commit()

            return jsonify(ticket.serialize)
        
    def api(self):
        '''
        Web API placeholder
        '''
        
        @self.route('/api/list/', methods=['GET'])
        def api_service_list():
            services = Service.query.all()
            return Response(json.dumps([i.serialize for i in services]), \
                            mimetype='application/json')
        
        @self.route('/api/id/<id>/', methods=['GET'])
        def api_by_id(id):
            service = Service.query.filter_by(service_id=id).first()
            return Response(json.dumps(service.serialize if service else None), \
                            mimetype='application/json')

        @self.route('/api/set-estimated-time/<id>/', methods=['POST'])
        def api_set_estimated_time(id):
            service = Service.query.filter_by(service_id=id).first()
            service.estimated_time = float(request.form['estimated-time'])
            db.session.commit()
            return Response(json.dumps(service.serialize), mimetype='application/json')

