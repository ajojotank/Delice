<?php
$username = "s2670867";
$password = "s2670867";
$database = "d2670867";
$link = mysqli_connect("127.0.0.1", $username, $password, $database);

if (!$link) {
    die("Connection failed: " . mysqli_connect_error());
}

$query = "SELECT * FROM Ingredients";
$result = mysqli_query($link, $query);
$output = array();

while ($row = mysqli_fetch_assoc($result)) {
    $output[] = $row;
}

echo json_encode($output);

mysqli_close($link);
?>
