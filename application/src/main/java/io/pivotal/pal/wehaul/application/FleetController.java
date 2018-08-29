package io.pivotal.pal.wehaul.application;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.pivotal.pal.wehaul.fleet.domain.FleetService;
import io.pivotal.pal.wehaul.fleet.domain.FleetTruck;
import io.pivotal.pal.wehaul.fleet.domain.Vin;
import io.pivotal.pal.wehaul.rental.domain.RentalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@RequestMapping("/fleet/trucks")
@RestController
public class FleetController {

    private final FleetService fleetService;


    public FleetController(FleetService fleetService) {
        this.fleetService = fleetService;

    }


    @PostMapping
    public ResponseEntity<Void> buyTruck(@RequestBody BuyTruckDto buyTruckDto) {

        fleetService.buyTruck(
                Vin.of(buyTruckDto.getVin()),
                buyTruckDto.getOdometerReading(),
                buyTruckDto.getTruckLength()
        );

        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<Collection<TruckDto>> getAllTrucks() {
        Collection<FleetTruck> fleetTrucks = fleetService.findAll();

        List<TruckDto> trucksDto = fleetTrucks.stream()
                .map(truck -> mapTruckToDto(truck))
                .collect(Collectors.toList());

        return ResponseEntity.ok(trucksDto);
    }

    @PostMapping("/{vin}/send-for-inspection")
    public ResponseEntity<Void> sendForInspection(@PathVariable String vin) {

        fleetService.sendForInspection(Vin.of(vin));

        return ResponseEntity.ok().build();
    }

    @PostMapping("/{vin}/return-from-inspection")
    public ResponseEntity<Void> returnFromInspection(
            @PathVariable String vin,
            @RequestBody ReturnFromInspectionDto returnFromInspectionDto
    ) {

        String notes = returnFromInspectionDto.getNotes();
        int odometerReading = returnFromInspectionDto.getOdometerReading();

        fleetService.returnFromInspection(Vin.of(vin), notes, odometerReading);


        return ResponseEntity.ok().build();
    }

    private TruckDto mapTruckToDto(FleetTruck fleetTruck) {
        return new TruckDto(
                fleetTruck.getVin().getVin(),
                fleetTruck.getStatus().name(),
                fleetTruck.getOdometerReading(),
                fleetTruck.getTruckLength()
        );
    }

    static class ReturnFromInspectionDto {


        private final String notes;
        private final int odometerReading;

        @JsonCreator
        public ReturnFromInspectionDto(
                @JsonProperty(value = "notes", required = true) String notes,
                @JsonProperty(value = "odometerReading", required = true) int odometerReading
        ) {
            this.notes = notes;
            this.odometerReading = odometerReading;
        }

        public String getNotes() {
            return notes;
        }

        public int getOdometerReading() {
            return odometerReading;
        }

        @Override
        public String toString() {
            return "ReturnFromInspectionDto{" +
                    "notes='" + notes + '\'' +
                    ", odometerReading=" + odometerReading +
                    '}';
        }
    }

    static class BuyTruckDto {

        private final String vin;
        private final int odometerReading;
        private final int truckLength;

        @JsonCreator
        public BuyTruckDto(@JsonProperty(value = "vin", required = true) String vin,
                           @JsonProperty(value = "odometerReading", required = true) int odometerReading,
                           @JsonProperty(value = "truckLength", required = true) int truckLength) {
            this.vin = vin;
            this.odometerReading = odometerReading;
            this.truckLength = truckLength;
        }

        public String getVin() {
            return vin;
        }

        public int getOdometerReading() {
            return odometerReading;
        }

        public int getTruckLength() {
            return truckLength;
        }

        @Override
        public String toString() {
            return "BuyTruckDto{" +
                    "vin='" + vin + '\'' +
                    ", odometerReading=" + odometerReading +
                    ", truckLength=" + truckLength +
                    '}';
        }
    }

    static class TruckDto {

        private final String vin;
        private final String status;
        private final Integer odometerReading;
        private final Integer truckLength;

        TruckDto(String vin, String status, Integer odometerReading, Integer truckLength) {
            this.vin = vin;
            this.status = status;
            this.odometerReading = odometerReading;
            this.truckLength = truckLength;
        }

        public String getVin() {
            return vin;
        }

        public String getStatus() {
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
            return "TruckDto{" +
                    "vin='" + vin + '\'' +
                    ", status='" + status + '\'' +
                    ", odometerReading=" + odometerReading +
                    ", truckLength=" + truckLength +
                    '}';
        }
    }
}
