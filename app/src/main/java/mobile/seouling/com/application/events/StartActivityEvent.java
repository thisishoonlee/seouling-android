package mobile.seouling.com.application.events;


import android.content.Intent;

import java.util.function.Consumer;

public class StartActivityEvent {

    public final Intent intent;
    public final Consumer<Exception> errorHandler;

    public StartActivityEvent(Intent intent) {
        this(intent, null);
    }

    public StartActivityEvent(Intent intent, Consumer<Exception> errorHandler) {
        this.intent = intent;
        this.errorHandler = errorHandler;
    }
}
