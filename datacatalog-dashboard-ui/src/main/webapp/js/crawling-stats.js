var crawlDataUpdater = {};

function Point(x, y) {
    this.x = x;
    this.y = y;
}

function Polygon() {
    this.points = new Array();
}

Polygon.prototype.addPoint = function(point) {
    this.points.push(point);
}

function CrawlingStats(dashboard, toolBar) {
    this.indexStatChartVisible = false;
    this.availableDataProductsVisible = false;
    this.toolBar = toolBar;
    this.dashboard = dashboard;

    this.addCrawlingStatsWidget();
}

// Remove the widgets from index.html. Widgets will add dynamically through javascript.
// Adds crawling stat widget to main body of the dashboard and setup even listeners.
CrawlingStats.prototype.addCrawlingStatsWidget = function() {
    var cstat = this;
    var dboard = this.dashboard;

    this.toolBar.addButton({id: "get-data-products", label: "Get Data Products"}).click(
        function(e) {
            function getPolygon(vertices) {
                var polygon = new Polygon();
                for (vertice in vertices) {
                    vertices[vertice].transform(new OpenLayers.Projection("EPSG:900913"), new OpenLayers.Projection("EPSG:4326"));
                    polygon.addPoint(new Point(vertices[vertice].x, vertices[vertice].y));
                }

                return polygon;
            }

            e.preventDefault();

            var map = dboard.getMap();
            var boxes = new OpenLayers.Layer.Vector("boxes");
            var radarLayers = map.getLayersByName("Radars");
            if (radarLayers && radarLayers.length > 0) {
                for (var layerIndex in radarLayers) {
                    map.removeLayer(radarLayers[layerIndex]);
                }
            }
            $.blockUI({ message: '<h1 class="block"><img src="img/2.gif" /> Loading...</h1>' });
            for (var featureIndex in dboard.currentSelectedFeatures) {
                var feature = dboard.currentSelectedFeatures[featureIndex];
                var featureBounds = feature.geometry.getVertices();
                $.ajax({
                    type:"POST",
                    url: "api/dataproducts/inArea",
                    data: JSON.stringify(getPolygon(featureBounds)),
                    success: function(data) {
                        $.unblockUI();
                        if (data) {
                            var collections = data.collections;
                            var layers = new Array();
                            dboard.startAddingRadars();
                            if (collections && !(typeof collections === "string")) {
                                for (var item in collections) {
                                    $.get('nexradstation', {stationId: collections[item]}, function(stationDetails) {
                                        var featureCollection = {"type" : "FeatureCollection", "features": []};
                                        featureCollection["features"].push(stationDetails);
                                        dboard.addRadar(featureCollection, layers, collections.length);
                                    });
                                }
                            } else {
                                $.get('nexradstation', {stationId: collections[item]}, function(stationDetails) {
                                    var featureCollection = {"type" : "FeatureCollection", "features": []};
                                    featureCollection["features"].push(stationDetails);
                                    dboard.addRadar(featureCollection, layers);
                                });
                            }
                            //dboard.doneAddingRadars();
                        } else {
                            alert("No data collections found in selected area.");
                        }

                        var layer = feature.layer;
                        layer.removeFeatures([feature]);
                        delete feature;
                    },
                    contentType:"application/json; charset=utf-8"
                });
            }
            map.addLayer(boxes);
        }
    );

    this.toolBar.addButton({id: "clear-selection", label: "Clear Selection"}).click(
        function(e) {
            e.preventDefault();

            for (var featureIndex in dboard.currentSelectedFeatures) {
                var feature = dboard.currentSelectedFeatures[featureIndex];
                var layer = feature.layer;
                layer.removeFeatures([feature]);
                delete feature; // Deleting from current selection map.
            }
        }
    );

    this.toolBar.addButton({id: "get-history", label: "Get Crawling History"}).click(
        function(e) {
            e.preventDefault();
            var docWidth = $(document).width();
            var docHieght = $(document).height();
            var dialogWidth = docWidth * 0.6;
            var dialogHeight = docHieght * 0.6 + 40;
            $("#dialog").dialog({
                modal: true,
                minWidth: dialogWidth,
                minHeight: dialogHeight,
                open: function(event, ui) {
                    var timelineHeight = docHieght * 0.6;

                    $("#time-line").height(timelineHeight);
                    var eventSource = new Timeline.DefaultEventSource();
                    var bandInfos = [
                        Timeline.createBandInfo({
                            eventSource:    eventSource,
                            width:          "70%",
                            date:           "Nov 11 2011 00:00:00 GMT",
                            intervalUnit:   Timeline.DateTime.DAY,
                            intervalPixels: 100
                        }),
                        Timeline.createBandInfo({
                            overview: true,
                            eventSource:    eventSource,
                            width:          "30%",
                            date:           "Nov 11 2011 00:00:00 GMT",
                            intervalUnit:   Timeline.DateTime.MONTH,
                            intervalPixels: 200
                        })
                    ];

                    bandInfos[1].syncWith = 0;
                    bandInfos[1].highlight = true;
                    tl = Timeline.create(document.getElementById("time-line"), bandInfos);

                    var diag = this;
                    $.get("api/perflog/history", function(data) {
                        if (data.error) {
                            alert(data.error);
                            diag.close();
                        } else {
                            eventSource.loadJSON(data, document.location.href);
                        }
                    }, "json");
                }
            });
        }
    );

    this.toolBar.addButton({id: "remove-radars", label:"Clear Map"}).click(
        function(e) {
            e.preventDefault();
            var rlayer = dboard.map.getLayersByName("Radars");
            if (rlayer && rlayer.length > 0) {
                dboard.map.removeLayer(rlayer[0]);
            }
        }
    );
}

CrawlingStats.prototype.drawCrawlStatChart = function() {
    var data = [];
    for (var i = 0; i < 40; i += 1)
        data.push([i,  Math.sin(i) + Math.random() * .5 + 2]);

    $.plot($("#crawl-stat-chart"), [
        {data: data, bars: {show: true, lineWidth: 0 , fillColor:"#FFFFFF", barWidth:0.3}, yaxes:{show: false}}
    ]);
}





