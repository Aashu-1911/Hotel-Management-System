package model;

public class HotelCatalog {
    private final int hotelId;
    private final String hotelName;
    private final String address;
    private final double rating;
    private final Double startingPrice;
    private final int availableRooms;
    private final int roomTypes;

    public HotelCatalog(int hotelId, String hotelName, String address, double rating,
                        Double startingPrice, int availableRooms, int roomTypes) {
        this.hotelId = hotelId;
        this.hotelName = hotelName;
        this.address = address;
        this.rating = rating;
        this.startingPrice = startingPrice;
        this.availableRooms = availableRooms;
        this.roomTypes = roomTypes;
    }

    public int getHotelId() {
        return hotelId;
    }

    public String getHotelName() {
        return hotelName;
    }

    public String getAddress() {
        return address;
    }

    public double getRating() {
        return rating;
    }

    public Double getStartingPrice() {
        return startingPrice;
    }

    public int getAvailableRooms() {
        return availableRooms;
    }

    public int getRoomTypes() {
        return roomTypes;
    }
}
