function ToolBar(id) {
    var toolBarElementSelector = "#" + id;

    this.toolBarContainer = $(toolBarElementSelector);
    this.buttonCSSClass = "tool-bar-btn";
    this.buttonContainerID = "button-container";
    // Adding tool bar button container div
    this.toolBarContainer.append("<div id='" + this.buttonContainerID + "'></div>");
    this.buttonContainer = $("#" + this.buttonContainerID);
}

ToolBar.prototype.addButton = function(options) {
    this.buttonContainer.append("<div class='" + this.buttonCSSClass + "'><a href=''#' id='" +
        options.id + "'>" + options.label + "</a></div>");
    // Method will return button object it self and module which add button can bind event
    // handlers to that object.
    return $("#" + options.id);
}