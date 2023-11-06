import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket clientSocket;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try (
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))
        ) {
            String clientMessage;
            while ((clientMessage = in.readLine()) != null) {
                // Implement your custom communication protocol here
                // Handle different types of client requests
                // You can use JSON or other message structures.
                if (clientMessage.contains("NFTList")) {
                    // Implement code to fetch and send NFT list data
                    // You can use ClientHelper class or similar for API interaction
                    // Use out.println() to send responses back to the client.
                } else if (clientMessage.contains("NFTDetails")) {
                    // Implement code to fetch and send NFT details data
                } else {
                    // Handle unsupported or invalid requests
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
