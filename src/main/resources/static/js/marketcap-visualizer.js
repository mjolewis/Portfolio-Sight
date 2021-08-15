document.addEventListener("DOMContentLoaded", plotData);

function plotData() {

    var dataByMarketCap = [myMicroCapData, mySmallCapData, myMidCapData, myLargeCapData, myMegaCapData]
    var stringByMarketCap = ["Micro Cap", "Small Cap", "Mid Cap", "Large Cap", "Mega Cap"]

    am4core.useTheme(am4themes_animated);

    var container = am4core.create("amchart-container", am4core.Container);
    container.width = am4core.percent(100);
    container.height = am4core.percent(100);
    container.layout = "horizontal";

    var chart = container.createChild(am4charts.PieChart);
    chart.data = []

    const sumValues = obj => Object.values(obj).reduce((a, b) => a + b, 0);

    for (let i = 0; i < dataByMarketCap.length; i++) {

        marketCapObj = {
            "Market Cap": stringByMarketCap[i],
            "Value": sumValues(dataByMarketCap[i]),
            "subData" : []
        }

        // If there's no stocks in the market cap, don't
        // display
        if(marketCapObj["Value"] == 0) {
            
            continue;
        }

        let mydata = dataByMarketCap[i];
        for (var key in mydata) {
            if (mydata.hasOwnProperty(key)) {

                newObj = {
                    "ticker": key,
                    "value": mydata[key]
                }
                marketCapObj["subData"].push(newObj);
            }
        }

        chart.data.push(marketCapObj)
    }

    console.log(chart.data)

    // Add and configure Series
    var pieSeries = chart.series.push(new am4charts.PieSeries());
    pieSeries.dataFields.value = "Value";
    pieSeries.dataFields.category = "Market Cap";
    pieSeries.slices.template.stroke = am4core.color("#fff");
    pieSeries.slices.template.strokeOpacity = 1;
    pieSeries.slices.template.states.getKey("active").properties.shiftRadius = 0;

    pieSeries.slices.template.events.on("hit", function (event) {
        selectSlice(event.target.dataItem);
    })

    var chart2 = container.createChild(am4charts.PieChart);
    chart2.width = am4core.percent(30);
    chart2.radius = am4core.percent(80);

    chart2.legend = new am4charts.Legend();

    // Add and configure Series
    var pieSeries2 = chart2.series.push(new am4charts.PieSeries());
    pieSeries2.dataFields.value = "value";
    pieSeries2.dataFields.category = "ticker";
    pieSeries2.slices.template.stroke = am4core.color("#fff");
    pieSeries2.slices.template.strokeOpacity = 1;
    pieSeries2.slices.template.states.getKey("active").properties.shiftRadius = 0;

    pieSeries2.labels.template.disabled = true;
    pieSeries2.ticks.template.disabled = true;
    pieSeries2.alignLabels = false;
    pieSeries2.events.on("positionchanged", updateLines);

    var interfaceColors = new am4core.InterfaceColorSet();

    var line1 = container.createChild(am4core.Line);
    line1.strokeDasharray = "2,2";
    line1.strokeOpacity = 0.5;
    line1.stroke = interfaceColors.getFor("alternativeBackground");
    line1.isMeasured = false;

    var line2 = container.createChild(am4core.Line);
    line2.strokeDasharray = "2,2";
    line2.strokeOpacity = 0.5;
    line2.stroke = interfaceColors.getFor("alternativeBackground");
    line2.isMeasured = false;

    var selectedSlice;

    function selectSlice(dataItem) {

        selectedSlice = dataItem.slice;

        var fill = selectedSlice.fill;

        var count = dataItem.dataContext.subData.length;
        pieSeries2.colors.list = [];
        for (var i = 0; i < count; i++) {
            pieSeries2.colors.list.push(fill.brighten(i * 2 / count));
        }

        chart2.data = dataItem.dataContext.subData;
        pieSeries2.appear();

        var middleAngle = selectedSlice.middleAngle;
        var firstAngle = pieSeries.slices.getIndex(0).startAngle;
        var animation = pieSeries.animate([{ property: "startAngle", to: firstAngle - middleAngle }, { property: "endAngle", to: firstAngle - middleAngle + 360 }], 600, am4core.ease.sinOut);
        animation.events.on("animationprogress", updateLines);

        selectedSlice.events.on("transformed", updateLines);
    }

    function updateLines() {
        if (selectedSlice) {
            var p11 = { x: selectedSlice.radius * am4core.math.cos(selectedSlice.startAngle), y: selectedSlice.radius * am4core.math.sin(selectedSlice.startAngle) };
            var p12 = { x: selectedSlice.radius * am4core.math.cos(selectedSlice.startAngle + selectedSlice.arc), y: selectedSlice.radius * am4core.math.sin(selectedSlice.startAngle + selectedSlice.arc) };

            p11 = am4core.utils.spritePointToSvg(p11, selectedSlice);
            p12 = am4core.utils.spritePointToSvg(p12, selectedSlice);

            var p21 = { x: 0, y: -pieSeries2.pixelRadius };
            var p22 = { x: 0, y: pieSeries2.pixelRadius };

            p21 = am4core.utils.spritePointToSvg(p21, pieSeries2);
            p22 = am4core.utils.spritePointToSvg(p22, pieSeries2);

            line1.x1 = p11.x;
            line1.x2 = p21.x;
            line1.y1 = p11.y;
            line1.y2 = p21.y;

            line2.x1 = p12.x;
            line2.x2 = p22.x;
            line2.y1 = p12.y;
            line2.y2 = p22.y;
        }
    }

    chart.events.on("datavalidated", function () {
        setTimeout(function () {
            selectSlice(pieSeries.dataItems.getIndex(0));
        }, 1000);
    });
}
