<html>
<head>
  <title>Config</title>
</head>
<body>
  Blok Sensus
  <form action="http://localhost:2222/vrp/config/census-block/upload" method="post" enctype="multipart/form-data">
      <input type="file" name="file" id="file">
      <input type="submit" value="Upload Image" name="submit">
  </form>

  Enumerator
  <form action="http://localhost:2222/vrp/config/enumerator/upload" method="post" enctype="multipart/form-data">
      <input type="file" name="file" id="file">
      <input type="submit" value="Upload Image" name="submit">
  </form>

  Cost Matrix
  <form action="http://localhost:2222/vrp/config/cost-matrix/upload" method="post" enctype="multipart/form-data">
      <input type="file" name="file" id="file">
      <input type="submit" value="Upload Image" name="submit">
  </form>
</body>
</html>