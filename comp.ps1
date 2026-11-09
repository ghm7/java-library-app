New-Item -ItemType Directory -Force out | Out-Null

$files = Get-ChildItem -Recurse -Filter *.java src |
    ForEach-Object { $_.FullName }

javac --release 17 -encoding UTF-8 -d out $files

$fontsSource = 'src\com\example\library\ui\fonts'
if (Test-Path $fontsSource) {
    $fontsOutput = 'out\com\example\library\ui\fonts'
    New-Item -ItemType Directory -Force $fontsOutput | Out-Null
    Copy-Item -Force "$fontsSource\*" $fontsOutput
}
