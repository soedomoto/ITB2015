import uuid, time_uuid
from datetime import datetime
import requests
from app import app, db

class Ticket(db.Model):
    __tablename__       = 'ticket'
    
    ticket_id           = db.Column(db.String(60), primary_key=True, autoincrement=True)
    ticket_order        = db.Column(db.Integer, nullable=False)
    service_id          = db.Column(db.Integer, nullable=False)
    service_date        = db.Column(db.Date, nullable=False)
    ticket_time         = db.Column(db.DateTime(timezone=False), nullable=False)
    service_start_time  = db.Column(db.DateTime(timezone=False))
    service_finish_time = db.Column(db.DateTime(timezone=False))
    skipped             = db.Column(db.Boolean, default=False)
    service_counter     = db.Column(db.Integer, nullable=True)
    
    @property
    def ticket_uuid(self):
        return self.ticket_id
    
    @ticket_uuid.setter
    def ticket_uuid(self, u):
        ts = time_uuid.TimeUUID(bytes=u.bytes).get_timestamp()
        dt = datetime.fromtimestamp(ts)
        
        self.ticket_id = str(u)
        self.service_date = dt
        self.ticket_time = dt
        
        last_ticket = Ticket.query.with_entities(Ticket.ticket_order) \
                        .filter_by(service_date=dt.date()) \
                        .order_by(Ticket.ticket_order.desc()).first()
                
        self.ticket_order = last_ticket.ticket_order + 1 if last_ticket else 1
    
    @property
    def service(self):
        url = '{}/api/service/id/{}'.format(app.config.get('BASE_URL'), self.service_id)
        resp = requests.get(url)
        return resp.json() if resp else None

    @property
    def counter(self):
        url = '{}/api/counter/id/{}'.format(app.config.get('BASE_URL'), self.service_counter)
        resp = requests.get(url)
        return resp.json() if resp else None
    
    @property
    def serialize(self):
        """Return object data in easily serializeable format"""
        return {
            'ticket_id'             : self.ticket_id,
            'ticket_order'          : self.ticket_order,
            'service'               : self.service,
            'service_date'          : self.service_date.isoformat(),
            'ticket_time'           : self.ticket_time.isoformat(),
            'service_start_time'    : self.service_start_time.isoformat() if self.service_start_time else None,
            'service_finish_time'   : self.service_finish_time.isoformat() if self.service_finish_time else None,
            'skipped'               : self.skipped,
            'counter'               : self.counter
        }

class TicketArrival(db.Model):
    __tablename__       = 'ticket_arrival'

    ticket_date         = db.Column(db.Date, primary_key=True)
    service_id          = db.Column(db.Integer, primary_key=True)
    arrival_rate        = db.Column(db.Float, nullable=False)

    @property
    def serialize(self):
        """Return object data in easily serializeable format"""
        return {
            'ticket_date': self.ticket_date,
            'service_id' : self.service_id,
            'arrival_rate': self.arrival_rate
        }
    