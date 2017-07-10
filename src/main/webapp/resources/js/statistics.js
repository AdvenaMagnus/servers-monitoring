/**
 * Created by Grey on 29.06.2017.
 */

window.onload = function() {
    google.charts.load("current", {packages: ["timeline"]});
    google.charts.setOnLoadCallback(drawChart);
    function drawChart() {
        addNew('example7.1');
        addNew('example7.2');
    }
}

function addNew(name) {
    var container = document.getElementById(name);
    var chart = new google.visualization.Timeline(container);
    var dataTable = new google.visualization.DataTable();
    dataTable.addColumn({type: 'string', id: 'Room'});
    dataTable.addColumn({type: 'string', id: 'Name'});
    dataTable.addColumn({type: 'date', id: 'Start'});
    dataTable.addColumn({type: 'date', id: 'End'});
    dataTable.addRows([
        ['Magnolia Room', 'Offline', new Date(2017, 0, 0, 14, 0, 0), new Date(2017, 0, 0, 15, 0, 0)],
        ['Magnolia Room', 'Online', new Date(2017, 0, 0, 15, 0, 0), new Date(2017, 0, 0, 16, 0, 0)],
        ['Magnolia Room', 'Offline', new Date(2017, 0, 0, 16, 0, 0), new Date(2017, 0, 0, 17, 0, 0)]]);

    var options = {
        timeline: {showRowLabels: false},
        avoidOverlappingGridLines: false
    };

    chart.draw(dataTable, options);
}