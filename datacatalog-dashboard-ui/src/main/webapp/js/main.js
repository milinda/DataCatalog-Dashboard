function Dashboard(container) {
    this.containerElementId = container;
    this.map = {};
    this.mapLayer = {};
    this.maxExtent = new OpenLayers.Bounds(-20037508, -20037508, 20037508, 20037508);
    this.restrictedExtent = this.maxExtent.clone();
    this.maxResolution = 156543.0339;

    this.mapOptions = {
        projection: new OpenLayers.Projection("EPSG:900913"),
        displayProjection: new OpenLayers.Projection("EPSG:4326"),
        units: "m",
        numZoomLevels: 18,
        maxResolution: this.maxResolution,
        maxExtent: this.maxExtent,
        restrictedExtent: this.restrictedExtent,
        controls:[
            new OpenLayers.Control.Navigation(
                {dragPanOptions: {enableKinetic: true}}
            )
        ]
    }
}

Dashboard.prototype.initialize = function() {
    this.map = new OpenLayers.Map(this.containerElementId, this.mapOptions);
    this.mapLayer = new OpenLayers.Layer.Google(
        "Google Streets",
        {sphericalMercator: true});
    var gphy = new OpenLayers.Layer.Google(
        "Google Physical",
        {type: google.maps.MapTypeId.TERRAIN, visibility: false}
    );

    this.map.addLayer(gphy);


    var projection = new OpenLayers.Projection("EPSG:4326");
    var point = new OpenLayers.LonLat(-94, 38);
    point.transform(projection, this.map.getProjectionObject());

    this.map.setCenter(point, 4);

    //this.initializeInfo();
    //this.initToolBar();
    this.testStateSelection();
}


Dashboard.prototype.initializeInfo = function() {
    $("#info").css("bottom", $("#tool-bar").height() + 50);
    $("#info").css("width", $("#tool-bar").width() - 3);
}

Dashboard.prototype.initToolBar = function() {
    var dashboard = this;
    $("#zooming-controls button:first").button({
        icons: {
            primary: "ui-icon-plus"
        },
        text: false
    }).click(
        function() {
            dashboard.map.zoomIn();
        }).next().button({
            icons: {
                primary: "ui-icon-minus"
            },
            text: false
        }).click(function() {
            dashboard.map.zoomOut();
        });

    $("#zooming-controls").buttonset();

    $("#other-controls button:first").button({
        icons: {
            primary: "ui-icon-refresh"
        }
    }).next().button({
            icons: {
                primary: "ui-icon-info"
            }
        });

    this.initializeRadarToggle();
}

Dashboard.prototype.initializeRadarToggle = function() {
    $("#check").button({
        icons: {
            primary: "ui-icon-signal-diag"
        }
    }).click(
        function() {
            var checked = $(this).attr("checked");

            if (checked == "checked") {
                dashboard.addFeatures();
                dashboard.testStateSelection();
                dashboard.map.refresh();
            } else {
                dashboard.removeFeature();
            }

        }).next();
}

Dashboard.prototype.testVectorLayer = function() {
    // allow testing of specific renderers via "?renderer=Canvas", etc
    var renderer = OpenLayers.Util.getParameters(window.location.href).renderer;
    renderer = (renderer) ? [renderer] : OpenLayers.Layer.Vector.prototype.renderers;

    var vectorLayer = new OpenLayers.Layer.Vector("Simple Geometry", {
        projection:new OpenLayers.Projection("EPSG:4326"),
        styleMap: new OpenLayers.StyleMap({
            'default': new OpenLayers.Style(null, {
                rules: [
                    new OpenLayers.Rule({
                        symbolizer: {
                            graphic: false,
                            label: "Label for invisible point",
                            labelSelect: true,
                            fontStyle: "italic"
                        },
                        filter: new OpenLayers.Filter.Comparison({
                            type: "==",
                            property: "topic",
                            value: "point_invisible"
                        })
                    }),
                    new OpenLayers.Rule({
                        symbolizer: {
                            stroke: true,
                            fill: true,
                            label: "Polygon with stroke and fill defaults"
                        },
                        filter: new OpenLayers.Filter.Comparison({
                            type: "==",
                            property: "topic",
                            value: "polygon_defaults"
                        })
                    }),
                    new OpenLayers.Rule({
                        symbolizer: {
                            stroke: true,
                            fill: false,
                            label: "Point without fill",
                            labelAlign: "rb",
                            fontColor: "#ff0000",
                            fontOpacity: 0.4
                        },
                        filter: new OpenLayers.Filter.Comparison({
                            type: "==",
                            property: "topic",
                            value: "point_nofill"
                        })
                    })
                ]
            })
        }),
        renderers: renderer
    });

    // create a point feature
    var point = new OpenLayers.Geometry.Point(-111.04, 45.68);
    var pointFeature = new OpenLayers.Feature.Vector(point);
    pointFeature.attributes = {
        topic: "point_invisible"
    };

    // create a polygon feature from a linear ring of points
    var pointList = [];
    for (var p = 0; p < 6; ++p) {
        var a = p * (2 * Math.PI) / 7;
        var r = Math.random(1) + 1;
        var newPoint = new OpenLayers.Geometry.Point(point.x + 5 + (r * Math.cos(a)),
            point.y + 5 + (r * Math.sin(a)));
        pointList.push(newPoint);
    }
    pointList.push(pointList[0]);

    var linearRing = new OpenLayers.Geometry.LinearRing(pointList);
    var polygonFeature = new OpenLayers.Feature.Vector(
        new OpenLayers.Geometry.Polygon([linearRing]));
    polygonFeature.attributes = {
        topic: "polygon_defaults"
    };

    multiFeature = new OpenLayers.Feature.Vector(
        new OpenLayers.Geometry.Collection([
            new OpenLayers.Geometry.LineString([
                new OpenLayers.Geometry.Point(-105, 40),
                new OpenLayers.Geometry.Point(-95, 45)
            ]),
            new OpenLayers.Geometry.Point(-105, 40)
        ]),
        {
            topic: "point_nofill"
        });

    this.map.addLayer(vectorLayer);
    vectorLayer.drawFeature(multiFeature);
    vectorLayer.addFeatures([pointFeature, polygonFeature, multiFeature]);
    var select = new OpenLayers.Control.SelectFeature(vectorLayer, {
        selectStyle: OpenLayers.Util.extend(
            {fill: true, stroke: true},
            OpenLayers.Feature.Vector.style["select"])
    });
    this.map.addControl(select);
    select.activate();
}


