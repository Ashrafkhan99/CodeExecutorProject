#!/usr/bin/env bash
set -euo pipefail
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"

docker build -t coderank/lang-python:3.11 "$SCRIPT_DIR/python"
docker build -t coderank/lang-java:17    "$SCRIPT_DIR/java"
docker build -t coderank/lang-cpp:17     "$SCRIPT_DIR/cpp"
