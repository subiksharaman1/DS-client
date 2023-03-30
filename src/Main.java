import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Scanner;

/**
 * Main menu for client: Instantiates instance of UDPClient.
 */
public class Main {

	public static void main(String[] args) throws SocketException, UnknownHostException {
		int argPort = 65535;
		String argServerAddr = "127.0.0.1";
		int argServerPort = 8888;

		// if (args.length > 0) {
		// try {
		// if (args.length == 1) {
		// argPort = Integer.parseInt(args[0]);
		// System.out.println("Client port set to: " + argPort);
		// }
		// if (args.length == 2) {
		// argPort = Integer.parseInt(args[0]);
		// argServerAddr = args[1];
		// System.out.println("Client port set to " + argPort);
		// System.out.println("Server address set to " + argServerAddr);
		// }
		// if (args.length == 3) {
		// argPort = Integer.parseInt(args[0]);
		// argServerAddr = args[1];
		// argServerPort = Integer.parseInt(args[2]);
		// System.out.println("Client port set to " + argPort);
		// System.out.println("Server address set to " + argServerAddr);
		// System.out.println("Server port set to " + argServerPort);
		// }
		// } catch (Exception e) {
		// System.err.println("Invalid arguments.");
		// System.exit(1);
		// }
		// }

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