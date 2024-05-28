<?php
$username = "s2670867";
$password = "s2670867";
$database = "d2670867";
$link = mysqli_connect("127.0.0.1", $username, $password, $database);

if (!$link) {
    die("Connection failed: " . mysqli_connect_error());
}

$favorite_id = intval($_GET['id']);

$query = "DELETE FROM Favorites WHERE recipe_id = $favorite_id";
if (mysqli_query($link, $query)) {
    echo json_encode(["success" => true, "message" => "Favorite recipe removed successfully."]);
} else {
    echo json_encode(["success" => false, "message" => "Error: " . mysqli_error($link)]);
}

mysqli_close($link);
?>
