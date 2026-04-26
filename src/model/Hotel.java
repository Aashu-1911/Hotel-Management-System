package model;

public class Hotel {
    private int hotelId;
    private String hotelName;
    private String address;
    private double rating;

    public Hotel() {
    }

    public Hotel(int hotelId, String hotelName, String address) {
        this(hotelId, hotelName, address, 4.0);
    }

    public Hotel(int hotelId, String hotelName, String address, double rating) {
        this.hotelId = hotelId;
        this.hotelName = hotelName;
        this.address = address;
        this.rating = rating;
    }

    public int getHotelId() {
        return hotelId;
    }

    public void setHotelId(int hotelId) {
        this.hotelId = hotelId;
    }

    public String getHotelName() {
        return hotelName;
    }

    public void setHotelName(String hotelName) {
        this.hotelName = hotelName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    @Override
    public String toString() {
        return hotelName + " - " + address;
    }
}
