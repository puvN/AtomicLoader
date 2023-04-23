#!/bin/bash

# Check if Docker is installed
if ! command -v docker &> /dev/null
then
    echo "Docker is not installed"
    exit 1
fi

# Build Docker image from Dockerfile
docker build -t atomicloadertarget .

# Start Docker container from image
docker run -d -p 8080:80 atomicloadertarget

echo "Docker container started successfully"
