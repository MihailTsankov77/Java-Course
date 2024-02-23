package bg.sofia.uni.fmi.mjt.simcity.plot;

import bg.sofia.uni.fmi.mjt.simcity.exception.BuildableAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.simcity.exception.BuildableNotFoundException;
import bg.sofia.uni.fmi.mjt.simcity.exception.InsufficientPlotAreaException;
import bg.sofia.uni.fmi.mjt.simcity.property.buildable.Buildable;

import java.util.HashMap;
import java.util.Map;

public class Plot<E extends Buildable> implements PlotAPI<E> {

    private final int buildableArea;
    private final HashMap<String, E> buildings = new HashMap<>();
    private int remainingBuildableArea;

    public Plot(int buildableArea) {
        this.remainingBuildableArea = buildableArea;
        this.buildableArea = buildableArea;
    }

    @Override
    public void construct(String address, E buildable) {
        if (address == null || address.isBlank() || buildable == null) {
            throw new IllegalArgumentException("#construct args could not be null");
        }

        if (buildings.containsKey(address)) {
            throw new BuildableAlreadyExistsException(address);
        }

        if (remainingBuildableArea - buildable.getArea() < 0) {
            throw new InsufficientPlotAreaException(remainingBuildableArea);
        }

        remainingBuildableArea -= buildable.getArea();

        buildings.put(address, buildable);
    }

    @Override
    public void constructAll(Map<String, E> buildables) {
        if (buildables == null || buildables.isEmpty()) {
            throw new IllegalArgumentException("#constructAll args could not be null");
        }

        for (String address : buildables.keySet()) {
            construct(address, buildables.get(address));
        }
    }

    @Override
    public void demolish(String address) {
        if (address == null || address.isBlank()) {
            throw new IllegalArgumentException("#demolish args could not be null");
        }

        if (!buildings.containsKey(address)) {
            throw new BuildableNotFoundException(address);
        }

        Buildable removedBuilding = buildings.remove(address);
        remainingBuildableArea += removedBuilding.getArea();
    }

    @Override
    public void demolishAll() {
        buildings.clear();
        remainingBuildableArea = buildableArea;
    }

    @Override
    public Map<String, E> getAllBuildables() {
        return Map.copyOf(buildings);
    }

    @Override
    public int getRemainingBuildableArea() {
        return remainingBuildableArea;
    }

}
