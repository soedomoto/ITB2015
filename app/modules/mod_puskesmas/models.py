from app import db


class SuratLahir(db.Model):
    __tablename__   = 'surat_lahir'

    id_surat            = db.Column(db.Integer, primary_key=True)
    no_surat            = db.Column(db.String(100), nullable=False)
    tanggal_surat       = db.Column(db.Date, nullable=False)
    penanda_tangan      = db.Column(db.String(255), nullable=True)
    nip_penanda_tangan  = db.Column(db.String(50), nullable=True)
    nama                = db.Column(db.String(255), nullable=True)
    tempat_lahir        = db.Column(db.String(255), nullable=False)
    waktu_lahir         = db.Column(db.DateTime(timezone=False),
                                    nullable=False)
    berat_badan         = db.Column(db.Float, nullable=False) #ons
    panjang_badan       = db.Column(db.Float, nullable=False) #cm
    nama_ayah           = db.Column(db.String(255), nullable=False)
    nama_ibu            = db.Column(db.String(255), nullable=False)
    persalinan_oleh     = db.Column(db.String(255), nullable=False)

    @property
    def serialize(self):
        return {
            'id_surat'              : self.id_surat,
            'no_surat'              : self.no_surat,
            'tanggal_surat'         : self.tanggal_surat.isoformat(),
            'penanda_tangan'        : self.penanda_tangan,
            'nip_penanda_tangan'    : self.nip_penanda_tangan,
            'nama'                  : self.nama,
            'tempat_lahir'          : self.tempat_lahir,
            'waktu_lahir'           : self.waktu_lahir.isoformat(),
            'berat_badan'           : self.berat_badan,
            'panjang_badan'         : self.panjang_badan,
            'nama_ayah'             : self.nama_ayah,
            'nama_ibu'              : self.nama_ibu,
            'persalinan_oleh'       : self.persalinan_oleh,
        }

class SuratSehat(db.Model):
    __tablename__   = 'surat_sehat'

    id_surat            = db.Column(db.Integer, primary_key=True)
    no_surat            = db.Column(db.String(100), nullable=False)
    tanggal_surat       = db.Column(db.Date, nullable=False)
    penanda_tangan      = db.Column(db.String(255), nullable=True)
    nip_penanda_tangan  = db.Column(db.String(50), nullable=True)
    pasien_id           = db.Column(db.String(50), nullable=True)
    keterangan_sehat    = db.Column(db.String(100), nullable=False)
    tinggi_badan        = db.Column(db.Float, nullable=False) #cm
    tekanan_darah       = db.Column(db.Integer, nullable=False)
    golongan_darah      = db.Column(db.String(5), nullable=False)

    @property
    def serialize(self):
        return {
            'id_surat'              : self.id_surat,
            'no_surat'              : self.no_surat,
            'tanggal_surat'         : self.tanggal_surat.isoformat(),
            'penanda_tangan'        : self.penanda_tangan,
            'nip_penanda_tangan'    : self.nip_penanda_tangan,
            'pasien_id'             : self.pasien_id,
            'keterangan_sehat'      : self.keterangan_sehat,
            'tinggi_badan'          : self.tinggi_badan,
            'tekanan_darah'         : self.tekanan_darah,
            'golongan_darah'        : self.golongan_darah
        }

