package io.pivotal.pal.wehaul.rental.domain;

import javax.persistence.*;

@Entity(name = "rentalTruck")
@Table(name = "rental_truck")
public class RentalTruck {

    @EmbeddedId
    private Vin vin;

    @Enumerated(EnumType.STRING)
    @Column
    private RentalTruckStatus status;

    @Enumerated(EnumType.STRING)
    @Column
    private TruckSize truckSize;

    RentalTruck() {
        // default constructor required by JPA
    }

    public RentalTruck(Vin vin, TruckSize truckSize) {
        this.vin = vin;
        this.truckSize = truckSize;
        this.status = RentalTruckStatus.RENTABLE;
    }

    public void reserve() {


        if(this.status == RentalTruckStatus.NOT_RENTABLE)
            throw new IllegalStateException("Truck cannot be reserved");

        this.status = RentalTruckStatus.RESERVED;
    }

    public void pickUp() {


        if(this.status != RentalTruckStatus.RESERVED)
            throw new IllegalStateException("Only reserved trucks can be picked up");

        this.status = RentalTruckStatus.RENTED;
    }

    public void dropOff() {

        if(this.status != RentalTruckStatus.RENTED)
        {
            throw new IllegalStateException("Truck is not currently rented");
        }

        this.status = RentalTruckStatus.RENTABLE;
    }

    public void preventRenting() {
        if (status != RentalTruckStatus.RENTABLE) {
            throw new IllegalStateException("Truck cannot be prevented from renting");
        }
        status = RentalTruckStatus.NOT_RENTABLE;
    }

    public void allowRenting() {
        if (status != RentalTruckStatus.NOT_RENTABLE) {
            throw new IllegalStateException("Truck is not rentable");
        }

        status = RentalTruckStatus.RENTABLE;
    }

    public Vin getVin() {
        return vin;
    }

    public RentalTruckStatus getStatus() {
        return status;
    }

    public TruckSize getTruckSize() {
        return truckSize;
    }

    @Override
    public String toString() {
        return "Truck{" +
                "vin=" + vin +
                ", status=" + status +
                ", truckSize=" + truckSize +
                '}';
    }
}
