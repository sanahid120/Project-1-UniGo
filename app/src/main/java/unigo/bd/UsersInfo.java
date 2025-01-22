package unigo.bd;

public class UsersInfo {
    public String username;
    public String email;

    // Default constructor for Firebase
    public UsersInfo() {}

    public UsersInfo(String username, String email) {
        this.username = username;
        this.email = email;
    }
}
