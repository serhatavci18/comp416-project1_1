import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
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
                try {
                    System.out.println("Received from client: " + clientMessage);

                    JSONObject request = new JSONObject(clientMessage);
                    String requestType = request.getString("requestType");

                    if ("NFTList".equals(requestType)) {
                        JSONArray nftList = fetchNFTListData();
                        JSONObject response = new JSONObject();
                        response.put("responseType", "NFTListResponse");
                        response.put("data", nftList);
                        out.println(response.toString());
                    } else if ("NFTDetails".equals(requestType)) {
                        String nftId = request.getString("parameters");
                        JSONObject nftDetails = fetchNFTDetailsData(nftId);
                        JSONObject response = new JSONObject();
                        response.put("responseType", "NFTDetailsResponse");
                        response.put("data", nftDetails);
                        out.println(response.toString());
                    } else {
                        // Handle unsupported or invalid requests
                        JSONObject response = new JSONObject();
                        response.put("responseType", "ErrorResponse");
                        response.put("message", "Invalid request");
                        out.println(response.toString());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    sendErrorResponse(out, "Invalid JSON format");
                } catch (IOException e) {
                    e.printStackTrace();
                    sendErrorResponse(out, "Internal server error");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private JSONArray fetchNFTListData() throws IOException, JSONException {
        URL url = new URL("https://api.coingecko.com/api/v3/nfts/list");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;

            while ((inputLine = reader.readLine()) != null) {
                response.append(inputLine);
            }

            reader.close();

            return new JSONArray(response.toString());
        } else {
            throw new IOException("Failed to fetch NFT list. HTTP response code: " + responseCode);
        }
    }
    private JSONObject fetchNFTDetailsData(String nftId) throws IOException, JSONException {
        String apiUrl = "https://api.coingecko.com/api/v3/nfts/" + nftId;

        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;

            while ((inputLine = reader.readLine()) != null) {
                response.append(inputLine);
            }

            reader.close();

            return new JSONObject(response.toString());
        } else {
            throw new IOException("Failed to fetch NFT details. HTTP response code: " + responseCode);
        }
    }

    private void sendErrorResponse(PrintWriter out, String errorMessage) {
        JSONObject response = new JSONObject();
        response.put("responseType", "ErrorResponse");
        response.put("message", errorMessage);
        out.println(response.toString());
    }
}
