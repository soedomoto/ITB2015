<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="initial-scale=1,user-scalable=no,maximum-scale=1,width=device-width">
    <meta name="mobile-web-app-capable" content="yes">
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="theme-color" content="#000000">
    <meta name="description" content="">
    <meta name="author" content="">
    <title>Map</title>

    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/font-awesome/4.4.0/css/font-awesome.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/leaflet/0.7.7/leaflet.css">
    <link rel="stylesheet" href="https://api.tiles.mapbox.com/mapbox.js/plugins/leaflet-markercluster/v0.4.0/MarkerCluster.css">
    <link rel="stylesheet" href="https://api.tiles.mapbox.com/mapbox.js/plugins/leaflet-markercluster/v0.4.0/MarkerCluster.Default.css">
    <link rel="stylesheet" href="https://api.tiles.mapbox.com/mapbox.js/plugins/leaflet-locatecontrol/v0.43.0/L.Control.Locate.css">
    <link rel="stylesheet" href="/assets/leaflet-groupedlayercontrol/leaflet.groupedlayercontrol.css">
    <link rel="stylesheet" href="/assets/css/app.css">

    <link rel="apple-touch-icon" sizes="76x76" href="/assets/img/favicon-76.png">
    <link rel="apple-touch-icon" sizes="120x120" href="/assets/img/favicon-120.png">
    <link rel="apple-touch-icon" sizes="152x152" href="/assets/img/favicon-152.png">
    <link rel="icon" sizes="196x196" href="/assets/img/favicon-196.png">
    <link rel="icon" type="image/x-icon" href="/assets/img/favicon.ico">
</head>

<body>
<div class="navbar navbar-inverse navbar-fixed-top" role="navigation">
    <div class="container-fluid">
        <div class="navbar-header">
            <div class="navbar-icon-container">
                <a href="#" class="navbar-icon pull-right visible-xs" id="nav-btn"><i class="fa fa-bars fa-lg white"></i></a>
                <a href="#" class="navbar-icon pull-right visible-xs" id="sidebar-toggle-btn"><i class="fa fa-search fa-lg white"></i></a>
            </div>
            <a class="navbar-brand" href="#">RT-VRP</a>
        </div>
        <div class="navbar-collapse collapse">
            <!--<form class="navbar-form navbar-right" role="search">
                <div class="form-group has-feedback">
                    <input id="searchbox" type="text" placeholder="Search" class="form-control">
                    <span id="searchicon" class="fa fa-search form-control-feedback"></span>
                </div>
            </form>-->
            <ul class="nav navbar-nav">
                <li><a href="#" data-toggle="collapse" data-target=".navbar-collapse.in" id="about-btn"><i class="fa fa-question-circle white"></i>&nbsp;&nbsp;About</a></li>
                <li class="dropdown">
                    <a id="toolsDrop" href="#" role="button" class="dropdown-toggle" data-toggle="dropdown"><i class="fa fa-globe white"></i>&nbsp;&nbsp;Tools <b class="caret"></b></a>
                    <ul class="dropdown-menu">
                        <li><a href="#" data-toggle="collapse" data-target=".navbar-collapse.in" id="full-extent-btn"><i class="fa fa-arrows-alt"></i>&nbsp;&nbsp;Zoom To Full Extent</a></li>
                        <li><a href="#" data-toggle="collapse" data-target=".navbar-collapse.in" id="legend-btn"><i class="fa fa-picture-o"></i>&nbsp;&nbsp;Show Legend</a></li>
                        <li class="divider hidden-xs"></li>
                        <li><a href="#" data-toggle="collapse" data-target=".navbar-collapse.in" id="login-btn"><i class="fa fa-user"></i>&nbsp;&nbsp;Login</a></li>
                    </ul>
                </li>
                <li class="dropdown">
                    <a class="dropdown-toggle" id="downloadDrop" href="#" role="button" data-toggle="dropdown"><i class="fa fa-cloud-download white"></i>&nbsp;&nbsp;Download <b class="caret"></b></a>
                    <ul class="dropdown-menu">
                        <li><a href="data/boroughs.geojson" download="boroughs.geojson" target="_blank" data-toggle="collapse" data-target=".navbar-collapse.in"><i class="fa fa-download"></i>&nbsp;&nbsp;Boroughs</a></li>
                        <li><a href="data/subways.geojson" download="subways.geojson" target="_blank" data-toggle="collapse" data-target=".navbar-collapse.in"><i class="fa fa-download"></i>&nbsp;&nbsp;Subway Lines</a></li>
                        <li><a href="data/DOITT_THEATER_01_13SEPT2010.geojson" download="theaters.geojson" target="_blank" data-toggle="collapse" data-target=".navbar-collapse.in"><i class="fa fa-download"></i>&nbsp;&nbsp;Theaters</a></li>
                        <li><a href="data/DOITT_MUSEUM_01_13SEPT2010.geojson" download="museums.geojson" target="_blank" data-toggle="collapse" data-target=".navbar-collapse.in"><i class="fa fa-download"></i>&nbsp;&nbsp;Museums</a></li>
                    </ul>
                </li>
                <li class="hidden-xs"><a href="#" data-toggle="collapse" data-target=".navbar-collapse.in" id="list-btn"><i class="fa fa-list white"></i>&nbsp;&nbsp;POI List</a></li>
            </ul>
        </div><!--/.navbar-collapse -->
    </div>
