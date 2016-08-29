var appTitle = 'Mobile Survey'

// Initialize your app
var myApp = new Framework7({
    precompileTemplates: true
});

var mainView = myApp.addView('.view-main')
mainView.router.loadPage('pages/home.html')

// Export selectors engine
var $$ = Dom7;

document.addEventListener("deviceready", onDeviceReady, false);
function onDeviceReady() {
    $$('.app-title').html(appTitle);

    //  Autostart OSGi Framework
    setTimeout(function () {
        myApp.showPreloader('OSGi Framework is starting...')
        setTimeout(function () {
            status = window.server.startFramework();
            myApp.hidePreloader()

            if(status == 'true') {
                myApp.addNotification({
                    hold: 3000,
                    closeOnClick: true,
                    title: 'OSGi Framework',
                    message: 'OSGi Framework is started'
                });
            } else {
                myApp.addNotification({
                    hold: 3000,
                    closeOnClick: true,
                    title: 'OSGi Framework',
                    message: status
                });
            }
        }, 10);
    }, 1000);

    //  Handle back button
    document.addEventListener("backbutton", function (e) {
        e.preventDefault();

        if($$('.panel-left.active, .panel-right.active').length > 0) {
            myApp.closePanel();
        } else {
            if($$(".back").length > 0) {
                $$(".back").click();
            } else {
                myApp.confirm('Are you sure want to exit from App?', appTitle,
                    function() {
                        setTimeout(function () {
                            myApp.showPreloader('OSGi Framework is stopping...')
                            setTimeout(function () {
                                status = window.server.stopFramework();
                                myApp.hidePreloader()

                                if(status == 'true') {
                                    navigator.app.exitApp();
                                } else {
                                    myApp.addNotification({
                                        hold: 3000,
                                        closeOnClick: true,
                                        title: 'Bundle',
                                        message: status
                                    });
                                }
                            }, 10);
                        }, 10);
                    },
                    function() { return; }
                );
            }
        }
    }, false );
}

var proxyURL = 'http://localhost:5555';
$$(document).on('click', '.list-application a', function (e) {
    href = proxyURL + $$(this).attr('href');

    $.ajax({
        url: href,
        context: document.body,
        success: function(page) {
            div = $('<div/>').html(page);
            mainView.router.loadContent(div.find('.view').html());

            div.find('script[ajax-load="true"]').each(function(i) {
                src = $(this).attr('src');
                src = href + '/' + src;
                $.getScript(src).done(function( script, textStatus ) {
                    console.log( textStatus );
                }).fail(function( jqxhr, settings, exception ) {
                    console.log( "Triggered ajaxError handler." );
                });
            });
        }
    });

    e.preventDefault();
});

