New-Item -ItemType Directory -Force out | Out-Null

$files = Get-ChildItem -Recurse -Filter *.java src |
    ForEach-Object { $_.FullName }

javac --release 17 -encoding UTF-8 -d out $files
