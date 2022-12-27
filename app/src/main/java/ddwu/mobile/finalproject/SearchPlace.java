package ddwu.mobile.finalproject;

public class SearchPlace {
    private long _id;
    private String title;
    private String address;

    public long get_id() {
        return _id;
    }

    public String getTitle() {
        return title;
    }

    public String getAddress() {
        return address;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "Location{" +
                "_id=" + _id +
                ", title='" + title + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
