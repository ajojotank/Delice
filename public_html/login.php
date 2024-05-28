<?php
$username = "s2670867";
$password = "s2670867";
$database = "d2670867";
$link = mysqli_connect("127.0.0.1", $username, $password, $database);

if (!$link) {
    die("Connection failed: " . mysqli_connect_error());
}

$username = mysqli_real_escape_string($link, $_POST['username']);
$password_hash = mysqli_real_escape_string($link, $_POST['password']); // Consider hashing on the client side

$query = "SELECT user_id FROM Users WHERE username='$username' AND password_hash='$password_hash'";
$result = mysqli_query($link, $query);

if ($row = mysqli_fetch_assoc($result)) {
    echo json_encode(["success" => true, "message" => "Login successful.", "user_id" => $row['user_id']]);
} else {
    echo json_encode(["success" => false, "message" => "Invalid username or password."]);
}

mysqli_close($link);
?>
