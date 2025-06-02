package com.example.projeto_tcc.util;

import com.example.projeto_tcc.entity.DurationMeasurement;
import org.apache.commons.math3.distribution.RealDistribution;

import java.util.ArrayList;
import java.util.List;

public class MeasurementFactory {

    public static List<DurationMeasurement> createRealDistributionForDuration(RealDistribution realDistribution, int sampleSize) {
        List<DurationMeasurement> durationMeasurements = new ArrayList<>();
        for (int i = 0; i < sampleSize; i++) {
            double value = Math.round(realDistribution.sample() * 100.0) / 100.0;
            DurationMeasurement measurement = new DurationMeasurement();
            measurement.setValue(value);
            durationMeasurements.add(measurement);
        }
        return durationMeasurements;
    }
}

