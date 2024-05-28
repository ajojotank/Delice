<?php
$username = "s2670867";
$password = "s2670867";
$database = "d2670867";
$link = mysqli_connect("127.0.0.1", $username, $password, $database);

if (!$link) {
    die("Connection failed: " . mysqli_connect_error());
}

$check_username = mysqli_real_escape_string($link, $_POST['username']);

$query = "SELECT * FROM Users WHERE username='$check_username'";
$result = mysqli_query($link, $query);

if (mysqli_num_rows($result) > 0) {
    echo json_encode(["success" => true, "message" => "Username already exists."]);
} else {
    echo json_encode(["success" => false, "message" => "Username is available."]);
}

mysqli_close($link);
?>