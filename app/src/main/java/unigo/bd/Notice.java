package unigo.bd;

public class Notice {
    private String title, description, imageUrl, id,date;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Notice() {
        // Default Constructor (Needed for Firebase)
    }

    public Notice(String title, String description, String imageUrl, String id, String date) {
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.id = id;
        this.date=date;
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
