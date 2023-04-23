@echo off

REM Check if Docker is installed
docker -v > nul 2>&1
if %errorlevel% neq 0 (
    echo Docker is not installed
    exit /b 1
)

REM Check if image is already built
docker images | findstr /c:"atomicloadertarget" > nul 2>&1
if %errorlevel% equ 0 (
    echo Docker image is already built
    goto check_container
)

REM Build Docker image from Dockerfile
echo Building Docker image...
docker build -t atomicloadertarget .\docker\.

REM Check if image is built successfully
docker images | findstr /c:"atomicloadertarget" > nul 2>&1
if %errorlevel% neq 0 (
    echo Failed to build Docker image
    exit /b 1
)

:check_container
REM Check if container is already running
docker ps | find "atomicloadertarget" > nul 2>&1
if %errorlevel% equ 0 (
    echo Docker container is already running
    goto continue
)

REM Start Docker container from image
echo Starting Docker container...
docker run -d -p 8080:8080 atomicloadertarget

REM Wait for container to start
echo Waiting for Docker container to start...
:loop
ping -n 1 -w 1000 localhost > nul
docker ps | find "atomicloadertarget" > nul 2>&1
if %errorlevel% equ 0 (
    goto continue
)
goto loop

:continue
echo Docker container started successfully
