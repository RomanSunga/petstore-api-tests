import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import org.json.JSONObject;
import org.json.JSONArray;



public class PetStoreTest {
    
    private static final String BASE_URL = "https://petstore.swagger.io/v2";
    private static int passedTests = 0;
    private static int failedTests = 0;
    
    public static void main(String[] args) {
        System.out.println("=== PETSTORE API TESTS ===\n");
        
        // Test 1: Get store inventory
        runTest("Get store inventory", () -> testGetInventory());
        
        // Test 2: Create new pet
        runTest("Create new pet", () -> testCreatePet());
        
        // Test 3: Get pet by ID (может упасть)
        runTest("Get pet by ID", () -> testGetPet("12345")); // Пробуем получить несуществующего
        
        // Test 4: Find pets by status
        runTest("Find available pets", () -> testFindByStatus("available"));
        
        // Test 5: Find pending pets  
        runTest("Find pending pets", () -> testFindByStatus("pending"));
        
        // Test 6: Find sold pets
        runTest("Find sold pets", () -> testFindByStatus("sold"));
        
        // Test 7: Create order (может упасть)
        runTest("Create order", () -> testCreateOrder("12345"));
        
        // Test 8: Get order by ID (может упасть)  
        runTest("Get order by ID", () -> testGetOrder("1"));
        
        // Test 9: Delete order (может упасть)
        runTest("Delete order", () -> testDeleteOrder("1"));
        
        // Test 10: Create user
        runTest("Create user", () -> testCreateUser());
        
        // Test 11: Get user (может упасть)
        runTest("Get user", () -> testGetUser("testuser123"));
        
        // Test 12: Update user (может упасть)
        runTest("Update user", () -> testUpdateUser("testuser123"));
        
        // Test 13: Login user (может упасть)
        runTest("Login user", () -> testLoginUser("testuser123"));
        
        // Test 14: Logout user
        runTest("Logout user", () -> testLogoutUser());
        
        // Test 15: Delete user (может упасть)
        runTest("Delete user", () -> testDeleteUser("testuser123"));
        
        // Test 16: Delete pet (может упасть)
        runTest("Delete pet", () -> testDeletePet("12345"));
        
        System.out.println("\n=== TEST SUMMARY ===");
        System.out.println("PASSED: " + passedTests);
        System.out.println("FAILED: " + failedTests);
        System.out.println("TOTAL: " + (passedTests + failedTests));
        
        if (failedTests == 0) {
            System.out.println("\n🎉 ALL TESTS PASSED!");
        } else {
            System.out.println("\n⚠️  Some tests failed, but all methods were tested!");
        }
    }
    
    // Метод для запуска тестов с обработкой ошибок
    public static void runTest(String testName, Runnable test) {
        System.out.println("\nTEST: " + testName);
        try {
            test.run();
            System.out.println("✅ PASSED");
            passedTests++;
        } catch (Exception e) {
            System.out.println("❌ FAILED: " + e.getMessage());
            failedTests++;
        }
    }
    
    // ==================== STORE TESTS ====================
    
    public static void testGetInventory() throws Exception {
        String response = sendGet("/store/inventory");
        JSONObject inventory = new JSONObject(response);
        System.out.println("   Inventory received - Available: " + inventory.optInt("available", 0));
    }
    
    public static void testCreateOrder(String petId) throws Exception {
        JSONObject order = new JSONObject();
        order.put("id", 1);
        order.put("petId", Long.parseLong(petId));
        order.put("quantity", 1);
        order.put("shipDate", "2023-12-15T10:00:00.000Z");
        order.put("status", "placed");
        order.put("complete", true);
        
        String response = sendPost("/store/order", order.toString());
        JSONObject orderResponse = new JSONObject(response);
        System.out.println("   Order created - ID: " + orderResponse.get("id"));
    }
    
    public static void testGetOrder(String orderId) throws Exception {
        String response = sendGet("/store/order/" + orderId);
        JSONObject order = new JSONObject(response);
        System.out.println("   Order received - ID: " + order.get("id"));
    }
    
    public static void testDeleteOrder(String orderId) throws Exception {
        String response = sendDelete("/store/order/" + orderId);
        JSONObject result = new JSONObject(response);
        System.out.println("   Order deleted - Code: " + result.get("code"));
    }
    
    // ==================== PET TESTS ====================
    
    public static void testCreatePet() throws Exception {
        JSONObject pet = new JSONObject();
        long petId = System.currentTimeMillis();
        pet.put("id", petId);
        pet.put("name", "test-doggie");
        pet.put("status", "available");
        
        JSONObject category = new JSONObject();
        category.put("id", 1);
        category.put("name", "Dogs");
        pet.put("category", category);
        
        JSONArray photoUrls = new JSONArray();
        photoUrls.put("http://test.com/photo1.jpg");
        pet.put("photoUrls", photoUrls);
        
        String response = sendPost("/pet", pet.toString());
        JSONObject petResponse = new JSONObject(response);
        System.out.println("   Pet created - ID: " + petResponse.get("id"));
    }
    
