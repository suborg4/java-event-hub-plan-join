package ru.suborg.ehpj.compilations.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.suborg.ehpj.compilations.Compilation;
import ru.suborg.ehpj.compilations.CompilationMapper;
import ru.suborg.ehpj.compilations.CompilationRepository;
import ru.suborg.ehpj.compilations.dto.CompilationDto;
import ru.suborg.ehpj.compilations.dto.NewCompilationDto;
import ru.suborg.ehpj.compilations.dto.UpdateCompilationRequest;
import ru.suborg.ehpj.events.EventMapper;
import ru.suborg.ehpj.events.EventRepository;
import ru.suborg.ehpj.events.model.Event;
import ru.suborg.ehpj.exceptions.NotFoundException;
import ru.suborg.ehpj.requests.RequestRepository;
import ru.suborg.ehpj.requests.dto.ConfirmedRequests;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.suborg.ehpj.requests.model.RequestStatus.CONFIRMED;

@Service
@RequiredArgsConstructor
@Transactional
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;

    @Override
    public CompilationDto addCompilation(NewCompilationDto newCompilationDto) {
        Compilation compilation = CompilationMapper.toCompilation(newCompilationDto);
        if (newCompilationDto.getEvents() != null) {
            compilation.setEvents(eventRepository.findAllByIdIn(newCompilationDto.getEvents()));
        }
        CompilationDto compilationDto = CompilationMapper.toCompilationDto(compilationRepository.save(compilation));
        if (compilation.getEvents() != null) {
            List<Long> ids = compilation.getEvents().stream().map(Event::getId).collect(Collectors.toList());
            Map<Long, Long> confirmedRequests = requestRepository.findAllByEventIdInAndStatus(ids, CONFIRMED)
                    .stream()
                    .collect(Collectors.toMap(ConfirmedRequests::getEvent, ConfirmedRequests::getCount));
            compilationDto.setEvents(compilation.getEvents().stream()
                    .map(event -> EventMapper.toEventShortDto(event, confirmedRequests.get(event.getId())))
                    .collect(Collectors.toList()));
        }
        return compilationDto;
    }

    @Override
    public CompilationDto updateCompilation(Long compId, UpdateCompilationRequest updateCompilation) {
        Compilation compilation = getCompilation(compId);
        if (updateCompilation.getEvents() != null) {
            Set<Event> events = updateCompilation.getEvents().stream().map(id -> {
                Event event = new Event();
                event.setId(id);
                return event;
            }).collect(Collectors.toSet());
            compilation.setEvents(events);
        }
        if (updateCompilation.getPinned() != null) {
            compilation.setPinned(updateCompilation.getPinned());
        }
        String title = updateCompilation.getTitle();
        if (title != null && !title.isBlank()) {
            compilation.setTitle(title);
        }
        CompilationDto compilationDto = CompilationMapper.toCompilationDto(compilationRepository.save(compilation));
        if (compilation.getEvents() != null) {
            List<Long> ids = compilation.getEvents().stream().map(Event::getId).collect(Collectors.toList());
            Map<Long, Long> confirmedRequests = requestRepository.findAllByEventIdInAndStatus(ids, CONFIRMED)
                    .stream()
                    .collect(Collectors.toMap(ConfirmedRequests::getEvent, ConfirmedRequests::getCount));
            compilationDto.setEvents(compilation.getEvents().stream()
                    .map(event -> EventMapper.toEventShortDto(event, confirmedRequests.get(event.getId())))
                    .collect(Collectors.toList()));
        }
        return compilationDto;
    }

    @Transactional(readOnly = true)
    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        if (pinned != null) {
            List<Compilation> compilations = compilationRepository.findAllByPinned(pinned, pageable);
            List<CompilationDto> result = new ArrayList<>();
            for (Compilation compilation : compilations) {
                CompilationDto compilationDto = CompilationMapper.toCompilationDto(compilation);
                if (compilation.getEvents() != null) {
                    List<Long> ids = compilation.getEvents().stream().map(Event::getId).collect(Collectors.toList());
                    Map<Long, Long> confirmedRequests = requestRepository.findAllByEventIdInAndStatus(ids, CONFIRMED)
                            .stream()
                            .collect(Collectors.toMap(ConfirmedRequests::getEvent, ConfirmedRequests::getCount));
                    compilationDto.setEvents(compilation.getEvents().stream()
                            .map(event -> EventMapper.toEventShortDto(event, confirmedRequests.get(event.getId())))
                            .collect(Collectors.toList()));
                }
                result.add(compilationDto);
            }
            return result;
        } else {
            List<Compilation> compilations = compilationRepository.findAll(pageable).getContent();
            List<CompilationDto> result = new ArrayList<>();
            for (Compilation compilation : compilations) {
                CompilationDto compilationDto = CompilationMapper.toCompilationDto(compilation);
                if (compilation.getEvents() != null) {
                    List<Long> ids = compilation.getEvents().stream().map(Event::getId).collect(Collectors.toList());
                    Map<Long, Long> confirmedRequests = requestRepository.findAllByEventIdInAndStatus(ids, CONFIRMED)
                            .stream()
                            .collect(Collectors.toMap(ConfirmedRequests::getEvent, ConfirmedRequests::getCount));
                    compilationDto.setEvents(compilation.getEvents().stream()
                            .map(event -> EventMapper.toEventShortDto(event, confirmedRequests.get(event.getId())))
                            .collect(Collectors.toList()));
                }
                result.add(compilationDto);
            }
            return result;
        }
    }

    @Transactional(readOnly = true)
    @Override
    public CompilationDto getCompilationById(Long compilationId) {
        Compilation compilation = getCompilation(compilationId);
        CompilationDto compilationDto = CompilationMapper.toCompilationDto(compilation);
        if (compilation.getEvents() != null) {
            List<Long> ids = compilation.getEvents().stream().map(Event::getId).collect(Collectors.toList());
            Map<Long, Long> confirmedRequests = requestRepository.findAllByEventIdInAndStatus(ids, CONFIRMED)
                    .stream()
                    .collect(Collectors.toMap(ConfirmedRequests::getEvent, ConfirmedRequests::getCount));
            compilationDto.setEvents(compilation.getEvents().stream()
                    .map(event -> EventMapper.toEventShortDto(event, confirmedRequests.get(event.getId())))
                    .collect(Collectors.toList()));
        }
        return compilationDto;
    }

    @Override
    public void deleteCompilation(Long compilationId) {
        getCompilation(compilationId);
        compilationRepository.deleteById(compilationId);
    }

    private Compilation getCompilation(Long compilationId) {
        return compilationRepository.findById(compilationId).orElseThrow(() ->
                new NotFoundException("Compilation id=" + compilationId + " not found"));
    }
}