$$(document).on('pageInit', '.page[data-page="bundle"]', function (e) {
    var list = $$(this).find('.list-bundle ul');
    var apps = $$('.list-application ul');

    function listBundles() {
        var bundles = window.server.listBundles();
        bundles = JSON.parse(bundles);

        list.children().remove();
        apps.children().remove();

        bundles.forEach(function(bundle) {
            $$('<li class="accordion-item">' +
                '<a href="#" class="item-content item-link"><div class="item-inner">'+
                    '<div class="item-title">'+ bundle['name'] + '</div>' +
                    '<div class="item-after">'+ bundle['state'][1] + '</div>' +
                '</div></a>' +
                '<div class="accordion-item-content"><div class="content-block">' +
                    '<div class="row">' +
                        '<div class="col-50"><a href="#" bundle-id="'+ bundle['id'] +'" class="button start">Start</a></div>' +
                        '<div class="col-50"><a href="#" bundle-id="'+ bundle['id'] +'" class="button stop">Stop</a></div>' +
                    '</div>' +
                    '<div class="row">' +
                        '<div class="col-50"><a href="#" bundle-id="'+ bundle['id'] +'" class="button update">Update</a></div>' +
                        '<div class="col-50"><a href="#" bundle-id="'+ bundle['id'] +'" class="button uninstall">Uninstall</a></div>' +
                    '</div>' +
                '</div></div>'+
            '</li>').appendTo(list);

            if(bundle['state'][0] == 0x00000020 && bundle['context'] != null) {
                $$('<li class="item-content"><div class="item-inner">'+
                    '<a href="'+ bundle['context'] +'" class="item-link close-panel">' +
                        '<div class="item-title">'+ bundle['name'] + '</div>'+
                    '</a>' +
                '</div></li>').appendTo(apps);
            }
        });
    }

    $$(document).on('click', '.page[data-page="bundle"] .start', function() {
        var id = parseInt($$(this).attr('bundle-id'));

        setTimeout(function () {
            myApp.showPreloader('Bundle is starting...')
            setTimeout(function () {
                status = window.server.startBundle(id);
                myApp.hidePreloader()

                if(status == 'true') {
                    myApp.addNotification({
                        hold: 3000,
                        closeOnClick: true,
                        title: 'Bundle',
                        message: 'Bundle is started'
                    });

                    listBundles();
                } else {
                    myApp.addNotification({
                        hold: 3000,
                        closeOnClick: true,
                        title: 'Bundle',
                        message: status
                    });
                }
            }, 10);
        }, 10);
    });

    $$(document).on('click', '.page[data-page="bundle"] .stop', function() {
        var id = parseInt($$(this).attr('bundle-id'));

        setTimeout(function () {
            myApp.showPreloader('Bundle is stopping...')
            setTimeout(function () {
                status = window.server.stopBundle(id);
                myApp.hidePreloader()

                if(status == 'true') {
                    myApp.addNotification({
                        hold: 3000,
                        closeOnClick: true,
                        title: 'Bundle',
                        message: 'Bundle is stopped'
                    });

                    listBundles();
                } else {
                    myApp.addNotification({
                        hold: 3000,
                        closeOnClick: true,
                        title: 'Bundle',
                        message: status
                    });
                }
            }, 10);
        }, 10);
    });

    $$(document).on('click', '.page[data-page="bundle"] .update', function() {
        var id = parseInt($$(this).attr('bundle-id'));

        setTimeout(function () {
            myApp.showPreloader('Bundle is updating...')
            setTimeout(function () {
                status = window.server.updateBundle(id);
                myApp.hidePreloader()

                if(status == 'true') {
                    myApp.addNotification({
                        hold: 3000,
                        closeOnClick: true,
                        title: 'Bundle',
                        message: 'Bundle is updated'
                    });

                    listBundles();
                } else {
                    myApp.addNotification({
                        hold: 3000,
                        closeOnClick: true,
                        title: 'Bundle',
                        message: status
                    });
                }
            }, 10);
        }, 10);
    });

    $$(document).on('click', '.page[data-page="bundle"] .uninstall', function() {
        var id = parseInt($$(this).attr('bundle-id'));

        setTimeout(function () {
            myApp.showPreloader('Bundle is uninstalling...')
            setTimeout(function () {
                status = window.server.uninstallBundle(id);
                myApp.hidePreloader()

                if(status == 'true') {
                    myApp.addNotification({
                        hold: 3000,
                        closeOnClick: true,
                        title: 'Bundle',
                        message: 'Bundle is uninstaled'
                    });

                    listBundles();
                } else {
                    myApp.addNotification({
                        hold: 3000,
                        closeOnClick: true,
                        title: 'Bundle',
                        message: status
                    });
                }
            }, 10);
        }, 10);
    });

    listBundles();
});















// Add view
var mainView = myApp.addView('.view-main', {
    // Because we use fixed-through navbar we can enable dynamic navbar
    dynamicNavbar: true
});

// Callbacks to run specific code for specific pages, for example for About page:
myApp.onPageInit('about', function (page) {
    // run createContentPage func after link was clicked
    $$('.create-page').on('click', function () {
        createContentPage();
    });
});

// Generate dynamic page
var dynamicPageIndex = 0;
function createContentPage() {
	mainView.router.loadContent(
        '<!-- Top Navbar-->' +
        '<div class="navbar">' +
        '  <div class="navbar-inner">' +
        '    <div class="left"><a href="#" class="back link"><i class="icon icon-back"></i><span>Back</span></a></div>' +
        '    <div class="center sliding">Dynamic Page ' + (++dynamicPageIndex) + '</div>' +
        '  </div>' +
        '</div>' +
        '<div class="pages">' +
        '  <!-- Page, data-page contains page name-->' +
        '  <div data-page="dynamic-pages" class="page">' +
        '    <!-- Scrollable page content-->' +
        '    <div class="page-content">' +
        '      <div class="content-block">' +
        '        <div class="content-block-inner">' +
        '          <p>Here is a dynamic page created on ' + new Date() + ' !</p>' +
        '          <p>Go <a href="#" class="back">back</a> or go to <a href="services.html">Services</a>.</p>' +
        '        </div>' +
        '      </div>' +
        '    </div>' +
        '  </div>' +
        '</div>'
    );
	return;
}