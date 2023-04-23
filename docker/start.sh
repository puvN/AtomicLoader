#!/bin/bash

# Check if Docker is installed
if ! command -v docker &> /dev/null; then
    echo "Docker is not installed"
    exit 1
fi

# Check if image is already built
if docker images | grep -q atomicloadertarget; then
    echo "Docker image is already built"
    check_container
else
    # Build Docker image from Dockerfile
    echo "Building Docker image..."
    docker build -t atomicloadertarget ./docker/.

    # Check if image is built successfully
    if ! docker images | grep -q atomicloadertarget; then
        echo "Failed to build Docker image"
        exit 1
    fi
fi

check_container() {
    # Check if container is already running
    if docker ps | grep -q atomicloadertarget; then
        echo "Docker container is already running"
        exit 0
    else
        # Start Docker container from image
        echo "Starting Docker container..."
        docker run -d -p 8080:8080 atomicloadertarget

        # Wait for container to start
        echo "Waiting for Docker container to start..."
        while true; do
            sleep 1
            if docker ps | grep -q atomicloadertarget; then
                break
            fi
        done

        # Check if application started successfully
        if docker logs -n 1 atomicloadertarget | grep -q "Application started successfully"; then
            echo "Docker container started successfully"
            exit 0
        else
            echo "Failed to start application inside Docker container"
            exit 1
        fi
    fi
}

check_container
