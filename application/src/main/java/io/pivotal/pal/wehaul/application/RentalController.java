package io.pivotal.pal.wehaul.application;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.pivotal.pal.wehaul.fleet.domain.FleetService;
import io.pivotal.pal.wehaul.fleet.domain.Vin;
import io.pivotal.pal.wehaul.rental.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RequestMapping("/rentals")
@RestController
public class RentalController {

    private final RentalService rentalService;


    public RentalController(RentalService rentalService) {
        this.rentalService = rentalService;

    }


    @PostMapping
    public ResponseEntity<Void> createRental(@RequestBody CreateRentalDto createRentalDto) {

        String customerName = createRentalDto.getCustomerName();
        String truckSize = createRentalDto.getTruckSize();
        RentalTruck rentalTruck = rentalService.create(customerName, TruckSize.valueOf(truckSize));

        return ResponseEntity.ok().build();
    }

    @PostMapping("/{confirmationNumber}/pick-up")
    public ResponseEntity<Void> pickUpRental(@PathVariable String confirmationNumber) {

        rentalService.pickUp(ConfirmationNumber.of(confirmationNumber));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{confirmationNumber}/drop-off")
    public ResponseEntity<Void> dropOffRental(@PathVariable String confirmationNumber,
                                              @RequestBody DropOffRentalDto dropOffRentalDto) {

        int distanceTraveled = dropOffRentalDto.getDistanceTraveled();
        RentalTruck rentalTruck = rentalService.dropOff(ConfirmationNumber.of(confirmationNumber), distanceTraveled);

        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<RentalDto>> getAllRentals() {

        List<RentalDto> rentalDtos = rentalService.findAll().stream()
                .map(RentalTruck::getRental)
                .map(rental -> mapRentalToDto(rental))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return ResponseEntity.ok(rentalDtos);
    }

    private RentalDto mapRentalToDto(Rental rental) {
        return new RentalDto(
                rental.getConfirmationNumber().getConfirmationNumber().toString(),
                rental.getCustomerName(),
                rental.getTruckVin().getVin(),
                rental.getDistanceTraveled()
        );
    }

    static class DropOffRentalDto {

        private final int distanceTraveled;

        @JsonCreator
        public DropOffRentalDto(@JsonProperty(value = "distanceTraveled", required = true) int distanceTraveled) {
            this.distanceTraveled = distanceTraveled;
        }

        public int getDistanceTraveled() {
            return distanceTraveled;
        }

        @Override
        public String toString() {
            return "DropOffRentalDto{" +
                    "distanceTraveled=" + distanceTraveled +
                    '}';
        }
    }

    static class CreateRentalDto {

        private final String customerName;
        private final String truckSize;

        @JsonCreator
        public CreateRentalDto(@JsonProperty(value = "customerName", required = true) String customerName,
                               @JsonProperty(value = "truckSize", required = true) String truckSize) {
            this.customerName = customerName;
            this.truckSize = truckSize;
        }

        public String getCustomerName() {
            return customerName;
        }

        public String getTruckSize() {
            return truckSize;
        }

        @Override
        public String toString() {
            return "CreateRentalDto{" +
                    "customerName='" + customerName + '\'' +
                    ", truckSize='" + truckSize + '\'' +
                    '}';
        }
    }

    static class RentalDto {

        private final String confirmationNumber;
        private final String customerName;
        private final String truckVin;
        private final Integer distanceTraveled;

        RentalDto(String confirmationNumber, String customerName, String truckVin, Integer distanceTraveled) {
            this.confirmationNumber = confirmationNumber;
            this.customerName = customerName;
            this.truckVin = truckVin;
            this.distanceTraveled = distanceTraveled;
        }

        public String getConfirmationNumber() {
            return confirmationNumber;
        }

        public String getCustomerName() {
            return customerName;
        }

        public String getTruckVin() {
            return truckVin;
        }

        public Integer getDistanceTraveled() {
            return distanceTraveled;
        }

        @Override
        public String toString() {
            return "RentalDto{" +
                    "confirmationNumber='" + confirmationNumber + '\'' +
                    ", customerName='" + customerName + '\'' +
                    ", truckVin='" + truckVin + '\'' +
                    ", distanceTraveled=" + distanceTraveled +
                    '}';
        }
    }
}
