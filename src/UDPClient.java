import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
        this.reqID = Math.abs(UUID.randomUUID().hashCode());
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

        // update requestID, marshal and send request to server
        reqID++;
        byte[] request_msg = marshal(byteArray, reqID, serviceID);
        byte[] response_msg = sendMessage(request_msg);

        // unmarshal response from server and display
        // System.out.println(response_msg.toString());
        int req_id = UIUtils.extractReqId(response_msg);
        int status_code = UIUtils.extractStatusCode(response_msg);
        byte[] payload = UIUtils.extractPayload(response_msg);
        System.out.println("RequestID: " + req_id);
        System.out.println("StatusCode: " + status_code);

        try {
            int[] flightidsarray = UIUtils.unmarshalIntArray(payload);
            if (flightidsarray.length > 0) {
                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < flightidsarray.length; i++) {
                    builder.append(flightidsarray[i]);
                    if (i < flightidsarray.length - 1) {
                        builder.append(", ");
                    }
                }
                String flightids = builder.toString();
                System.out.println(
                        "The following flight IDs fly from " + source + " to " + destination + ": " + flightids);
            } else {
                System.out.println("No flights were found to fly from " + source + " to " + destination);
            }
        } catch (Exception e) {
            System.out.println("No flights were found to fly from " + source + " to " + destination);
        }
    }

    public void checkFlightDetails() {
        final int serviceID = 2;

        // obtain flightID
        System.out.println("Please input the Flight ID: ");
        int flightID = UIUtils.checkIntInput();

        System.out.println("Retrieving flight information...");

        // put flightID into msg byte array
        ByteBuffer msg_bytes = ByteBuffer.allocate(4);
        msg_bytes.putInt(flightID);
        byte[] byteArray = msg_bytes.array();

        // update requestID, marshal and send request to server
        reqID++;
        byte[] request_msg = marshal(byteArray, reqID, serviceID);
        byte[] response_msg = sendMessage(request_msg);

        // unmarshal response from server and display
        // System.out.println(response_msg.toString());

        int req_id = UIUtils.extractReqId(response_msg);
        int status_code = UIUtils.extractStatusCode(response_msg);
        byte[] payload = UIUtils.extractPayload(response_msg);
        System.out.println("RequestID: " + req_id);
        System.out.println("StatusCode: " + status_code);

        try {
            ByteBuffer buffer = ByteBuffer.wrap(payload, 0, 8);
            byte[] slice = new byte[8];
            buffer.get(slice);
            long departure_time = UIUtils.unmarshalLong(slice);

            buffer = ByteBuffer.wrap(payload, 8, 16);
            slice = new byte[8];
            buffer.get(slice);
            double price = UIUtils.unmarshalDouble(slice);

            buffer = ByteBuffer.wrap(payload, 16, 20);
            slice = new byte[4];
            buffer.get(slice);
            int seats_left = UIUtils.unmarshalInt(slice);

            System.out.println("Departure Time: " + new java.util.Date(departure_time * 1000));
            System.out.println("Airfare: $" + (double) Math.round(price * 100) / 100);
            System.out.println("Seat Availability: " + seats_left);
        } catch (Exception e) {
            System.out.println("No flight was found with Flight ID " + flightID);
        }
    }

    public void reserveSeats() {
        final int serviceID = 3;

        try {
            // obtain flightID
            System.out.println("Please input the Flight ID: ");
            int flight_id = UIUtils.checkIntInput();

            // obtain numSeats
            System.out.println("How many seats would you like to reserve?");
            int numSeats = UIUtils.checkIntInput();

            System.out.println("Reserving your seats...");

            // combine flightID and numSeats into msg byte array here

            ByteBuffer msg_bytes = ByteBuffer.allocate(8);
            msg_bytes = UIUtils.marshalInt(msg_bytes, flight_id);
            msg_bytes = UIUtils.marshalInt(msg_bytes, numSeats);
            byte[] byteArray = msg_bytes.array();

            // update requestID, marshal and send request to server
            reqID++;
            byte[] request_msg = marshal(byteArray, reqID, serviceID);
            byte[] response_msg = sendMessage(request_msg);

            // unmarshal response from server and display
            // System.out.println(response_msg.toString());
            int req_id = UIUtils.extractReqId(response_msg);
            int status_code = UIUtils.extractStatusCode(response_msg);
            byte[] payload = UIUtils.extractPayload(response_msg);
            System.out.println("RequestID: " + req_id);
            System.out.println("StatusCode: " + status_code);

            // unmarshal payload
            int[] seats = UIUtils.unmarshalIntArray(payload);

            if (seats.length == 0) {
                System.out.println("The flight is fully booked!");
            } else {
                System.out.println("Success! Your seats are:" + Arrays.toString(seats));
            }
        } catch (Exception e) {
            System.out.println("Sorry! You cannot book that many seats at once!");
        }
        
    }

    public void monitorUpdates() {
        final int serviceID = 4;

        // obtain flightID
        System.out.println("Please input the Flight ID: ");
        int flight_id = UIUtils.checkIntInput();

        // obtain monitor interval
        System.out.println("Until when would you like to receive updates on seat availability?");
        System.out.println("Please input your response in DD/MM/YY format.");
        String monitorInterval = UIUtils.checkStringInput();

        try {
            // parse monitorInterval for date and convert to unixTime
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
            Date date = dateFormat.parse(monitorInterval);
            long unixTime = date.getTime() / 1000;

            System.out.println("Registering you for updates...");

            // combine flightID and unixTime into msg byte array here
            ByteBuffer msg_bytes = ByteBuffer.allocate(12);
            msg_bytes = UIUtils.marshalInt(msg_bytes, flight_id);
            msg_bytes = UIUtils.marshalLong(msg_bytes, unixTime);
            byte[] byteArray = msg_bytes.array();

            // update requestID, marshal and send request to server
            reqID++;
            byte[] request_msg = marshal(byteArray, reqID, serviceID);
            byte[] response_msg = sendMessage(request_msg);

            // unmarshal response from server and display
            int req_id = UIUtils.extractReqId(response_msg);
            int status_code = UIUtils.extractStatusCode(response_msg);
            byte[] payload = UIUtils.extractPayload(response_msg);
            System.out.println("RequestID: " + req_id);
            System.out.println("StatusCode: " + status_code);

            int response = payload[0];

            if (response == 1) {
                System.out.println("Successfully registered for updates! We'll keep you posted.");
            } else {
                System.out.println("Unable to register. Please try again!");
            }
        } catch (ParseException e) {
            System.out.println("Invalid date! Please enter a valid date.");
        } catch (Exception e) {
            System.out.println("No flight was found with Flight ID " + flight_id);
        }
    }

    public void getSeatsById() {
        final int serviceID = 5;

        // obtain flightID
        System.out.println("Please input the Flight ID: ");
        int flight_id = UIUtils.checkIntInput();

        ByteBuffer msg_bytes = ByteBuffer.allocate(4);
        msg_bytes = UIUtils.marshalInt(msg_bytes, flight_id);
        byte[] byteArray = msg_bytes.array();

        // update requestID, marshal and send request to server
        reqID++;
        byte[] request_msg = marshal(byteArray, reqID, serviceID);
        byte[] response_msg = sendMessage(request_msg);

        // unmarshal response from server and display
        int req_id = UIUtils.extractReqId(response_msg);
        int status_code = UIUtils.extractStatusCode(response_msg);
        byte[] payload = UIUtils.extractPayload(response_msg);
        System.out.println("RequestID: " + req_id);
        System.out.println("StatusCode: " + status_code);

        try {
            int[] seats = UIUtils.unmarshalIntArray(payload);
            System.out.println("Your seats for flight " + flight_id + " are:" + Arrays.toString(seats));
        } catch (Exception e) {
            System.out.println("No flight was found with Flight ID " + flight_id);
        }
    }

    public void refundSeatBySeatNumAndId() {
        final int serviceID = 6;

        // obtain flightID
        System.out.println("Please input the Flight ID: ");
        int flight_id = UIUtils.checkIntInput();

        System.out.println("Which seat would you like to refund?");
        int seat_number = UIUtils.checkIntInput();

        ByteBuffer msg_bytes = ByteBuffer.allocate(8);
        msg_bytes = UIUtils.marshalInt(msg_bytes, flight_id);
        msg_bytes = UIUtils.marshalInt(msg_bytes, seat_number);
        byte[] byteArray = msg_bytes.array();

        // update requestID, marshal and send request to server
        reqID++;
        byte[] request_msg = marshal(byteArray, reqID, serviceID);
        byte[] response_msg = sendMessage(request_msg);

        // unmarshal response from server and display
        int req_id = UIUtils.extractReqId(response_msg);
        int status_code = UIUtils.extractStatusCode(response_msg);
        byte[] payload = UIUtils.extractPayload(response_msg);
        System.out.println("RequestID: " + req_id);
        System.out.println("StatusCode: " + status_code);

        try {
            int response = UIUtils.unmarshalInt(payload);

            if (response == 1) {
                System.out.println("Sucessfully refunded! Please come again!");
            } else {
                System.out.println("Unable to refund. Please try again!");
            }

        } catch (Exception e) {
            System.out.println("No flight was found with Flight ID " + flight_id);
        }
    }

    private byte[] marshal(byte[] msg_bytes, int reqID, int serviceID) {
        // bytes for request ID
        byte[] reqID_bytes = ByteBuffer.allocate(4).putInt(reqID).array();

        // bytes for service ID
        byte[] serviceID_bytes = ByteBuffer.allocate(4).putInt(serviceID).array();

        // combine
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
