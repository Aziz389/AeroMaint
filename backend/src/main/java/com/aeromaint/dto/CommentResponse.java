package com.aeromaint.dto;

import com.aeromaint.entity.TicketComment;

import java.time.LocalDateTime;

public record CommentResponse(Long id, Long ticketId, UserSummary author, String content, LocalDateTime createdAt) {

    public static CommentResponse from(TicketComment c) {
        return new CommentResponse(c.getId(), c.getTicket().getId(), UserSummary.from(c.getAuthor()),
                c.getContent(), c.getCreatedAt());
    }
}
