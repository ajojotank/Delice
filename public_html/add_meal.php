<?php
$username = "s2670867";
$password = "s2670867";
$database = "d2670867";
$link = mysqli_connect("127.0.0.1", $username, $password, $database);

if (!$link) {
    die("Connection failed: " . mysqli_connect_error());
}

$user_id = intval($_POST['user_id']);
$recipe_id = intval($_POST['recipe_id']);
$day = mysqli_real_escape_string($link, $_POST['day']);
$meal_type = mysqli_real_escape_string($link, $_POST['meal_type']);

// Begin transaction
mysqli_begin_transaction($link);

try {
    // Insert the meal into the Meals table
    $query = "INSERT INTO Meals (user_id, recipe_id, day, meal_type) VALUES ($user_id, $recipe_id, '$day', '$meal_type')";
    if (!mysqli_query($link, $query)) {
        throw new Exception("Error inserting meal: " . mysqli_error($link));
    }

    // Fetch ingredients for the recipe
    $ingredient_query = "SELECT ingredient_id FROM Recipe_Ingredients WHERE recipe_id = $recipe_id";
    $ingredient_result = mysqli_query($link, $ingredient_query);

    if (!$ingredient_result) {
        throw new Exception("Error fetching ingredients: " . mysqli_error($link));
    }

    // Insert ingredients into the Grocery_List table
    while ($ingredient_row = mysqli_fetch_assoc($ingredient_result)) {
        $ingredient_id = $ingredient_row['ingredient_id'];
        $grocery_query = "INSERT INTO Grocery_List (user_id, ingredient_id) VALUES ($user_id, $ingredient_id)
                          ON DUPLICATE KEY UPDATE ingredient_id=ingredient_id"; // Prevent duplicate entries
        if (!mysqli_query($link, $grocery_query)) {
            throw new Exception("Error inserting into grocery list: " . mysqli_error($link));
        }
    }

    // Commit transaction
    mysqli_commit($link);

    echo json_encode(["success" => true, "message" => "Meal added to planner and grocery list successfully."]);
} catch (Exception $e) {
    // Rollback transaction on error
    mysqli_rollback($link);
    echo json_encode(["success" => false, "message" => $e->getMessage()]);
}

mysqli_close($link);
?>
