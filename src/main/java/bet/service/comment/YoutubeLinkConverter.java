package bet.service.comment;

import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class YoutubeLinkConverter implements LinkConverter {

    public String convertLink(String link) {
        String videoUrl = extractVideoUrl(link);
        return String.format("<div class=\"embed-responsive embed-responsive-4by3\"><iframe class=\"embed-responsive-item\" src=\"https://www.youtube.com/embed/%s?rel=0\" allowfullscreen></iframe></div>", videoUrl);
    }

    public boolean isApplicable(String link) {
        return isYoutubeLink(link);
    }

    String extractVideoUrl(String link) {
        if (link.contains("youtube.com")) {
            try {
                URL url = new URL(link);
                return getQueryMap(url.getQuery()).getOrDefault("v", "");
            } catch (MalformedURLException e) {
                throw new RuntimeException("Invalid youtube link", e);
            }
        } else {
            String[] ar = link.split("/");
            if (ar.length == 0) {
                throw new RuntimeException("Invalid short youtube link");
            }
            return ar[ar.length - 1];
        }
    }

    private Map<String, String> getQueryMap(String query) {
        return Arrays.stream(query.split("&"))
                .collect(Collectors.toMap(o -> o.split("=")[0], o -> o.split("=")[1]));
    }


    private boolean isYoutubeLink(String link) {
        return link.contains("youtube.com") || link.contains("youtu.be") || link.contains("y2u.be");
    }
}