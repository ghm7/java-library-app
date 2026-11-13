New-Item -ItemType Directory -Force out | Out-Null

$files = Get-ChildItem -Recurse -Filter *.java src |
    ForEach-Object { $_.FullName }

javac --release 17 -encoding UTF-8 -d out $files

$resourcesSource = 'src\com\example\library\ui'
$resourcesOutput = 'out\com\example\library\ui'

Get-ChildItem -LiteralPath $resourcesSource -Directory -Recurse |
    Where-Object { $_.Name -in @('fonts', 'assets') } |
    ForEach-Object {
        $relativePath = $_.FullName.Substring((Resolve-Path $resourcesSource).Path.Length).TrimStart('\')
        $outputDirectory = Join-Path $resourcesOutput $relativePath
        New-Item -ItemType Directory -Force $outputDirectory | Out-Null
        Copy-Item -Path (Join-Path $_.FullName '*') -Destination $outputDirectory -Recurse -Force
    }
