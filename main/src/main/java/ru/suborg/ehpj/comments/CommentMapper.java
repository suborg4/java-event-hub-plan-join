package ru.suborg.ehpj.comments;

import lombok.experimental.UtilityClass;
import ru.suborg.ehpj.comments.dto.NewCommentDto;
import ru.suborg.ehpj.comments.dto.CommentDto;
import ru.suborg.ehpj.events.dto.EventShortDto;
import ru.suborg.ehpj.events.model.Event;
import ru.suborg.ehpj.users.User;
import ru.suborg.ehpj.users.dto.UserShortDto;

import java.time.LocalDateTime;

@UtilityClass
public class CommentMapper {
    public Comment toComment(NewCommentDto newCommentDto, User author, Event event) {
        Comment comment = new Comment();
        comment.setAuthor(author);
        comment.setEvent(event);
        comment.setText(newCommentDto.getText());
        comment.setCreated(LocalDateTime.now());
        return comment;
    }

    public CommentDto toCommentDto(Comment comment, UserShortDto author, EventShortDto event) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                author,
                event,
                comment.getCreated(),
                comment.getEdited()
        );
    }
}
