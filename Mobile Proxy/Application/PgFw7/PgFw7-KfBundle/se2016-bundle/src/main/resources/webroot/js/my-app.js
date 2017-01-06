if(location.protocol == 'file:') {
    var ctxHost = 'http://localhost:5555/se2016';
} else {
    ctxHost = location.protocol + '//' + location.host + '/se2016';
}

if($) {
    $.fn.serializeObject = function() {
        var o = {};
        var a = this.serializeArray();
        $.each(a, function() {
            if (o[this.name] !== undefined) {
                if (!o[this.name].push) {
                    o[this.name] = [o[this.name]];
                }
                o[this.name].push(this.value || '');
            } else {
                o[this.name] = this.value || '';
            }
        });
        return o;
    };
}
if(! $$) var $$ = Dom7;
if(! myApp) {
    var myApp = new Framework7();
    var mainView = myApp.addView('.view-main', { dynamicNavbar: true });
}

//  ====================================================================================================================
//  ====================================================================================================================
//  ====================================================================================================================

//  1. Load default wilayah cacah for this pencacah
loadWilayahCacah();

$(document).on('click', '.view[data-page="workspace"] .refresh-workspace', function() {
    setTimeout(function () {
        myApp.showPreloader('Wilayah Kerja sedang diperbarui...')
        setTimeout(function () {
            loadWilayahCacah(function() {
                myApp.hidePreloader();
                myApp.addNotification({
                    hold: 3000,
                    closeOnClick: true,
                    title: 'Wilayah Kerja',
                    message: 'Wilayah Kerja telah diperbarui'
                });
            });
        }, 100);
    }, 10);
});

function loadWilayahCacah(callbackFn) {
    var userID = getUserSession().username;

    listWilayahCacah(userID, function(wilCahs) {
        $('#l1-list li.row').remove();

        var numL1Retrieved = 0;
        wilCahs.forEach(function(wilcah, idx) {
            nkss = []; wilcah['nks'].forEach(function(nks) { nkss.push(nks['nama']) });
            slss = []; wilcah['sls'].forEach(function(sls) { slss.push(sls['nama']) });

            $('<li id="wilcah-'+ wilcah['fullKode'] +'" class="item-content row" style="cursor: pointer;">' +
            '<div class="item-inner">'+
                '<div class="item-title"><span>' + wilcah['blokSensus']['fullKode'] + '</span></div>'+
                '<div class="item-title"><span>' + nkss.join(', ') + '</span></div>'+
                '<div class="item-title"><span>' + slss.join(', ') + '</span></div>'+
            '</div></li>').appendTo($('#l1-list'));

            getDataFormL1(wilcah['blokSensus'], function(bs, dataL1) {
                $('#l1-list #wilcah-' + bs['fullKode'])
                    .data('bs', bs)
                    .data('l1', dataL1);

                numL1Retrieved += 1;
            });
        });

        if(callbackFn) {
            function check() {
                if(numL1Retrieved >= wilCahs.length) setTimeout(callbackFn, 0);
                else setTimeout(check, 500);
            }
            setTimeout(check, 500);
        }
    });
}

//  2. Apply click to each wilayah cacah (p1) --> Open formL1
//      + Load v109 options
//      + Prefilled v204 (if exists before)
//      + Prefilled field v101 - v105
//      + Load SubBS, NKS, and SLS
$(document).on('click', '#l1-list li.row', function() {
    var formL1 = $(this);
    var bs = formL1.data('bs');
    var dataL1 = formL1.data('l1');

    $.get(ctxHost + '/form-l1.html', function(form) {
        mainView.router.loadContent(form);

        if(! $.isEmptyObject(dataL1)) {
            preFilledFormL1(bs, dataL1);
        } else {
            getDataFormL1(formL1.attr('id').replace('wilcah-', ''), function(bs, dataL1) {
               formL1.data('bs', bs).data('l1', dataL1);
                preFilledFormL1(bs, dataL1);
            });
        }
    });
});

