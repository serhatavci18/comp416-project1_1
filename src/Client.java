import java.io.*;
import java.net.Socket;
import java.util.Scanner;

import org.json.JSONObject;
import org.json.JSONArray;

public class Client {
    public static void main(String[] args) {
        String serverAddress = "localhost";
        int serverPort = 12345;

        try (Socket socket = new Socket(serverAddress, serverPort);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             Scanner scanner = new Scanner(System.in)) {

            while (true) {
                System.out.println("Enter your request (e.g., NFTList, NFTDetails NFTId):");

                String userInput = scanner.nextLine();

                String[] inputParts = userInput.split("\\s+", 2);
                String command = inputParts[0];

                JSONObject request = new JSONObject();

                if ("NFTList".equalsIgnoreCase(command) || "NFTDetails".equalsIgnoreCase(command)) {
                    request.put("requestType", command);

                    if (inputParts.length == 2) {
                        request.put("parameters", inputParts[1]);
                    }
                } else {
                    System.out.println("Invalid command. Supported commands are NFTList and NFTDetails.");
                    continue;
                }

                out.println(request.toString());

                String response = in.readLine();
                if (response != null) {
                    JSONObject jsonResponse = new JSONObject(response);

                    String responseType = jsonResponse.getString("responseType");
                    System.out.println("Received from server:");

                    if ("NFTListResponse".equals(responseType)) {
                        JSONArray data = jsonResponse.getJSONArray("data");
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject nft = data.getJSONObject(i);
                            System.out.println("Symbol: " + nft.getString("symbol"));
                            System.out.println("Name: " + nft.getString("name"));
                            System.out.println("ID: " + nft.getString("id"));
                            if (!nft.isNull("contract_address")) {
                                System.out.println("Contract Address: " + nft.getString("contract_address"));
                            } else {
                                System.out.println("Contract Address: None");
                            }
                            System.out.println();
                        }
                    }
                    if ("NFTDetailsResponse".equals(responseType)) {
                        JSONObject data = jsonResponse.getJSONObject("data");
                        System.out.println("Received from server:");
                        System.out.println("Name: " + data.getString("name"));
                        System.out.println("Asset Platform ID: " + data.getString("asset_platform_id"));

                        // Handle nested floor_price object
                        JSONObject floorPriceObj = data.optJSONObject("floor_price");
                        if (floorPriceObj != null) {
                            System.out.println("Price in USD: " + floorPriceObj.optDouble("usd", 0.0));
                        } else {
                            System.out.println("Price: None");
                        }

                        System.out.println();
                    } else {
                        System.out.println(response);
                    }
                } else {
                    System.out.println("Server closed the connection.");
                    break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
