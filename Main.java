import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

class Room {
    String roomno;
    char roomtype;
    boolean isOccupiedAndCheckedIn;
    int pricepernight;
    int extraBedRate;

    public Room(String roomNo, char roomType, int pricePerNight) {
        this.roomno = roomNo;
        this.roomtype = roomType;
        this.isOccupiedAndCheckedIn = false;
        this.pricepernight = pricePerNight;
        this.extraBedRate = (20 * pricePerNight) / 100;
    }

    public int getcostPerNight() {
        return pricepernight;
    }

    public int getExtraBedRate() {
        return extraBedRate;
    }

    public char getRoomType() {
        return roomtype;
    }

    public String getRoomNo() {
        return roomno;
    }

    public boolean getisCheckedIn() {
        return isOccupiedAndCheckedIn;
    }

    public void setCheckedIn(boolean isCheckedIn) {
        this.isOccupiedAndCheckedIn = isCheckedIn;
    }
}

class Guest {
    String name;
    String contactNumber;
    String address;
    private static Map<String, Integer> hotelDatabase = new HashMap<>();

    public Guest(String name, String contactNumber, String address) {
        this.name = name;
        this.contactNumber = contactNumber;
        this.address = address;
    }

    public void checkIn() {
        hotelDatabase.put(name, hotelDatabase.getOrDefault(name, 0) + 1);
    }
}

class Booking {
    Guest guest;
    Room room;
    String bookingCheckInDate;
    String bookingCheckInTime;
    String bookingCheckOutDate;
    String bookingCheckOutTime;
    int numberOfExtraBeds;
    double totalCost;
    LocalDateTime checkInDateTime;
    LocalDateTime checkOutDateTime;

    public Booking(Guest guest, Room room, String bookingCheckInDate, String bookingCheckInTime, String bookingCheckOutDate, String bookingCheckOutTime, int numberOfExtraBeds) {
        this.guest = guest;
        this.room = room;
        this.bookingCheckInDate = bookingCheckInDate;
        this.bookingCheckInTime = bookingCheckInTime;
        this.bookingCheckOutDate = bookingCheckOutDate;
        this.bookingCheckOutTime = bookingCheckOutTime;
        this.numberOfExtraBeds = numberOfExtraBeds;
        this.checkInDateTime = LocalDateTime.of(LocalDate.parse(bookingCheckInDate, DateTimeFormatter.ofPattern("dd:MM:yyyy")),
                LocalTime.parse(bookingCheckInTime));
        this.checkOutDateTime = LocalDateTime.of(LocalDate.parse(bookingCheckOutDate, DateTimeFormatter.ofPattern("dd:MM:yyyy")),
                LocalTime.parse(bookingCheckOutTime));
        calculateTotalCost();
    }

    public int numberOfDaysStayed() {
        return (int) ChronoUnit.DAYS.between(checkInDateTime.toLocalDate(), checkOutDateTime.toLocalDate());
    }

    public void calculateTotalCost() {
        int baseRoomCost = room.pricepernight * numberOfDaysStayed();
        int extraBedCost = room.extraBedRate * numberOfExtraBeds * numberOfDaysStayed();
        this.totalCost = baseRoomCost + extraBedCost;
    }

    public Guest getGuest() {
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

    public String getBookingCheckInTime() {
        return bookingCheckInTime;
    }

    public String getBookingCheckOutTime() {
        return bookingCheckOutTime;
    }

    public LocalDateTime getCheckInDateTime() {
        return checkInDateTime;
    }
}

class Hotel {
    List<Room> rooms;
    List<Booking> bookings;
    Queue<Booking> bookingQueue;
    Map<Character, Integer> availableRooms;

    public Hotel() {
        rooms = new ArrayList<>();
        bookings = new ArrayList<>();
        bookingQueue = new LinkedList<>();
        availableRooms = new HashMap<>();
        availableRooms.put('S', 0);
        availableRooms.put('D', 0);
        availableRooms.put('P', 0);
    }

    public void addRoom(Room room) {
        rooms.add(room);
        availableRooms.put(room.getRoomType(), availableRooms.get(room.getRoomType()) + 1);
    }

    public String getRoomTypeName(char roomType) {
        switch (roomType) {
            case 'S': return "Standard Room";
            case 'D': return "Deluxe Room";
            case 'P': return "Premium Room";
            default: return "Unknown Room Type";
        }
    }

    public boolean isRoomAvailable(String roomNo, LocalDateTime checkIn, LocalDateTime checkOut) {
        for (Booking booking : bookings) {
            if (booking.getRoom().getRoomNo().equals(roomNo)) {
                LocalDateTime existingCheckIn = booking.getCheckInDateTime();
                LocalDateTime existingCheckOut = existingCheckIn.plusDays(booking.numberOfDaysStayed());
                if (!(checkIn.isAfter(existingCheckOut) || checkOut.isBefore(existingCheckIn))) {
                    return false;
                }
            }
        }
        return true;
    }

