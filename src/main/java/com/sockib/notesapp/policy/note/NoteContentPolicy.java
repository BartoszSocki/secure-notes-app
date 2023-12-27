package com.sockib.notesapp.policy.note;

import org.owasp.html.*;

public class NoteContentPolicy implements Sanitizer {

    private final PolicyFactory policyFactory;

    public NoteContentPolicy() {
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
