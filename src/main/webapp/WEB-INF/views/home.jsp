<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Home</title>
    </head>
    <body>
        <h1>Hello World!</h1>
        <p>This is the homepage!</p>
        <form action="/top-comet/test" method="post">
        	id:<input id="id" name="id" type="text"/><br>
        	name:<input id="name" name="name" type="text"/><br>
        	age:<input id="age" name="age" type="text"/><br>
        	sex:<input id="sex" name="sex" type="text"/><br>
        	<input id="submit" name="提交" type="submit"/><br>
        </form>
    </body>
</html>
