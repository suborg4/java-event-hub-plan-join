package ru.suborg.ehpj.events.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.suborg.ehpj.events.dto.EventFullDto;
import ru.suborg.ehpj.events.dto.EventFullDtoWithViews;
import ru.suborg.ehpj.events.dto.UpdateEventAdminRequest;
import ru.suborg.ehpj.events.service.EventService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

import static ru.suborg.ehpj.util.DateConstant.DATE_TIME_PATTERN;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/events")
public class EventControllerAdmin {
    private final EventService eventService;

    @PatchMapping("/{eventId}")
    public EventFullDto updateEventByAdmin(@PathVariable Long eventId,
                                           @RequestBody @Valid UpdateEventAdminRequest updateEventAdminRequest) {
        return eventService.updateEventByAdmin(eventId, updateEventAdminRequest);
    }

    @GetMapping
    public List<EventFullDtoWithViews> getEventsByAdminParams(@RequestParam(required = false) List<Long> users,
                                                              @RequestParam(required = false) List<String> states,
                                                              @RequestParam(required = false) List<Long> categories,
                                                              @RequestParam(required = false) @DateTimeFormat(pattern =
                                                                      DATE_TIME_PATTERN) LocalDateTime rangeStart,
                                                              @RequestParam(required = false) @DateTimeFormat(pattern =
                                                                      DATE_TIME_PATTERN) LocalDateTime rangeEnd,
                                                              @RequestParam(value = "from", defaultValue = "0")
                                                                  @PositiveOrZero Integer from,
                                                              @RequestParam(value = "size", defaultValue = "10")
                                                                  @Positive Integer size) {
        return eventService.getEventsByAdminParams(users, states, categories, rangeStart, rangeEnd, from, size);
    }
}