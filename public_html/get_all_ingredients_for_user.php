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
    SELECT DISTINCT Ingredients.name 
    FROM Grocery_List 
    JOIN Ingredients ON Grocery_List.ingredient_id = Ingredients.ingredient_id 
    WHERE Grocery_List.user_id = $user_id
";
$result = mysqli_query($link, $query);

if (!$result) {
    echo json_encode(["success" => false, "message" => "Error: " . mysqli_error($link)]);
    mysqli_close($link);
    exit();
}

$ingredients = [];
while ($row = mysqli_fetch_assoc($result)) {
    $ingredients[] = $row['name'];
}

echo json_encode(["success" => true, "ingredients" => $ingredients]);

mysqli_close($link);
?>
