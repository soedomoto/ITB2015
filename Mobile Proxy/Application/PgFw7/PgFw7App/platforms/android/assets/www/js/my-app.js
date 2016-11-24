var appTitle = 'Mobile Survey'
var proxyURL = 'http://localhost:5555';

// Initialize your app
var myApp = new Framework7({
    precompileTemplates: true
});

// Add view
var appView = myApp.addView('#app-view', { dynamicNavbar: true });
var settingView = myApp.addView('#setting-view', { dynamicNavbar: true });

var mainView = appView;

// Export selectors engine
var $$ = Dom7;

// On device ready
document.addEventListener("deviceready", onDeviceReady, false);

function onDeviceReady() {
    //    Autostart OSGi Framework
    setTimeout(function () {
        myApp.showPreloader('OSGi Framework is starting...')
        setTimeout(function () {
            status = window.osgi.startFramework();
            myApp.hidePreloader()

            if(status == 'true') {
                myApp.addNotification({
                    hold: 3000,
                    closeOnClick: true,
                    title: 'OSGi Framework',
                    message: 'OSGi Framework is started'
                });

                $(document).trigger('frameworkReady');
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

    //    Handle back button
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
                                status = window.osgi.stopFramework();
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

    /**
    document.addEventListener('offline', function() {
        console.log('OFFLINE...');
    }, false);

    document.addEventListener('online', function() {
        var networkState = navigator.connection.type;

        var states = {};
        states[Connection.UNKNOWN]  = 'Unknown connection';
        states[Connection.ETHERNET] = 'Ethernet connection';
        states[Connection.WIFI]     = 'WiFi connection';
        states[Connection.CELL_2G]  = 'Cell 2G connection';
        states[Connection.CELL_3G]  = 'Cell 3G connection';
        states[Connection.CELL_4G]  = 'Cell 4G connection';
        states[Connection.CELL]     = 'Cell generic connection';
        states[Connection.NONE]     = 'No network connection';

        console.log('Connection type: ' + states[networkState]);
    }, false);
    */
}

$(document).on('frameworkReady', function() {
    // handle login
    if(! getUserSession()) {
        showLoginDialog();
        $(document).trigger('loggedOut');
    } else {
        var account = getUserSession();
        doLogin(account.username, account.password, true);
        $(document).trigger('loggedIn');
    }
});

$(document).on('click', '.login-button', function (e) {
    showLoginDialog();
});

$(document).on('click', '.logout-button', function (e) {
    $.ajax({
        url: proxyURL + '/account/logout',
        type: 'get',
        success: function(user) {
            clearUserSession();
            showLoginDialog();
            $(document).trigger('loggedOut');
        },
        error: function(xhr, textStatus, resp){
            if(xhr.status == 404) {
                myApp.alert('Bundle account seems not installed or started. Please install or start it first.', appTitle);
                settingView.router.loadPage('pages/bundle.html');
            } else {
                myApp.alert(resp, appTitle);
            }
        }
    });
});

$(document).on('loggedIn', function() {
    $('.login-button').hide();
    $('.logout-button').show();

    // list applications
    var list = $$('.page[data-page="apps"]').find('.list-apps ul');
    listApplications(list);
});

$(document).on('loggedOut', function() {
    $('.logout-button').hide();
    $('.login-button').show();
});


function getUserSession() {
    if (typeof(Storage) !== "undefined") {
        return JSON.parse(localStorage.getItem("user")) || false;
    } else {
        return false;
    }
}

function setUserSession(user) {
    if (typeof(Storage) !== "undefined") {
        localStorage.setItem("user", JSON.stringify(user));
    }

    return getUserSession();
}

function clearUserSession() {
    if (typeof(Storage) !== "undefined") {
        localStorage.removeItem("user");
    }
}

function showLoginDialog() {
    myApp.modalLogin('Authentication required', appTitle, function (username, password) {
        doLogin(username, password);
    });
}

function doLogin(username, password, local) {
    local = typeof local !== 'undefined' ? local : false;

    $.ajax({
        url: proxyURL + '/account/login',
        type: 'post',
        contentType: 'application/x-www-form-urlencoded',
        data: { 'username': username, 'password': password, 'local': local },
        success: function(user) {
            myApp.alert('Welcome ' + user.username, appTitle);
            setUserSession(user);
            $(document).trigger('loggedIn');
        },
        error: function(xhr, textStatus, resp){
            if(xhr.status == 404 || xhr.status == 0) {
                myApp.alert('Bundle account seems not installed or started. Please install or start it first.', appTitle);
                settingView.router.loadPage('pages/bundle.html');
            } else {
                myApp.alert(textStatus + ' (' + xhr.status + ') : ' + resp, appTitle);
            }
        }
    });
}

function listApplications(list) {
    var bundles = window.osgi.listBundles();
    bundles = JSON.parse(bundles);

    list.children().remove();

    bundles.forEach(function(bundle) {
        if(bundle['state'][0] == 0x00000020 && bundle['context'] != null) {
            $.ajax({
                url: proxyURL + bundle['context'] + '/index.html',
                type: 'get',
                success: function(content) {
                    $$('<li>' +
                        '<a href="'+ bundle['context'] +'" class="item-content item-link"><div class="item-inner">'+
                            '<div class="item-title">'+ bundle['name'] + '</div>' +
                        '</div></a>' +
                    '</li>').appendTo(list);
                }
            });
        }
    });
}

function listBundles(list) {
    var bundles = window.osgi.listBundles();
    bundles = JSON.parse(bundles);

    list.children().remove();

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
    });
}


/*Bundle*/

$$(document).on('click', '#app-view .refresh-app', function (e) {
    var list = $$('.page[data-page="apps"]').find('.list-apps ul');

    setTimeout(function () {
        myApp.showPreloader('App List is being refreshed...')
        setTimeout(function () {
            listApplications(list);
            myApp.hidePreloader()

            myApp.addNotification({
                hold: 3000,
                closeOnClick: true,
                title: 'App List',
                message: 'App List is refreshed'
            });
        }, 100);
    }, 10);
});

$$(document).on('click', '.list-apps a', function (e) {
    href = proxyURL + $$(this).attr('href');

    $.ajax({
        url: href,
        context: document.body,
        success: function(page) {
            div = $('<div/>').html(page);
            appView.router.loadContent(div.find('.view').html());

            div.find('script[ajax-load="true"]').each(function(i) {
                src = $(this).attr('src');
                src = href + '/' + src;
                $.getScript(src).done(function( script, textStatus ) {
                    console.log('Script "' + script + '" is loaded.');
                }).fail(function( jqxhr, settings, exception ) {
                    console.log( "Triggered ajaxError handler." );
                });
            });
        }
    });

    e.preventDefault();
});

$$(document).on('pageInit', '.page[data-page="bundle"]', function (e) {
    var list = $$('.page[data-page="bundle"]').find('.list-bundle ul');
    listBundles(list);

    $$(document).on('click', '.refresh-bundle', function (e) {
        setTimeout(function () {
            myApp.showPreloader('Bundle List is being refreshed...')
            setTimeout(function () {
                listBundles(list);
                myApp.hidePreloader()

                myApp.addNotification({
                    hold: 3000,
                    closeOnClick: true,
                    title: 'Bundle List',
                    message: 'Bundle List is refreshed'
                });
            }, 100);
        }, 10);
    });
});

$$(document).on('click', '.page[data-page="bundle"] .install', function() {
    var list = $$('.page[data-page="bundle"]').find('.list-bundle ul');

    myApp.prompt('Enter bundle URI', 'Install Bundle', function (uri) {
        setTimeout(function () {
            myApp.showPreloader('Bundle is installing...')
            setTimeout(function () {
                status = window.osgi.installBundle(uri);
                myApp.hidePreloader()

                if(status == 'true') {
                    myApp.addNotification({
                        hold: 3000,
                        closeOnClick: true,
                        title: 'Bundle',
                        message: 'Bundle is installed'
                    });

                    listBundles(list);
                } else {
                    myApp.addNotification({
                        hold: 3000,
                        closeOnClick: true,
                        title: 'Bundle',
                        message: status
                    });
                }
            }, 200);
        }, 100);
    });
});

$$(document).on('click', '.page[data-page="bundle"] .start', function() {
    var list = $$('.page[data-page="bundle"]').find('.list-bundle ul');
    var id = parseInt($$(this).attr('bundle-id'));

    setTimeout(function () {
        myApp.showPreloader('Bundle is starting...')
        setTimeout(function () {
            status = window.osgi.startBundle(id);
            myApp.hidePreloader()

            if(status == 'true') {
                myApp.addNotification({
                    hold: 3000,
                    closeOnClick: true,
                    title: 'Bundle',
                    message: 'Bundle is started'
                });

                listBundles(list);
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
    var list = $$('.page[data-page="bundle"]').find('.list-bundle ul');
    var id = parseInt($$(this).attr('bundle-id'));

    setTimeout(function () {
        myApp.showPreloader('Bundle is stopping...')
        setTimeout(function () {
            status = window.osgi.stopBundle(id);
            myApp.hidePreloader()

            if(status == 'true') {
                myApp.addNotification({
                    hold: 3000,
                    closeOnClick: true,
                    title: 'Bundle',
                    message: 'Bundle is stopped'
                });

                listBundles(list);
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
    var list = $$('.page[data-page="bundle"]').find('.list-bundle ul');
    var id = parseInt($$(this).attr('bundle-id'));

    setTimeout(function () {
        myApp.showPreloader('Bundle is updating...')
        setTimeout(function () {
            status = window.osgi.updateBundle(id);
            myApp.hidePreloader()

            if(status == 'true') {
                myApp.addNotification({
                    hold: 3000,
                    closeOnClick: true,
                    title: 'Bundle',
                    message: 'Bundle is updated'
                });

                listBundles(list);
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
    var list = $$('.page[data-page="bundle"]').find('.list-bundle ul');
    var id = parseInt($$(this).attr('bundle-id'));

    setTimeout(function () {
        myApp.showPreloader('Bundle is uninstalling...')
        setTimeout(function () {
            status = window.osgi.uninstallBundle(id);
            myApp.hidePreloader()

            if(status == 'true') {
                myApp.addNotification({
                    hold: 3000,
                    closeOnClick: true,
                    title: 'Bundle',
                    message: 'Bundle is uninstaled'
                });

                listBundles(list);
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