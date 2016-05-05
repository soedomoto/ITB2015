import logging
import sys
import datetime
import math
import uuid

import requests
from SimPy.Simulation import Simulation,Process,Resource,hold,request,release
from random import expovariate, seed, randint, randrange
from app import app, cron, db
from models import Ticket, TicketArrival
from sqlalchemy import and_

root = logging.getLogger()
root.setLevel(logging.DEBUG)

ch = logging.StreamHandler(sys.stdout)
ch.setLevel(logging.DEBUG)
formatter = logging.Formatter('%(asctime)s - %(name)s - %(levelname)s - %(message)s')
ch.setFormatter(formatter)
root.addHandler(ch)

'''
Simulate customer using simpy ------------------------------------------------------------------
'''
#@cron.scheduled_job(None, id='create_simulation_data', \
#                    next_run_time=datetime.datetime.now() + datetime.timedelta(minutes=1))
def create_simulation_data():
    ## Model components ---------------------------------------------------------------
    class Source(Process):
        """ Source generates customers randomly """

        def generate(self,number, start):
            for i in range(number):
                c = Customer(name = "Customer%02d"%(i,),sim=self.sim)
                self.sim.activate(c,c.visit(b=self.sim.k, start=start))

                # customer arrive at random time between 2 and 10
                t = expovariate(1.0/randint(2,10))
                yield hold,self,t

    class Customer(Process):
        """ Customer arrives, is served and leaves """

        def generate_uuid(self, dt):
            nanoseconds = int(((dt - datetime.datetime.utcfromtimestamp(0)).total_seconds()) * 1e9)
            timestamp = int(nanoseconds//100) + 0x01b21dd213814000L
            clock_seq = randrange(1<<14L)

            time_low = timestamp & 0xffffffffL
            time_mid = (timestamp >> 32L) & 0xffffL
            time_hi_version = (timestamp >> 48L) & 0x0fffL
            clock_seq_low = clock_seq & 0xffL
            clock_seq_hi_variant = (clock_seq >> 8L) & 0x3fL
            node = uuid.getnode()

            return uuid.UUID(fields=(time_low, time_mid, time_hi_version,
                                     clock_seq_hi_variant, clock_seq_low, node), version=1)

        def visit(self,b, start):
            arrive = self.sim.now()     # minutes from start
            arrive_time = start + datetime.timedelta(minutes=arrive)

            # Save into database
            ticket = Ticket()
            ticket.service_id       = randint(1,3)
            ticket.ticket_uuid      = self.generate_uuid(arrive_time)
            db.session.add(ticket)
            db.session.commit()

            print("%s %s: Here I am     "%(arrive_time.isoformat(), self.name))
            yield request,self,b
            wait = self.sim.now()-arrive
            print("%8.4f %s: Waited %6.3f"%(self.sim.now(),self.name,wait))
            tib = expovariate(1.0/timeInBank)
            yield hold,self,tib
            yield release,self,b
            print("%8.4f %s: Finished      "%(self.sim.now(),self.name))

    class BankModel(Simulation):
        def run(self,aseed):
            self.initialize()
            seed(aseed)
            self.k = Resource(capacity=Nc,name="Counter",unitName="Clerk", sim=self)
            s = Source('Source',sim=self)
            self.activate(s, s.generate(number=maxNumber, start=datetime.datetime(\
                                        hour=8, minute=0, second=0, year=2016, month=5, day=5)),\
                          at=0.0)
            self.simulate(until=maxTime)

    print("Starting simulation -------------------------------------------------------------")
    maxNumber = 1000   # of customers
    maxTime = 8 * 60.0 # minutes
    timeInBank = 12.0 # mean, minutes
    Nc = 2          # of clerks/counters
    seedVal = 12345

    ## Experiment ------------------------------
    BankModel().run(aseed=seedVal)

'''
End of simulation --------------------------------------------------------------------------------
'''

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
        resp = requests.get('{}/api/counter/list/service-group/{}'.format(\
                app.config.get('BASE_URL'), service['service_group']))
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
                           (pow(rho, c)/(math.factorial(c)*(1-rho/c)))) \
                    for n in range(c)]), -1)
            # Expected average queue length
            em = p0 * (pow(rho, c+1)/(c*math.factorial(c))) * (1/pow((1-rho/c),2))
            # Expected average number in the systems
            en = em + rho
            # Expected average total time
            ev = en / lambd
            # Expected average waiting time
            ew = ev + 1/miu

            waiting_url = '{}/api/service/set-waiting-time/{}/'.format(app.config.get('BASE_URL'), \
                                                             service['service_id'])
            requests.post(waiting_url, data={'waiting-time': ew})