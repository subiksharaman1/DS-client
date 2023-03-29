import java.util.Scanner;

public class UIUtils {
    static Scanner sc = new Scanner(System.in);

    public static String checkStringInput(){
        try {
			String input = sc.nextLine();
			while (input.equals("")) {
				System.out.println("Input is empty! Please try again.");
				input = sc.nextLine();
			}
			return input;
		} catch (Exception e) {
			System.out.println("Input is invalid! Please try again.");
			return checkStringInput();
		}
    }

	public static int checkIntInput(){
        try {
			int input = sc.nextInt();
			return input;
		} catch (Exception e) {
			System.out.println("Input is invalid! Please try again.");
			return checkIntInput();
		}
    }

}