function preFilledFormL1(bs, dataL1) {
    var bsFullKode = bs;
    if($.isPlainObject(bs)) {
        bsFullKode = bs['fullKode'];
    }

    //  Blok I
    getBlokSensusByKode(bsFullKode, true, function(bs) {
        preFilledPropBS(bs);
        getSubBlokSensusByBlokSensus(function() {
            if(dataL1 && dataL1.v106) $('select[name="v106"]').val(dataL1.v106.kode);
        });
        getNKSByBlokSensus(function() {
            if(dataL1 && dataL1.v107) $('select[name="v107"]').val(dataL1.v107.kode);
        });
        getSLSByBlokSensus(function() {
           if(dataL1 && dataL1.v108) $('select[name="v108"]').val(dataL1.v108.kode);
       });
    });

    listKriteriaBlokSensus(function() {
        if(dataL1) $('select[name="v109"]').val(dataL1.v109.kode);
    });

    //  Blok II
    if(dataL1) $('input[name="v201"]').val(dataL1.pencacah.nama);
    if(dataL1) $('input[name="v202"]').val(dataL1.pencacah.id);
    if(dataL1) $('input[name="v203"]').val(dataL1.pencacah.handphone);
    setTanggalPencacahan(dataL1 ? dataL1.v204 : null);

    //  Blok III
    if(dataL1) $('input[name="v301"]').val(dataL1.v301);
    if(dataL1) $('input[name="v302"]').val(dataL1.v302);
    if(dataL1) $('input[name="v303"]').val(dataL1.v303);
    if(dataL1) $('input[name="v304"]').val(dataL1.v304);
    if(dataL1) $('input[name="v305"]').val(dataL1.v305);
    if(dataL1) $('input[name="v306"]').val(dataL1.v306);
    if(dataL1) $('input[name="v307"]').val(dataL1.v307);
    if(dataL1) $('input[name="v308"]').val(dataL1.v308);

    //  Blok V
    if(dataL1 && dataL1.b5) {
        for(var idx in dataL1.b5) {
            var b5Data = dataL1.b5[idx];
            var b5ID = b5Data['v501'] + b5Data['v502'] + b5Data['v503'] + b5Data['v505'];
            $('<li class="item-content row" id="b5-'+ b5ID +'" style="cursor: pointer;"><div class="item-inner">'+
                '<div class="item-title"><span name="v501">'+ b5Data['v501'] +'</span></div>'+
                '<div class="item-title"><span name="v502">'+ b5Data['v502'] +'</span></div>'+
                '<div class="item-title"><span name="v503">'+ b5Data['v503'] +'</span></div>'+
                '<div class="item-title"><span name="v505">'+ b5Data['v505'] +'</span></div>'+
                '<div class="item-title"><span name="v506">'+ b5Data['v506'] +'</span></div>'+
            '</div></li>').appendTo($('#b5-list')).data(b5Data);
        }
    }
}

function preFilledPropBS(bs) {
    $('select[name="v105"] option').remove();
    myApp.smartSelectAddOption('select[name="v105"]', '<option value="'+ bs.kode +'">'+ bs.nama +'</option>');
    $('select[name="v104"] option').remove();
    myApp.smartSelectAddOption('select[name="v104"]', '<option value="'+ bs.kelurahan.kode +'">'+
        bs.kelurahan.nama +'</option>');
    $('select[name="v103"] option').remove();
    myApp.smartSelectAddOption('select[name="v103"]', '<option value="'+ bs.kelurahan.kecamatan.kode +'">'+
        bs.kelurahan.kecamatan.nama +'</option>');
    $('select[name="v102"] option').remove();
    myApp.smartSelectAddOption('select[name="v102"]', '<option value="'+ bs.kelurahan.kecamatan.kabupaten.kode +'">'+
        bs.kelurahan.kecamatan.kabupaten.nama +'</option>');
    $('select[name="v101"] option').remove();
    myApp.smartSelectAddOption('select[name="v101"]', '<option value="'+ bs.kelurahan.kecamatan.kabupaten.propinsi.kode +
        '">'+ bs.kelurahan.kecamatan.kabupaten.propinsi.nama +'</option>');
}

