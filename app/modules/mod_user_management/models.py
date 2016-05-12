import requests, json
from app import app, db
from sqlalchemy import ForeignKey
    
class UserManagementRequestor(db.Model):
    __tablename__   = 'user_management_requestor'

    '''
    nik, password, email, no hp
    '''

    nik					= db.Column(db.String(100), primary_key=True)
    password			= db.Column(db.String(8), nullable=False)
    email				= db.Column(db.String(128))
    no_hp				= db.Column(db.String(128))
    
    @property
    def obj_populasi(self):
        url = '{}/api/population/id/{}/'.format(app.config.get('BASE_URL'), self.nik)
        resp = requests.get(url)
        return resp.json()
    
    @property
    def serialize(self):
        """Return object data in easily serializeable format"""
        return{
            'nik'			    : self.obj_populasi,
            'password'			: self.password,
            'email'				: self.email,
            'no_hp'				: self.no_hp
        }

class UserManagementAdmin(db.Model):
    __tablename__ = 'user_management_admin'
	
    '''
    username, password, level otorisasi
    '''
	
    username			= db.Column(db.String(100), primary_key=True)
    password			= db.Column(db.String(8), nullable=False)
    level				= db.Column(db.Integer, nullable=False)
    
    @property
    def obj_level(self):
        url = '{}/master/api/levelotorisasi/id/{}/'.format(app.config.get('BASE_URL'), self.level)
        resp = requests.get(url)
        return resp.json()
    
    @property
    def serialize(self):
        """Return object data in easily serializeable format"""
        return{
            'username'		    : self.username,
			'password'			: self.password,
			'level'				: self.obj_level
        }