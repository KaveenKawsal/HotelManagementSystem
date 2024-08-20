# Hotel Management System

This Java-based Hotel Management System is designed to handle room bookings, guest management, and billing for a hotel. It provides a simple command-line interface for managing hotel operations.

## Features

- Room management (Standard, Deluxe, and Premium rooms)
- Guest registration
- Room booking with date and time
- Automatic bill generation
- Check-in and check-out functionality
- Extra bed requests

## Classes

- `Room`: Represents a hotel room with its properties and methods.
- `Guest`: Manages guest information and check-in status.
- `Booking`: Handles booking details, including dates, times, and cost calculation.
- `Hotel`: Main class for hotel operations, including room management and booking processes.
- `Main`: Contains the main method to run the program and handle user input.

## How to Use

1. Compile the Java files:
2. Run the program :
3. Follow the on-screen prompts to:
- Enter the number of guests
- Provide guest details (name, contact number, address)
- Select room type
- Enter check-in and check-out dates and times
- Specify the number of extra beds (if any)
4. The program will display a bill for each successful booking.

  ## Notes

- The system currently supports a fixed number of rooms (2 each of Standard, Deluxe, and Premium).
- All dates should be entered in the format dd:MM:yyyy.
- All times should be entered in the 24-hour format HH:mm.
- The program does not persist data between runs. All bookings and guest information are lost when the program terminates.

## Future Improvements

- Implement data persistence (database or file storage)
- Add a graphical user interface (GUI)
- Implement more advanced search and filtering options for rooms and bookings
- Add support for multiple hotels or hotel chains

## Contributing

Feel free to fork this repository and submit pull requests with any improvements or bug fixes. For major changes, please open an issue first to discuss what you would like to change.

