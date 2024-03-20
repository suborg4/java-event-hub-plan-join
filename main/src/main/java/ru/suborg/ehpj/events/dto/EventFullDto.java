package ru.suborg.ehpj.events.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.suborg.ehpj.categories.dto.CategoryDto;
import ru.suborg.ehpj.events.model.State;
import ru.suborg.ehpj.locations.LocationDto;
import ru.suborg.ehpj.users.dto.UserShortDto;

import java.time.LocalDateTime;

import static ru.suborg.ehpj.util.DateConstant.DATE_TIME_PATTERN;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventFullDto {
    Long id;

    String annotation;

    CategoryDto category;

    Long confirmedRequests;

    @JsonFormat(pattern = DATE_TIME_PATTERN)
    LocalDateTime createdOn;

    String description;

    @JsonFormat(pattern = DATE_TIME_PATTERN)
    LocalDateTime eventDate;

    UserShortDto initiator;

    LocationDto location;

    Boolean paid;

    Integer participantLimit;

    @JsonFormat(pattern = DATE_TIME_PATTERN)
    LocalDateTime publishedOn;

    Boolean requestModeration;

    State state;

    String title;
}
