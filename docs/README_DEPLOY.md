Quick deploy guide (scripts included)

Files created:
- deploy_railway.ps1   # Interactive PowerShell helper for Railway deploy
- setup_supabase.ps1   # Interactive helper to run init-db.sql against Supabase

Deploy steps (summary):
1. Create Supabase project at https://app.supabase.com and get DB connection info
2. Run setup_supabase.ps1 to execute init-db.sql on Supabase (requires psql)
3. In Railway, create project and use deploy_railway.ps1 to set variables and deploy

If you want, I can now attempt to run `deploy_railway.ps1` here (will require interactive Railway login). If prefieres, ejecútalo localmente y copia aquí cualquier error para ayudarte a resolverlo.