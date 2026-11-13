param(
    [switch]$Check
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

$formatterVersion = "1.28.0"
$toolsDirectory = Join-Path $PSScriptRoot ".tools"
$formatterJar = Join-Path `
    $toolsDirectory `
    "google-java-format-$formatterVersion-all-deps.jar"
$downloadUrl = `
    "https://github.com/google/google-java-format/releases/download/" + `
    "v$formatterVersion/google-java-format-$formatterVersion-all-deps.jar"

if (-not (Get-Command java -ErrorAction SilentlyContinue)) {
    throw "Java no está disponible en PATH. Instalá Java 17 o posterior."
}

if (-not (Test-Path -LiteralPath $formatterJar -PathType Leaf)) {
    New-Item -ItemType Directory -Force $toolsDirectory | Out-Null
    Write-Host "Descargando google-java-format $formatterVersion..."
    Invoke-WebRequest -Uri $downloadUrl -OutFile $formatterJar
}

$sourceDirectories = @("src", "test") |
    ForEach-Object { Join-Path $PSScriptRoot $_ } |
    Where-Object { Test-Path -LiteralPath $_ -PathType Container }

$sourceFiles = @(
    $sourceDirectories |
        ForEach-Object {
            Get-ChildItem -LiteralPath $_ -Recurse -File -Filter *.java
        } |
        ForEach-Object { $_.FullName } |
        Sort-Object -Unique
)

if ($sourceFiles.Count -eq 0) {
    throw "No se encontraron archivos Java en src ni test."
}

$formatterArguments = @("-jar", $formatterJar)
if ($Check) {
    $formatterArguments += @("--dry-run", "--set-exit-if-changed")
} else {
    $formatterArguments += "--replace"
}
$formatterArguments += $sourceFiles

& java @formatterArguments
if ($LASTEXITCODE -ne 0) {
    if ($Check) {
        throw "Hay archivos Java sin formatear. Ejecutá .\format.ps1 para corregirlos."
    }

    throw "google-java-format no pudo formatear los archivos Java."
}

if ($Check) {
    Write-Host "Todos los archivos Java tienen el formato correcto."
} else {
    Write-Host "Se formatearon $($sourceFiles.Count) archivos Java."
}
