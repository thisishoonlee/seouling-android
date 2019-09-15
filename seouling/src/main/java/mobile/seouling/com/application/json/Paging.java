package mobile.seouling.com.application.json;

public class Paging {
    public String after;
    public String before;

    @Override
    public String toString() {
        return "Paging{" +
                "after='" + after + '\'' +
                ", before='" + before + '\'' +
                '}';
    }
}
