import requests
import allure
import pytest

BASE_URL = "https://petstore.swagger.io/v2"

@allure.epic("PetStore API")
@allure.feature("Pet Management")
class TestPet:
    
    @allure.title("Add new pet to store")
    def test_add_pet(self):
        pet_data = {
            "id": 12345,
            "category": {"id": 1, "name": "Dogs"},
            "name": "Buddy",
            "photoUrls": ["http://example.com/photo.jpg"],
            "tags": [{"id": 1, "name": "friendly"}],
            "status": "available"
        }
        response = requests.post(f"{BASE_URL}/pet", json=pet_data)
        assert response.status_code == 200
        assert response.json()["name"] == "Buddy"
    
    @allure.title("Update existing pet")  
    def test_update_pet(self):
        pet_data = {
            "id": 12345,
            "name": "Buddy Updated",
            "status": "sold"
        }
        response = requests.put(f"{BASE_URL}/pet", json=pet_data)
        assert response.status_code == 200
    
    @allure.title("Find pet by ID")
    def test_find_pet_by_id(self):
        response = requests.get(f"{BASE_URL}/pet/12345")
        assert response.status_code in [200, 404]  # 404 если удален
    
    @allure.title("Find pets by status")
    def test_find_pets_by_status(self):
        for status in ["available", "pending", "sold"]:
            response = requests.get(f"{BASE_URL}/pet/findByStatus?status={status}")
            assert response.status_code == 200
    
    @allure.title("Delete pet")
    def test_delete_pet(self):
        response = requests.delete(f"{BASE_URL}/pet/12345")
        assert response.status_code == 200

@allure.feature("Store Management")
class TestStore:
    
    @allure.title("Place order for pet")
    def test_place_order(self):
        order_data = {
            "id": 1,
            "petId": 12345,
            "quantity": 1,
            "shipDate": "2023-12-20T10:00:00.000Z",
            "status": "placed",
            "complete": True
        }
        response = requests.post(f"{BASE_URL}/store/order", json=order_data)
        assert response.status_code == 200
    
    @allure.title("Find order by ID")
    def test_find_order(self):
        response = requests.get(f"{BASE_URL}/store/order/1")
        assert response.status_code in [200, 404]
    
    @allure.title("Get store inventory")
    def test_get_inventory(self):
        response = requests.get(f"{BASE_URL}/store/inventory")
        assert response.status_code == 200
    
    @allure.title("Delete order")
    def test_delete_order(self):
        response = requests.delete(f"{BASE_URL}/store/order/1")
        assert response.status_code == 200

@allure.feature("User Management") 
class TestUser:
    
    @allure.title("Create user")
    def test_create_user(self):
        user_data = {
            "id": 1,
            "username": "testuser",
            "firstName": "Test",
            "lastName": "User",
            "email": "test@example.com",
            "password": "password123",
            "phone": "1234567890",
            "userStatus": 1
        }
        response = requests.post(f"{BASE_URL}/user", json=user_data)
        assert response.status_code == 200
    
    @allure.title("Get user by username")
    def test_get_user(self):
        response = requests.get(f"{BASE_URL}/user/testuser")
        assert response.status_code in [200, 404]
    
    @allure.title("Update user")
    def test_update_user(self):
        user_data = {
            "id": 1,
            "username": "testuser",
            "firstName": "Updated",
            "lastName": "User",
            "email": "updated@example.com",
            "password": "newpassword",
            "phone": "1234567890",
            "userStatus": 1
        }
        response = requests.put(f"{BASE_URL}/user/testuser", json=user_data)
        assert response.status_code == 200
    
    @allure.title("Delete user")
    def test_delete_user(self):
        response = requests.delete(f"{BASE_URL}/user/testuser")
        assert response.status_code == 200
    
    @allure.title("User login")
    def test_user_login(self):
        response = requests.get(f"{BASE_URL}/user/login?username=testuser&password=password123")
        assert response.status_code in [200, 400]
    
    @allure.title("User logout") 
    def test_user_logout(self):
        response = requests.get(f"{BASE_URL}/user/logout")
        assert response.status_code == 200

# ИТОГО: 15+ тестов
