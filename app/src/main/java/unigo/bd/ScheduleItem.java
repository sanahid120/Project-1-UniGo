package unigo.bd;

public class ScheduleItem {
    private String id;
    private String route;
    private String time;
    private String bus;
    private String type;
    private boolean markedCompleted;

    public ScheduleItem(){

    }
    public ScheduleItem(String route, String time, String bus) {
        this.route = route;
        this.time = time;
        this.bus = bus;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public String getTime() {
        return time;
    }
    public String getType(){return  type;}
    public void setType(String type){this.type=type; }

    public void setTime(String time) {
        this.time = time;
    }

    public String getBus() {
        return bus;
    }

    public void setBus(String bus) {
        this.bus = bus;
    }

    public boolean isMarkedCompleted() {
        return markedCompleted;
    }

    public void setMarkedCompleted(boolean markedCompleted) {
        this.markedCompleted = markedCompleted;
    }
}
