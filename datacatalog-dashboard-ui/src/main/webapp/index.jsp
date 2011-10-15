<%@ include file="includes/header.html" %>

<body id="home">

<div id="map"></div>
<div id="crawling-stat">
    <p>Crawling Statistics</p>

    <div id="crawl-stat-chart">

    </div>
</div>

<div id="tools">

</div>
<script type="text/javascript">

    var dashboard = new Dashboard("map");
    dashboard.initialize();
    var toolBar = new ToolBar("tools");
    var crawlingStats = new CrawlingStats(dashboard, toolBar);

</script>

</body>

<%@ include file="includes/footer.html" %>
