package unigo.bd;

public class ScheduleItem {
    private String route, time, bus;

    public ScheduleItem(String route, String time, String bus) {
        this.route = route;
        this.time = time;
        this.bus = bus;
    }

    public String getRoute() {
        return route;
    }

    public String getTime() {
        return time;
    }

    public String getBus() {
        return bus;
    }
}