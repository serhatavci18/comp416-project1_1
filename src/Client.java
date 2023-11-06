import java.io.*;
import java.net.Socket;

public class Client {
    public static void main(String[] args) {
        String serverAddress = "localhost"; // Change to the server's address.
        int serverPort = 12345; // Change to the server's port number.

        try (Socket socket = new Socket(serverAddress, serverPort);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            // Send a request to the server (for example, a request to get NFT list).
            String request = "NFTList";
            out.println(request);

            // Read and process the server's response (replace this with your custom protocol handling).
            String response;
            while ((response = in.readLine()) != null) {
                System.out.println("Received from server: " + response);
                // You need to parse the response based on your protocol.
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