//  3. Apply click to each ruta or usaha (Blok V) --> Open formL1B5
//      + Load v109 options
//      + Prefilled v504 (if exists before)
//      + Prefilled field v501 - v507
$(document).on('click', '#form-l1 #b5-list li.row, #form-l1 .b5-add-item', function() {
    var b5Data = $(this).data();

    $$.get(ctxHost + '/popup-b5-add-item.html', function(form) {
        myApp.popup(form);

        preFilledFormL1B5(b5Data);
        $$('.popup-b5-add-item').on('opened', function () {
          // preFilledFormL1B5(b5Data);
        });
    });
});

function preFilledFormL1B5(b5Data) {
    if(b5Data) $('#form-b5-add-item input[name="v501"]').val(b5Data.v501);
    if(b5Data) $('#form-b5-add-item input[name="v502"]').val(b5Data.v502);
    if(b5Data) $('#form-b5-add-item input[name="v503"]').val(b5Data.v503);
    listPenggunaanBangunanSensus(function() {
        if(b5Data) $('#form-b5-add-item select[name="v504"]').val(b5Data.v504);
    });
    if(b5Data) $('#form-b5-add-item input[name="v505"]').val(b5Data.v505);
    if(b5Data) $('#form-b5-add-item input[name="v506"]').val(b5Data.v506);
    if(b5Data) $('#form-b5-add-item input[name="v507"]').val(b5Data.v507);

    //  Table b5-usaha-list
    if(b5Data && b5Data.usaha) {
        b5Data.usaha.forEach(function(usaha) {
            $('<li class="item-content row" style="cursor: pointer;"><div class="item-inner">'+
                '<div class="item-title"><span name="v508">'+ usaha.v508 +'</span></div>'+
                '<div class="item-title"><span name="v509">'+ usaha.v509 +'</span></div>'+
                '<div class="item-title"><span name="v510">'+ usaha.v510 +'</span></div>'+
            '</div></li>').appendTo($('#b5-usaha-list')).data(usaha);
        });
    }
}

//  4. Change jumlah usaha in v507
//      + Auto-match row number of form-b5-add-usaha
$(document).on('change', '#form-b5-add-item  input[name="v507"]', function() {
    v507 = parseInt($(this).val());
    b5Usaha = $('#b5-usaha-list li.row').length;

    tobeAdded = v507 - b5Usaha;
    if(tobeAdded > 0) {
        for(a=0; a<tobeAdded; a++) {
            $('<li class="item-content row" style="cursor: pointer;"><div class="item-inner">'+
                '<div class="item-title"><span name="v508"></span></div>'+
                '<div class="item-title"><span name="v509"></span></div>'+
                '<div class="item-title"><span name="v510"></span></div>'+
            '</div></li>').appendTo($('#b5-usaha-list'));
        }
    } else {
        for(a=tobeAdded; a<0; a++) {
            $('#b5-usaha-list li.row').last().remove();
        }
    }
});

//  5. Apply click to each usaha (Blok V Variabel 508-510) --> Open formL1B5Usaha
//      + Load v510 options
//      + Prefilled v508 - v510 (if exists before)
$(document).on('click', '#b5-usaha-list li.row', function() {
    var row = $(this);
    var b5Usaha = row.data();

    $$.get(ctxHost + '/form-b5-add-usaha.html', function(form) {
        mainView.router.loadContent(form);
        preFilledFormL1B5Usaha(b5Usaha)
    });

    //  6. Submit formL1B5Usaha
    //      + Override data in #b5-usaha-list li.row
    $(document).on('click', '#form-b5-add-usaha .submit-b5-add-usaha', function() {
        b5Usaha = $('#form-b5-add-usaha').serializeObject();

        $('span[name="v508"]').html(b5Usaha['v508']);
        $('span[name="v509"]').html(b5Usaha['v509']);
        $('span[name="v510"]').html(b5Usaha['v510']);

        row.data(b5Usaha);
    });
});

