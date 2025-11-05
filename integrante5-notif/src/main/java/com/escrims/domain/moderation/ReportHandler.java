package com.escrims.domain.moderation;

import com.escrims.domain.feedback.Rating;

public abstract class ReportHandler {
    private ReportHandler next;
    public ReportHandler linkWith(ReportHandler next){
        this.next = next; return next;
    }
    public final ModerationResult handle(Rating r){
        ModerationResult res = process(r);
        if (res.status() != ModerationStatus.PASSTHROUGH) return res;
        if (next == null) return new ModerationResult(ModerationStatus.APPROVED, "No issues");
        return next.handle(r);
    }
    protected abstract ModerationResult process(Rating r);
}
