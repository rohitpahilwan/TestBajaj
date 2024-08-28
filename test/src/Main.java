import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class Main {

    public static void main(String[] args) {
        String prnNumber = args[0].toLowerCase().replace(" ", "");
        String filePath = args[1];

        try {
            InputStream is = new FileInputStream(filePath);
            JSONObject jsonObject = new JSONObject(new JSONTokener(is));
            String destinationValue = findDestinationValue(jsonObject);

            if (destinationValue == null) {
                System.out.println("No destination key found in the JSON file.");
                return;
            }

            String randomString = generateRandomString(8);
            String concatenatedValue = prnNumber + destinationValue + randomString;
            String hash = generateMD5Hash(concatenatedValue);

            System.out.println(hash + ";" + randomString);
        } catch (FileNotFoundException e) {
            System.out.println("JSON file not found at the given path.");
        }
    }

    private static String findDestinationValue(JSONObject jsonObject) {
        for (String key : jsonObject.keySet()) {
            Object value = jsonObject.get(key);
            if (key.equals("destination")) {
                return value.toString();
            } else if (value instanceof JSONObject) {
                String result = findDestinationValue((JSONObject) value);
                if (result != null) return result;
            } else if (value instanceof JSONArray) {
                JSONArray jsonArray = (JSONArray) value;
                for (int i = 0; i < jsonArray.length(); i++) {
                    if (jsonArray.get(i) instanceof JSONObject) {
                        String result = findDestinationValue(jsonArray.getJSONObject(i));
                        if (result != null) return result;
                    }
                }
            }
        }
        return null;
    }

    private static String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }
        return sb.toString();
    }

    private static String generateMD5Hash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashInBytes = md.digest(input.getBytes());

            StringBuilder sb = new StringBuilder();
            for (byte b : hashInBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}

