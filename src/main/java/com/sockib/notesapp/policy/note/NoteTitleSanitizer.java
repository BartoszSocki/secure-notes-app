package com.sockib.notesapp.policy.note;

import com.sockib.notesapp.policy.Sanitizer;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;
import org.springframework.web.util.HtmlUtils;

public class NoteTitleSanitizer implements Sanitizer {

    @Override
    public String sanitize(String text) {
        return HtmlUtils.htmlEscape(text);
    }

}
