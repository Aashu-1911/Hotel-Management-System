# Hotel-Management-System-
The place for booking hotel  rooms 



Core Java + Swing + JDBC + Oracle desktop application with customer booking, simulated Razorpay test payment, and hotel admin management.

## Project Structure

```text
src/
  db/DBConnection.java
  model/Customer.java
  model/Room.java
  model/Booking.java
  model/Hotel.java
  model/BookingRecord.java
  dao/CustomerDAO.java
  dao/RoomDAO.java
  dao/BookingDAO.java
  dao/HotelDAO.java
  dao/HotelAdminDAO.java
  dao/UserDAO.java
  ui/UserLoginFrame.java
  ui/SignupFrame.java
  ui/LoginFrame.java
  ui/Dashboard.java
  ui/BookingForm.java
  ui/PaymentFrame.java
  ui/SuccessFrame.java
  ui/HotelLoginFrame.java
  ui/HotelDashboard.java
  ui/ViewBookingsFrame.java
  ui/CheckoutFrame.java
  ui/UIStyle.java
  main/Main.java
database/
  oracle_schema.sql
  oracle_users_update.sql
```

## Database Setup

For a fresh database, run:

```text
database/oracle_schema.sql
```

For an older version of this project that already has `customers`, `rooms`, `bookings`, `hotels`, and `hotel_admin`, run:

```text
database/oracle_users_update.sql
```

Then update the password in `src/db/DBConnection.java`:

```java
private static final String PASSWORD = "your_password";
```

## Compile

```powershell
javac -d out src\db\DBConnection.java src\model\Customer.java src\model\Room.java src\model\Booking.java src\model\Hotel.java src\model\BookingRecord.java src\dao\CustomerDAO.java src\dao\RoomDAO.java src\dao\BookingDAO.java src\dao\HotelDAO.java src\dao\HotelAdminDAO.java src\ui\UIStyle.java src\ui\LoginFrame.java src\ui\Dashboard.java src\ui\BookingForm.java src\ui\PaymentFrame.java src\ui\SuccessFrame.java src\ui\HotelLoginFrame.java src\ui\HotelDashboard.java src\ui\ViewBookingsFrame.java src\ui\CheckoutFrame.java src\main\Main.java
```

## Run

Use your Oracle JDBC jar at runtime. This project already contains `ojdbc14.jar`, so you can run:

```powershell
java -cp "out;ojdbc14.jar" main.Main
```

## Logins

Customer side:

Use `Create Account` on the first screen, then login with that email and password.

Hotel admin examples:

```text
blueadmin / blue123
mountainadmin / mountain123
cityadmin / city123
```

## Main Flow

Customer:

1. Signup or login.
2. Select `Book Room`.
3. Select hotel.
4. Select available room for that hotel.
5. Enter check-in and check-out dates.
6. Pay through simulated Razorpay test flow.
7. Booking is saved with `user_id` and `hotel_id`, and room status becomes `Booked`.

Hotel admin:

1. Login through `Hotel Admin Login`.
2. View only bookings for the logged-in hotel.
3. Checkout customer by booking ID.
4. Room status becomes `Available` again.


compile
javac -cp "ojdbc14.jar" -d out src\db\DBConnection.java src\model\Customer.java src\model\Room.java src\model\Booking.java src\model\Hotel.java src\model\BookingRecord.java src\dao\CustomerDAO.java src\dao\RoomDAO.java src\dao\BookingDAO.java src\dao\HotelDAO.java src\dao\HotelAdminDAO.java src\dao\UserDAO.java src\ui\UIStyle.java src\ui\UserLoginFrame.java src\ui\LoginFrame.java src\ui\SignupFrame.java src\ui\Dashboard.java src\ui\BookingForm.java src\ui\PaymentFrame.java src\ui\SuccessFrame.java src\ui\HotelLoginFrame.java src\ui\HotelDashboard.java src\ui\ViewBookingsFrame.java src\ui\CheckoutFrame.java src\main\Main.java


run
java -cp "out;ojdbc14.jar" main.Main
