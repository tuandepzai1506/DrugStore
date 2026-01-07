@echo off
cd /d "f:\New folder"

set JAVA_HOME=C:\Program Files\Java\jdk-23
set CLASSPATH=target\classes

REM Download all dependencies first
call mvn dependency:resolve

REM Find all jar files in repository and add to classpath
for /r "%USERPROFILE%\.m2\repository\org\openjfx" %%G in (*.jar) do (
    set "CLASSPATH=!CLASSPATH!;%%G"
)

"%JAVA_HOME%\bin\java.exe" -cp "%CLASSPATH%" --add-modules javafx.controls,javafx.fxml com.quanlykho.App
pause
