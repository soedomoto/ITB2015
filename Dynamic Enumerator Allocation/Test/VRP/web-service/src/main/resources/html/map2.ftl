<html>
<head>
    <title>Map</title>
    <link rel="stylesheet" href="https://unpkg.com/leaflet@1.0.2/dist/leaflet.css" />
    <script src="https://unpkg.com/leaflet@1.0.2/dist/leaflet.js"></script>
</head>
<body>
    <div id="map" style="height: 600px; border: 1px solid #AAA;"></div>

    <script>
        // create the tile layer with correct attribution
        var osmUrl='http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png';
        var osmAttrib='Map data © <a href="http://openstreetmap.org">OpenStreetMap</a> contributors';
        var osm = new L.TileLayer(osmUrl, {minZoom: 2, maxZoom: 20, attribution: osmAttrib});

        // set up the map
        map = new L.Map('map');
        // start the map in South-East England
        map.setView(new L.LatLng(-1.348051, 100.581783), 9);
        map.addLayer(osm);
        L.control.scale().addTo(map);

        <#if enumerators??>
            <#list enumerators as enumerator>
                var marker = L.marker([${enumerator.lat}, ${enumerator.lon}]).addTo(map);
                marker.bindPopup("<b>${enumerator.id}</b><br>I am an enumerator.").openPopup();
            </#list>

            map.fitBounds(map.getBounds());
        </#if>
    </script>
</body>
</html>