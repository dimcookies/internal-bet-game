package bet.service.comment;

import org.springframework.stereotype.Component;

@Component
public class SimpleLinkConverter implements LinkConverter {


    public String convertLink(String link) {
        return String.format("<a target=\"_blank\" href=\"%s\" rel=\"noopener noreferrer\" >%s</a>", link, link);
    }

    public boolean isApplicable(String link) {
        return true;
    }
}