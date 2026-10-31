@echo off
title ExamGuard AI Server
cd /d "%~dp0"
echo ============================================================
echo          Starting ExamGuard AI Web Application
echo ============================================================
echo.
echo Application will be available at: http://localhost:8080
echo.

if not exist "target\examguard-ai-1.0.0.jar" (
    echo [INFO] Building executable package for first run...
    call mvnw.cmd package -DskipTests
)

java -jar target\examguard-ai-1.0.0.jar
pause
