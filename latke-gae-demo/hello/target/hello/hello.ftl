<!DOCTYPE html>
<html>
    <body>
        Time: ${time?string("yyyy-MM-dd HH:mm:ss")} <br/>
        <form action="/greeting" method="POST">
            Your Name: <input name="name" type="text"/><input type="submit" value="Submit"/>
        </form>
        
        <#if name??>
        Hello, ${name}!
        </#if>
    </body>
</html>
