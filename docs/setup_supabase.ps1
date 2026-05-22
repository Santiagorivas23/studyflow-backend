# Setup Supabase helper script
# Usage: run from backend/ directory in PowerShell
# Requires: psql installed locally (or use Supabase SQL editor)

param(
    [string]$Host = $env:SUPABASE_DB_HOST,
    [string]$Port = $env:SUPABASE_DB_PORT,
    [string]$DbName = $env:SUPABASE_DB_NAME,
    [string]$User = $env:SUPABASE_DB_USER,
    [string]$Password = $env:SUPABASE_DB_PASSWORD
)

if (-not $Host -or -not $DbName -or -not $User -or -not $Password) {
    Write-Host "Faltan variables de conexión. Introduce los siguientes valores:"
    $Host = Read-Host "SUPABASE DB Host (ej: db.abcd.supabase.co)"
    $Port = Read-Host "SUPABASE DB Port (default 5432)" -Default 5432
    $DbName = Read-Host "SUPABASE DB Name (ej: postgres)"
    $User = Read-Host "SUPABASE DB User"
    $Password = Read-Host -AsSecureString "SUPABASE DB Password (oculto)"
    $Password = [Runtime.InteropServices.Marshal]::PtrToStringAuto([Runtime.InteropServices.Marshal]::SecureStringToBSTR($Password))
}

# Build connection string
$connection = "host=$Host port=$Port dbname=$DbName user=$User password=$Password"

# Check if psql is available
$psql = Get-Command psql -ErrorAction SilentlyContinue
if (-not $psql) {
    Write-Host "psql no está instalado o no está en PATH. Abre Supabase SQL Editor y ejecuta init-db.sql manualmente."
    exit 1
}

# Execute init-db.sql
$file = "src/main/resources/init-db.sql"
if (-not (Test-Path $file)) { Write-Host "No se encontró init-db.sql en $file"; exit 1 }

Write-Host "Ejecutando init-db.sql en Supabase..."
psql "$connection" -f $file

Write-Host "Script ejecutado. Verifica tablas en Supabase dashboard."