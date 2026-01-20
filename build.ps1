$ErrorActionPreference = "Continue"
Set-Location "E:\Claude Projekte\Hytale\Gorduan-PlayerMorphToMob-1.0.0"

Write-Host "Cleaning build directories..."
if (Test-Path "build\stub-classes") { Remove-Item -Recurse -Force "build\stub-classes" }
if (Test-Path "build\plugin-classes") { Remove-Item -Recurse -Force "build\plugin-classes" }
New-Item -ItemType Directory -Force "build\stub-classes" | Out-Null
New-Item -ItemType Directory -Force "build\plugin-classes" | Out-Null

Write-Host "Compiling stubs..."
$stubFiles = Get-ChildItem -Path "stubs" -Recurse -Filter "*.java"
Write-Host "  Found $($stubFiles.Count) stub files"

# Use relative paths to avoid space issues - UTF8 without BOM
$argFile = Join-Path (Get-Location) "stubs-args.txt"
$relativePaths = $stubFiles | ForEach-Object {
    $rel = $_.FullName.Replace((Get-Location).Path + "\", "")
    $rel
}
$utf8NoBom = New-Object System.Text.UTF8Encoding $false
[System.IO.File]::WriteAllLines($argFile, $relativePaths, $utf8NoBom)

Write-Host "Running javac on stubs..."
javac -d "build\stub-classes" "@stubs-args.txt" 2>&1
if ($LASTEXITCODE -ne 0) {
    Write-Host "Stub compilation failed!"
    Write-Host "First 5 lines of stubs-args.txt:"
    Get-Content "stubs-args.txt" | Select-Object -First 5
    exit 1
}
Remove-Item "stubs-args.txt" -ErrorAction SilentlyContinue
Write-Host "Stubs compiled successfully."

Write-Host "Compiling plugin..."
$srcFiles = Get-ChildItem -Path "src" -Recurse -Filter "*.java"
Write-Host "  Found $($srcFiles.Count) source files"

$argFile = Join-Path (Get-Location) "src-args.txt"
$relativePaths = $srcFiles | ForEach-Object {
    $rel = $_.FullName.Replace((Get-Location).Path + "\", "")
    $rel
}
[System.IO.File]::WriteAllLines($argFile, $relativePaths, $utf8NoBom)

Write-Host "Running javac on src..."
$compileOutput = javac -d "build\plugin-classes" -cp "build\stub-classes" "@src-args.txt" 2>&1
$compileOutput | ForEach-Object { Write-Host $_ }
if ($LASTEXITCODE -ne 0) {
    Write-Host "Plugin compilation failed!"
    exit 1
}
Remove-Item "src-args.txt" -ErrorAction SilentlyContinue
Write-Host "Plugin compiled successfully."

Write-Host "Creating JAR..."
if (Test-Path "build\Gorduan-PlayerMorphToMob-1.0.0.jar") { Remove-Item "build\Gorduan-PlayerMorphToMob-1.0.0.jar" }
jar cf "build\Gorduan-PlayerMorphToMob-1.0.0.jar" -C "build\plugin-classes" . -C . "manifest.json" -C "resources" .
if ($LASTEXITCODE -ne 0) {
    Write-Host "JAR creation failed!"
    exit 1
}
Write-Host "JAR created successfully: build\Gorduan-PlayerMorphToMob-1.0.0.jar"

Write-Host "Done!"