function preFilledFormL1B5Usaha(b5Usaha) {
    if(b5Usaha) $('#form-b5-add-usaha input[name="v508"]').val(b5Usaha['v508']);
    if(b5Usaha) $('#form-b5-add-usaha input[name="v509"]').val(b5Usaha['v509']);
    listLokasiUsahaRuta(function() {
        if(b5Usaha) $('#form-b5-add-usaha select[name="v510"]').val(b5Usaha['v510']);
    });
}

//  7. Submit formL1B5
//      + Override data in #b5-usaha-list li.row
$(document).on('click', '#form-b5-add-item .submit-b5-add-item', function() {
    var b5Data = $('#form-b5-add-item').serializeObject();
    var b5ID = b5Data['v501'] + b5Data['v502'] + b5Data['v503'] + b5Data['v505'];

    b5Data['usaha'] = []
    $('#b5-usaha-list li.row').each(function() {
        var dataB5Usaha = $(this).data();
        b5Data['usaha'].push(dataB5Usaha);
    });

    if($('#b5-'+ b5ID).length > 0) {
        $('#b5-'+ b5ID + ' span[name="v501"]').html(b5Data['v501']);
        $('#b5-'+ b5ID + ' span[name="v502"]').html(b5Data['v502']);
        $('#b5-'+ b5ID + ' span[name="v503"]').html(b5Data['v503']);
        $('#b5-'+ b5ID + ' span[name="v505"]').html(b5Data['v505']);
        $('#b5-'+ b5ID + ' span[name="v506"]').html(b5Data['v506']);
    } else {
        $('<li class="item-content row" id="b5-'+ b5ID +'" style="cursor: pointer;"><div class="item-inner">'+
            '<div class="item-title"><span name="v501">'+ b5Data['v501'] +'</span></div>'+
            '<div class="item-title"><span name="v502">'+ b5Data['v502'] +'</span></div>'+
            '<div class="item-title"><span name="v503">'+ b5Data['v503'] +'</span></div>'+
            '<div class="item-title"><span name="v505">'+ b5Data['v505'] +'</span></div>'+
            '<div class="item-title"><span name="v506">'+ b5Data['v506'] +'</span></div>'+
        '</div></li>').appendTo($('#b5-list'));
    }

    $('#b5-'+ b5ID).data(b5Data);
});

//  8. Submit formL1
//      + Submit to server
$(document).on('click', '.view[data-page="form-l1"] .submit-l1', function() {
    var dataL1 = $('#form-l1').serializeObject();

    dataL1['b5'] = []
    $('#b5-list li.row').each(function() {
        var dataB5 = $(this).data();
        dataL1['b5'].push(dataB5);
    });

    setTimeout(function () {
        myApp.showPreloader('Form L1 sedang disubmit...')
        setTimeout(function () {
            $.ajax({
                url: ctxHost + '/submit',
                type: "POST",
                dataType: "json", // expected format for response
                contentType: "application/json", // send as JSON
                data: JSON.stringify(dataL1),
                complete: function(xhr, status) {
                    myApp.hidePreloader();
                },
                success: function(data, status, xhr) {
                    myApp.addNotification({
                        hold: 3000,
                        closeOnClick: true,
                        title: 'App List',
                        message: 'Form L1 berhasil disubmit. ' + data
                    });
                },
                error: function(xhr, status, error) {
                    myApp.addNotification({
                        hold: 3000,
                        closeOnClick: true,
                        title: 'App List',
                        message: 'Form L1 gagal disubmit. ' + error
                    });
                },
            });
        }, 100);
    }, 100);
});

//$(document).on('click', '.reset-l1', function() {
//    myApp.formDeleteData('form-l1');
//});

$(document).on('change', 'select[name="v101"]', function() {
    getKabupatenByPropinsi();
});