    public static void testGetPet(String petId) throws Exception {
        String response = sendGet("/pet/" + petId);
        JSONObject pet = new JSONObject(response);
        System.out.println("   Pet received - ID: " + pet.get("id"));
    }
    
    public static void testFindByStatus(String status) throws Exception {
        String response = sendGet("/pet/findByStatus?status=" + status);
        JSONArray pets = new JSONArray(response);
        System.out.println("   Found " + pets.length() + " pets with status: " + status);
    }
    
    public static void testDeletePet(String petId) throws Exception {
        String response = sendDelete("/pet/" + petId);
        JSONObject result = new JSONObject(response);
        System.out.println("   Pet deleted - Code: " + result.get("code"));
    }
    
    // ==================== USER TESTS ====================
    
    public static void testCreateUser() throws Exception {
        JSONObject user = new JSONObject();
        String username = "testuser" + System.currentTimeMillis();
        user.put("username", username);
        user.put("firstName", "Test");
        user.put("lastName", "User");
        user.put("email", "test@example.com");
        user.put("password", "password123");
        user.put("phone", "1234567890");
        user.put("userStatus", 1);
        
        String response = sendPost("/user", user.toString());
        JSONObject result = new JSONObject(response);
        System.out.println("   User created - Username: " + username);
    }
    
    public static void testGetUser(String username) throws Exception {
        String response = sendGet("/user/" + username);
        JSONObject user = new JSONObject(response);
        System.out.println("   User received - Username: " + user.get("username"));
    }
    
    public static void testUpdateUser(String username) throws Exception {
        JSONObject user = new JSONObject();
        user.put("username", username);
        user.put("firstName", "Updated");
        user.put("lastName", "User");
        user.put("email", "updated@example.com");
        user.put("password", "newpassword123");
        user.put("phone", "0987654321");
        user.put("userStatus", 1);
        
        String response = sendPut("/user/" + username, user.toString());
        JSONObject result = new JSONObject(response);
        System.out.println("   User updated - Username: " + username);
    }
    
    public static void testLoginUser(String username) throws Exception {
        String response = sendGet("/user/login?username=" + username + "&password=password123");
        JSONObject result = new JSONObject(response);
        System.out.println("   Login successful - Message: " + result.get("message"));
    }
    
    public static void testLogoutUser() throws Exception {
        String response = sendGet("/user/logout");
        JSONObject result = new JSONObject(response);
        System.out.println("   Logout successful - Message: " + result.get("message"));
    }
    
    public static void testDeleteUser(String username) throws Exception {
        String response = sendDelete("/user/" + username);
        JSONObject result = new JSONObject(response);
        System.out.println("   User deleted - Code: " + result.get("code"));
    }
    
    // ==================== HTTP METHODS ====================
    
    public static String sendGet(String endpoint) throws Exception {
        URL url = new URL(BASE_URL + endpoint);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);
        
        int status = conn.getResponseCode();
        BufferedReader br;
        if (status >= 200 && status < 300) {
            br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }
        
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            response.append(line);
        }
        br.close();
        
        if (status != 200) {
            throw new RuntimeException("HTTP " + status + ": " + response.toString());
        }
        
        return response.toString();
    }
    
    public static String sendPost(String endpoint, String body) throws Exception {
        URL url = new URL(BASE_URL + endpoint);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "application/json");
        conn.setDoOutput(true);
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);
        
        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = body.getBytes("utf-8");
            os.write(input, 0, input.length);
        }
        
        int status = conn.getResponseCode();
        BufferedReader br;
        if (status >= 200 && status < 300) {
            br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }
        
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            response.append(line);
        }
        br.close();
        
        if (status != 200) {
            throw new RuntimeException("HTTP " + status + ": " + response.toString());
        }
        
        return response.toString();
    }
    
    public static String sendPut(String endpoint, String body) throws Exception {
        URL url = new URL(BASE_URL + endpoint);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("PUT");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "application/json");
        conn.setDoOutput(true);
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);
        
        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = body.getBytes("utf-8");
            os.write(input, 0, input.length);
        }
        
        int status = conn.getResponseCode();
        BufferedReader br;
        if (status >= 200 && status < 300) {
            br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }
        
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            response.append(line);
        }
        br.close();
        
        if (status != 200) {
            throw new RuntimeException("HTTP " + status + ": " + response.toString());
        }
        
        return response.toString();
    }
    
    public static String sendDelete(String endpoint) throws Exception {
        URL url = new URL(BASE_URL + endpoint);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("DELETE");
        conn.setRequestProperty("Accept", "application/json");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);
        
        int status = conn.getResponseCode();
        BufferedReader br;
        if (status >= 200 && status < 300) {
            br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }
        
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            response.append(line);
        }
        br.close();
        
        if (status != 200) {
            throw new RuntimeException("HTTP " + status + ": " + response.toString());
        }
        
        return response.toString();
    }
}
