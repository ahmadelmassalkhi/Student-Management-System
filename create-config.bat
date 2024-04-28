@echo off
setlocal

@REM Specify the base directory for the Student Management System
set "APP_DIR=C:\Users\%USERNAME%\AppData\Roaming\StudentManagementSystem"

@REM Construct the paths for the config and databases directories
set "CONFIG_DIR=%APP_DIR%\config"
set "DATABASES_DIR=%APP_DIR%\databases"

@REM Create the config directory if it doesn't exist
if not exist "%CONFIG_DIR%" mkdir "%CONFIG_DIR%"

@REM Create the databases directory if it doesn't exist
if not exist "%DATABASES_DIR%" mkdir "%DATABASES_DIR%"

@REM Write the path of the SQLite database file into DbConfig.txt
echo %DATABASES_DIR%\sqlite.db > "%CONFIG_DIR%\DbConfig.txt"

@REM Launch the Java application with the specified config directory
@REM java -Dconfig.dir="%CONFIG_DIR%" -jar YourApp.jar

endlocal
