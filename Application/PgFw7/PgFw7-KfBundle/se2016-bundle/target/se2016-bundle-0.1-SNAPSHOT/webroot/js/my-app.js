if(location.protocol == 'file:') {
    var ctxHost = 'http://localhost:8080/se2016';
} else {
    ctxHost = location.protocol + '//' + location.host + '/se2016';
}

if(! $$) var $$ = Dom7;
if(! myApp) {
    var myApp = new Framework7();
    var mainView = myApp.addView('.view-main');
}

$$.get(ctxHost + '/form-l1.html', function(page) {
    mainView.router.loadContent(page);
    listPropinsi();
});

var data = {
    'v101' : null, 'v102' : null, 'v103' : null, 'v104' : null,
}



$(document).on('change', 'select[name="v101"]', function() {
    data['v101'] = $(this).find('option:selected').val();
    getKabupatenByPropinsi();
});

$(document).on('change', 'select[name="v102"]', function() {
    data['v102'] = $(this).find('option:selected').val();
    getKecamatanByKabupaten();
});

$(document).on('change', 'select[name="v103"]', function() {
    data['v103'] = $(this).find('option:selected').val();
    getKelurahanByKecamatan();
});

$(document).on('change', 'select[name="v104"]', function() {
    data['v104'] = $(this).find('option:selected').val();
    getBlokSensusByKelurahan();
});

$(document).on('click', '.b5-add-item', function() {
    mainView.router.loadPage(ctxHost + '/form-b5-add-item.html');

    $(document).on('click', '.reset-b5-add-item', function() {
        myApp.formDeleteData('form-b5-add-item');
    });

    $(document).on('click', '.submit-b5-add-item', function() {
        var b5Data = myApp.formGetData('form-b5-add-item');

        if($('#b5-'+ b5Data['v501'] + b5Data['v502'] + b5Data['v503'] + b5Data['v505']).length > 0) {
            $('#b5-'+ b5Data['v501'] + b5Data['v502'] + b5Data['v503'] + b5Data['v505'] + ' input[name="v501"]').val(b5Data['v501']);
            $('#b5-'+ b5Data['v501'] + b5Data['v502'] + b5Data['v503'] + b5Data['v505'] + ' input[name="v502"]').val(b5Data['v502']);
            $('#b5-'+ b5Data['v501'] + b5Data['v502'] + b5Data['v503'] + b5Data['v505'] + ' input[name="v503"]').val(b5Data['v503']);
            $('#b5-'+ b5Data['v501'] + b5Data['v502'] + b5Data['v503'] + b5Data['v505'] + ' input[name="v504"]').val(b5Data['v504']);
            $('#b5-'+ b5Data['v501'] + b5Data['v502'] + b5Data['v503'] + b5Data['v505'] + ' input[name="v505"]').val(b5Data['v505']);
            $('#b5-'+ b5Data['v501'] + b5Data['v502'] + b5Data['v503'] + b5Data['v505'] + ' input[name="v506"]').val(b5Data['v506']);
            $('#b5-'+ b5Data['v501'] + b5Data['v502'] + b5Data['v503'] + b5Data['v505'] + ' input[name="v507"]').val(b5Data['v507']);
            $('#b5-'+ b5Data['v501'] + b5Data['v502'] + b5Data['v503'] + b5Data['v505'] + ' input[name="v508"]').val(b5Data['v508']);
            $('#b5-'+ b5Data['v501'] + b5Data['v502'] + b5Data['v503'] + b5Data['v505'] + ' input[name="v509"]').val(b5Data['v509']);
            $('#b5-'+ b5Data['v501'] + b5Data['v502'] + b5Data['v503'] + b5Data['v505'] + ' input[name="v510"]').val(b5Data['v510']);
        } else {
            $('<li class="item-content" id="b5-'+ b5Data['v501'] + b5Data['v502'] + b5Data['v503'] + b5Data['v505'] +'"><div class="item-inner">'+
                '<div class="item-input"><input type="text" readonly name="v501" placeholder="No. Segmen" value="'+ b5Data['v501'] +'" /></div>'+
                '<div class="item-input"><input type="text" readonly name="v502" placeholder="No. Bangunan Fisik" value="'+ b5Data['v502'] +'" /></div>'+
                '<div class="item-input"><input type="text" readonly name="v503" placeholder="No. Bangunan Sensus" value="'+ b5Data['v503'] +'" /></div>'+
                '<input type="hidden" readonly name="v504" placeholder="Kode Penggunaan Bangunan Sensus" value="'+ b5Data['v504'] +'" />'+
                '<div class="item-input"><input type="text" readonly name="v505" placeholder="No. Urut Rumah Tangga" value="'+ b5Data['v505'] +'" /></div>'+
                '<div class="item-input"><input type="text" readonly name="v506" placeholder="Nama Kepala Rumah Tangga" value="'+ b5Data['v506'] +'" /></div>'+
                '<input type="hidden" name="v507" readonly placeholder="Jumlah Usaha Rumah Tangga" value="'+ b5Data['v507'] +'" />'+
                '<input type="hidden" name="v508" readonly placeholder="No. Urut Usaha/Perusahaan" value="'+ b5Data['v508'] +'" />'+
                '<input type="hidden" name="v509" readonly placeholder="Nama Usaha/Perusahaan/Pemilik Usaha/Bangunan" value="'+ b5Data['v509'] +'" />'+
                '<input type="hidden" name="v510" readonly placeholder="Lokasi Usaha Rumah Tangga" value="'+ b5Data['v510'] +'" />'+
            '</div></li>').appendTo($('#b5-list'));
        }

        myApp.formDeleteData('form-b5-add-item');
    });
});

