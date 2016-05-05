import requests
from app import app, db

class Counter(db.Model):
    __tablename__       = 'counter'
    
    counter_id          = db.Column(db.Integer, primary_key=True, autoincrement=True)
    officer             = db.Column(db.String(255), nullable=False)
    service_id          = db.Column(db.Integer, nullable=False)

    @property
    def service(self):
        url = '{}/api/service/id/{}'.format(app.config.get('BASE_URL'), self.service_id)
        resp = requests.get(url)
        return resp.json() if resp else None
    
    @property
    def serialize(self):
        """Return object data in easily serializeable format"""
        return {
            'counter_id'            : self.counter_id,
            'officer'               : self.officer,
            'service'               : self.service
        }