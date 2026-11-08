param(
    [switch]$Check
)

$ErrorActionPreference = "Stop"

# v1.28.0 runs with this project's Java 17 toolchain.
$formatterVersion = "1.28.0"
$toolsDirectory = Join-Path $PSScriptRoot ".tools"
$formatterJar = Join-Path $toolsDirectory "google-java-format-$formatterVersion-all-deps.jar"
$downloadUrl = "https://github.com/google/google-java-format/releases/download/v$formatterVersion/google-java-format-$formatterVersion-all-deps.jar"

if (-not (Test-Path $formatterJar)) {
    New-Item -ItemType Directory -Force $toolsDirectory | Out-Null
    Write-Host "Downloading google-java-format $formatterVersion..."
    Invoke-WebRequest -Uri $downloadUrl -OutFile $formatterJar
}

$sourceFiles = Get-ChildItem -Path (Join-Path $PSScriptRoot "src") -Recurse -File -Filter *.java |
    ForEach-Object { $_.FullName }

if ($sourceFiles.Count -eq 0) {
    throw "No Java source files found under src."
}

$arguments = @("-jar", $formatterJar)
if ($Check) {
    $arguments += @("--dry-run", "--set-exit-if-changed")
} else {
    $arguments += "--replace"
}
$arguments += $sourceFiles

& java @arguments
if ($LASTEXITCODE -ne 0) {
    if ($Check) {
        throw "Java sources are not formatted. Run .\\format.ps1 to fix them."
    }
    throw "google-java-format failed."
}
