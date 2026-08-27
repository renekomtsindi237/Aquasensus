param(
  [string]$ComposeFile = "$PSScriptRoot\..\..\compose.yml",
  [string]$EnvFile = "$PSScriptRoot\..\..\.env"
)

$ErrorActionPreference = "Stop"
$destDir = Join-Path $PSScriptRoot "dumps"
New-Item -ItemType Directory -Force -Path $destDir | Out-Null
$stamp = Get-Date -Format "yyyyMMdd-HHmmss"
$out = Join-Path $destDir "aquasensus-$stamp.dump"

$composeArgs = @("-f", $ComposeFile)
if (Test-Path $EnvFile) {
  $composeArgs += @("--env-file", $EnvFile)
}

docker compose @composeArgs exec -T db pg_dump -U aquasensus -d aquasensus -Fc -f /tmp/aquasensus.dump
docker compose @composeArgs cp db:/tmp/aquasensus.dump $out
Write-Host "Dump écrit : $out"
