import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.HashMap;


class Room {
    String roomno;
    char roomtype;
    boolean isOccupiedAndCheckedIn;
    int pricepernight;
    int extraBedRate;

    public Room(String roomNo, char roomType, int pricePerNight, int extraBedRate) {
        this.roomno = roomNo;
        this.roomtype = roomType;
        this.isOccupiedAndCheckedIn = false;
        this.pricepernight = pricePerNight;
        this.extraBedRate = extraBedRate;
    }

    public int calculateTotalCostPerNight() {
        return pricepernight;
    }

    public int getExtraBedRate() {
        return extraBedRate;
    }
    
    public String toString() {
        return getClass().getSimpleName() + " - Room Number: " + roomno;
    }
}

class StandardRoom extends Room {
    private static int standardRoomPrice = 10000;
    private static int extraBedRate = (20 * standardRoomPrice) / 100;

    public StandardRoom(String roomno, char roomType) {
        super(roomno, roomType, standardRoomPrice, extraBedRate);
    }
}

class DeluxeRoom extends Room {
    private static int deluxeRoomPrice = 17500;
    private static int extraBedRate = (20 * deluxeRoomPrice) / 100;

    public DeluxeRoom(String roomno, char roomType) {
        super(roomno, roomType, deluxeRoomPrice, extraBedRate);
    }
}

class PremiumRoom extends Room {
    private static int premiumRoomPrice = 25000;
    private static int extraBedRate = (20 * premiumRoomPrice) / 100;

    public PremiumRoom(String roomno, char roomType) {
        super(roomno, roomType, premiumRoomPrice, extraBedRate);
    }
}


class GuestClass {
    static Map<String, Integer> hotelDatabase = new HashMap<>();
    static HashSet<String> temporaryGuests = new HashSet<>();
    
    String name;
    String contactNumber;
    String address;
    List<Booking> bookings;

    public GuestClass(String name, String contactNumber, String address) {
        this.name = name;
        this.contactNumber = contactNumber;
        this.address = address;
        this.bookings = new ArrayList<>();
        temporaryGuests.add(name);
    }

    public void addBooking(Booking booking) {
        bookings.add(booking);
    }

    public boolean hasPreviousBookings() {
        if (hotelDatabase.containsKey(name)) {
            int count = hotelDatabase.get(name);
            return count > 1;
        } else {
            return false;
        }
    }

    public void checkout() {
        temporaryGuests.remove(name);
    }

    public void checkIn() {
        // If the guest is already in the database, increment their count
        if (hotelDatabase.containsKey(name)) {
            int count = hotelDatabase.get(name);
            hotelDatabase.put(name, count + 1);
        } else {
            // If the guest is not in the database, add them with count 1
            hotelDatabase.put(name, 1);
        }
    }
}


class Booking {
    GuestClass guest;
    Room room;
    String bookingCheckInDate;
    String bookingCheckInTime;
    String bookingCheckOutDate;
    String bookingCheckOutTime;
    int numberOfExtraBeds;
    double totalCost;
    LocalDateTime checkInDateTime;

    public Booking(GuestClass guest, Room room, String bookingCheckInDate, String bookingCheckInTime, String bookingCheckOutDate, String bookingCheckOutTime, int numberOfExtraBeds) {
        this.guest = guest;
        this.room = room;
        this.bookingCheckInDate = bookingCheckInDate;
        this.bookingCheckInTime = bookingCheckInTime;
        this.bookingCheckOutDate = bookingCheckOutDate;
        this.bookingCheckOutTime = bookingCheckOutTime;
        this.numberOfExtraBeds = numberOfExtraBeds;
        this.totalCost = calculateTotalCost();
        guest.checkIn();
    }

    public int numberOfDaysStayed() {
        LocalDate startDate = LocalDate.parse(bookingCheckInDate, DateTimeFormatter.ofPattern("dd:MM:yyyy"));
        LocalDate endDate = LocalDate.parse(bookingCheckOutDate, DateTimeFormatter.ofPattern("dd:MM:yyyy"));
        return (int) ChronoUnit.DAYS.between(startDate, endDate);
    }

    public double calculateTotalCost() {
        int baseRoomCost = room.calculateTotalCostPerNight() * numberOfDaysStayed();
        int extraBedCost = room.getExtraBedRate() * numberOfExtraBeds * numberOfDaysStayed();
        return baseRoomCost + extraBedCost;
    }

