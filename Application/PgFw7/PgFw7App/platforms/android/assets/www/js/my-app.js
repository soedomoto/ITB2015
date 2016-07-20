var appTitle = 'Mobile Survey'

// Initialize your app
var myApp = new Framework7({
    precompileTemplates: true,
});

var mainView = myApp.addView('.view-main')
mainView.router.loadPage('pages/home.html')

// Export selectors engine
var $$ = Dom7;

document.addEventListener("deviceready", onDeviceReady, false);
function onDeviceReady() {
    $$('.app-title').html(appTitle);

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
                        window.server.stop();
                        navigator.app.exitApp();
                    },
                    function() {
                        return;
                    }
                );
            }
        }
    }, false );
}

$$(document).on('pageInit', '.page[data-page="server"]', function (e) {
    var page = e.detail.page;

    function startServer() {
        setTimeout(function () {
            myApp.showPreloader('Server is starting...')
            setTimeout(function () {
                status = window.server.start();
                myApp.hidePreloader()
                if(status == 'true') {
                    myApp.addNotification({
                        hold: 3000,
                        closeOnClick: true,
                        title: 'Proxy Server',
                        message: 'Server is started'
                    });

                    $$('.start').addClass('disabled');
                    $$('.stop').removeClass('disabled');
                } else {
                    myApp.addNotification({
                        hold: 3000,
                        closeOnClick: true,
                        title: 'Proxy Server',
                        message: status
                    });
                }
            }, 10);
        }, 10);
    }

    function stopServer() {
        setTimeout(function () {
            myApp.showPreloader('Server is stopping...')
            setTimeout(function () {
                status = window.server.stop();
                myApp.hidePreloader()

                if(status == 'true') {
                    myApp.addNotification({
                        hold: 3000,
                        closeOnClick: true,
                        title: 'Proxy Server',
                        message: 'Server is stopped'
                    });

                    $$('.stop').addClass('disabled');
                    $$('.start').removeClass('disabled');
                } else {
                    myApp.addNotification({
                        hold: 3000,
                        closeOnClick: true,
                        title: 'Proxy Server',
                        message: status
                    });
                }
            }, 10);
        }, 10);
    }

    function serverStatus() {
        isRunning = window.server.status();
        if(isRunning) {
            myApp.alert('Server is running', 'Proxy Server');
        } else {
            myApp.alert('Server is stopped', 'Proxy Server');
        }
    }

    if(!window.server.status()) {
        $$('.stop').addClass('disabled');
        $$('.start').removeClass('disabled');
    } else {
        $$('.start').addClass('disabled');
        $$('.stop').removeClass('disabled');
    }

    $$('.start').on('click', function() { startServer() });
    $$('.stop').on('click', function() { stopServer() });
    $$('.status').on('click', function() { serverStatus() });

    if(page.query['action'] == 'start') {
        if(!window.server.status()) {
            startServer()
        } else {
            myApp.alert('Server is still running', 'Proxy Server');
        }
    }
    else if(page.query['action'] == 'stop') {
        if(window.server.status()) {
            stopServer()
        } else {
            myApp.alert('Server has been stopped', 'Proxy Server');
        }
    }
    else if(page.query['action'] == 'status') { serverStatus() }
})

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
                      '<div class="col-33"><a href="#" bundle-id="'+ bundle['id'] +'" class="button start">Start</a></div>' +
                      '<div class="col-33"><a href="#" bundle-id="'+ bundle['id'] +'" class="button stop">Stop</a></div>' +
                      '<div class="col-33"><a href="#" bundle-id="'+ bundle['id'] +'" class="button uninstall">Uninstall</a></div>' +
                    '</div>' +
                '</div></div>'+
            '</li>').appendTo(list);

            if(bundle['state'][0] == 0x00000020 && bundle['context'] != null) {
                $$('<li class="item-content"><div class="item-inner">'+
                    '<a href="http://localhost:8080'+ bundle['context'] +'" class="close-panel">' +
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