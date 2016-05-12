from app import db
from sqlalchemy import ForeignKey

class Laporan(db.Model):
    __tablename__ = 'laporan'
    
    id_laporan = db.Column(db.Integer, primary_key=True)
    tipe_laporan = db.Column(db.String(100), nullable=False)
    subjek_laporan = db.Column(db.String(100), nullable=False)
    isi_laporan= db.Column(db.String(100), nullable=False)
 
    @property
    def serialize(self):
        """Return object data in easily serializeable format"""
        
        return {
            'id_laporan'         : self.id_laporan,
            'tipe_laporan'        : self.tipe_laporan, 
            'subjek_laporan'         : self.subjek_laporan, 
            'isi_laporan'         : self.isi_laporan
            
        }
        