package mobile.seouling.com.application.json;

import androidx.annotation.Nullable;

/**
 * Created by Jonguk on 2016. 12. 27..
 */
public class Response<T> {
    @Nullable public T data;
    @Nullable public Paging paging;
    @Nullable public ErrorJson error;

    @Nullable
    public T getData() {
        return data;
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder().append("Response{").append("data=").append(data);
        if (paging != null) { b.append(", paging=").append(paging); }
        if (error != null) { b.append(", error=").append(error); }
        return b.append('}').toString();
    }
}
