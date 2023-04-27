#!/usr/bin/bash

# finds environment variables that have been added to env_var.txt
source "staging-backend/env_var.txt"

# Run the staging backend server

fuser -k 9499/tcp || true
java -jar staging-backend/libs/backend-0.0.1-SNAPSHOT.jar --spring.profiles.active=staging
