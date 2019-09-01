package mobile.seouling.com.application.common.network;

import androidx.annotation.NonNull;
import io.reactivex.functions.Consumer;
import mobile.seouling.com.framework.GsonFactory;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RequestBodyUtil {
    private static final String FILE_MEDIA_TYPE = "multipart/form-data";
    private static final String JSON_MEDIA_TYPE = "application/json; charset=utf-8";

    public static RequestBody json(Consumer<JsonObjectBuilder> action) {
        JsonObjectBuilder builder = new JsonObjectBuilder();
        try {
            action.accept(builder);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return builder.build();
    }

    public static RequestBody jsonArray(Consumer<JsonArrayBuilder> action) {
        JsonArrayBuilder builder = new JsonArrayBuilder();
        try {
            action.accept(builder);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return builder.build();
    }

    @NonNull
    public static MultipartBody.Part file(String name, File file) {
        RequestBody body = RequestBody.create(MediaType.parse(FILE_MEDIA_TYPE), file);
        return MultipartBody.Part.createFormData(name, file.getName(), body);
    }

    @NonNull
    public static MultipartBody.Part file(String name, byte[] content) {
        RequestBody body = RequestBody.create(MediaType.parse(FILE_MEDIA_TYPE), content);
        return MultipartBody.Part.createFormData(name, "image", body);
    }
//
//    @Nullable
//    public static MultipartBody.Part file(String name, Uri uri) {
//        if ("content".equals(uri.getScheme())) {
//            byte[] result = MallangRequest.getContentFromUri(uri);
//            if (result != null) {
//                return file(name, result);
//            }
//        } else {
//            File file = new File(uri.getPath());
//            if (file.exists()) {
//                return file(name, file);
//            }
//        }
//        return null;
//    }

    public static class JsonObjectBuilder {
        private final HashMap<String, Object> map = new HashMap<>();

        private JsonObjectBuilder() {}

        public JsonObjectBuilder putObject(String key, Object value) {
            map.put(key, value);
            return this;
        }

        public JsonObjectBuilder putJsonObject(String key, Consumer<JsonObjectBuilder> value) {
            JsonObjectBuilder t = new JsonObjectBuilder();
            try {
                value.accept(t);
            } catch (Exception e) {
                e.printStackTrace();
            }
            putObject(key, t.map);
            return this;
        }

        public JsonObjectBuilder putJsonArray(String key, Consumer<JsonArrayBuilder> value) {
            JsonArrayBuilder t = new JsonArrayBuilder();
            try {
                value.accept(t);
            } catch (Exception e) {
                e.printStackTrace();
            }
            putObject(key, t.list);
            return this;
        }

        private RequestBody build() {
            return RequestBody.create(MediaType.parse(JSON_MEDIA_TYPE), GsonFactory.getGson().toJson(map));
        }
    }

    public static class JsonArrayBuilder {
        private final List<Object> list = new ArrayList<>();

        private JsonArrayBuilder() {}

        public JsonArrayBuilder addObject(Object value) {
            list.add(value);
            return this;
        }

        public JsonArrayBuilder addJsonObject(Consumer<JsonObjectBuilder> value) {
            JsonObjectBuilder t = new JsonObjectBuilder();
            try {
                value.accept(t);
            } catch (Exception e) {
                e.printStackTrace();
            }
            addObject(t.map);
            return this;
        }

        private RequestBody build() {
            return RequestBody.create(MediaType.parse(JSON_MEDIA_TYPE), GsonFactory.getGson().toJson(list));
        }
    }
}