    public boolean checkedIn() {
        return room.isOccupiedAndCheckedIn;
    }

    public GuestClass getGuest() {
        return guest;
    }

    public Room getRoom() {
        return room;
    }

    public String getBookingCheckInDate() {
        return bookingCheckInDate;
    }

    public String getBookingCheckOutDate() {
        return bookingCheckOutDate;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void getBookingDetails() {
        String roomTypeName;
        switch (room.roomtype) {
            case 'S':
                roomTypeName = "Standard Room";
                break;
            case 'D':
                roomTypeName = "Deluxe Room";
                break;
            case 'P':
                roomTypeName = "Premium Room";
                break;
            default:
                roomTypeName = "Unknown Room Type";
                break;
        }
        
        System.out.println("\nBooking Details:\n" +
                "Guest: " + guest.name + "\n" +
                "Room Number: " + room.roomno + "\n" +
                "Room Type: " + roomTypeName + "\n" +
                "Check-in Date: " + bookingCheckInDate + "\n" +
                "Check-out Date: " + bookingCheckOutDate + "\n" +
                "Number of Extra Beds: " + numberOfExtraBeds + "\n");
    }
}

class Hotel {
    List<Room> rooms;
    List<Booking> bookings;
    Map<Character, Integer> availableRooms;

    public Hotel() {
        rooms = new ArrayList<>();
        bookings = new ArrayList<>();
        availableRooms = new Hashtable<>();
    }

    public void addRoom(Room room, char roomType) {
        rooms.add(room);
        availableRooms.put(roomType, availableRooms.getOrDefault(roomType, 0) + 1);
    }

    public void checkOut(Booking booking, String checkoutDate, String checkoutTime) {
        availableRooms.put(booking.getRoom().roomtype, availableRooms.get(booking.getRoom().roomtype) + 1);

        LocalDateTime checkOutDateTime = LocalDateTime.of(LocalDate.parse(checkoutDate, DateTimeFormatter.ofPattern("dd:MM:yyyy")), LocalTime.parse(checkoutTime));
        LocalDateTime bookingCheckOutDateTime = LocalDateTime.of(LocalDate.parse(booking.bookingCheckOutDate, DateTimeFormatter.ofPattern("dd:MM:yyyy")), LocalTime.parse(booking.bookingCheckOutTime));

        long extraStayDays = ChronoUnit.DAYS.between(bookingCheckOutDateTime, checkOutDateTime);
        long extraStayHours = ChronoUnit.HOURS.between(bookingCheckOutDateTime, checkOutDateTime);

        double extraCost = 0;
        if (extraStayDays > 0) {
            extraCost += extraStayDays * booking.getRoom().calculateTotalCostPerNight();
        }
        if (extraStayHours > 0) {
            extraCost += extraStayHours * (booking.getRoom().calculateTotalCostPerNight() / 24.0);
        }

        if (extraCost > 0) {
            booking.totalCost += extraCost;
        }

        BillGenerator.generateBill("Hotel DC", "123 Main St, Gotham City", booking);
        booking.getGuest().checkout();
        System.out.flush();
    }

    public int getAvailableRoomsCount(char roomType) {
        return availableRooms.getOrDefault(roomType, 0);
    }

    public void printAvailableRooms() {
        System.out.println("Available Rooms:");
        for (Map.Entry<Character, Integer> entry : availableRooms.entrySet()) {
            char roomType = entry.getKey();
            int count = entry.getValue();
            String roomTypeName;
            switch (roomType) {
                case 'S':
                    roomTypeName = "Standard Room";
                    break;
                case 'D':
                    roomTypeName = "Deluxe Room";
                    break;
                case 'P':
                    roomTypeName = "Premium Room";
                    break;
                default:
                    roomTypeName = "Unknown Room Type";
                    break;
            }
            for (Room room : rooms) {
                if (room.roomtype == roomType && !room.isOccupiedAndCheckedIn) {
                    System.out.println(roomTypeName + " - Room Number: " + room.roomno);
                }
            }
        }
        System.out.println("--------------------------------------------------");
        System.out.flush();
    }

