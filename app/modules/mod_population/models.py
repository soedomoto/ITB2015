from app import db

class Penduduk(db.Model):
    __tablename__       = 'penduduk'
    
    '''
    Data di buku induk kependudukan (Formulir F1-01) :
    nik, no. kk, nama, jenkel, tmpt lahir, tgl lahir, gol darah, agama, status perkawinan, status hub keluarga, pddkn akhir, pekerjaan, penyandang cacat, nama ibu, nama ayah, nama kepala keluarga, alamat, no rt, no rw, no kel, nama kel, nama kec
    '''
    
    nik                 = db.Column(db.String(100), primary_key=True)
    no_kk               = db.Column(db.String(128), nullable=False)
    nama                = db.Column(db.String(255), nullable=False)
    jenis_kelamin       = db.Column(db.String(255), nullable=False)
    tempat_lahir        = db.Column(db.String(255), nullable=False)
    tanggal_lahir       = db.Column(db.String(255), nullable=False)
    golongan_darah      = db.Column(db.String(255), nullable=False)
    agama               = db.Column(db.String(255), nullable=False)
    status_perkawinan   = db.Column(db.String(255), nullable=False)
    hubungan_keluarga   = db.Column(db.String(255), nullable=False)
    pendidikan_akhir    = db.Column(db.String(255), nullable=False)
    pekerjaan           = db.Column(db.String(255), nullable=False)
    penyandang_cacat    = db.Column(db.String(255), nullable=False)
    nama_ibu            = db.Column(db.String(255), nullable=False)
    nama_ayah           = db.Column(db.String(255), nullable=False)
    nama_kepala_keluarga= db.Column(db.String(255), nullable=False)
    alamat              = db.Column(db.String(255), nullable=False)
    no_rt               = db.Column(db.String(255), nullable=False)
    no_rw               = db.Column(db.String(255), nullable=False)
    kelurahan           = db.Column(db.String(255), nullable=False)
    kecamatan           = db.Column(db.String(255), nullable=False)
    
    