package ru.suborg.ehpj.requests.service;

import ru.suborg.ehpj.requests.dto.EventRequestStatusUpdateRequest;
import ru.suborg.ehpj.requests.dto.EventRequestStatusUpdateResult;
import ru.suborg.ehpj.requests.dto.ParticipationRequestDto;

import java.util.List;

public interface RequestService {
    ParticipationRequestDto addRequest(Long userId, Long eventId);

    EventRequestStatusUpdateResult updateRequestsStatus(Long userId, Long eventId,
                                                        EventRequestStatusUpdateRequest statusUpdateRequest);

    ParticipationRequestDto cancelRequest(Long userId, Long requestId);

    List<ParticipationRequestDto> getRequestsByEventOwner(Long userId, Long eventId);

    List<ParticipationRequestDto> getRequestsByUser(Long userId);
}
