<?php
$username = "s2670867";
$password = "s2670867";
$database = "d2670867";
$link = mysqli_connect("127.0.0.1", $username, $password, $database);

if (!$link) {
    die("Connection failed: " . mysqli_connect_error());
}

$user_id = intval($_GET['user_id']);

$query = "SELECT friend_id FROM Friends WHERE user_id = $user_id";
$result = mysqli_query($link, $query);
$output = array();

while ($row = mysqli_fetch_assoc($result)) {
    $output[] = $row;
}

echo json_encode($output);

mysqli_close($link);
?>
