package model;

import java.time.LocalDate;

public class Booking {
    private int bookingId;
    private int customerId;
    private int userId;
    private int hotelId;
    private int roomId;
    private int numberOfPeople;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private double totalAmount;
    private String paymentStatus;
    private String paymentId;

    public Booking() {
    }

    public Booking(int customerId, int hotelId, int roomId, int numberOfPeople, LocalDate checkIn, LocalDate checkOut,
                   double totalAmount, String paymentStatus, String paymentId) {
        this.customerId = customerId;
        this.hotelId = hotelId;
        this.roomId = roomId;
        this.numberOfPeople = numberOfPeople;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.totalAmount = totalAmount;
        this.paymentStatus = paymentStatus;
        this.paymentId = paymentId;
    }

    public Booking(int userId, int hotelId, int roomId, int numberOfPeople, LocalDate checkIn, LocalDate checkOut,
                   double totalAmount, String paymentStatus, String paymentId, boolean userBooking) {
        this.userId = userId;
        this.hotelId = hotelId;
        this.roomId = roomId;
        this.numberOfPeople = numberOfPeople;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.totalAmount = totalAmount;
        this.paymentStatus = paymentStatus;
        this.paymentId = paymentId;
    }

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getHotelId() {
        return hotelId;
    }

    public void setHotelId(int hotelId) {
        this.hotelId = hotelId;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public int getNumberOfPeople() {
        return numberOfPeople;
    }

    public void setNumberOfPeople(int numberOfPeople) {
        this.numberOfPeople = numberOfPeople;
    }

    public LocalDate getCheckIn() {
        return checkIn;
    }

    public void setCheckIn(LocalDate checkIn) {
        this.checkIn = checkIn;
    }

    public LocalDate getCheckOut() {
        return checkOut;
    }

    public void setCheckOut(LocalDate checkOut) {
        this.checkOut = checkOut;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }
}
