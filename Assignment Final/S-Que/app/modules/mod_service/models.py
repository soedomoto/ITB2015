import requests
from app import app, db

class Service(db.Model):
    __tablename__       = 'service'
    
    service_id          = db.Column(db.Integer, primary_key=True, autoincrement=True)
    service_name        = db.Column(db.String(225), nullable=False)
    estimated_time      = db.Column(db.Float, nullable=True)
    
    @property
    def serialize(self):
        """Return object data in easily serializeable format"""
        return {
            'service_id'     : self.service_id,
            'service_name'   : self.service_name,
            'estimated_time' : self.estimated_time
        }
    