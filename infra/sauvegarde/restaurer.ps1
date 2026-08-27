param(
  [Parameter(Mandatory = $true)]
  [string]$Dump,
  [string]$ComposeFile = "$PSScriptRoot\..\..\compose.yml",
  [string]$EnvFile = "$PSScriptRoot\..\..\.env"
)

$ErrorActionPreference = "Stop"
if (-not (Test-Path $Dump)) {
  throw "Dump introuvable : $Dump"
}

$composeArgs = @("-f", $ComposeFile)
if (Test-Path $EnvFile) {
  $composeArgs += @("--env-file", $EnvFile)
}

docker compose @composeArgs cp $Dump db:/tmp/aquasensus.restore.dump
docker compose @composeArgs exec -T db pg_restore -U aquasensus -d aquasensus --clean --if-exists /tmp/aquasensus.restore.dump
Write-Host "Restauration demandée depuis $Dump"
