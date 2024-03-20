package ru.suborg.ehpj.requests.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.suborg.ehpj.requests.model.RequestStatus;

import java.time.LocalDateTime;

import static ru.suborg.ehpj.util.DateConstant.DATE_TIME_PATTERN;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParticipationRequestDto {
    private Long id;

    @JsonFormat(pattern = DATE_TIME_PATTERN)
    private LocalDateTime created;

    private Long event;

    private Long requester;

    private RequestStatus status;
}
