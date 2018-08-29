package io.pivotal.pal.wehaul.fleet.domain;

import javax.persistence.*;

@Entity(name = "fleetTruck")
@Table(name = "fleet_truck")
public class FleetTruck {

    @EmbeddedId
    private Vin vin;

    @Enumerated(EnumType.STRING)
    @Column
    private FleetTruckStatus status;

    @Column
    private Integer odometerReading;

    @Column
    private Integer truckLength;

    FleetTruck() {
        // default constructor required by JPA
    }

    public FleetTruck(Vin vin, Integer odometerReading, Integer truckLength) {

        if(odometerReading < 0)
            throw new IllegalArgumentException("Cannot buy a truck with negative odometer reading");

        this.vin = vin;
        this.odometerReading = odometerReading;
        this.truckLength = truckLength;
        this.status = FleetTruckStatus.INSPECTABLE;



    }

    public void returnFromInspection(int odometerReading) {


        if(this.odometerReading > odometerReading)
            throw new IllegalArgumentException("Odometer reading cannot be less than previous reading");


        if(this.status != FleetTruckStatus.IN_INSPECTION)
            throw new IllegalStateException("Truck is not currently in inspection");


        this.status = FleetTruckStatus.INSPECTABLE;

        this.odometerReading = odometerReading;
    }

    public void sendForInspection() {

        if(this.status != FleetTruckStatus.INSPECTABLE)
            throw new IllegalStateException("Truck cannot be inspected");

        this.status = FleetTruckStatus.IN_INSPECTION;
    }

    public void removeFromYard() {

        if(this.status == FleetTruckStatus.IN_INSPECTION)
            throw new IllegalStateException("Cannot remove truck, currently in inspection");

        this.status = FleetTruckStatus.NOT_INSPECTABLE;
    }

    public void returnToYard(int distanceTraveled) {
        if (status != FleetTruckStatus.NOT_INSPECTABLE) {
            throw new IllegalStateException("Truck is not currently out of yard");
        }
        if (distanceTraveled < 0) {
            throw new IllegalArgumentException("Distance traveled cannot be negative");
        }

        status = FleetTruckStatus.INSPECTABLE;
        this.odometerReading = distanceTraveled;
    }

    public Vin getVin() {
        return vin;
    }

    public FleetTruckStatus getStatus() {
        return status;
    }

    public Integer getOdometerReading() {
        return odometerReading;
    }

    public Integer getTruckLength() {
        return truckLength;
    }

    @Override
    public String toString() {
        return "Truck{" +
                "vin=" + vin +
                ", status=" + status +
                ", odometerReading=" + odometerReading +
                ", truckLength=" + truckLength +
                '}';
    }
}
