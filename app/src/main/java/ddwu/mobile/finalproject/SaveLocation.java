package ddwu.mobile.finalproject;

public class SaveLocation {
    private long _id;
    private String create_date;
    private String address;
    private String latitude;
    private String longitude;
    private String filename;

    public SaveLocation(long _id, String create_date, String address, String latitude, String longitude, String filename) {
        this._id = _id;
        this.create_date = create_date;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.filename = filename;
    }

    public long get_id() {
        return _id;
    }

    public String getCreate_date() {
        return create_date;
    }

    public String getAddress() {
        return address;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getFilename() {
        return filename;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public void setCreate_date(String create_date) {
        this.create_date = create_date;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    @Override
    public String toString() {
        return "SaveLocation{" +
                "_id=" + _id +
                ", create_date='" + create_date + '\'' +
                ", address='" + address + '\'' +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                ", filename='" + filename + '\'' +
                '}';
    }
}
