package com.sockib.notesapp.policy.note;

import com.sockib.notesapp.policy.Sanitizer;
import org.owasp.html.*;

public class NoteContentSanitizer implements Sanitizer {

    private final PolicyFactory policyFactory;

    public NoteContentSanitizer() {
        this.policyFactory = Sanitizers.BLOCKS
                .and(Sanitizers.FORMATTING)
                .and(Sanitizers.IMAGES)
                .and(Sanitizers.LINKS)
                .and(Sanitizers.STYLES);
    }

    @Override
    public String sanitize(String text) {
        return policyFactory.sanitize(text);
    }

}
