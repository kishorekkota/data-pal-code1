package io.pivotal.pal.wehaul.rental.domain;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class RentalService {

    private final TruckAllocationService truckAllocationService;

    private final RentalTruckRepository rentalTruckRepository;
    private final TruckSizeChart truckSizeChart;


    public RentalService(TruckAllocationService truckAllocationService,
                         RentalTruckRepository rentalTruckRepository,
                         TruckSizeChart truckSizeChart) {
        this.truckAllocationService = truckAllocationService;

        this.rentalTruckRepository = rentalTruckRepository;
        this.truckSizeChart = truckSizeChart;
    }

    public void addTruck(Vin vin, int truckLength) {
        TruckSize truckSize = truckSizeChart.getSizeByTruckLength(truckLength);
        RentalTruck rentalTruck = new RentalTruck(vin, truckSize);
        rentalTruckRepository.save(rentalTruck);
    }

    @Transactional
    public RentalTruck create(String customerName, TruckSize truckSize) {

        RentalTruck rentalTruck = truckAllocationService.allocateTruck(truckSize);

        rentalTruck.reserve(customerName);

        rentalTruckRepository.save(rentalTruck);


        return rentalTruck;
    }

    @Transactional
    public void pickUp(ConfirmationNumber confirmationNumber) {
        RentalTruck rentalTruck = rentalTruckRepository.findOneByRentalConfirmationNumber(confirmationNumber.getConfirmationNumber());
        if (rentalTruck == null) {
            throw new IllegalArgumentException(String.format("No rental found for id=%s", confirmationNumber));
        }

        rentalTruck.pickUp();
        rentalTruckRepository.save(rentalTruck);
    }

    @Transactional
    public RentalTruck dropOff(ConfirmationNumber confirmationNumber, int distanceTraveled) {

        RentalTruck rentalTruck = rentalTruckRepository.findOneByRentalConfirmationNumber(confirmationNumber.getConfirmationNumber());

        if (rentalTruck == null) {
            throw new IllegalArgumentException(String.format("No rental found for id=%s", confirmationNumber));
        }

        rentalTruck.dropOff(distanceTraveled);
        rentalTruckRepository.save(rentalTruck);

        return rentalTruck;
    }

    public void preventRenting(Vin vin) {
        RentalTruck rentalTruck = rentalTruckRepository.findOne(vin);
        if (rentalTruck == null) {
            throw new IllegalArgumentException(String.format("No truck found with vin=%s", vin));
        }

        rentalTruck.preventRenting();

        rentalTruckRepository.save(rentalTruck);
    }

    public void allowRenting(Vin vin) {
        RentalTruck rentalTruck = rentalTruckRepository.findOne(vin);
        if (rentalTruck == null) {
            throw new IllegalArgumentException(String.format("No truck found with vin=%s", vin));
        }

        rentalTruck.allowRenting();

        rentalTruckRepository.save(rentalTruck);
    }

    public Collection<RentalTruck> findAll() {
        return StreamSupport
                .stream(rentalTruckRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }
}
