@echo off
setlocal enabledelayedexpansion

cd /d "E:\Claude Projekte\Hytale\Gorduan-PlayerMorphToMob-1.0.0"

echo Cleaning build directories...
if exist build\stub-classes rmdir /s /q build\stub-classes
if exist build\plugin-classes rmdir /s /q build\plugin-classes
mkdir build\stub-classes
mkdir build\plugin-classes

echo Compiling stubs...
dir /s /b stubs\*.java > stubs-files.txt
javac -d build/stub-classes @stubs-files.txt
if errorlevel 1 (
    echo Stub compilation failed!
    del stubs-files.txt
    exit /b 1
)
del stubs-files.txt
echo Stubs compiled successfully.

echo Compiling plugin...
dir /s /b src\*.java > src-files.txt
javac -d build/plugin-classes -cp "build/stub-classes" @src-files.txt
if errorlevel 1 (
    echo Plugin compilation failed!
    del src-files.txt
    exit /b 1
)
del src-files.txt
echo Plugin compiled successfully.

echo Creating JAR...
if exist build\Gorduan-PlayerMorphToMob-1.0.0.jar del build\Gorduan-PlayerMorphToMob-1.0.0.jar
jar cf build/Gorduan-PlayerMorphToMob-1.0.0.jar -C build/plugin-classes . -C . manifest.json -C resources .
if errorlevel 1 (
    echo JAR creation failed!
    exit /b 1
)
echo JAR created successfully: build\Gorduan-PlayerMorphToMob-1.0.0.jar

echo Done!
