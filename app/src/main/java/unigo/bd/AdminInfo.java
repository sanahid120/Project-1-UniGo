package unigo.bd;

public class AdminInfo {

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String key;
    public String email;
    public String password;

    // Default constructor for Firebase
    public AdminInfo() {}

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public AdminInfo(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
