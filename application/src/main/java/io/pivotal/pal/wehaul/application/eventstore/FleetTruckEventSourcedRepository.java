package io.pivotal.pal.wehaul.application.eventstore;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.pivotal.pal.wehaul.fleet.domain.FleetTruck;
import io.pivotal.pal.wehaul.fleet.domain.FleetTruckRepository;
import io.pivotal.pal.wehaul.fleet.domain.Vin;
import io.pivotal.pal.wehaul.fleet.domain.event.FleetTruckEvent;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@NoRepositoryBean
public class FleetTruckEventSourcedRepository implements FleetTruckRepository {

    private static final ObjectMapper objectMapper = new ObjectMapper()
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .findAndRegisterModules();

    private final FleetTruckEventStoreRepository eventStoreRepository;

    public FleetTruckEventSourcedRepository(FleetTruckEventStoreRepository eventStoreRepository) {
        this.eventStoreRepository = eventStoreRepository;
    }

    @Override
    public FleetTruck save(FleetTruck fleetTruck) {



        List<FleetTruckEventStoreEntity> eventEntities = mapEventToEntities(fleetTruck.getDirtyEvents(), fleetTruck.getVersion()+1);

        eventStoreRepository.save(eventEntities);

        return findOne(fleetTruck.getVin());
    }

    @Override
    public FleetTruck findOne(Vin vin) {

        List<FleetTruckEvent> fleetTruckEvents = this.eventStoreRepository.findAllByKeyVinOrderByKeyVersion(vin.getVin()).stream().map(eventEntity -> deserializeEvent(eventEntity)).collect(Collectors.toList());




        return FleetTruck.fromEvents(fleetTruckEvents);

    }

    @Override
    public List<FleetTruck> findAll() {
        Map<String, List<FleetTruckEventStoreEntity>> eventEntitiesByVin =
                eventStoreRepository.findAll(new Sort(Sort.Direction.ASC, "key.vin", "key.version"))
                        .stream()
                        .collect(Collectors.groupingBy(eventEntity -> eventEntity.getKey().getVin()));

        return eventEntitiesByVin.entrySet()
                .stream()
                .map(eventEntities -> mapEntitiesToEvents(eventEntities.getValue()))
                .map(FleetTruck::fromEvents)
                .sorted(Comparator.comparing(truck -> truck.getVin().getVin()))
                .collect(Collectors.toList());
    }

    private List<FleetTruckEventStoreEntity> mapEventToEntities(List<FleetTruckEvent> events, Integer versionStart) {

        return IntStream.range(0, events.size())
                .mapToObj(i -> {
                    FleetTruckEvent event = events.get(i);
                    String eventJson = serializeEvent(event);


                    FleetTruckEventStoreEntity entity = new FleetTruckEventStoreEntity(new FleetTruckEventStoreEntityKey(event.getVin(),i+versionStart),event.getClass(),eventJson);


                    return entity;
                })
                .collect(Collectors.toList());
    }

    private List<FleetTruckEvent> mapEntitiesToEvents(List<FleetTruckEventStoreEntity> eventEntities) {
        return eventEntities.stream()
                .map(eventEntity -> deserializeEvent(eventEntity))
                .collect(Collectors.toList());
    }

    private String serializeEvent(FleetTruckEvent event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private FleetTruckEvent deserializeEvent(FleetTruckEventStoreEntity eventEntity) {
        try {
            return (FleetTruckEvent) objectMapper.readValue(eventEntity.getData(), eventEntity.getEventClass());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
