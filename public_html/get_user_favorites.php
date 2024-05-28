<?php
$username = "s2670867";
$password = "s2670867";
$database = "d2670867";
$link = mysqli_connect("127.0.0.1", $username, $password, $database);

if (!$link) {
    die("Connection failed: " . mysqli_connect_error());
}

$user_id = intval($_GET['user_id']);

$query = "
    SELECT 
        Recipes.recipe_id,
        Recipes.title, 
        Recipes.description, 
        Recipes.author_id, 
        Recipes.image_path, 
        Users.name AS author_name
    FROM Favorites
    JOIN Recipes ON Favorites.recipe_id = Recipes.recipe_id
    JOIN Users ON Recipes.author_id = Users.user_id
    WHERE Favorites.user_id = $user_id
";
$result = mysqli_query($link, $query);
$output = array();

while ($row = mysqli_fetch_assoc($result)) {
    $recipe_id = $row['recipe_id'];

    // Fetch ingredients
    $ingredient_query = "
        SELECT Ingredients.name 
        FROM Recipe_Ingredients 
        JOIN Ingredients ON Recipe_Ingredients.ingredient_id = Ingredients.ingredient_id 
        WHERE Recipe_Ingredients.recipe_id = $recipe_id
    ";
    $ingredient_result = mysqli_query($link, $ingredient_query);
    $ingredients = [];
    while ($ingredient_row = mysqli_fetch_assoc($ingredient_result)) {
        $ingredients[] = $ingredient_row['name'];
    }

    // Fetch instructions
    $instruction_query = "
        SELECT instruction 
        FROM Instructions 
        WHERE recipe_id = $recipe_id
        ORDER BY step_number
    ";
    $instruction_result = mysqli_query($link, $instruction_query);
    $instructions = [];
    while ($instruction_row = mysqli_fetch_assoc($instruction_result)) {
        $instructions[] = $instruction_row['instruction'];
    }

    // Create recipe array
    $recipe = [
        "recipe_id" => $row['recipe_id'],
        "title" => $row['title'],
        "description" => $row['description'],
        "author" => $row['author_name'],
        "image_url" => $row['image_path'],
        "ingredients" => $ingredients,
        "instructions" => $instructions,
        "is_favorite" => true // Since this query is for favorites, it's always true
    ];
    
    $output[] = $recipe;
}

echo json_encode($output);

mysqli_close($link);
?>
