# run-local.ps1 - Arrancar StudyFlow backend en LOCAL con H2 (sin PostgreSQL)
# Uso: .\run-local.ps1

param(
    [string]$GeminiKey = ""
)

# Cargar desde .env si existe
$envFile = Join-Path $PSScriptRoot ".env"
if (Test-Path $envFile) {
    Get-Content $envFile | ForEach-Object {
        if ($_ -match '^\s*([^#][^=]+)=(.*)$') {
            $key = $matches[1].Trim()
            $val = $matches[2].Trim()
            [System.Environment]::SetEnvironmentVariable($key, $val, "Process")
            Write-Host "  [env] $key cargado"
        }
    }
    Write-Host ""
}

# Usar el parametro si se pasa
if ($GeminiKey -ne "") {
    $env:GEMINI_API_KEY = $GeminiKey
}

if (-not $env:GEMINI_API_KEY) {
    Write-Host "ADVERTENCIA: GEMINI_API_KEY no configurada. La IA no funcionará." -ForegroundColor Yellow
    Write-Host "  Opciones:"
    Write-Host "    1. Crea un archivo 'backend\.env' con: GEMINI_API_KEY=tu_clave"
    Write-Host "    2. O pasa el parametro: .\run-local.ps1 -GeminiKey 'AIza...'"
    Write-Host ""
}

# Compilar
Write-Host "Compilando proyecto (DskipTests)..." -ForegroundColor Cyan
Set-Location $PSScriptRoot
mvn -B -DskipTests clean package -q
if ($LASTEXITCODE -ne 0) {
    Write-Host "ERROR: Fallo la compilacion Maven" -ForegroundColor Red
    exit 1
}

# Configurar perfil local
$env:SPRING_PROFILES_ACTIVE = "local"

Write-Host ""
Write-Host "Iniciando StudyFlow Backend..." -ForegroundColor Green
Write-Host "  Perfil:    local (H2 en memoria)"
Write-Host "  Puerto:    http://localhost:8080"
Write-Host "  API:       http://localhost:8080/api"
Write-Host "  Health:    http://localhost:8080/api/health"
Write-Host "  H2 Console: http://localhost:8080/api/h2-console"
Write-Host "  IA:        $(if ($env:GEMINI_API_KEY) { 'HABILITADA' } else { 'DESHABILITADA (sin clave)' })"
Write-Host ""

java -jar target\studyflow-backend-1.0.0.jar
