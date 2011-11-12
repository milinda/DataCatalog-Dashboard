<%@ include file="includes/header.html" %>

<body id="home">

<div id="map"></div>
<div id="sidebar">
    <div id="crawling-stat">
        <p>Data Catalog v1.0</p>

        <div id="data-products">
            <p>Data Products</p>
            <ul id="data-products-list">

            </ul>
        </div>
        <div id="collection-stat">
            <p>Content</p>
            <ul>
                <li id="summary-collections">0 Collections</li>
                <li id="summary-files">0 Files</li>
            </ul>
        </div>
    </div>
    <div id="indexing-stat">
        <p>Indexing NWS/NEXTRAD2 and currently in 'Harvesting' stage..</p>
    </div>
</div>


<div id="tools">

</div>
<div id="dialog" title="Crawling History">
	<div id="time-line" style=" border: 1px solid #aaa"></div>
</div>
<script type="text/javascript">

    var dashboard = new Dashboard("map");
    dashboard.initialize();
    var toolBar = new ToolBar("tools");
    var crawlingStats = new CrawlingStats(dashboard, toolBar);

    function updateCurrentState(){
        $.get("api/perflog/currentstate", function(data){
            $("#indexing-stat").html("<p>" + data + "</p>");
            dashboard.initSidebar();
        });

    }

    function updateSummary(){
        $.get("api/dataproducts/summary", function(data){
            $("#summary-collections").html(data.collections + " Collections");
            $("#summary-files").html(data.files + " Files");
        })
    }

    updateCurrentState();
    updateSummary();
    setInterval(updateCurrentState, 60000);
    setInterval(updateSummary, 300000);
</script>

<div id="radar-dialog-parent">

</div>
</body>

<%@ include file="includes/footer.html" %>
