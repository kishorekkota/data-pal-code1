package io.pivotal.pal.wehaul.application;

import io.pivotal.pal.wehaul.fleet.domain.event.FleetTruckPurchased;
import io.pivotal.pal.wehaul.fleet.domain.event.FleetTruckReturnedFromInspection;
import io.pivotal.pal.wehaul.fleet.domain.event.FleetTruckSentForInspection;
import io.pivotal.pal.wehaul.rental.domain.RentalService;
import io.pivotal.pal.wehaul.rental.domain.Vin;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Async
public class FleetEventListener {

    private final RentalService rentalService;

    public FleetEventListener(RentalService rentalService) {
        this.rentalService = rentalService;
    }

    @EventListener
    public void onFleetTruckPurchased(FleetTruckPurchased event) {
        rentalService.addTruck(Vin.of(event.getVin()), event.getTruckLength());
    }

    @EventListener
    public void onFleetTruckSentForInspection(FleetTruckSentForInspection event) {
        rentalService.preventRenting( Vin.of(event.getVin()));
    }

    @EventListener
    public void onFleetTruckReturnedFromInspection(FleetTruckReturnedFromInspection event) {
        this.rentalService.allowRenting(Vin.of(event.getVin()));
    }
}
