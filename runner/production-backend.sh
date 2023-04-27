#!/usr/bin/bash

# finds environment variables that have been added to env_var.txt
source "production-backend/env_var.txt"

# Run the production backend server

fuser -k 8999/tcp || true
java -jar production-backend/libs/backend-0.0.1-SNAPSHOT.jar --spring.profiles.active=production