Dashboard.prototype.addFeatures = function() {
    var db = this;
    this.sprintersLayer = new OpenLayers.Layer.Vector("Sprinters", {
        styleMap: new OpenLayers.StyleMap({
            externalGraphic: "/public/images/radar-2.png",
            graphicOpacity: 1.0,
            graphicWith: 16,
            graphicHeight: 26,
            graphicYOffset: -26
        })
    });

    var sprinters = this.getFeatures();
    this.sprintersLayer.addFeatures(sprinters);

    function onSelectFeatureFunction(feature) {
        var b = feature.geometry.bounds;
        var centerPixel = this.map.getPixelFromLonLat(b.getCenterLonLat());

        $("#dialog-parent").html("<div id='dialog' style='padding:5px;display: none;font-size: 11px;font-family: Verdana, sans-serif;' title='Data Catalog Dashboard'><p>" +
            feature.data.City +
            "</p><div id='chart' style='width: 350px; height: 200px;'></div></div>");
        $("#dialog").dialog({
            autoOpen: false,
            show: "blind",
            hide: "blind",
            position: [centerPixel.x + 10, centerPixel.y + 5],
            close: function(event, ui) {
                $(this).dialog('destroy').remove();
            }
        });
        $("#dialog").dialog("open");
        db.drawChart();
    }

    this.map.addLayer(this.sprintersLayer);

    var selectControl = new OpenLayers.Control.SelectFeature(this.sprintersLayer, {
        autoActivate:true,
        onSelect: onSelectFeatureFunction});

    this.map.addControlToMap(selectControl);
    selectControl.activate();

}

Dashboard.prototype.removeFeature = function() {
    if (this.sprintersLayer) {
        this.map.removeLayer(this.sprintersLayer);
    }
}

Dashboard.prototype.getFeatures = function() {
    var features = {
        "type": "FeatureCollection",
        "features": [
            { "type": "Feature", "geometry": {"type": "Point", "coordinates": [-7909900, 5215100]},
                "properties": {"Name": "Christopher Schmidt", "Country":"United States of America", "City":"Boston"}},
            { "type": "Feature", "geometry": {"type": "Point", "coordinates": [-12362007.067301,5729082.2365672]},
                "properties": {"Name": "Tim Schaub", "Country":"United States of America", "City":"Bozeman"}},
            { "type": "Feature", "geometry": {"type": "Point", "coordinates": [-9577337.067301,4829082.2365672]},
                "properties": {"Name": "Tim Schaub", "Country":"United States of America", "City":"Indianapolis"}}
        ]
    };


    var reader = new OpenLayers.Format.GeoJSON();

    return reader.read(features);
}

Dashboard.prototype.testStateSelection = function() {
    var context = {
        getColour: function(feature) {
            return feature.attributes["colour"];
        }
    };

    var template = {
        fillOpacity: 0.3,
        strokeColor: "#555555",
        strokeWidth: 1,
        strokeDashstyle: "dash",
        fillColor: "${getColour}"
    };

    var style = new OpenLayers.Style(template, {context: context});
    var styleMap = new OpenLayers.StyleMap({'default': style});

    var vectors = new OpenLayers.Layer.Vector("vector", {styleMap: styleMap});
    this.map.addLayer(vectors);

    $.getJSON("states", function(data) {
        var reader = new OpenLayers.Format.GeoJSON({
            'internalProjection': new OpenLayers.Projection("EPSG:900913"),
            'externalProjection': new OpenLayers.Projection("EPSG:4326")
        });
        vectors.addFeatures(reader.read(data));

    });

    var report = function(e) {
        OpenLayers.Console.log(e.type, e.feature.id);
    };

    var highlightCtrl = new OpenLayers.Control.SelectFeature(vectors, {
        hover: true,
        highlightOnly: true,
        renderIntent: "temporary",
        eventListeners: {
            beforefeaturehighlighted: report,
            featurehighlighted: report,
            featureunhighlighted: report
        }
    });

    var selectCtrl = new OpenLayers.Control.SelectFeature(
        vectors,
        {
            clickout: true, toggle: false,
            multiple: false, hover: false,
            toggleKey: "ctrlKey", // ctrl key removes from selection
            multipleKey: "shiftKey", // shift key adds to selection
            box: true
        }
    )

    vectors.events.on({
        'featureselected': function(feature) {
        },
        'featureunselected': function(feature) {
        }
    });


    this.map.addControl(highlightCtrl);
    this.map.addControl(selectCtrl);

    highlightCtrl.activate();
    selectCtrl.activate();
}
