package ru.suborg.ehpj.comments.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.suborg.ehpj.comments.Comment;
import ru.suborg.ehpj.comments.CommentMapper;
import ru.suborg.ehpj.comments.CommentRepository;
import ru.suborg.ehpj.comments.dto.CommentDto;
import ru.suborg.ehpj.comments.dto.NewCommentDto;
import ru.suborg.ehpj.events.EventMapper;
import ru.suborg.ehpj.events.EventRepository;
import ru.suborg.ehpj.events.dto.EventShortDto;
import ru.suborg.ehpj.events.model.Event;
import ru.suborg.ehpj.exceptions.NotFoundException;
import ru.suborg.ehpj.exceptions.ValidationException;
import ru.suborg.ehpj.requests.RequestRepository;
import ru.suborg.ehpj.requests.dto.ConfirmedRequests;
import ru.suborg.ehpj.users.User;
import ru.suborg.ehpj.users.UserMapper;
import ru.suborg.ehpj.users.UserRepository;
import ru.suborg.ehpj.users.dto.UserShortDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.suborg.ehpj.events.model.State.PUBLISHED;
import static ru.suborg.ehpj.requests.model.RequestStatus.CONFIRMED;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;

    @Override
    public CommentDto addComment(Long userId, Long eventId, NewCommentDto newCommentDto) {
        User author = checkAndGetUser(userId);
        Event event = checkAndGetEvent(eventId);
        if (event.getState() != PUBLISHED) {
            throw new ValidationException("Comments are available only for published events.");
        }
        Comment comment = commentRepository.save(CommentMapper.toComment(newCommentDto, author, event));
        UserShortDto userShort = UserMapper.toUserShortDto(author);
        EventShortDto eventShort = EventMapper.toEventShortDto(event,
                requestRepository.countByEventIdAndStatus(eventId, CONFIRMED));
        return CommentMapper.toCommentDto(comment, userShort, eventShort);
    }

    @Override
    public CommentDto updateComment(Long userId, Long eventId, Long commentId, NewCommentDto newCommentDto) {
        User author = checkAndGetUser(userId);
        Event event = checkAndGetEvent(eventId);
        Comment comment = commentRepository.findById(commentId).orElseThrow(() ->
                new NotFoundException("Comment with id=" + commentId + " was not found"));
        if (comment.getEvent() != event) {
            throw new ValidationException("This comment is for other event.");
        }
        comment.setText(newCommentDto.getText());
        comment.setEdited(LocalDateTime.now());
        UserShortDto userShort = UserMapper.toUserShortDto(author);
        EventShortDto eventShort = EventMapper.toEventShortDto(event,
                requestRepository.countByEventIdAndStatus(eventId, CONFIRMED));
        return CommentMapper.toCommentDto(comment, userShort, eventShort);
    }

    @Transactional(readOnly = true)
    @Override
    public List<CommentDto> getCommentsByAuthor(Long userId, Integer from, Integer size) {
        User author = checkAndGetUser(userId);
        List<Comment> comments = commentRepository.findAllByAuthorId(userId, PageRequest.of(from / size, size));
        List<Long> eventIds = comments.stream().map(comment -> comment.getEvent().getId()).collect(Collectors.toList());
        Map<Long, Long> confirmedRequests = requestRepository.findAllByEventIdInAndStatus(eventIds, CONFIRMED)
                .stream()
                .collect(Collectors.toMap(ConfirmedRequests::getEvent, ConfirmedRequests::getCount));
        UserShortDto userShort = UserMapper.toUserShortDto(author);
        List<CommentDto> result = new ArrayList<>();
        for (Comment c : comments) {
            Long eventId  = c.getEvent().getId();
            EventShortDto eventShort = EventMapper.toEventShortDto(c.getEvent(), confirmedRequests.get(eventId));
            result.add(CommentMapper.toCommentDto(c, userShort, eventShort));
        }
        return result;
    }

    @Transactional(readOnly = true)
    @Override
    public List<CommentDto> getComments(Long eventId, Integer from, Integer size) {
        Event event = checkAndGetEvent(eventId);
        EventShortDto eventShort = EventMapper.toEventShortDto(event,
                requestRepository.countByEventIdAndStatus(eventId, CONFIRMED));
        return commentRepository.findAllByEventId(eventId, PageRequest.of(from / size, size))
                .stream()
                .map(c -> CommentMapper.toCommentDto(c, UserMapper.toUserShortDto(c.getAuthor()), eventShort))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public CommentDto getCommentById(Long commentId) {
        Comment comment = checkAndGetComment(commentId);
        UserShortDto userShort = UserMapper.toUserShortDto(comment.getAuthor());
        EventShortDto eventShort = EventMapper.toEventShortDto(comment.getEvent(),
                requestRepository.countByEventIdAndStatus(comment.getEvent().getId(), CONFIRMED));
        return CommentMapper.toCommentDto(comment, userShort, eventShort);
    }

    @Override
    public void deleteComment(Long userId, Long commentId) {
        User author = checkAndGetUser(userId);
        Comment comment = checkAndGetComment(commentId);
        if (comment.getAuthor() != author) {
            throw new ValidationException("Only author can delete the comment.");
        }
        commentRepository.deleteById(commentId);
    }

    @Override
    public void deleteComment(Long commentId) {
        checkAndGetComment(commentId);
        commentRepository.deleteById(commentId);
    }

    private User checkAndGetUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("User with id=" + userId + " was not found"));
    }

    private Event checkAndGetEvent(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("Event with id=" + eventId + " was not found"));
    }

    private Comment checkAndGetComment(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(() ->
                new NotFoundException("Comment with id=" + commentId + " was not found"));
    }
}
