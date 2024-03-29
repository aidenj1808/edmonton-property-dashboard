package com.example.cmpt305finalproject.cmpt305;


import javafx.scene.chart.XYChart;

import java.util.Map;

public class LineGraphSeries {
    private XYChart.Series<String, Long> series = new XYChart.Series<String, Long>();


    public LineGraphSeries(AssessValueYearMap map) {
        map.forEach((key, value) -> series.getData().add(new XYChart.Data<String, Long>(key, value)));
    }

    public XYChart.Series<String, Long> getSeries() {
        return this.series;
    }
}