$(document).on('change', 'select[name="v102"]', function() {
    getKecamatanByKabupaten();
});

$(document).on('change', 'select[name="v103"]', function() {
    getKelurahanByKecamatan();
});

$(document).on('change', 'select[name="v104"]', function() {
    getBlokSensusByKelurahan();
});

$(document).on('change', 'select[name="v105"]', function() {
    getSubBlokSensusByBlokSensus();
    getNKSByBlokSensus();
    getSLSByBlokSensus();
});

function getCurrentPencacah(kodePencacah, callbackFn) {
    $$.getJSON(ctxHost + '/pencacah?kode=' + kodePencacah, function (pencacah) {
        if(callbackFn) setTimeout(callbackFn, 0, pencacah);
    });
}

function listWilayahCacah(kodePencacah, callbackFn) {
    $$.getJSON(ctxHost + '/wilcah?pencacah=' + kodePencacah, function (wilCahs) {
        if(callbackFn) setTimeout(callbackFn, 0, wilCahs);
    });
}

function getDataFormL1(bs, callbackFn) {
    var fullKode = bs;
    if($.isPlainObject(bs)) {
        fullKode = bs['fullKode'];
    }

    $$.getJSON(ctxHost + '/l1?bs=' + fullKode, function (listDataL1) {
        dataL1 = null; if(listDataL1.length > 0) dataL1 = listDataL1[0];
        if(callbackFn) setTimeout(callbackFn, 0, bs, dataL1);
    });
}

function listPropinsi(callbackFn) {
    $$.getJSON(ctxHost + '/propinsi', function (props) {
        $('select[name="v101"] option').remove();
        myApp.smartSelectAddOption('select[name="v101"]', '<option value="">Pilih</option>');
        props.forEach(function(prop) {
            myApp.smartSelectAddOption('select[name="v101"]', '<option value="'+ prop.kode +'">'+ prop.nama +'</option>');
        });

        if(callbackFn) setTimeout(callbackFn, 0, props);
    });
}

function getPropinsiByKode(fullKode, callbackFn) {
    $$.getJSON(ctxHost + '/propinsi/by-kode?fullKode=' + fullKode, function (prop) {
        $('select[name="v101"] option').remove();
        myApp.smartSelectAddOption('select[name="v101"]', '<option value="'+ prop.kode +'">'+ prop.nama +'</option>');

        if(callbackFn) setTimeout(callbackFn, 0, prop);
    });
}

function getKabupatenByPropinsi(callbackFn) {
    prov = $('select[name="v101"] option:selected').val();

    $$.getJSON(ctxHost + '/kabupaten?propinsi=' + prov, function (kabs) {
        $('select[name="v102"] option').remove();
        myApp.smartSelectAddOption('select[name="v102"]', '<option value="">Pilih</option>');
        kabs.forEach(function(kab) {
            myApp.smartSelectAddOption('select[name="v102"]', '<option value="'+ kab.kode +'">'+ kab.nama +'</option>');
        });

        if(callbackFn) setTimeout(callbackFn, 0, kabs);
    });
}

function getKabupatenByKode(fullKode, callbackFn) {
    $$.getJSON(ctxHost + '/kabupaten/by-kode?fullKode=' + fullKode, function (kab) {
        $('select[name="v102"] option').remove();
        myApp.smartSelectAddOption('select[name="v102"]', '<option value="'+ kab.kode +'">'+ kab.nama +'</option>');

        if(callbackFn) setTimeout(callbackFn, 0, kab);
    });
}

function getKecamatanByKabupaten(callbackFn) {
    prov = $('select[name="v101"] option:selected').val();
    kab = $('select[name="v102"] option:selected').val();

    $$.getJSON(ctxHost + '/kecamatan?propinsi=' + prov + '&kabupaten=' + kab, function (kecs) {
        $('select[name="v103"] option').remove();
        myApp.smartSelectAddOption('select[name="v103"]', '<option value="">Pilih</option>');
        kecs.forEach(function(kec) {
            myApp.smartSelectAddOption('select[name="v103"]', '<option value="'+ kec.kode +'">'+ kec.nama +'</option>');
        });

        if(callbackFn) setTimeout(callbackFn, 0, kecs);
    });
}

