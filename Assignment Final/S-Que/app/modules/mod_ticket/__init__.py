import datetime
import json
import time
import uuid
import os
import requests
import ticket_cron
from app import app, db
from flask import Blueprint, request, render_template, \
    Response
from models import Ticket, TicketArrival
from sqlalchemy import and_


class CTicket(Blueprint):
    def __init__(self):
        '''
        Initialize module with provided template and static directory location
        '''
        
        Blueprint.__init__(self, 'ticket', __name__, url_prefix='/ticket', \
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
            url = '{}/api/service/list/'.format(app.config.get('BASE_URL'))
            resp = requests.get(url)
            return render_template("index.html", services=resp.json())
        
        @self.route('/save/', methods=['POST'])
        def save():
            '''
            Save selected service choosen by customer
            '''
            form = request.form
            
            ticket = Ticket()
            ticket.service_id       = int(form['service'])
            ticket.ticket_uuid      = uuid.uuid1()
            
            db.session.add(ticket)
            db.session.commit()

            # Get last number
            next_ticket = Ticket.query \
                .filter(and_(\
                    Ticket.service_date == datetime.datetime.now().date(), \
                    Ticket.service_start_time != None
                )).order_by(Ticket.ticket_order.desc()).first()

            json_ticket = ticket.serialize
            json_ticket['last_ticket'] = next_ticket.ticket_order if next_ticket else None
            json_ticket['est_service_time'] = time.mktime(\
                datetime.datetime.now().timetuple())

            return Response(json.dumps(json_ticket), mimetype='application/json')
    def api(self):
        '''
        Web API placeholder
        '''
        
        @self.route('/api/list/', methods=['GET'])
        def api_list():
            tickets = Ticket.query.all()
            return Response(json.dumps([i.serialize for i in tickets]), \
                            mimetype='application/json')
        
        @self.route('/api/id/<id>/', methods=['GET'])
        def api_by_id(id):
            ticket = Ticket.query.filter_by(ticket_id=id).first()
            return Response(json.dumps(ticket.serialize), mimetype='application/json')

        @self.route('/api/next/<counter>', methods=['GET'])
        def api_next_ticket(counter):
            next_ticket = Ticket.query \
                        .filter_by(service_date=datetime.datetime.now().date(), \
                                   service_start_time=None) \
                        .order_by(Ticket.ticket_order.asc()).first()

            if next_ticket:
                next_ticket.service_counter = int(counter)
                next_ticket.service_start_time = datetime.datetime.now()
                db.session.commit()

            return Response(json.dumps(next_ticket.serialize if next_ticket else None), \
                            mimetype='application/json')

        @self.route('/api/unresolved/<counter>', methods=['GET'])
        def api_unresolved_ticket(counter):
            unresolved_ticket = Ticket.query.filter(and_(\
                Ticket.service_start_time!=None, \
                Ticket.service_finish_time==None, \
                Ticket.service_date==datetime.datetime.now().date(), \
                Ticket.service_counter==int(counter))).first()

            return Response(json.dumps(unresolved_ticket.serialize if unresolved_ticket \
                                           else None), mimetype='application/json')

        @self.route('/api/stop/<id>', methods=['GET'])
        def api_stop_ticket(id):
            finished_ticket = Ticket.query.filter_by(ticket_id=id).first()

            if finished_ticket:
                finished_ticket.service_finish_time = datetime.datetime.now()
                db.session.commit()

            return Response(json.dumps({'success' : True}), mimetype='application/json')
            