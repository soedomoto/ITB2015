import requests, json
from app import app, db
from sqlalchemy import ForeignKey
    
class Komplain(db.Model):
	__tablename__   = 'komplain'
	
	'''
	id komplain, nik, komplain, respon
	'''
	
	id_komplain = db.Column(db.Integer, primary_key=True, autoincrement=True)
	nik = db.Column(db.String(100), nullable=False)
	komplain = db.Column(db.Text, nullable=False)
	respon = db.Column(db.Text)
	
	@property
	def obj_populasi(self):
		url = '{}/api/population/id/{}/'.format(app.config.get('BASE_URL'), self.nik)
		resp = requests.get(url)
		return resp.json()
	
	@property
	def serialize(self):
		"""Return object data in easily serializeable format"""
		return{
			'id_komplain'	: self.id_komplain,
			'nik_komplain'	: self.obj_populasi,
			'komplain'		: self.komplain,
			'respon'		: self.respon
		}