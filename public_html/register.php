<?php
$username = "s2670867";
$password = "s2670867";
$database = "d2670867";
$link = mysqli_connect("127.0.0.1", $username, $password, $database);

if (!$link) {
    die("Connection failed: " . mysqli_connect_error());
}

$username = mysqli_real_escape_string($link, $_POST['username']);
$name = mysqli_real_escape_string($link, $_POST['name']);
$password_hash = mysqli_real_escape_string($link, $_POST['password']); // Consider hashing on the client side

$query = "INSERT INTO Users (username, name, password_hash) VALUES ('$username', '$name', '$password_hash')";
if (mysqli_query($link, $query)) {
    echo json_encode(["success" => true, "message" => "User registered successfully."]);
} else {
    echo json_encode(["success" => false, "message" => "Error: " . mysqli_error($link)]);
}

mysqli_close($link);
?>
