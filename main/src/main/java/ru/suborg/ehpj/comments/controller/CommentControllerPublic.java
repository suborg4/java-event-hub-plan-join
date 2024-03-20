package ru.suborg.ehpj.comments.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.suborg.ehpj.comments.dto.CommentDto;
import ru.suborg.ehpj.comments.service.CommentService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Validated
@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentControllerPublic {
    private final CommentService commentService;

    @GetMapping("/event/{eventId}")
    List<CommentDto> getComments(@PathVariable Long eventId,
                                 @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                 @RequestParam(value = "size", defaultValue = "10") @Positive Integer size) {
        return commentService.getComments(eventId, from, size);
    }

    @GetMapping("/{commentId}")
    CommentDto getCommentById(@PathVariable Long commentId) {
        return commentService.getCommentById(commentId);
    }
}
