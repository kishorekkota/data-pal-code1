package io.pivotal.pal.wehaul.fleet.domain;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class FleetService {

    private final FleetTruckRepository fleetTruckRepository;

    public FleetService(FleetTruckRepository fleetTruckRepository) {
        this.fleetTruckRepository = fleetTruckRepository;
    }

    public void buyTruck(Vin vin, int odometerReading, int truckLength) {
        FleetTruck fleetTruck = new FleetTruck(vin, odometerReading, truckLength);

        fleetTruckRepository.save(fleetTruck);
    }

    public void sendForInspection(Vin vin) {
        FleetTruck fleetTruck = fleetTruckRepository.findOne(vin);

        if (fleetTruck == null) {
            throw new IllegalArgumentException(String.format("No truck found with VIN=%s", vin));
        }

        fleetTruck.sendForInspection();

        fleetTruckRepository.save(fleetTruck);


    }

    public void returnFromInspection(Vin vin, String notes, int odometerReading) {
        FleetTruck fleetTruck = fleetTruckRepository.findOne(vin);

        if (fleetTruck == null) {
            throw new IllegalArgumentException(String.format("No truck found with VIN=%s", vin));
        }

        fleetTruck.returnFromInspection(notes, odometerReading);

        fleetTruckRepository.save(fleetTruck);
    }

    public void removeFromYard(Vin vin) {
        FleetTruck truck = fleetTruckRepository.findOne(vin);

        if (truck == null) {
            throw new IllegalArgumentException(String.format("No truck found with VIN=%s", vin));
        }

        truck.removeFromYard();

        fleetTruckRepository.save(truck);
    }

    public void returnToYard(Vin vin, int distanceTraveled) {
        FleetTruck truck = fleetTruckRepository.findOne(vin);

        if (truck == null) {
            throw new IllegalArgumentException(String.format("No truck found with VIN=%s", vin));
        }

        truck.returnToYard(distanceTraveled);

        fleetTruckRepository.save(truck);
    }

    public Collection<FleetTruck> findAll() {

        return StreamSupport.stream(fleetTruckRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }
}
