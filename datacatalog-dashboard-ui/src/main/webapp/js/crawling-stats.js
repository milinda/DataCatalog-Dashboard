var crawlDataUpdater = {};

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
    // Adding the button responsible for toggling the widget
    this.toolBar.addButton({id: "crawl-stat-toggle", label: "Crawl Stats"}).click(
        function() {
            $("#crawling-stat").fadeToggle("slow", function() {
                if ($("#crawling-stat").css("display") == "none") {
                    clearInterval(crawlDataUpdater);
                } else {
                    crawlDataUpdater = setInterval(function() {
                        cstat.drawCrawlStatChart()
                    }, 100);
                }
            });
            return false;
        }
    );

    this.toolBar.addButton({id: "get-data-products", label: "Get Data Products"}).click(
        function(e) {
            e.preventDefault();

            var map = dboard.getMap();
            var boxes = new OpenLayers.Layer.Vector("boxes");

            for (feature in dboard.currentSelectedFeatures) {
                var featureBounds = dboard.currentSelectedFeatures[feature].geometry.getVertices();
                alert(featureBounds);
                //boxes.addFeatures(new OpenLayers.Feature.Vector(OpenLayers.Bounds.fromArray(featureBounds).toGeometry()));
            }
            map.addLayer(boxes);
        }
    )
}

CrawlingStats.prototype.drawCrawlStatChart = function() {
    var data = [];
    for (var i = 0; i < 40; i += 1)
        data.push([i,  Math.sin(i) + Math.random() * .5 + 2]);

    $.plot($("#crawl-stat-chart"), [
        {data: data, bars: {show: true, lineWidth: 0 , fillColor:"#FFFFFF", barWidth:0.3}, yaxes:{show: false}}
    ]);
}





