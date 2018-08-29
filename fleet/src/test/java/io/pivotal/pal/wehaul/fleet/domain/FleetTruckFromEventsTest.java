package io.pivotal.pal.wehaul.fleet.domain;

import io.pivotal.pal.wehaul.fleet.domain.event.*;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class FleetTruckFromEventsTest {

    @Test
    public void purchaseTruck_fromEvents() {
        FleetTruckPurchased truckPurchasedEvent = new FleetTruckPurchased(
                "some-vin",
                FleetTruckStatus.INSPECTABLE.toString(),
                20,
                100
        );
        List<FleetTruckEvent> events = Collections.singletonList(truckPurchasedEvent);


        FleetTruck truck = FleetTruck.fromEvents(events);


        assertThat(truck.getVin()).isEqualTo(Vin.of("some-vin"));
        assertThat(truck.getVersion()).isEqualTo(0);
        assertThat(truck.getStatus()).isEqualTo(FleetTruckStatus.INSPECTABLE);
        assertThat(truck.getTruckLength()).isEqualTo(20);
        assertThat(truck.getOdometerReading()).isEqualTo(100);
    }

    @Test
    public void sendForInspection_fromEvents() {
        FleetTruckPurchased truckPurchasedEvent = new FleetTruckPurchased(
                "some-vin",
                FleetTruckStatus.INSPECTABLE.toString(),
                20,
                100
        );
        FleetTruckSentForInspection truckSentForInspectionEvent = new FleetTruckSentForInspection(
                "some-vin",
                FleetTruckStatus.IN_INSPECTION.toString()
        );
        List<FleetTruckEvent> events =
                Arrays.asList(truckPurchasedEvent, truckSentForInspectionEvent);


        FleetTruck truck = FleetTruck.fromEvents(events);


        assertThat(truck.getVersion()).isEqualTo(1);
        assertThat(truck.getStatus()).isEqualTo(FleetTruckStatus.IN_INSPECTION);
       
    }

    @Test
    public void returnFromInspection_fromEvents() {
        FleetTruckPurchased truckPurchasedEvent = new FleetTruckPurchased(
                "some-vin",
                FleetTruckStatus.INSPECTABLE.toString(),
                20,
                100
        );
        FleetTruckSentForInspection truckSentForInspectionEvent = new FleetTruckSentForInspection(
                "some-vin",
                FleetTruckStatus.IN_INSPECTION.toString()
        );
        FleetTruckReturnedFromInspection truckReturnedFromInspectionEvent = new FleetTruckReturnedFromInspection(
                "some-vin",
                FleetTruckStatus.INSPECTABLE.toString(),
                200,
                "some-notes"
        );
        List<FleetTruckEvent> events =
                Arrays.asList(truckPurchasedEvent, truckSentForInspectionEvent, truckReturnedFromInspectionEvent);


        FleetTruck truck = FleetTruck.fromEvents(events);


        assertThat(truck.getVin()).isEqualTo(Vin.of("some-vin"));
        assertThat(truck.getVersion()).isEqualTo(2);
        assertThat(truck.getStatus()).isEqualTo(FleetTruckStatus.INSPECTABLE);
        assertThat(truck.getOdometerReading()).isEqualTo(200);

        assertThat(truck.getInspections()).hasSize(1);
        assertThat(truck.getInspections().get(0)).isEqualToIgnoringGivenFields(
                new TruckInspection(Vin.of("some-vin"), 200, "some-notes"),
                "id"
        );
    }

    @Test
    public void removeFromYard_fromEvents() {
        FleetTruckPurchased truckPurchasedEvent = new FleetTruckPurchased(
                "some-vin",
                FleetTruckStatus.INSPECTABLE.toString(),
                20,
                100
        );
        FleetTruckRemovedFromYard truckRemovedFromYardEvent = new FleetTruckRemovedFromYard(
                "some-vin",
                FleetTruckStatus.NOT_INSPECTABLE.toString()
        );
        List<FleetTruckEvent> events = Arrays.asList(truckPurchasedEvent, truckRemovedFromYardEvent);


        FleetTruck truck = FleetTruck.fromEvents(events);


        assertThat(truck.getVin()).isEqualTo(Vin.of("some-vin"));
        assertThat(truck.getVersion()).isEqualTo(1);
        assertThat(truck.getStatus()).isEqualTo(FleetTruckStatus.NOT_INSPECTABLE);
    }

    @Test
    public void returnToYard_fromEvents() {
        FleetTruckPurchased truckPurchasedEvent = new FleetTruckPurchased(
                "some-vin",
                FleetTruckStatus.INSPECTABLE.toString(),
                20,
                100
        );
        FleetTruckRemovedFromYard truckRemovedFromYardEvent = new FleetTruckRemovedFromYard(
                "some-vin",
                FleetTruckStatus.NOT_INSPECTABLE.toString()
        );
        FleetTruckReturnedToYard fleetTruckReturnedToYardEvent = new FleetTruckReturnedToYard(
                "some-vin",
                FleetTruckStatus.INSPECTABLE.toString(),
                200
        );
        List<FleetTruckEvent> events =
                Arrays.asList(truckPurchasedEvent, truckRemovedFromYardEvent, fleetTruckReturnedToYardEvent);


        FleetTruck truck = FleetTruck.fromEvents(events);


        assertThat(truck.getVin()).isEqualTo(Vin.of("some-vin"));
        assertThat(truck.getVersion()).isEqualTo(2);
        assertThat(truck.getStatus()).isEqualTo(FleetTruckStatus.INSPECTABLE);
        assertThat(truck.getOdometerReading()).isEqualTo(300);
    }

    @Test
    public void returnToYard_clearEvents() {
        FleetTruckPurchased truckPurchasedEvent = new FleetTruckPurchased(
                "some-vin",
                FleetTruckStatus.INSPECTABLE.toString(),
                20,
                100
        );
        List<FleetTruckEvent> events = Collections.singletonList(truckPurchasedEvent);


        FleetTruck truck = FleetTruck.fromEvents(events);


        assertThat(truck.getDirtyEvents()).isEmpty();
    }
}
