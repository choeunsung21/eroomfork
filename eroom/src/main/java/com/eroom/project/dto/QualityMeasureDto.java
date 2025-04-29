package com.eroom.project.dto;

public class QualityMeasureDto {
    private String metric;
    private String value;

    public QualityMeasureDto(String metric, String value) {
        this.metric = metric;
        this.value = value;
    }

    public String getMetric() {
        return metric;
    }

    public String getValue() {
        return value;
    }
}
