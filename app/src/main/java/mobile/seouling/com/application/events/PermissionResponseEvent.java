package mobile.seouling.com.application.events;

public class PermissionResponseEvent {
    public final String[] permissions;
    public final int[] grantResults;

    public PermissionResponseEvent(String[] permissions, int[] grantResults) {
        this.permissions = permissions;
        this.grantResults = grantResults;
    }
}
