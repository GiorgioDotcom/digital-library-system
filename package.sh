#!/bin/bash

echo "📦 Digital Library System - Packaging"
echo "====================================="

echo "🔨 Creating JAR package..."
mvn clean package -q

if [ $? -eq 0 ]; then
    echo "✅ Packaging successful!"
    echo ""
    echo "📁 Generated files:"
    ls -la target/*.jar
    echo ""
    echo "🚀 To run the JAR:"
    echo "java -jar target/digital-library-system-1.0.0.jar"
else
    echo "❌ Packaging failed!"
    exit 1
fi
