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

$user_id = intval($_GET['user_id']);
$day = mysqli_real_escape_string($conn, $_GET['day']);
$meal_type = mysqli_real_escape_string($conn, $_GET['meal_type']);

// Begin transaction
$conn->begin_transaction();

try {
    // Find the recipe_id associated with the meal to be deleted
    $recipe_id_query = "SELECT recipe_id FROM Meals WHERE user_id = $user_id AND day = '$day' AND meal_type = '$meal_type'";
    $recipe_id_result = $conn->query($recipe_id_query);

    if ($recipe_id_result->num_rows > 0) {
        $recipe_id_row = $recipe_id_result->fetch_assoc();
        $recipe_id = $recipe_id_row['recipe_id'];

        // Delete the meal
        $delete_meal_query = "DELETE FROM Meals WHERE user_id = $user_id AND day = '$day' AND meal_type = '$meal_type'";
        if (!$conn->query($delete_meal_query)) {
            throw new Exception("Error deleting meal: " . $conn->error);
        }

        // Update grocery list: Remove ingredients associated with the deleted meal
        $delete_grocery_query = "DELETE FROM Grocery_List WHERE user_id = $user_id AND ingredient_id IN (
            SELECT ingredient_id FROM Recipe_Ingredients WHERE recipe_id = $recipe_id
        )";
        if (!$conn->query($delete_grocery_query)) {
            throw new Exception("Error updating grocery list: " . $conn->error);
        }

        // Commit transaction
        $conn->commit();

        echo json_encode(["success" => true, "message" => "Meal and grocery list updated successfully."]);
    } else {
        throw new Exception("Meal not found.");
    }
} catch (Exception $e) {
    // Rollback transaction on error
    $conn->rollback();
    echo json_encode(["success" => false, "message" => $e->getMessage()]);
}

$conn->close();
?>
