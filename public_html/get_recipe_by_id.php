<?php
$username = "s2670867";
$password = "s2670867";
$database = "d2670867";
$link = mysqli_connect("127.0.0.1", $username, $password, $database);

if (!$link) {
    die("Connection failed: " . mysqli_connect_error());
}

$recipe_id = intval($_GET['id']);

$query = "SELECT * FROM Recipes WHERE recipe_id = $recipe_id";
$result = mysqli_query($link, $query);

if ($row = mysqli_fetch_assoc($result)) {
    echo json_encode($row);
} else {
    echo json_encode(["success" => false, "message" => "Recipe not found."]);
}

mysqli_close($link);
?>
