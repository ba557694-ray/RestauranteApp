@echo off
cd /d "%~dp0"
where java >nul 2>nul
if errorlevel 1 (
    echo No se encontro Java. Instale Java 17 o superior y vuelva a intentar.
    pause
    exit /b 1
)
java -jar RestauranteApp.jar
if errorlevel 1 pause