function getKecamatanByKode(fullKode, callbackFn) {
    $$.getJSON(ctxHost + '/kecamatan/by-kode?fullKode=' + fullKode, function (kec) {
        $('select[name="v103"] option').remove();
        myApp.smartSelectAddOption('select[name="v103"]', '<option value="'+ kec.kode +'">'+ kec.nama +'</option>');

        if(callbackFn) setTimeout(callbackFn, 0, kec);
    });
}

function getKelurahanByKecamatan(callbackFn) {
    prov = $('select[name="v101"] option:selected').val();
    kab = $('select[name="v102"] option:selected').val();
    kec = $('select[name="v103"] option:selected').val();

    $$.getJSON(ctxHost + '/kelurahan?propinsi=' + prov + '&kabupaten=' + kab + '&kecamatan=' + kec, function (kels) {
        $('select[name="v104"] option').remove();
        myApp.smartSelectAddOption('select[name="v104"]', '<option value="">Pilih</option>');
        kels.forEach(function(kel) {
            myApp.smartSelectAddOption('select[name="v104"]', '<option value="'+ kel.kode +'">'+ kel.nama +'</option>');
        });

        if(callbackFn) setTimeout(callbackFn, 0, kels);
    });
}

function getKelurahanByKode(fullKode, callbackFn) {
    $$.getJSON(ctxHost + '/kelurahan/by-kode?fullKode=' + fullKode, function (kel) {
        $('select[name="v104"] option').remove();
        myApp.smartSelectAddOption('select[name="v104"]', '<option value="'+ kel.kode +'">'+ kel.nama +'</option>');

        if(callbackFn) setTimeout(callbackFn, 0, kel);
    });
}

function getBlokSensusByKelurahan(callbackFn) {
    prov = $('select[name="v101"] option:selected').val();
    kab = $('select[name="v102"] option:selected').val();
    kec = $('select[name="v103"] option:selected').val();
    kel = $('select[name="v104"] option:selected').val();

    $$.getJSON(ctxHost + '/bloksensus?propinsi=' + prov + '&kabupaten=' + kab + '&kecamatan=' + kec +
    '&kelurahan=' + kel, function (bss) {
        $('select[name="v105"] option').remove();
        myApp.smartSelectAddOption('select[name="v105"]', '<option value="">Pilih</option>');
        bss.forEach(function(bs) {
            myApp.smartSelectAddOption('select[name="v105"]', '<option value="'+ bs.kode +'">'+ bs.nama +'</option>');
        });

        if(callbackFn) setTimeout(callbackFn, 0, bss);
    });
}

function getBlokSensusByKode(fullKode, refresh, callbackFn) {
    $$.getJSON(ctxHost + '/bloksensus/by-kode?fullKode=' + fullKode + '&refreshForeign=' + refresh, function (bs) {
        $('select[name="v105"] option').remove();
        myApp.smartSelectAddOption('select[name="v105"]', '<option value="'+ bs.kode +'">'+ bs.nama +'</option>');

        if(callbackFn) setTimeout(callbackFn, 0, bs);
    });
}

function getSubBlokSensusByBlokSensus(callbackFn) {
    prov = $('select[name="v101"] option:selected').val();
    kab = $('select[name="v102"] option:selected').val();
    kec = $('select[name="v103"] option:selected').val();
    kel = $('select[name="v104"] option:selected').val();
    bs = $('select[name="v105"] option:selected').val();

    $$.getJSON(ctxHost + '/subbloksensus?propinsi=' + prov + '&kabupaten=' + kab + '&kecamatan=' + kec +
    '&kelurahan=' + kel + '&bloksensus=' + bs, function (sbss) {
        $('select[name="v106"] option').remove();
        myApp.smartSelectAddOption('select[name="v106"]', '<option value="">Pilih</option>');
        sbss.forEach(function(sbs) {
            myApp.smartSelectAddOption('select[name="v106"]', '<option value="'+ sbs.kode +'">'+ sbs.nama +'</option>');
        });

        if(callbackFn) setTimeout(callbackFn, 0, sbss);
    });
}

