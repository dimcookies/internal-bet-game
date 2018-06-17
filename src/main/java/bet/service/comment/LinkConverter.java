package bet.service.comment;

public interface LinkConverter {

    String convertLink(String link);

    boolean isApplicable(String comment);

}