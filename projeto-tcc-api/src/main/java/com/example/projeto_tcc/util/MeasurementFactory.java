package com.example.projeto_tcc.util;

import com.example.projeto_tcc.entity.DurationMeasurement;
import com.example.projeto_tcc.enums.TimeScale;
import org.apache.commons.math3.distribution.IntegerDistribution;
import org.apache.commons.math3.distribution.RealDistribution;

import java.util.ArrayList;
import java.util.List;

public class MeasurementFactory {

    public static List<DurationMeasurement> createRealDistributionForDuration(RealDistribution realDistribution, int sampleSize, TimeScale timeScale) {
        List<DurationMeasurement> durationMeasurements = new ArrayList<>();
        for (int i = 0; i < sampleSize; i++) {
            double rawValue = realDistribution.sample();
            double scaledValue = scaleValue(rawValue, timeScale);
            double value = Math.round(scaledValue * 100.0) / 100.0;

            DurationMeasurement measurement = new DurationMeasurement();
            measurement.setValue(value);
            durationMeasurements.add(measurement);
        }
        return durationMeasurements;
    }

    public static List<DurationMeasurement> createIntegerDistributionForDuration(IntegerDistribution integerDistribution, int sampleSize, TimeScale timeScale) {
        List<DurationMeasurement> durationMeasurements = new ArrayList<>();
        for (int i = 0; i < sampleSize; i++) {
            int rawValue = integerDistribution.sample();
            double scaledValue = scaleValue(rawValue, timeScale);

            DurationMeasurement measurement = new DurationMeasurement();
            measurement.setValue(scaledValue);
            durationMeasurements.add(measurement);
        }
        return durationMeasurements;
    }

    // Novo método para distribuição CONSTANT
    public static List<DurationMeasurement> createConstantDurationMeasurements(double constantValue, int sampleSize, TimeScale timeScale) {
        List<DurationMeasurement> durationMeasurements = new ArrayList<>();
        double scaledValue = scaleValue(constantValue, timeScale);
        double value = Math.round(scaledValue * 100.0) / 100.0;

        for (int i = 0; i < sampleSize; i++) {
            DurationMeasurement measurement = new DurationMeasurement();
            measurement.setValue(value);
            durationMeasurements.add(measurement);
        }

        return durationMeasurements;
    }

    // Método utilitário interno:
    private static double scaleValue(double value, TimeScale timeScale) {
        TimeScale ts = (timeScale == null) ? TimeScale.SECONDS : timeScale;
        return switch (ts) {
            case SECONDS -> value;
            case MINUTES -> value * 60;
            case HOURS -> value * 3600;
            case DAYS -> value * 86400;
        };
    }

}
