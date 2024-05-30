<?php
$username = "s2670867";
$password = "s2670867";
$database = "d2670867";
$host = "127.0.0.1";

// Create connection
$conn = new mysqli($host, $username, $password, $database);

if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $recipe_id = $_POST['recipe_id'];
    $user_id = $_POST['user_id'];
    $comment_text = $_POST['comment_text'];
    $rating = $_POST['rating'];

    $stmt = $conn->prepare("INSERT INTO comments (recipe_id, user_id, comment_text, rating) VALUES (?, ?, ?, ?)");
    $stmt->bind_param("isd", $recipe_id, $user_id, $comment_text, $rating);

    if ($stmt->execute()) {
        echo "New comment added successfully";
    } else {
        echo "Error: " . $stmt->error;
    }

    $stmt->close();
}

$conn->close();
?>
