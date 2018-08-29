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


    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "truckVin")
    private Rental rental;

    RentalTruck() {
        // default constructor required by JPA
    }

    public RentalTruck(Vin vin, TruckSize truckSize) {
        this.vin = vin;
        this.status = RentalTruckStatus.RENTABLE;
        this.truckSize = truckSize;
    }

    public void reserve(String customerName) {
        if (status != RentalTruckStatus.RENTABLE) {
            throw new IllegalStateException("Truck cannot be reserved");
        }

        this.status = RentalTruckStatus.RESERVED;

        this.rental = new Rental(customerName,Vin.of(this.vin.getVin()));
    }

    public void pickUp() {
        if (status != RentalTruckStatus.RESERVED) {
            throw new IllegalStateException("Only reserved trucks can be picked up");
        }

        this.status = RentalTruckStatus.RENTED;


        this.rental.pickUp();
    }

    public void dropOff(int distanceTraveled) {
        if (status != RentalTruckStatus.RENTED) {
            throw new IllegalStateException("Truck is not currently rented");
        }

        this.status = RentalTruckStatus.RENTABLE;


        this.rental.dropOff(distanceTraveled);
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

    public Rental getRental() {
        return this.rental;
    }

    @Override
    public String toString() {
        return "RentalTruck{" +
                "vin=" + vin +
                ", status=" + status +
                ", truckSize=" + truckSize +
                '}';
    }
}
