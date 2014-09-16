<?php

$username = "root";
$password = "";
$host = "localhost";
$database = "test";

@mysql_connect($host, $username, $password) or die("Can not connect to database: ".mysql_error());

@mysql_select_db($database) or die("Can not select the database: ".mysql_error());


$query = mysql_query("SELECT * FROM store");
$row = mysql_fetch_array($query);
$content = $row[1];
echo $content;
//header('Content-type: image/jpg');
//echo $content;



?>
