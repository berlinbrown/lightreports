#!/bin/sh
# Simple helper to format Java files using google-java-format (CLI or jar)
# Usage: tools/format-java.sh

set -euo pipefail

GJF=${GJF:-google-java-format}

if command -v "$GJF" >/dev/null 2>&1; then
  echo "Formatting Java files using $GJF..."
  find . -name "*.java" -print0 | xargs -0 "$GJF" -i
  echo "Done."
else
  echo "google-java-format not found. To install on macOS: brew install google-java-format"
  echo "Or download the jar from https://github.com/google/google-java-format and run:" 
  echo "  java -jar google-java-format.jar --replace \$(find . -name '*.java')"
  exit 1
fi
