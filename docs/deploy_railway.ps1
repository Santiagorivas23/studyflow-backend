# Deploy Railway helper script
# Usage: run from backend/ directory in PowerShell
# Requires: Node/npm, Railway CLI (npm i -g railway) and Git configured

param(
    [string]$ProjectName = "studyflow-backend",
    [string]$Branch = "main"
)

Write-Host "1) Ensure you are logged into Railway CLI"
Write-Host "If not, run: railway login"
Write-Host "2) Initialize or link the project (interactive)"
Write-Host "Running: railway init --name $ProjectName"

railway init --name $ProjectName

Write-Host "3) Set environment variables on Railway."
Write-Host "You will be prompted to paste sensitive values."

# Read from environment or ask
if (-not $env:GEMINI_API_KEY) {
    $gemini = Read-Host -AsSecureString "Introduce tu GEMINI_API_KEY (input oculto)"
    $geminiPlain = [Runtime.InteropServices.Marshal]::PtrToStringAuto([Runtime.InteropServices.Marshal]::SecureStringToBSTR($gemini))
} else {
    $geminiPlain = $env:GEMINI_API_KEY
}

# SUPABASE connection vars: expect user to set env vars or input now
if (-not $env:SUPABASE_DATABASE_URL) {
    $supabaseUrl = Read-Host "Introduce SUPABASE_DATABASE_URL (jdbc:postgresql://host:5432/db)"
} else { $supabaseUrl = $env:SUPABASE_DATABASE_URL }

if (-not $env:SUPABASE_DB_USER) {
    $supabaseUser = Read-Host "Introduce SUPABASE_DB_USER"
} else { $supabaseUser = $env:SUPABASE_DB_USER }

if (-not $env:SUPABASE_DB_PASSWORD) {
    $supabasePass = Read-Host -AsSecureString "Introduce SUPABASE_DB_PASSWORD (input oculto)"
    $supabasePassPlain = [Runtime.InteropServices.Marshal]::PtrToStringAuto([Runtime.InteropServices.Marshal]::SecureStringToBSTR($supabasePass))
} else { $supabasePassPlain = $env:SUPABASE_DB_PASSWORD }

# Set variables via railway CLI
if ($supabaseUrl) { railway variables set SUPABASE_DATABASE_URL="$supabaseUrl" }
if ($supabaseUser) { railway variables set SUPABASE_DB_USER="$supabaseUser" }
if ($supabasePassPlain) { railway variables set SUPABASE_DB_PASSWORD="$supabasePassPlain" }
if ($geminiPlain) { railway variables set GEMINI_API_KEY="$geminiPlain" }

Write-Host "4) Deploying (railway up). This will build the Dockerfile and deploy."
railway up --branch $Branch

Write-Host "Deployment finished. Use 'railway logs' and 'railway status' to inspect."