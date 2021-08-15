document.addEventListener("DOMContentLoaded", plotData);

function plotData() {

    // Themes begin
    am4core.useTheme(am4themes_animated);
    // Themes end

    var chart = am4core.create("amchart-container", am4charts.PieChart);

    for (var key in mydata) {
        if (mydata.hasOwnProperty(key)) {

            newObj = {
                "ticker": key,
                "value": mydata[key]
            }
            chart.data.push(newObj);
        }
    }

    chart.legend = new am4charts.Legend();

    // Add and configure Series
    var series = chart.series.push(new am4charts.PieSeries());
    series.dataFields.value = "value";
    series.dataFields.category = "ticker";
    series.slices.template.stroke = am4core.color("#fff");
    series.slices.template.strokeOpacity = 1;

    // This creates initial animation
    series.hiddenState.properties.opacity = 1;
    series.hiddenState.properties.endAngle = -90;
    series.hiddenState.properties.startAngle = -90;

    chart.hiddenState.properties.radius = am4core.percent(0);
}
