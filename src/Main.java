import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Scanner;

/**
 * Main menu for client: Instantiates instance of UDPClient.
 */
public class Main {

	public static void main(String[] args) throws SocketException, UnknownHostException {
		int argPort = 65535;
		String argServerAddr = "159.223.54.186";
		int argServerPort = 8888;

		// menu
		String MENU = "---------------------------------------\n" +
				"Distributed Flight Information System\n" +
				"---------------------------------------\n" +
				"Select an option from [1-7]:\n" +
				"1) Find flights\n" +
				"2) Check flight details\n" +
				"3) Reserve flight seats\n" +
				"4) Monitor flight updates\n" +
				"5) Check Seats Reserved\n" +
				"6) Refund Seat\n" +
				"7) Quit\n" +
				"---------------------------------------\n";

		// set ports
		String serverIpAddr = argServerAddr;
		int clientPort = argPort;
		int serverPort = argServerPort;

		// initialize UDPClient
		UDPClient client = new UDPClient(serverPort);

		// initialize Scanner to process user input
		Scanner sc = new Scanner(System.in);

		boolean quit = false;
		while (!quit) {
			System.out.print(MENU);
			int selection = sc.nextInt();
			switch (selection) {
				case 1:
					client.findFlights();
					break;
				case 2:
					client.checkFlightDetails();
					break;
				case 3:
					client.reserveSeats();
					break;
				case 4:
					client.monitorUpdates();
					break;
				case 5:
					client.getSeatsById();
					break;
				case 6:
					client.refundSeatBySeatNumAndId();
					break;
				case 7:
					quit = true;
					break;
				default:
					System.out.println("Selection is invalid! Please try again!");
					break;
			}
		}
		System.out.println("Quitting system!");
		sc.close();

	}

}