function getNKSByBlokSensus(callbackFn) {
    prov = $('select[name="v101"] option:selected').val();
    kab = $('select[name="v102"] option:selected').val();
    kec = $('select[name="v103"] option:selected').val();
    kel = $('select[name="v104"] option:selected').val();
    bs = $('select[name="v105"] option:selected').val();

    $$.getJSON(ctxHost + '/nks?propinsi=' + prov + '&kabupaten=' + kab + '&kecamatan=' + kec +
    '&kelurahan=' + kel + '&bloksensus=' + bs, function (nkss) {
        $('select[name="v107"] option').remove();
        myApp.smartSelectAddOption('select[name="v107"]', '<option value="">Pilih</option>');
        nkss.forEach(function(nks) {
            myApp.smartSelectAddOption('select[name="v107"]', '<option value="'+ nks.kode +'">'+ nks.nama +'</option>');
        });

        if(callbackFn) setTimeout(callbackFn, 0, nkss);
    });
}

function getSLSByBlokSensus(callbackFn) {
    prov = $('select[name="v101"] option:selected').val();
    kab = $('select[name="v102"] option:selected').val();
    kec = $('select[name="v103"] option:selected').val();
    kel = $('select[name="v104"] option:selected').val();
    bs = $('select[name="v105"] option:selected').val();

    $$.getJSON(ctxHost + '/sls?propinsi=' + prov + '&kabupaten=' + kab + '&kecamatan=' + kec +
    '&kelurahan=' + kel + '&bloksensus=' + bs, function (slss) {
        $('select[name="v108"] option').remove();
        myApp.smartSelectAddOption('select[name="v108"]', '<option value="">Pilih</option>');
        slss.forEach(function(sls) {
            myApp.smartSelectAddOption('select[name="v108"]', '<option value="'+ sls.kode +'">'+ sls.nama +'</option>');
        });

        if(callbackFn) setTimeout(callbackFn, 0, slss);
    });
}

function listKriteriaBlokSensus(callbackFn) {
    $$.getJSON(ctxHost + '/v109-options', function (options) {
        $('select[name="v109"] option').remove();

        myApp.smartSelectAddOption('select[name="v109"]', '<option value="">Pilih</option>');
        options.forEach(function(option) {
            myApp.smartSelectAddOption('select[name="v109"]', '<option value="'+ option.kode +'">'+ option.nama +'</option>');
        });

        if(callbackFn) setTimeout(callbackFn, 100);
    });
}

function setTanggalPencacahan(tanggal, callbackFn) {
    if(!tanggal) tanggal = moment().format('YYYY-MM-DD HH:mm:ss.SSS');
    $('input[name="v204"]').val(tanggal);

    if(callbackFn) setTimeout(callbackFn, 100);
}

function listPenggunaanBangunanSensus(callbackFn) {
    $$.getJSON(ctxHost + '/v504-options', function (options) {
        $('select[name="v504"] option').remove();

        options.forEach(function(option) {
            myApp.smartSelectAddOption('select[name="v504"]', '<option value="'+ option.kode +'">'+ option.nama +'</option>');
        });

        if(callbackFn) setTimeout(callbackFn, 100);
    });
}

function listLokasiUsahaRuta(callbackFn) {
    $$.getJSON(ctxHost + '/v510-options', function (options) {
        $('select[name="v510"] option').remove();

        myApp.smartSelectAddOption('select[name="v510"]', '<option value="">Pilih</option>');
        options.forEach(function(option) {
            myApp.smartSelectAddOption('select[name="v510"]', '<option value="'+ option.kode +'">'+ option.nama +'</option>');
        });

        if(callbackFn) setTimeout(callbackFn, 100);
    });
}
