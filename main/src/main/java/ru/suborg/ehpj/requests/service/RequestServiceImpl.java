package ru.suborg.ehpj.requests.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.suborg.ehpj.events.EventRepository;
import ru.suborg.ehpj.events.model.Event;
import ru.suborg.ehpj.events.model.State;
import ru.suborg.ehpj.exceptions.ForbiddenException;
import ru.suborg.ehpj.exceptions.NotFoundException;
import ru.suborg.ehpj.exceptions.ValidationException;
import ru.suborg.ehpj.requests.RequestMapper;
import ru.suborg.ehpj.requests.RequestRepository;
import ru.suborg.ehpj.requests.dto.EventRequestStatusUpdateRequest;
import ru.suborg.ehpj.requests.dto.EventRequestStatusUpdateResult;
import ru.suborg.ehpj.requests.dto.ParticipationRequestDto;
import ru.suborg.ehpj.requests.model.ParticipationRequest;
import ru.suborg.ehpj.requests.model.RequestStatus;
import ru.suborg.ehpj.users.User;
import ru.suborg.ehpj.users.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.suborg.ehpj.requests.model.RequestStatus.*;

@Service
@RequiredArgsConstructor
@Transactional
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Override
    public ParticipationRequestDto addRequest(Long userId, Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
        User user = getUser(userId);
        if (requestRepository.existsByRequesterIdAndEventId(userId, eventId)) {
            throw new ForbiddenException("Request is already exist.");
        }
        if (userId.equals(event.getInitiator().getId())) {
            throw new ForbiddenException("Initiator can't send request to his own event.");
        }
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new ForbiddenException("Participation is possible only in published event.");
        }
        if (event.getParticipantLimit() != 0 && event.getParticipantLimit() <=
                requestRepository.countByEventIdAndStatus(eventId, CONFIRMED)) {
            throw new ForbiddenException("Participant limit has been reached.");
        }
        ParticipationRequest request = new ParticipationRequest();
        request.setCreated(LocalDateTime.now());
        request.setEvent(event);
        request.setRequester(user);

        if (event.getRequestModeration() && event.getParticipantLimit() != 0) {
            request.setStatus(PENDING);
        } else {
            request.setStatus(CONFIRMED);
        }
        return RequestMapper.toParticipationRequestDto(requestRepository.save(request));
    }

    @Override
    public EventRequestStatusUpdateResult updateRequestsStatus(Long userId, Long eventId,
                                                               EventRequestStatusUpdateRequest statusUpdateRequest) {
        User initiator = getUser(userId);
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId).orElseThrow(() ->
                new NotFoundException("Event with id=" + eventId + " was not found."));
        if (!event.getInitiator().equals(initiator)) {
            throw new ValidationException("User isn't initiator.");
        }
        long confirmedRequests = requestRepository.countByEventIdAndStatus(eventId, CONFIRMED);
        if (event.getParticipantLimit() > 0 && event.getParticipantLimit() <= confirmedRequests) {
            throw new ForbiddenException("The participant limit has been reached.");
        }
        List<ParticipationRequestDto> confirmed = new ArrayList<>();
        List<ParticipationRequestDto> rejected = new ArrayList<>();
        List<ParticipationRequest> requests = requestRepository.findAllByEventIdAndIdInAndStatus(eventId,
                statusUpdateRequest.getRequestIds(), PENDING);
        for (int i = 0; i < requests.size(); i++) {
            ParticipationRequest request = requests.get(i);
            if (statusUpdateRequest.getStatus() == REJECTED) {
                request.setStatus(REJECTED);
                rejected.add(RequestMapper.toParticipationRequestDto(request));
            }
            if (statusUpdateRequest.getStatus() == CONFIRMED && event.getParticipantLimit() > 0 &&
                    (confirmedRequests + i) < event.getParticipantLimit()) {
                request.setStatus(CONFIRMED);
                confirmed.add(RequestMapper.toParticipationRequestDto(request));
            } else {
                request.setStatus(REJECTED);
                rejected.add(RequestMapper.toParticipationRequestDto(request));
            }
        }
        return new EventRequestStatusUpdateResult(confirmed, rejected);
    }

    @Override
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        ParticipationRequest request = requestRepository.findByIdAndRequesterId(requestId, userId);
        request.setStatus(RequestStatus.CANCELED);
        return RequestMapper.toParticipationRequestDto(requestRepository.save(request));
    }

    @Transactional(readOnly = true)
    @Override
    public List<ParticipationRequestDto> getRequestsByEventOwner(Long userId, Long eventId) {
        checkUser(userId);
        eventRepository.findByIdAndInitiatorId(eventId, userId).orElseThrow(() ->
                new NotFoundException("Event with id=" + eventId + " was not found"));
        return requestRepository.findAllByEventId(eventId).stream()
                .map(RequestMapper::toParticipationRequestDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<ParticipationRequestDto> getRequestsByUser(Long userId) {
        checkUser(userId);
        return requestRepository.findAllByRequesterId(userId).stream()
                .map(RequestMapper::toParticipationRequestDto).collect(Collectors.toList());
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("User with id=" + userId + " was not found"));
    }

    private void checkUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User with id=" + userId + " was not found");
        }
    }
}