package bet.service.comment;

import org.springframework.stereotype.Component;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

@Component
public class ImageLinkConverter implements LinkConverter {

    private final List<String> imageSuffixes = Arrays.asList("jpg", "jpeg", "gif", "png", "bmp");

    public String convertLink(String link) {
        return String.format("<img class=\"img-fluid\" src=\"%s\" />", link);
    }

    public boolean isApplicable(String link) {
        return isImage(link);
    }

    private boolean isImage(String link) {
        try {
            URL url = new URL(link);
            if (isImageBySuffix(url.getPath())) {
                return true;
            }
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            String contentType = con.getHeaderField("content-type");
            if (contentType != null) {
                if (contentType.startsWith("image")) {
                    return true;
                }
            }
        } catch (Exception e) {
            return false;
        }

        return false;
    }

    private boolean isImageBySuffix(String link) {
        String extension = "";

        int i = link.lastIndexOf('.');
        if (i > 0) {
            extension = link.substring(i + 1);
        }

        return imageSuffixes.contains(extension);
    }
}