<?php
$username = "s2670867";
$password = "s2670867";
$database = "d2670867";
$link = mysqli_connect("127.0.0.1", $username, $password, $database);

if (!$link) {
    die("Connection failed: " . mysqli_connect_error());
}

$title = mysqli_real_escape_string($link, $_POST['title']);
$description = mysqli_real_escape_string($link, $_POST['description']);
$author_id = intval($_POST['author_id']);

// Handle file upload
$target_dir = "uploads/"; // Ensure this directory exists and has appropriate permissions
$file_extension = pathinfo($_FILES["imageFile"]["name"], PATHINFO_EXTENSION);  // Get file extension
$unique_filename = uniqid("img_", true) . '.' . $file_extension;  // Generate a unique ID and append the file extension
$image_file = $target_dir . $unique_filename;
$server_url = "https://lamp.ms.wits.ac.za/home/s2670867/";  // Base URL of your server
$image_path = $server_url . $image_file;  // Full URL path

// Attempt to move the uploaded file to the target directory
if (move_uploaded_file($_FILES["imageFile"]["tmp_name"], $image_file)) {
    // Prepare and bind
    $query = "INSERT INTO Recipes (title, description, author_id, image_path) VALUES ('$title', '$description', $author_id, '$image_path')";
    if (mysqli_query($link, $query)) {
        echo json_encode(["success" => true, "message" => "Recipe added successfully.", "imagePath" => $image_path]);
    } else {
        echo json_encode(["success" => false, "message" => "Error in database insertion: " . mysqli_error($link)]);
    }
} else {
    echo json_encode(["success" => false, "message" => "Error uploading file."]);
}

mysqli_close($link);
?>
