import requests
from app import app, db

class Counter(db.Model):
    __tablename__       = 'counter'
    
    counter_id          = db.Column(db.Integer, primary_key=True, autoincrement=True)
    officer             = db.Column(db.String(255), nullable=False)
    
    @property
    def serialize(self):
        """Return object data in easily serializeable format"""
        return {
            'counter_id'            : self.counter_id,
            'officer'               : self.officer
        }