</div>

<div id="container">
    <div id="sidebar">
        <div class="sidebar-wrapper">
            <div class="panel panel-default" id="features">
                <div class="panel-heading">
                    <h3 class="panel-title">Points of Interest
                        <button type="button" class="btn btn-xs btn-default pull-right" id="sidebar-hide-btn"><i class="fa fa-chevron-left"></i></button></h3>
                </div>
                <div class="panel-body">
                    <div class="row">
                        <div class="col-xs-8 col-md-8">
                            <input type="text" class="form-control search" placeholder="Filter" />
                        </div>
                        <div class="col-xs-4 col-md-4">
                            <button type="button" class="btn btn-primary pull-right sort" data-sort="feature-name" id="sort-btn"><i class="fa fa-sort"></i>&nbsp;&nbsp;Sort</button>
                        </div>
                    </div>
                </div>
                <div class="sidebar-table">
                    <table class="table table-hover" id="feature-list">
                        <thead class="hidden">
                        <tr>
                            <th>Icon</th>
                        <tr>
                        <tr>
                            <th>Name</th>
                        <tr>
                        <tr>
                            <th>Chevron</th>
                        <tr>
                        </thead>
                        <tbody class="list">
                            <tr class="feature-row" id="273" lat="40.759851" lng="-73.990618"><td style="vertical-align: middle;"><img width="16" height="18" src="/assets/img/enumerator.png"></td><td class="feature-name">45th Street Theater</td><td style="vertical-align: middle;"><i class="fa fa-chevron-right pull-right"></i></td></tr>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
    <div id="map"></div>
</div>
<!--<div id="loading">
    <div class="loading-indicator">
        <div class="progress progress-striped active">
            <div class="progress-bar progress-bar-info progress-bar-full"></div>
        </div>
    </div>
</div>-->

<script src="https://code.jquery.com/jquery-2.1.4.min.js"></script>
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/typeahead.js/0.10.5/typeahead.bundle.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/handlebars.js/3.0.3/handlebars.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/list.js/1.1.1/list.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/leaflet/0.7.7/leaflet.js"></script>
<script src="https://api.tiles.mapbox.com/mapbox.js/plugins/leaflet-markercluster/v0.4.0/leaflet.markercluster.js"></script>
<script src="https://api.tiles.mapbox.com/mapbox.js/plugins/leaflet-locatecontrol/v0.43.0/L.Control.Locate.min.js"></script>
<script src="/assets/leaflet-groupedlayercontrol/leaflet.groupedlayercontrol.js"></script>
<script src="http://momentjs.com/downloads/moment.min.js"></script>
<!--<script src="/assets/js/app.js"></script>-->

