package com.sockib.notesapp.policy.note;

import com.sockib.notesapp.policy.Sanitizer;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;

public class NoteTitleSanitizer implements Sanitizer {
    private final PolicyFactory policyFactory;

    public NoteTitleSanitizer() {
        this.policyFactory = new HtmlPolicyBuilder().toFactory();
    }
    @Override
    public String sanitize(String text) {
        return policyFactory.sanitize(text);
    }

}
