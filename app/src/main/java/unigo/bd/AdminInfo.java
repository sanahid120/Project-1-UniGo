package unigo.bd;

public class AdminInfo {
    public String email;
    public String password;

    // Default constructor for Firebase
    public AdminInfo() {}

    public AdminInfo(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