    public void printGuestNames() {
        System.out.println("Guest Names:");
        for (String guestName : GuestClass.hotelDatabase.keySet()) {
            System.out.println(guestName);
        }
        System.out.println("--------------------------------------------------");
    }

    private String getRoomTypeName(char roomType) {
        switch (roomType) {
            case 'S':
                return "Standard Room";
            case 'D':
                return "Deluxe Room";
            case 'P':
                return "Premium Room";
            default:
                return "Unknown Room Type";
        }
    }

    public void checkIn(Booking booking) {
        addBooking(booking);
    }

    public boolean hasTimeOverlap(Booking newBooking) {
        for (Booking existingBooking : bookings) {
            if (existingBooking.getRoom() == newBooking.getRoom() &&
                existingBooking.getBookingCheckInDate().equals(newBooking.getBookingCheckInDate())) {
                // Parse booking times
                LocalTime existingCheckInTime = LocalTime.parse(existingBooking.bookingCheckInTime);
                LocalTime existingCheckOutTime = LocalTime.parse(existingBooking.bookingCheckOutTime);
                LocalTime newCheckInTime = LocalTime.parse(newBooking.bookingCheckInTime);
                LocalTime newCheckOutTime = LocalTime.parse(newBooking.bookingCheckOutTime);

                // Check for overlap
                if ((newCheckInTime.isBefore(existingCheckOutTime) && newCheckInTime.isAfter(existingCheckInTime)) ||
                    (newCheckOutTime.isBefore(existingCheckOutTime) && newCheckOutTime.isAfter(existingCheckInTime)) ||
                    (newCheckInTime.isBefore(existingCheckInTime) && newCheckOutTime.isAfter(existingCheckOutTime)) ||
                    (newCheckInTime.equals(existingCheckInTime) && newCheckOutTime.equals(existingCheckOutTime))) {
                    return true; // Overlap found
                }
            }
        }
        return false; // No overlap found
    }

    public void addBooking(Booking booking) {
        if (hasTimeOverlap(booking)) {
            System.out.println("Booking cannot be made due to time overlap with existing booking.");
            return;
        }

        // Add booking
        bookings.add(booking);
        char roomType = booking.getRoom().roomtype;
        availableRooms.put(roomType, availableRooms.get(roomType) - 1);
        booking.room.isOccupiedAndCheckedIn = true;
        System.out.println("" + getRoomTypeName(roomType) + " - Room Number: " + booking.getRoom().roomno + " Booking confirmed!");
        System.out.flush();
        booking.getGuest().checkIn();
    }
}

class BillGenerator {
    private static double discountPercent = 5;
    public static double newtotalcost = 0;
    public static void setDiscountPercent(double percent) {
        discountPercent = percent;
    }

    public static void generateBill(String hotelName, String hotelAddress, Booking booking) {
        double totalCost = booking.getTotalCost();
        boolean isReturningCustomer = booking.getGuest().hasPreviousBookings();
        System.out.println(isReturningCustomer);
        if (isReturningCustomer) {
            System.out.println(totalCost);
            double discountAmount = (discountPercent / 100) * totalCost;
            totalCost -= discountAmount;
            booking.totalCost = totalCost;
            newtotalcost = totalCost;
        }

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = now.format(formatter);

        System.out.println("\n---------------------------------------------------------------------------------------------------------------------------------");
        System.out.println(hotelName);
        System.out.println(hotelAddress);
        System.out.println("Bill Date & Time: " + formattedDateTime);
        System.out.println("\n---------------------------------------------------------------------------------------------------------------------------------");
        System.out.println("           BILL                ");
        System.out.println("Guest Name: " + booking.getGuest().name);
        System.out.println("Room Number: " + booking.getRoom().roomno);
        System.out.println("Room Type: " + getRoomTypeName(booking.getRoom().roomtype));
        System.out.println("Check-in Date: " + booking.getBookingCheckInDate());
        System.out.println("Check-out Date: " + booking.getBookingCheckOutDate());
        System.out.println("Number of Extra Beds: " + booking.numberOfExtraBeds);
        System.out.println("Total Cost: " + totalCost);
        System.out.println("New Total Cost: " + newtotalcost);
        System.out.println("\n---------------------------------------------------------------------------------------------------------------------------------");
        System.out.println("Thank you for choosing " + hotelName + "!");
        System.out.println("We hope to see you again soon.");
        System.out.println("\n---------------------------------------------------------------------------------------------------------------------------------");
        System.out.flush();
    }

