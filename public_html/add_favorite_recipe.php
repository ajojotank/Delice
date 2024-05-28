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

$query = "INSERT INTO Favorites (user_id, recipe_id) VALUES ($user_id, $recipe_id)";
if (mysqli_query($link, $query)) {
    echo json_encode(["success" => true, "message" => "Recipe added to favorites successfully."]);
} else {
    echo json_encode(["success" => false, "message" => "Error: " . mysqli_error($link)]);
}

mysqli_close($link);
?>
