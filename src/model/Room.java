package model;

public class Room {
    private int roomId;
    private int hotelId;
    private String type;
    private double price;
    private String status;

    public Room() {
    }

    public Room(int roomId, int hotelId, String type, double price, String status) {
        this.roomId = roomId;
        this.hotelId = hotelId;
        this.type = type;
        this.price = price;
        this.status = status;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public int getHotelId() {
        return hotelId;
    }

    public void setHotelId(int hotelId) {
        this.hotelId = hotelId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return roomId + " - " + type + " - Rs. " + price;
    }
}
