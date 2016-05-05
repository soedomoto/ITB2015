import datetime
import json
import logging
import math
import requests
import sys
import time
import uuid

import os
from app import app, db, cron
from flask import Blueprint, request, render_template, \
    Response
from models import Ticket, TicketArrival
from sqlalchemy import and_
root = logging.getLogger()
root.setLevel(logging.DEBUG)

ch = logging.StreamHandler(sys.stdout)
ch.setLevel(logging.ERROR)
formatter = logging.Formatter('%(asctime)s - %(name)s - %(levelname)s - %(message)s')
ch.setFormatter(formatter)
root.addHandler(ch)


@cron.scheduled_job('interval', id='calculate_average_service_time', \
                    minutes=app.config.get('SERVICE_TIME_CHECK_INTERVAL'))
def calculate_average_service_time():
    url = '{}/api/service/list/'.format(app.config.get('BASE_URL'))
    resp = requests.get(url)
    for service in resp.json():
        tickets = Ticket.query.filter(and_(
            Ticket.service_start_time != None, \
            Ticket.service_finish_time != None, \
            Ticket.service_id == service['service_id']
        )).all()

        delta_services = []
        for ticket in tickets:
            delta = ticket.service_finish_time - ticket.service_start_time
            delta_services.append(delta.seconds)

        avg = sum(delta_services) / float(len(delta_services))
        avg_url = '{}/api/service/set-estimated-time/{}/'.format(app.config.get('BASE_URL'), \
                                                             service['service_id'])
        requests.post(avg_url, data={'estimated-time': avg})


@cron.scheduled_job('interval', id='calculate_average_arrival_time', \
                    minutes=app.config.get('ARRIVAL_TIME_CHECK_INTERVAL'))
def calculate_average_arrival_time():
    url = '{}/api/service/list/'.format(app.config.get('BASE_URL'))
    resp = requests.get(url)
    for service in resp.json():
        ticket_service_dates = Ticket.query.distinct(Ticket.service_date)\
            .filter(Ticket.service_id == int(service['service_id']))\
            .order_by(Ticket.service_date).all()

        for ticket_service_date in ticket_service_dates:
            date = ticket_service_date.service_date

            tickets = Ticket.query.filter(and_(
                Ticket.ticket_time != None, \
                Ticket.service_id == int(service['service_id']), \
                Ticket.service_date == date\
            )).order_by(Ticket.ticket_time.asc()).all()

            delta_arrivals = []
            for t in range(len(tickets)):
                if t == 0:
                    pass

                delta = tickets[t].ticket_time - tickets[t-1].ticket_time
                delta_arrivals.append(delta.seconds)

            avg = sum(delta_arrivals) / float(len(delta_arrivals))
            arrival = TicketArrival.query.filter(and_(\
                    TicketArrival.ticket_date == date, \
                    TicketArrival.service_id == int(service['service_id'])\
                )).first()
            if arrival:
                arrival.arrival_rate = avg
                db.session.commit()
            else:
                arrival = TicketArrival()
                arrival.ticket_date = date
                arrival.service_id = int(service['service_id'])
                arrival.arrival_rate = avg

                db.session.add(arrival)
                db.session.commit()

@cron.scheduled_job('interval', id='calculate_service_time_prediction', \
                    minutes=app.config.get('SERVICE_TIME_PREDICTION_CHECK_INTERVAL'))
def calculate_service_time_prediction():
    date = datetime.datetime.now().date()
    url = '{}/api/service/list/'.format(app.config.get('BASE_URL'))
    resp = requests.get(url)
    for service in resp.json:
        # Get service channel
        resp = requests.get('{}/api/counter/list/service/{}'.format(app.config.get('BASE_URL'), \
                                                                    service['service_id']))
        counters = resp.json()
        c = len(counters)
        miu = service['estimated_time']

        # Get arrival rate
        arrival = TicketArrival.query.filter(and_( \
                TicketArrival.ticket_date == date, \
                TicketArrival.service_id == int(service['service_id'])\
            )).first()
        if arrival:
            lambd = arrival.arrival_rate
            rho = lambd / float(miu)
            # The probability of having zero vehicles in the systems
            p0 = pow(sum([((pow(rho, n)/math.factorial(n)) + \
                           (pow(rho, c)/(math.factorial(c)*1-rho/c))) \
                    for n in range(c)]), -1)
            # Expected average queue length
            em = p0 * (pow(rho, c+1)/(c*math.factorial(c))) * (1/pow((1-rho/c),2))
            # Expected average number in the systems
            en = em + rho
            # Expected average total time
            ev = en / lambd
            # Expected average waiting time
            ew = ev + 1/miu


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
            