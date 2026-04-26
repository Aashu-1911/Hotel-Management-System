package model;

import java.sql.Date;

public class BookingRecord {
    private int bookingId;
    private String customerName;
    private int roomId;
    private int numberOfPeople;
    private Date checkIn;
    private Date checkOut;
    private double totalAmount;
    private String paymentStatus;

    public BookingRecord(int bookingId, String customerName, int roomId, int numberOfPeople, Date checkIn,
                         Date checkOut, double totalAmount, String paymentStatus) {
        this.bookingId = bookingId;
        this.customerName = customerName;
        this.roomId = roomId;
        this.numberOfPeople = numberOfPeople;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.totalAmount = totalAmount;
        this.paymentStatus = paymentStatus;
    }

    public int getBookingId() {
        return bookingId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public int getRoomId() {
        return roomId;
    }

    public int getNumberOfPeople() {
        return numberOfPeople;
    }

    public Date getCheckIn() {
        return checkIn;
    }

    public Date getCheckOut() {
        return checkOut;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }
}
