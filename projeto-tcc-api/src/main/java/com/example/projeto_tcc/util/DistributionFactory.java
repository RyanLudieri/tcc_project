package com.example.projeto_tcc.util;

import com.example.projeto_tcc.entity.DistributionParameter;
import com.example.projeto_tcc.enums.BestFitDistribution;
import org.apache.commons.math3.distribution.*;

public class DistributionFactory {

    public static Object createDistribution(BestFitDistribution type, DistributionParameter param) {
        return switch (type) {
            case NORMAL -> new NormalDistribution(param.getMean(), param.getStandardDeviation());
            case LOGNORMAL -> new LogNormalDistribution(param.getScale(), param.getShape());
            case POISSON -> new PoissonDistribution(param.getLambda());
            case GEOMETRIC -> new GeometricDistribution(param.getProbability());
            case EXPONENTIAL -> new ExponentialDistribution(param.getMean());
            case UNIFORM -> new UniformRealDistribution(param.getMin(), param.getMax());
            case WEIBULL -> new WeibullDistribution(param.getAlpha(), param.getBeta());
            case GAMMA -> new GammaDistribution(param.getShape(), param.getScale());
        };
    }
}

