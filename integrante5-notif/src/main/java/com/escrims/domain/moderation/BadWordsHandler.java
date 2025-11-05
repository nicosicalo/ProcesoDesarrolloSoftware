package com.escrims.domain.moderation;

import com.escrims.domain.feedback.Rating;
import java.util.Set;

public class BadWordsHandler extends ReportHandler {
    private static final Set<String> BAD = Set.of("noob", "troll", "toxico", "toxic");
    @Override
    protected ModerationResult process(Rating r) {
        String c = (r.comentario()==null?"":r.comentario()).toLowerCase();
        for (var b : BAD){
            if (c.contains(b)){
                return new ModerationResult(ModerationStatus.REJECTED, "Bad words detected: " + b);
            }
        }
        return new ModerationResult(ModerationStatus.PASSTHROUGH, "ok");
    }
}
