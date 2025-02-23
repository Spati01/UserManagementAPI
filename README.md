Project Structure
"My project follows a clean architecture, ensuring separation of concerns. The main packages include:

Controller Layer: Handles incoming requests.
Service Layer: Contains business logic.
Repository Layer: Communicates with the database.
DTO Layer: Used for data transfer.
Exception Handling: Provides meaningful error messages.
Now, let's look at the database schema."

Database Schema & Test Data
"I have created two tables:

Users Table – Stores user details like name, mobile number, PAN, manager ID, timestamps, and status.
Managers Table – Stores manager details.
The manager table is prefilled with test data, ensuring only active managers can be assigned.

Now, let's move to the API demonstrations."

API Demonstrations
1. Create User (/create_user)
"I will now send a POST request to create a user.

Here’s the JSON body:

json
Copy
Edit
{
  "full_name": "Alice Johnson",
  "mob_num": "+919876543210",
  "pan_num": "aabcd1234f",
  "manager_id": "550e8400-e29b-41d4-a716-446655440000"
}
Notice that the mobile number includes a country code and the PAN is in lowercase. My API normalizes the mobile number and converts PAN to uppercase before storing it.

Now, I will send the request... (click Send in Postman).

As you can see, the response confirms that the user was created successfully with a unique UUID. Let’s check the database... (show MySQL query result). The user has been stored correctly."

2. Get Users (/get_users)
"Next, I will retrieve users from the database.

If no filters are applied, the API returns all users.
If I provide a mobile number or user ID, it fetches specific user details.
If I filter by manager_id, it lists all users under that manager.
Now, I will test the API by sending a request with a mob_num filter... (click Send in Postman).

As you can see, the API returns the correct user details."

3. Delete User (/delete_user)
"Now, let’s delete a user.

I will send a request using the user_id:

json
Copy
Edit
{
  "user_id": "123e4567-e89b-12d3-a456-426614174000"
}
(Send request)

The API confirms the user has been deleted. Let’s verify by checking the database... (run SQL query). As expected, the user is removed from the database."

4. Update User (/update_user)
"Now, I will update a user's manager.

Here’s the JSON body:

json
Copy
Edit
{
  "user_ids": ["123e4567-e89b-12d3-a456-426614174000"],
  "update_data": {
    "manager_id": "660e8400-e29b-41d4-a716-446655440111"
  }
}
(Send request)

The API successfully updates the manager. If a user already had a manager, the previous record is marked as inactive, and a new record is created with the new manager. Let’s verify this in the database... (show SQL result).

For bulk updates, the API allows modifying only manager_id. If other fields are included, it returns an error. Let’s test this scenario now... (send incorrect request and show error message)."

Error Handling
"My API includes proper error handling.

For example:

If a required field is missing, it returns a 400 Bad Request.
If an invalid mobile number or PAN is entered, it provides a validation error.
If a manager ID is incorrect, it returns a meaningful error.
Let me show an example by sending an invalid request... (demonstrate error response in Postman)."

Conclusion
"That concludes my demonstration of the Spring Boot User Management API.

I have implemented:
✅ Structured and maintainable code
✅ Proper validation and error handling
✅ Meaningful responses and logging
✅ A clean database schema

The source code is available on GitHub, and this video serves as a guide to using the API.
