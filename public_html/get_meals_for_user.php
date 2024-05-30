<?php
// Enable error reporting
ini_set('display_errors', 1);
ini_set('display_startup_errors', 1);
error_reporting(E_ALL);

$username = "s2670867";
$password = "s2670867";
$database = "d2670867";
$link = mysqli_connect("127.0.0.1", $username, $password, $database);

if (!$link) {
    die("Connection failed: " . mysqli_connect_error());
}
nan
$user_id = intval($_GET['user_id']);
$day = mysqli_real_escape_string($link, $_GET['day']);

// Initialize output array with null values
$output = [
    "breakfast" => null,
    "lunch" => null,
    "dinner" => null
];

$query = "
    SELECT 
        Meals.meal_type, 
        Recipes.recipe_id,
        Recipes.title, 
        Recipes.description, 
        Recipes.author_id, 
        Recipes.image_path, 
        Users.name AS author_name,
        IF(Favorites.user_id IS NOT NULL, true, false) AS is_favorite
    FROM Meals
    JOIN Recipes ON Meals.recipe_id = Recipes.recipe_id
    JOIN Users ON Recipes.author_id = Users.user_id
    LEFT JOIN Favorites ON Meals.recipe_id = Favorites.recipe_id AND Favorites.user_id = $user_id
    WHERE Meals.user_id = $user_id AND Meals.day = '$day'
";
$result = mysqli_query($link, $query);

if (!$result) {
    die("Error in query: " . mysqli_error($link));
}

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

    if (!$ingredient_result) {
        die("Error in ingredient query: " . mysqli_error($link));
    }

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

    if (!$instruction_result) {
        die("Error in instruction query: " . mysqli_error($link));
    }

    $instructions = [];
    while ($instruction_row = mysqli_fetch_assoc($instruction_result)) {
        $instructions[] = $instruction_row['instruction'];
    }

    // Create recipe array
    $recipe = [
        "title" => $row['title'],
        "description" => $row['description'],
        "author" => $row['author_name'],
        "image_url" => $row['image_path'],
        "ingredients" => $ingredients,
        "instructions" => $instructions,
        "is_favorite" => $row['is_favorite']
    ];
    
    if ($row['meal_type'] === 'Breakfast') {
        $output['breakfast'] = $recipe;
    } elseif ($row['meal_type'] === 'Lunch') {
        $output['lunch'] = $recipe;
    } elseif ($row['meal_type'] === 'Dinner') {
        $output['dinner'] = $recipe;
    }
}

echo json_encode($output);

mysqli_close($link);
?>
