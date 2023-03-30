import java.util.Scanner;
import java.nio.ByteBuffer;

public class UIUtils {
	static Scanner sc = new Scanner(System.in);

	public static String checkStringInput() {
		try {
			String input = sc.nextLine();
			return input;
		} catch (Exception e) {
			System.out.println("Input is invalid! Please try again.");
			return checkStringInput();
		}
	}

	public static int checkIntInput() {
		try {
			int input = sc.nextInt();
			sc.nextLine();
			return input;
		} catch (Exception e) {
			System.out.println("Input is invalid! Please try again.");
			return checkIntInput();
		}
	}

	public static int extractReqId(byte[] input) {
		ByteBuffer buffer = ByteBuffer.wrap(input, 0, 4);
		byte[] slice = new byte[4];
		buffer.get(slice);
		int req_id = unmarshalInt(slice);
		return req_id;
	}

	public static int extractStatusCode(byte[] input) {
		ByteBuffer buffer = ByteBuffer.wrap(input, 4, 8);
		byte[] slice = new byte[4];
		buffer.get(slice);
		int status_code = unmarshalInt(slice);
		return status_code;
	}

	public static byte[] extractPayload(byte[] input) {
		ByteBuffer buffer = ByteBuffer.wrap(input, 8, input.length - 8);
		byte[] slice = new byte[input.length - 8];
		buffer.get(slice);
		return slice;
	}

	public static ByteBuffer marshalInt(ByteBuffer buffer, int input) {
		buffer.putInt(input);

		return buffer;
	}

	public static ByteBuffer marshalLong(ByteBuffer buffer, long input) {
		buffer.putLong(input);

		return buffer;
	}

	public static int[] unmarshalIntArray(byte[] input) {
		ByteBuffer buffer = ByteBuffer.wrap(input, 0, 4);
		byte[] slice = new byte[4];
		buffer.get(slice);
		int lengthData = unmarshalInt(slice);

		int[] value = new int[lengthData];
		for (int i = 0; i < lengthData; i++) {
			buffer = ByteBuffer.wrap(input, 4 + i * 4, 8 + i * 4);
			slice = new byte[4];
			buffer.get(slice);
			value[i] = unmarshalInt(slice);
		}
		return value;
	}

	public static int unmarshalInt(byte[] input) {
		ByteBuffer buffer = ByteBuffer.wrap(input, 0, 4);
		int value = buffer.getInt();
		return value;
	}

	public static long unmarshalLong(byte[] input) {
		ByteBuffer buffer = ByteBuffer.wrap(input, 0, 8);
		long value = buffer.getLong();
		return value;
	}

	public static double unmarshalDouble(byte[] input) {
		ByteBuffer buffer = ByteBuffer.wrap(input, 0, 8);
		double value = buffer.getDouble();
		return value;
	}
}
