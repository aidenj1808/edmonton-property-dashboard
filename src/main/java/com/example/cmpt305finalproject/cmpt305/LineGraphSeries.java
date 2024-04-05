package com.example.cmpt305finalproject.cmpt305;

import javafx.scene.chart.XYChart;
import java.util.Map;


public class LineGraphSeries {
    private XYChart.Series<String, Long> series = new XYChart.Series<String, Long>();


    public LineGraphSeries(Map<String, Long> map) {
        map.entrySet().stream().sorted(Map.Entry.<String, Long>comparingByKey())
                        .forEach(entry -> series.getData().add(new XYChart.Data<String, Long>(entry.getKey(), entry.getValue())));

        //map.forEach((key, value) -> series.getData().add(new XYChart.Data<String, Long>(key, value)));
    }

    public XYChart.Series<String, Long> getSeries() {
        return this.series;
    }
}
