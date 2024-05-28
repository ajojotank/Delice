<?php
$username = "s2670867";
$password = "s2670867";
$database = "d2670867";
$link = mysqli_connect("127.0.0.1", $username, $password, $atabase);

if (!$link) {
    die("Connection failed: " . mysqli_connect_error());
}

$friend_id = intval($_GET['id']);

$query = "DELETE FROM Friends WHERE friend_id = $friend_id";
if (mysqli_query($link, $query)) {
    echo json_encode(["success" => true, "message" => "Friend removed successfully."]);
} else {
    echo json_encode(["success" => false, "message" => "Error: " . mysqli_error($link)]);
}

mysqli_close($link);
?>
