import java.io.*;
import java.lang.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.PatternSyntaxException;

/**
 * Functionality of UDP Client
 */
public class UDPClient {
    private DatagramSocket clientSocket;
    private InetAddress aHost;
    private int port;
    private int reqID;

    public UDPClient(int serverPort) throws SocketException, UnknownHostException {
        this.clientSocket = new DatagramSocket();
        this.aHost = InetAddress.getByName("127.0.0.1");
        this.port = serverPort;
        this.reqID = 0;
    }

    public void findFlights() {

        final int serviceID = 1;

        // obtain source
        System.out.println("Where are you flying from?");
        String source = UIUtils.checkStringInput();
        int source_len = source.getBytes().length;

        // obtain destination
        System.out.println("Where are you flying to?");
        String destination = UIUtils.checkStringInput();
        int dest_len = destination.getBytes().length;

        System.out.println("Searching for flights...");

        // combine the two strings into one msg byte array here
        ByteBuffer msg_bytes = ByteBuffer.allocate(4 + source_len + 4 + dest_len);
        msg_bytes.putInt(source_len);
        msg_bytes.put(source.getBytes(StandardCharsets.UTF_8));
        msg_bytes.putInt(dest_len);
        msg_bytes.put(destination.getBytes(StandardCharsets.UTF_8));
        byte[] byteArray = msg_bytes.array();
        System.out.println("Message: " + Arrays.toString(byteArray));

        // update requestID, marshal and send request to server
        reqID++;
        byte[] request_msg = marshal(byteArray, reqID, serviceID);
        byte[] response_msg = sendMessage(request_msg);

        // unmarshal response from server and display
        // System.out.println(response_msg.toString());
        ByteBuffer buffer = ByteBuffer.wrap(response_msg, 0, 4);
        byte[] slice = new byte[4];
        buffer.get(slice);
        int req_id = UIUtils.unmarshalInt(slice);
        System.out.println("Request: " + Arrays.toString(slice));

        buffer = ByteBuffer.wrap(response_msg, 4, response_msg.length - 4);
        slice = new byte[response_msg.length - 4];
        buffer.get(slice);
        int[] response = UIUtils.unmarshalIntArray(slice);
        System.out.println("Length of response: " + slice);

        String res_str = new String(response_msg, 8, response_msg[7], StandardCharsets.UTF_8);
        System.out.println("Request ID:" + req_id);
        System.out.println("Response message: " + response);

        try {
            String[] split_res = res_str.split("\0");
            if (response.length > 0) {
                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < response.length; i++) {
                    builder.append(response[i]);
                    if (i < response.length - 1) {
                        builder.append(",");
                    }
                }
                String flightids = builder.toString();
                System.out.println("The following flights ids fly from " + source + " to " + destination + ": " + flightids);
            } else {
                System.out.println("No flights were found to fly from " + source + " to " + destination);
            }
        } catch (PatternSyntaxException e) {
            System.out.println("No flights were found to fly from " + source + " to " + destination);
        }
    }

    public void checkFlightDetails() {

        final int serviceID = 2;

        // obtain flightID
        System.out.println("Please input the Flight ID: ");
        String flightID = UIUtils.checkStringInput();
        int flightID_len = flightID.getBytes().length;

        System.out.println("Retrieving flight information...");

        // put flightID_len and flightID into msg byte array here
        ByteBuffer msg_bytes = ByteBuffer.allocate(4 + flightID_len);
        msg_bytes.putInt(flightID_len);
        msg_bytes.put(flightID.getBytes(StandardCharsets.UTF_8));
        byte[] byteArray = msg_bytes.array();
        System.out.println("Message: " + Arrays.toString(byteArray));

        // update requestID, marshal and send request to server
        reqID++;
        byte[] request_msg = marshal(byteArray, reqID, serviceID);
        byte[] response_msg = sendMessage(request_msg);

        // unmarshal response from server and display
        // System.out.println(response_msg.toString());
        System.out.println("RequestID: " + response_msg[3]);
        System.out.println("Length of response: " + response_msg[7]);
        String res_str = new String(response_msg, 8, response_msg[7], StandardCharsets.UTF_8);
        System.out.println("Response message: " + res_str);

        try {
            String[] split_res = res_str.split("\0");
            System.out.println("FLIGHT ID: " + flightID);
            System.out.println("Departure Time: " + split_res[0]);
            System.out.println("Airfare: " + split_res[1]);
            System.out.println("Seat Availability: " + split_res[2]);
            for (String s : split_res) {
                System.out.println(s);
            }
        } catch (PatternSyntaxException e) {
            System.out.println("No flight was found with Flight ID " + flightID);
        }
    }

    public void reserveSeats() {

        final int serviceID = 3;

        // obtain flightID
        System.out.println("Please input the Flight ID: ");
        String flightID = UIUtils.checkStringInput();
        int flightID_len = flightID.getBytes().length;

        // obtain numSeats
        System.out.println("How many seats would you like to reserve?");
        int numSeats = UIUtils.checkIntInput();
        int numSeats_len = 4;

        System.out.println("Reserving your seats...");

        // combine flightID and numSeats into msg byte array here
        ByteBuffer msg_bytes = ByteBuffer.allocate(4 + flightID_len + 4 + numSeats_len);
        msg_bytes.putInt(flightID_len);
        msg_bytes.put(flightID.getBytes(StandardCharsets.UTF_8));
        msg_bytes.putInt(numSeats_len);
        msg_bytes.putInt(numSeats);
        byte[] byteArray = msg_bytes.array();
        System.out.println("Message: " + Arrays.toString(byteArray));

        // update requestID, marshal and send request to server
        reqID++;
        byte[] request_msg = marshal(byteArray, reqID, serviceID);
        byte[] response_msg = sendMessage(request_msg);

        // unmarshal response from server and display
        // System.out.println(response_msg.toString());
        System.out.println("RequestID: " + response_msg[3]);
        System.out.println("Length of response: " + response_msg[7]);

        String res_str = new String(response_msg, 8, response_msg[7], StandardCharsets.UTF_8);
        System.out.println("Response message: " + res_str);

        // TODO: edit below section (try catch) to display ACK or display different
        // error messages
        try {
            String[] split_res = res_str.split("\0");
            System.out.println("FLIGHT ID: " + flightID);
            System.out.println("Departure Time: " + split_res[0]);
            System.out.println("Airfare: " + split_res[1]);
            System.out.println("Seat Availability: " + split_res[2]);
            for (String s : split_res) {
                System.out.println(s);
            }
        } catch (PatternSyntaxException e) {
            System.out.println("No flight was found with Flight ID " + flightID);
            System.out.println("The flight is fully booked!");
        }
    }

    public void monitorUpdates() {

        final int serviceID = 4;

        // obtain flightID
        System.out.println("Please input the Flight ID: ");
        String flightID = UIUtils.checkStringInput();
        int flightID_len = flightID.getBytes().length;

        // obtain monitor interval
        System.out.println("How often would you like to receive updates on seat availability?");
        System.out.println("Please input your response in hours.");
        int monitorInterval = UIUtils.checkIntInput();
        int monitorInterval_len = 4;

        System.out.println("Registering you for updates...");

        // combine flightID and monitorInterval into msg byte array here
        ByteBuffer msg_bytes = ByteBuffer.allocate(4 + flightID_len + 4 + monitorInterval_len);
        msg_bytes.putInt(flightID_len);
        msg_bytes.put(flightID.getBytes(StandardCharsets.UTF_8));
        msg_bytes.putInt(monitorInterval_len);
        msg_bytes.putInt(monitorInterval);
        byte[] byteArray = msg_bytes.array();
        System.out.println("Message: " + Arrays.toString(byteArray));

        // update requestID, marshal and send request to server
        reqID++;
        byte[] request_msg = marshal(byteArray, reqID, serviceID);
        byte[] response_msg = sendMessage(request_msg);

        // unmarshal response from server and display
        // System.out.println(response_msg.toString());
        System.out.println("RequestID: " + response_msg[3]);
        System.out.println("Length of response: " + response_msg[7]);

        String res_str = new String(response_msg, 8, response_msg[7], StandardCharsets.UTF_8);
        System.out.println("Response message: " + res_str);

        // TODO: edit below section (try catch) to display ACK or display different
        // error messages
        try {
            String[] split_res = res_str.split("\0");
            System.out.println("FLIGHT ID: " + flightID);
            System.out.println("Departure Time: " + split_res[0]);
            System.out.println("Airfare: " + split_res[1]);
            System.out.println("Seat Availability: " + split_res[2]);
            for (String s : split_res) {
                System.out.println(s);
            }
        } catch (PatternSyntaxException e) {
            System.out.println("No flight was found with Flight ID " + flightID);
            System.out.println("The flight is fully booked!");
        }
    }

    public void idempotent() {

    }

    public void nonidempotent() {

    }

    public byte[] marshal(byte[] msg_bytes, int reqID, int serviceID) {

        // bytes for request ID
        byte[] reqID_bytes = ByteBuffer.allocate(4).putInt(reqID).array();
        // System.out.println(Arrays.toString(reqID_bytes));

        // bytes for service ID
        byte[] serviceID_bytes = ByteBuffer.allocate(4).putInt(serviceID).array();
        // System.out.println(Arrays.toString(serviceID_bytes));

        // bytes for message
        // byte[] msg_bytes = msg.getBytes(StandardCharsets.UTF_8);
        // System.out.println(Arrays.toString(msg_bytes));

        byte[] FullByteArray = new byte[reqID_bytes.length + serviceID_bytes.length + msg_bytes.length];

        ByteBuffer buff = ByteBuffer.wrap(FullByteArray);
        buff.put(reqID_bytes);
        buff.put(serviceID_bytes);
        buff.put(msg_bytes);

        // marshall and return all the bytes together into single array
        byte[] byteArray = buff.array();
        return byteArray;
    }

    private byte[] sendMessage(byte[] msg) {
        System.out.println("Request: " + Arrays.toString(msg));

        try {
            // send packet
            DatagramPacket rqpacket = new DatagramPacket(msg, msg.length, aHost, port);
            clientSocket.send(rqpacket);
            System.out.println("Request sent to " + aHost.toString());

            // receive packet
            byte[] response = new byte[1024];
            DatagramPacket rspacket = new DatagramPacket(response, response.length);
            boolean received = false;
            while (!received) {
                try {
                    clientSocket.receive(rspacket);
                    received = true;
                } catch (SocketTimeoutException ste) {
                    // resend packet
                    System.out.println("Timeout! Resending request.");
                    clientSocket.send(rqpacket);
                }
            }
            System.out.println("Response returned: " + rspacket.getData());
            return rspacket.getData();
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return new byte[0];
        }

    }

    // public static void main(String[] args){
    // try {
    // UDPClient udpClient = new UDPClient(1234);
    // udpClient.findFlights();

    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // }
}
