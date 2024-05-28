# Délice

Délice is a digital recipe book and meal planner app that helps users plan their meals, discover new recipes, and share their culinary experiences with friends. The app is designed with a rustic minimal theme using Material Design 3 and Google fonts. It is built in Android Studio using Java and XML.

## Features

- Onboarding
- Sign In / Sign Up
- Homepage with weekly planner
- Cookbook
- Grocery list integration
- Search
- User profile view
- Recipe creation
- Account management
- Detailed recipe view
- Social sharing
- Recommendation system

## Endpoints

### User Management

- **Sign Up**: `POST /signup`
  - Request: `{ "name": "string", "password": "string" }`
  - Response: `{ "status": "success", "user_id": "int" }`

- **Sign In**: `POST /signin`
  - Request: `{ "name": "string", "password": "string" }`
  - Response: `{ "status": "success", "user_id": "int" }`

- **Get User**: `GET /user/{user_id}`
  - Response: `{ "user_id": "int", "name": "string" }`

### Recipe Handling

- **Create Recipe**: `POST /recipes`
  - Request: `{ "user_id": "int", "title": "string", "ingredients": ["string"], "instructions": "string", "image_path": "string" }`
  - Response: `{ "status": "success", "recipe_id": "int" }`

- **Get Recipe**: `GET /recipes/{recipe_id}`
  - Response: `{ "recipe_id": "int", "title": "string", "ingredients": ["string"], "instructions": "string", "image_path": "string" }`

- **Delete Recipe**: `DELETE /recipes/{recipe_id}`
  - Response: `{ "status": "success" }`

### Ingredient Retrieval

- **Get Ingredients**: `GET /ingredients`
  - Response: `{ "ingredients": ["string"] }`

### Meal Planning

- **Get Meals**: `GET /meals/{user_id}`
  - Response: `{ "meals": [{ "day": "enum", "recipe_id": "int" }] }`

- **Add Meal**: `POST /meals`
  - Request: `{ "user_id": "int", "day": "enum", "recipe_id": "int" }`
  - Response: `{ "status": "success" }`

- **Delete Meal**: `DELETE /meals/{user_id}/{day}`
  - Response: `{ "status": "success" }`

### Friends Management

- **Add Friend**: `POST /friends`
  - Request: `{ "user_id": "int", "friend_id": "int" }`
  - Response: `{ "status": "success" }`

- **Get Friends**: `GET /friends/{user_id}`
  - Response: `{ "friends": [{ "friend_id": "int", "name": "string" }] }`

### Favorites Management

- **Add Favorite**: `POST /favorites`
  - Request: `{ "user_id": "int", "recipe_id": "int" }`
  - Response: `{ "status": "success" }`

- **Get Favorites**: `GET /favorites/{user_id}`
  - Response: `{ "favorites": [{ "recipe_id": "int", "title": "string" }] }`

### Search Functionality (To-Do)

- **Search Recipes**: `GET /search?query=string`
  - Response: `{ "results": [{ "recipe_id": "int", "title": "string" }] }`

## To-Do

- Implement search integration
- Enhance favorites management
- Improve friends feature

## Setup

1. Clone the repository:
   ```bash
   git clone https://github.com/your-username/delice.git

Navigate to the project directory:

bash
Copy code
cd delice
Initialize a git repository and add the remote:

bash
Copy code
git init
git remote add origin https://github.com/your-username/delice.git
Add files and commit:

bash
Copy code
git add .
git commit -m "Initial commit"
Push to GitHub:

bash
Copy code
git push -u origin main
Contribution
Feel free to fork this repository and submit pull requests. For major changes, please open an issue first to discuss what you would like to change.

License
This project is licensed under the MIT License.