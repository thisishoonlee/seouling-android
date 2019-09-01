package mobile.seouling.com.application.events;

public class RequestPermissionEvent {
    public final String [] permissions;

    public RequestPermissionEvent(String [] permissions) {
        this.permissions = permissions;
    }
}

