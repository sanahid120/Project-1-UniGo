package unigo.bd;

public class BusRequestModel {
    private String time;
    private String route;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String id;
    private long count;


    public BusRequestModel() {
        // Empty constructor needed for Firebase
    }

    public BusRequestModel(String time, String route, long count) {
        this.time = time;
        this.route = route;
        this.count = count;
    }

    public String getTime() {
        return time;
    }

    public String getRoute() {
        return route;
    }

    public long getCount() {
        return count;
    }
}