    public Booking bookRoom(Guest guest, Room room, String checkInDate, String checkInTime, String checkOutDate, String checkOutTime, int numberOfExtraBeds) {
        LocalDateTime checkIn = LocalDateTime.of(LocalDate.parse(checkInDate, DateTimeFormatter.ofPattern("dd:MM:yyyy")),
                LocalTime.parse(checkInTime));
        LocalDateTime checkOut = LocalDateTime.of(LocalDate.parse(checkOutDate, DateTimeFormatter.ofPattern("dd:MM:yyyy")),
                LocalTime.parse(checkOutTime));

        if (isRoomAvailable(room.roomno, checkIn, checkOut)) {
            Booking booking = new Booking(guest, room, checkInDate, checkInTime, checkOutDate, checkOutTime, numberOfExtraBeds);
            bookings.add(booking);
            availableRooms.put(room.getRoomType(), availableRooms.get(room.getRoomType()) - 1);
            return booking;
        }
        return null;
    }

    public void checkIn(Booking booking) {
        booking.getGuest().checkIn();
        bookingQueue.remove(booking);
    }

    public void checkOut(Booking booking, String checkoutDate, String checkoutTime) {
        char roomType = booking.getRoom().getRoomType();

        booking.bookingCheckOutDate = checkoutDate;
        booking.bookingCheckOutTime = checkoutTime;
        booking.checkOutDateTime = LocalDateTime.of(LocalDate.parse(checkoutDate, DateTimeFormatter.ofPattern("dd:MM:yyyy")),
                LocalTime.parse(checkoutTime));

        availableRooms.put(roomType, availableRooms.get(roomType) + 1);
        booking.getRoom().setCheckedIn(false);
    }
    
    public void generateBill(Booking booking) {
        System.out.println("\n----- BILL -----");
        System.out.println("Guest Name: " + booking.guest.name);
        System.out.println("Room Number: " + booking.room.roomno);
        System.out.println("Room Type: " + getRoomTypeName(booking.room.roomtype));
        System.out.println("Check-in: " + booking.bookingCheckInDate + " " + booking.bookingCheckInTime);
        System.out.println("Check-out: " + booking.bookingCheckOutDate + " " + booking.bookingCheckOutTime);
        System.out.println("Number of Days: " + booking.numberOfDaysStayed());
        System.out.println("Number of Extra Beds: " + booking.numberOfExtraBeds);
        System.out.println("Total Cost: $" + booking.totalCost);
        System.out.println("----------------");
    }
}

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Hotel hotel = new Hotel();

        // Adding rooms to the hotel
        hotel.addRoom(new Room("101", 'S', 10000));
        hotel.addRoom(new Room("102", 'S', 10000));
        hotel.addRoom(new Room("201", 'D', 17500));
        hotel.addRoom(new Room("202", 'D', 17500));
        hotel.addRoom(new Room("301", 'P', 25000));
        hotel.addRoom(new Room("302", 'P', 25000));

        System.out.println("Enter the number of guests:");
        int numberOfGuests = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        for (int i = 1; i <= numberOfGuests; i++) {
            System.out.println("\nEnter details for Guest " + i + ":");
            System.out.println("Name:");
            String guestName = scanner.nextLine();
            System.out.println("Contact Number:");
            String guestContact = scanner.nextLine();
            System.out.println("Address:");
            String guestAddress = scanner.nextLine();

            Guest guest = new Guest(guestName, guestContact, guestAddress);

            System.out.println("Enter Room Type (S for Standard, D for Deluxe, P for Premium):");
            char roomType = scanner.nextLine().toUpperCase().charAt(0);
            
            Room selectedRoom = null;
            for (Room room : hotel.rooms) {
                if (room.roomtype == roomType && !room.isOccupiedAndCheckedIn) {
                    selectedRoom = room;
                    break;
                }
            }

            if (selectedRoom == null) {
                System.out.println("No available room of the selected type.");
                continue;
            }

            System.out.println("Enter Check-in Date (dd:MM:yyyy):");
            String checkInDate = scanner.nextLine();
            System.out.println("Enter Check-in Time (HH:mm):");
            String checkInTime = scanner.nextLine();
            System.out.println("Enter Check-out Date (dd:MM:yyyy):");
            String checkOutDate = scanner.nextLine();
            System.out.println("Enter Check-out Time (HH:mm):");
            String checkOutTime = scanner.nextLine();
            System.out.println("Enter Number of Extra Beds:");
            int extraBeds = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            Booking booking = hotel.bookRoom(guest, selectedRoom, checkInDate, checkInTime, checkOutDate, checkOutTime, extraBeds);
            if (booking != null) {
                selectedRoom.isOccupiedAndCheckedIn = true;
                hotel.generateBill(booking);
            } else {
                System.out.println("Booking failed. Room might be unavailable for the selected dates.");
            }
        }

        scanner.close();
    }
}
