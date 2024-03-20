package ru.suborg.ehpj.comments.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.suborg.ehpj.events.dto.EventShortDto;
import ru.suborg.ehpj.users.dto.UserShortDto;

import java.time.LocalDateTime;

import static ru.suborg.ehpj.util.DateConstant.DATE_TIME_PATTERN;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {
    private Long id;
    private String text;
    private UserShortDto author;
    private EventShortDto event;
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    private LocalDateTime created;
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    private LocalDateTime edited;
}
