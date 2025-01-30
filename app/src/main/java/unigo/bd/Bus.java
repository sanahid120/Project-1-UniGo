package unigo.bd;

public class Bus {
    private String id;
    private String busNumber;

    public Bus() {}

    public Bus(String busNumber) {
        this.busNumber = busNumber;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBusNumber() {
        return busNumber;
    }

    public void setBusNumber(String busNumber) {
        this.busNumber = busNumber;
    }
}
