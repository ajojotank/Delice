<?php
$username = "s2670867";
$password = "s2670867";
$database = "d2670867";
$host = "127.0.0.1";

// Create connection
$conn = new mysqli($host, $username, $password, $database);

// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

// Fetch all recipes
$sql = "SELECT recipe_id, title, description, author_id, image_path FROM Recipes";
$result = $conn->query($sql);
$output = [];

if ($result->num_rows > 0) {
    // output data of each row
    while ($row = $result->fetch_assoc()) {
        $recipe_id = $row['recipe_id'];
        $author_id = $row['author_id'];

        // Fetch the author's name
        $author_sql = "SELECT name FROM Users WHERE user_id = $author_id";
        $author_result = $conn->query($author_sql);
        if ($author_result->num_rows > 0) {
            $author_row = $author_result->fetch_assoc();
            $author_name = $author_row['name'];
        } else {
            $author_name = "Unknown";
        }

        // Fetch ingredients for the recipe
        $ingredient_sql = "SELECT Ingredients.name 
                           FROM Recipe_Ingredients 
                           JOIN Ingredients ON Recipe_Ingredients.ingredient_id = Ingredients.ingredient_id 
                           WHERE Recipe_Ingredients.recipe_id = $recipe_id";
        $ingredient_result = $conn->query($ingredient_sql);
        $ingredients = [];
        if ($ingredient_result->num_rows > 0) {
            while ($ingredient_row = $ingredient_result->fetch_assoc()) {
                $ingredients[] = $ingredient_row['name'];
            }
        }

        // Fetch instructions for the recipe
        $instruction_sql = "SELECT instruction 
                            FROM Instructions 
                            WHERE recipe_id = $recipe_id 
                            ORDER BY step_number";
        $instruction_result = $conn->query($instruction_sql);
        $instructions = [];
        if ($instruction_result->num_rows > 0) {
            while ($instruction_row = $instruction_result->fetch_assoc()) {
                $instructions[] = $instruction_row['instruction'];
            }
        }

        // Add ingredients and instructions to the recipe
        $row['author_name'] = $author_name;
        unset($row['author_id']); // Remove author_id from the output
        $row['ingredients'] = $ingredients;
        $row['instructions'] = $instructions;

        $output[] = $row;
    }
    echo json_encode($output);
} else {
    echo json_encode(["success" => false, "message" => "No recipes found"]);
}

$conn->close();
?>