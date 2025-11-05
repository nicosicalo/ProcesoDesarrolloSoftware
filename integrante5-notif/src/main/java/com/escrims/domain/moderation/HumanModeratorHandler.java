package com.escrims.domain.moderation;

import com.escrims.domain.feedback.Rating;

public class HumanModeratorHandler extends ReportHandler {
    @Override
    protected ModerationResult process(Rating r) {
        if (r.comentario()!=null && r.comentario().length()>300){
            return new ModerationResult(ModerationStatus.REJECTED, "Comment too long");
        }
        return new ModerationResult(ModerationStatus.APPROVED, "Human moderator approved");
    }
}
