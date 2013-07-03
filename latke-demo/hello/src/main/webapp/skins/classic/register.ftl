<!DOCTYPE html>
<html>
    <body>
        <form action="/register" method="POST">
            Your Name: <input name="name" type="text"/><input type="submit" value="Submit"/>
        </form>
        
        <#if name??>
        Hello, ${name}!
        </#if>
    </body>
</html>