function listPropinsi() {
    $$.getJSON(ctxHost + '/propinsi', function (props) {
        $('select[name="v101"] option').remove();

        myApp.smartSelectAddOption('select[name="v101"]', '<option>Pilih</option>');
        props.forEach(function(prop) {
            myApp.smartSelectAddOption('select[name="v101"]', '<option value="'+ prop.kode +'">'+ prop.nama +'</option>');
        });
    });
}

function getKabupatenByPropinsi() {
    $$.getJSON(ctxHost + '/kabupaten?propinsi=' + data['v101'], function (kabs) {
        $('select[name="v102"] option').remove();

        myApp.smartSelectAddOption('select[name="v102"]', '<option>Pilih</option>');
        kabs.forEach(function(kab) {
            myApp.smartSelectAddOption('select[name="v102"]', '<option value="'+ kab.kode +'">'+ kab.nama +'</option>');
        });
    });
}

function getKecamatanByKabupaten() {
    $$.getJSON(ctxHost + '/kecamatan?propinsi=' + data['v101'] + '&kabupaten=' + data['v102'], function (kecs) {
        $('select[name="v103"] option').remove();

        myApp.smartSelectAddOption('select[name="v103"]', '<option>Pilih</option>');
        kecs.forEach(function(kec) {
            myApp.smartSelectAddOption('select[name="v103"]', '<option value="'+ kec.kode +'">'+ kec.nama +'</option>');
        });
    });
}

function getKelurahanByKecamatan() {
    $$.getJSON(ctxHost + '/kelurahan?propinsi=' + data['v101'] + '&kabupaten=' + data['v102'] +
    '&kecamatan=' + data['v103'], function (kels) {
        $('select[name="v104"] option').remove();

        myApp.smartSelectAddOption('select[name="v104"]', '<option>Pilih</option>');
        kels.forEach(function(kel) {
            myApp.smartSelectAddOption('select[name="v104"]', '<option value="'+ kel.kode +'">'+ kel.nama +'</option>');
        });
    });
}

function getBlokSensusByKelurahan() {
    $$.getJSON(ctxHost + '/bloksensus?propinsi=' + data['v101'] + '&kabupaten=' + data['v102'] +
    '&kecamatan=' + data['v103'] + '&kelurahan=' + data['v104'], function (bss) {
        $('select[name="v105"] option').remove();

        myApp.smartSelectAddOption('select[name="v105"]', '<option>Pilih</option>');
        bss.forEach(function(bs) {
            myApp.smartSelectAddOption('select[name="v105"]', '<option value="'+ bs.kode +'">'+ bs.nama +'</option>');
        });
    });
}
