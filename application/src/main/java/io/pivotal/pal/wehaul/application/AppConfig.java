package io.pivotal.pal.wehaul.application;

import io.pivotal.pal.wehaul.application.eventstore.EventPublishingFleetTruckRepository;
import io.pivotal.pal.wehaul.application.eventstore.FleetTruckEventSourcedRepository;
import io.pivotal.pal.wehaul.application.eventstore.FleetTruckEventStoreRepository;
import io.pivotal.pal.wehaul.fleet.domain.FleetService;
import io.pivotal.pal.wehaul.fleet.domain.FleetTruckRepository;
import io.pivotal.pal.wehaul.rental.domain.*;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public TruckSizeChart truckSizeChart() {
        return new TruckSizeChart();
    }

    @Bean
    public TruckAllocationService truckAllocationService(RentalTruckRepository rentalTruckRepository) {
        return new TruckAllocationService(rentalTruckRepository);
    }

    @Bean
    public FleetService fleetService(FleetTruckRepository fleetTruckRepository) {
        return new FleetService(fleetTruckRepository);
    }

    @Bean
    public RentalService rentalService(TruckAllocationService truckAllocationService,
                                       RentalTruckRepository rentalTruckRepository,
                                       TruckSizeChart truckSizeChart) {
        return new RentalService(truckAllocationService, rentalTruckRepository, truckSizeChart);
    }

    @Bean
    public FleetTruckRepository eventPublishingFleetTruckRepository(FleetTruckEventStoreRepository eventStoreRepository, ApplicationEventPublisher applicationEventPublisher) {

        FleetTruckEventSourcedRepository eventSourcedRepository =
                new FleetTruckEventSourcedRepository(eventStoreRepository);

        return new EventPublishingFleetTruckRepository(eventSourcedRepository, applicationEventPublisher);
    }
}
