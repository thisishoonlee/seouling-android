package mobile.seouling.com.application.json;

/**
 * Created by jj on 6/14/17.
 */

public class ErrorJson {
    public int errorCode;
    public String message;
    public String code;
    public String summary;

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Error2Json{").append("httpCode=").append(errorCode);
        builder.append(", code='").append(code).append('\'');
        builder.append(", message='").append(message).append('\'');
        if (summary != null) { builder.append(", summary='").append(summary).append('\''); }
        return builder.append('}').toString();
    }
}
