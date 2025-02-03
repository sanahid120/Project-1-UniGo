package unigo.bd;

public class Notice {
    private String title, description, imageUrl, id;

    public Notice() {
        // Default Constructor (Needed for Firebase)
    }

    public Notice(String title, String description, String imageUrl, String id) {
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getId() {
        return id;
    }
}
