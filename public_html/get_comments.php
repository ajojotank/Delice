<?php

$username = "s2670867";
$password = "s2670867";
$database = "d2670867";
$host = "127.0.0.1";

$dsn = "mysql:host=$host;dbname=$database;charset=utf8mb4";
$options = [
    PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION,
    PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
    PDO::ATTR_EMULATE_PREPARES => false,
];

try {
    $pdo = new PDO($dsn, $username, $password, $options);
} catch (PDOException $e) {
    die("Connection failed: " . $e->getMessage());
}

$recipe_id = isset($_GET['recipe_id']) ? intval($_GET['recipe_id']) : null;

if ($recipe_id !== null) {
    try {
        $stmt = $pdo->prepare("SELECT comment_id, recipe_id, Users.user_id, comment, rating, username
                               FROM Comments
                               INNER JOIN Users ON Users.user_id = Comments.user_id
                               WHERE recipe_id = :recipe_id");
        $stmt->execute(['recipe_id' => $recipe_id]);
        $result = $stmt->fetchAll();

        if ($result) {
            $output = [];
            foreach ($result as $row) {
                $obj = [
                    'commenter_username' => $row['username'],
                    'comment' => $row['comment'],
                    'rating' => $row['rating'],
                ];
                $output[] = $obj;
            }
            echo json_encode($output);
        } else {
            echo json_encode(["success" => false, "message" => "No comments"]);
        }
    } catch (PDOException $e) {
        echo json_encode(["success" => false, "message" => "Query failed: " . $e->getMessage()]);
    }
} else {
    echo json_encode(["success" => false, "message" => "Invalid recipe ID"]);
}

$pdo = null;
?>
