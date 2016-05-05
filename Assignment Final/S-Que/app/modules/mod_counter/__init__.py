import json
import requests

import os
from app import app
from flask import Blueprint, render_template, \
    Response
from models import Counter


class CCounter(Blueprint):
    def __init__(self):
        '''
        Initialize module with provided template and static directory location
        '''

        Blueprint.__init__(self, 'counter', __name__, url_prefix='/counter', \
                           template_folder=os.path.join(os.path.dirname(__file__), 'views'), \
                           static_folder=os.path.join(os.path.dirname(__file__), 'static'))
        self.web()
        self.api()

    def before_request(self, f):
        print(self.template_folder)
    
    def web(self):
        '''
        Web GUI placeholder
        '''
        
        @self.route('/', methods=['GET'])
        def index():
            '''
            Main page GUI for counter module
            '''
            url = '{}/api/counter/list/'.format(app.config.get('BASE_URL'))
            resp = requests.get(url)
            return render_template("index.html", counters=resp.json())

        @self.route('/id/<id>', methods=['GET'])
        def counter(id):
            '''
            Counter Page
            '''
            url = '{}/api/counter/id/{}'.format(app.config.get('BASE_URL'), id)
            resp = requests.get(url)
            return render_template("counter.html", counter=resp.json())
        
    def api(self):
        '''
        Web API placeholder
        '''
        
        @self.route('/api/list', methods=['GET'])
        def api_list():
            counters = Counter.query.all()
            return Response(json.dumps([i.serialize for i in counters]), \
                            mimetype='application/json')
        
        @self.route('/api/id/<id>', methods=['GET'])
        def api_by_id(id):
            counter = Counter.query.filter_by(counter_id=id).first()
            return Response(json.dumps(counter.serialize if counter else None), \
                            mimetype='application/json')

        @self.route('/api/list/service/<service_id>', methods=['GET'])
        def api_list_by_service(service_id):
            counters = Counter.query.filter_by(service_id=service_id).all()
            return Response(json.dumps([i.serialize for i in counters]), \
                            mimetype='application/json')
            