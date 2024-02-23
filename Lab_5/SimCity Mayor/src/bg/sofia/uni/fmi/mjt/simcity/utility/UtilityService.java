package bg.sofia.uni.fmi.mjt.simcity.utility;

import bg.sofia.uni.fmi.mjt.simcity.property.billable.Billable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class UtilityService implements UtilityServiceAPI {
    private final Map<UtilityType, Double> taxRates;

    public UtilityService(Map<UtilityType, Double> taxRates) {
        this.taxRates = taxRates;
    }

    private double applyTaxes(UtilityType utilityType, double price) {
        return taxRates.get(utilityType) * price;
    }

    @Override
    public <T extends Billable> double getUtilityCosts(UtilityType utilityType, T billable) {
        if (utilityType == null || billable == null) {
            throw new IllegalArgumentException("#getUtilityCosts args could not be null");
        }

        switch (utilityType) {
            case WATER:
                return applyTaxes(UtilityType.WATER, billable.getWaterConsumption());

            case ELECTRICITY:
                return applyTaxes(UtilityType.ELECTRICITY, billable.getElectricityConsumption());

            case NATURAL_GAS:
                return applyTaxes(UtilityType.NATURAL_GAS, billable.getNaturalGasConsumption());

            default:
                throw new IllegalArgumentException("#getUtilityCosts utility type is wrong" + utilityType);
        }
    }

    @Override
    public <T extends Billable> double getTotalUtilityCosts(T billable) {
        if (billable == null) {
            throw new IllegalArgumentException("#getTotalUtilityCosts args could not be null");
        }

        double sum = 0;

        for (UtilityType utilityType : UtilityType.values()) {
            sum += getUtilityCosts(utilityType, billable);
        }

        return sum;
    }

    @Override
    public <T extends Billable> Map<UtilityType, Double> computeCostsDifference(T firstBillable, T secondBillable) {
        if (firstBillable == null || secondBillable == null) {
            throw new IllegalArgumentException("#computeCostsDifference args could not be null");
        }
        Map<UtilityType, Double> costsDifferenceByUtilityType = new HashMap<>();

        for (UtilityType utilityType : UtilityType.values()) {
            double costFirstBillable = getUtilityCosts(utilityType, firstBillable);
            double costSecondBillable = getUtilityCosts(utilityType, secondBillable);
            double difference = Math.abs(costFirstBillable - costSecondBillable);

            costsDifferenceByUtilityType.put(utilityType, difference);
        }

        return Collections.unmodifiableMap(costsDifferenceByUtilityType);
    }
}
