package ru.suborg.ehpj.requests.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ConfirmedRequests {
    private long count;
    private Long event;
}
