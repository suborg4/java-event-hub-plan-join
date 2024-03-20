package ru.suborg.ehpj.requests;

import lombok.experimental.UtilityClass;
import ru.suborg.ehpj.requests.dto.ParticipationRequestDto;
import ru.suborg.ehpj.requests.model.ParticipationRequest;

@UtilityClass
public class RequestMapper {
    public ParticipationRequestDto toParticipationRequestDto(ParticipationRequest participationRequest) {
        return new ParticipationRequestDto(
                participationRequest.getId(),
                participationRequest.getCreated(),
                participationRequest.getEvent().getId(),
                participationRequest.getRequester().getId(),
                participationRequest.getStatus()
        );
    }
}
