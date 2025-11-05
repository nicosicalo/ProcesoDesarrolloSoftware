package com.escrims.domain.moderation;

import com.escrims.domain.feedback.Rating;

public class LowStarsEscalationHandler extends ReportHandler {
    @Override
    protected ModerationResult process(Rating r) {
        if (r.estrellas() <= 2){
            return new ModerationResult(ModerationStatus.ESCALATED, "Low rating requires human review");
        }
        return new ModerationResult(ModerationStatus.PASSTHROUGH, "ok");
    }
}
