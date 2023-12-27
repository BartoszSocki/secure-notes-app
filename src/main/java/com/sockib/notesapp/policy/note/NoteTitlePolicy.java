package com.sockib.notesapp.policy.note;

import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;

public class NoteTitlePolicy implements Sanitizer {
    private final PolicyFactory policyFactory;

    public NoteTitlePolicy() {
        this.policyFactory = new HtmlPolicyBuilder().toFactory();
    }
    @Override
    public String sanitize(String text) {
        return policyFactory.sanitize(text);
    }

}
