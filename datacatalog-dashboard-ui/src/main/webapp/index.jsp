<%@ include file="includes/header.html" %>

<body id="home">

<div id="map"></div>
<div id="sidebar">
    <div id="crawling-stat">
        <p>Data Catalog v1.0</p>

        <div id="data-products">
            <p>Data Products</p>
            <ul>
                <li>NAM</li>
                <li>NEXTRAD Level II</li>
            </ul>
        </div>
        <div id="collection-stat">
            <p>Content</p>
            <ul>
                <li>14 Collections</li>
                <li>2543 Files</li>
            </ul>
        </div>
    </div>
    <div id="indexing-stat">
        <p>Indexing NWS/NEXTRAD2 and currently in 'Harvesting' stage..</p>
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
