package ru.suborg.ehpj.stats.server;

import lombok.experimental.UtilityClass;
import ru.suborg.ehpj.stats.dto.EndpointHitDto;
import ru.suborg.ehpj.stats.server.model.EndpointHit;

@UtilityClass
public class EndpointHitMapper {
    public EndpointHit toEndpointHit(EndpointHitDto hit) {
        return new EndpointHit(
                hit.getApp(),
                hit.getUri(),
                hit.getIp(),
                hit.getTimestamp()
        );
    }

    public EndpointHitDto toEndpointHitDto(EndpointHit hit) {
        return new EndpointHitDto(
                hit.getApp(),
                hit.getUri(),
                hit.getIp(),
                hit.getTimestamp()
        );
    }
}