<script>
    // create the tile layer with correct attribution
    var osmUrl      = 'http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png';
    var osmAttrib   = 'Map data &copy; <a href="http://openstreetmap.org">OpenStreetMap</a> contributors';
    var osm         = new L.TileLayer(osmUrl, {minZoom: 2, maxZoom: 20, attribution: osmAttrib});

    // set up the map
    map = new L.Map('map');
    map.setView(new L.LatLng(-1.348051, 100.581783), 9);
    map.addLayer(osm);
    L.control.scale().addTo(map);

    // Cache objects
    var locationsCache = {};
    var enumeratorsCache = {};

    // Set up enumeration locations
    $.get('/vrp/locations', function(locations) {
        locations.forEach(function(location) {
            var marker = L.marker([location.lat, location.lon]).addTo(map);
            marker.bindPopup('<b>' + location.id + '</b><br>I am a job.').openPopup();

            locationsCache[location.id] = {
                'location' : location,
                'marker' : marker,
            }
        });
    });

    // Set up enumerator
    var enumeratorIcon = L.icon({
        iconUrl: '/assets/img/enumerator.png',
        iconAnchor:   [16, 35],
        popupAnchor:  [0, -35]
    });

    $.get('/vrp/enumerators', function(enumerators) {
        enumerators.forEach(function(enumerator) {
            var marker = L.marker([enumerator.lat, enumerator.lon], {icon: enumeratorIcon}).addTo(map);
            marker.bindPopup('<b>' + enumerator.id + '</b><br>I am an enumerator.').openPopup();

            enumeratorsCache[enumerator.id] = {
                'enumerator' : enumerator,
                'marker' : marker,
            }
        });

        enumerators.forEach(function(enumerator) {
            subscribeLocation(enumerator.id);
        });

        subscribeLocation(1302070006);
    });

    function subscribeLocation(eId) {
        log(eId, moment().format() + ' : ' + eId + ' is subscribing new location');

        $.get('/vrp/subscribe/' + eId, function(location) {
            if('customer' in location) {
                log(location['enumerator'], moment().format() + ' : ' + location['enumerator'] + ' got new location');
                travelToLocation(location);
            } else {
                log(location['enumerator'], moment().format() + ' : No location for ' + location['enumerator']);
                subscribeLocation(eId);
            }
        }).fail(function() {
            console.log('failed')
            log(location['enumerator'], moment().format() + ' : No location for ' + location['enumerator']);
            subscribeLocation(eId);
        });
    }

    function travelToLocation(location) {
        duration = location['duration'] * 10;
        log(location['enumerator'],
            moment().format() + ' : ' + location['enumerator'] + ' is traveling to ' + location['customer'] + ' for ' + (duration/1000) + ' secs');

        setTimeout(function(location) {
            log(location['enumerator'],
                moment().format() + ' : ' + location['enumerator'] + ' is arrived in ' + location['customer']);

            // Move enumerator marker
            latDest = location['customer-coord'][0];
            lonDest = location['customer-coord'][1];
            enumeratorsCache[location['enumerator']]['marker'].setLatLng(new L.LatLng(latDest, lonDest));

            // Create line between old an new point
            latOri = location['depot-coord'][0];
            lonOri = location['depot-coord'][1];
            new L.polyline([new L.LatLng(latOri, lonOri), new L.LatLng(latDest, lonDest)], {
                color: 'red',
                weight: 3,
                opacity: 0.5,
                smoothFactor: 1,
            }).addTo(map);

            collectingData(location);
        }, duration, location);
    }

    function collectingData(location) {
        publishVisit(location);

        duration = location['service-time'] * 10;
        log(location['enumerator'],
            moment().format() + ' : ' + location['enumerator'] + ' is collecting data in ' + location['customer']);

        setTimeout(function(location) {
            log(location['enumerator'],
                moment().format() + ' : ' + location['enumerator'] + ' is finish collecting data in ' + location['customer'] + ' for ' + (duration/1000) + ' secs');

            subscribeLocation(location['enumerator'])
        }, duration, location);
    }

    function publishVisit(location) {
        $.get('/vrp/visit/' + location['customer'] + '/by/' + location['enumerator']);
    }

    function log(enumerator, message) {
        console.log(message);

        $.ajax({
            type: "POST",
            url: '/vrp/log/' + enumerator,
            data: message,
            headers: {
                'Accept': 'text/plain',
                'Content-Type': 'text/plain'
            }
        });
    }
</script>
</body>
</html>