    private static String getRoomTypeName(char roomType) {
        switch (roomType) {
            case 'S':
                return "Standard Room";
            case 'D':
                return "Deluxe Room";
            case 'P':
                return "Premium Room";
            default:
                return "Unknown Room Type";
        }
    }
}

public class Main {
    /**
     * @param args
     */
    public static void main(String[] args) {
        Hotel hotel = new Hotel();
        
        // Adding rooms to the hotel
        Room standardRoom1 = new StandardRoom("101", 'S');
        hotel.addRoom(standardRoom1, 'S');

        Room standardRoom2 = new StandardRoom("102", 'S');
        hotel.addRoom(standardRoom2, 'S');

        Room deluxeRoom1 = new DeluxeRoom("201", 'D');
        hotel.addRoom(deluxeRoom1, 'D');

        // Printing available rooms
        //hotel.printAvailableRooms();

        // Print guest names
        //hotel.printGuestNames();
        
        System.out.println("Hotel Database:");
        for (Map.Entry<String, Integer> entry : GuestClass.hotelDatabase.entrySet()) {
            String guestName = entry.getKey();
            int bookingCount = entry.getValue();
            System.out.println("Guest: " + guestName + ", Bookings: " + bookingCount);
        }
        System.out.println("--------------------------------------------------");

        // Creating guests and creating bookings
        GuestClass guest1 = new GuestClass("Batman", "245678", "Gotham City");
        Booking booking1 = new Booking(guest1, standardRoom1, "16:04:2024", "12:00", "18:04:2024", "12:00", 1);

        GuestClass guest2 = new GuestClass("Joker", "198765", "Gotham City");
        Booking booking2 = new Booking(guest2, deluxeRoom1, "16:04:2024", "12:00", "20:04:2024", "12:00", 2);

        GuestClass guest3 = new GuestClass("Superman", "123456", "Metropolis");
        Booking booking3 = new Booking(guest3, standardRoom2, "19:04:2024", "12:00", "21:04:2024", "12:00", 1);
        
        GuestClass guest4 = new GuestClass("Wonder Woman", "789012", "Themyscira");
        Booking booking4 = new Booking(guest4, standardRoom2, "01:05:2024", "12:00", "03:05:2024", "12:00", 0);
        // Creating a new booking for an existing guest
        Booking booking5 = new Booking(guest1, standardRoom1, "01:05:2024", "12:00", "03:05:2024", "12:00", 0);
        
        System.out.println("Hotel Database:");
        for (Map.Entry<String, Integer> entry : GuestClass.hotelDatabase.entrySet()) {
            String guestName = entry.getKey();
            int bookingCount = entry.getValue();
            System.out.println("Guest: " + guestName + ", Bookings: " + bookingCount);
        }
        System.out.println("--------------------------------------------------");
        
        // Checking in guests
        hotel.checkIn(booking1);
        hotel.checkIn(booking2);
        hotel.checkIn(booking3);
        hotel.checkIn(booking4);
        hotel.checkIn(booking5);
        
        System.out.println("Hotel Database:");
        for (Map.Entry<String, Integer> entry : GuestClass.hotelDatabase.entrySet()) {
            String guestName = entry.getKey();
            int bookingCount = entry.getValue();
            System.out.println("Guest: " + guestName + ", Bookings: " + bookingCount);
        }
        System.out.println("--------------------------------------------------");
        
        // Checking out guests
        hotel.checkOut(booking1, "18:04:2024", "12:00");
        hotel.checkOut(booking2, "20:04:2024", "12:00");
        hotel.checkOut(booking3, "21:04:2024", "12:00");
        hotel.checkOut(booking4, "03:05:2024", "12:00");
        hotel.checkOut(booking5, "03:05:2024", "12:00");
        
        System.out.println("Hotel Database:");
        for (Map.Entry<String, Integer> entry : GuestClass.hotelDatabase.entrySet()) {
            String guestName = entry.getKey();
            int bookingCount = entry.getValue();
            System.out.println("Guest: " + guestName + ", Bookings: " + bookingCount);
        }
        System.out.println("--------------------------------------------------");
    }
}
