$headers = @{
    'User-Agent' = 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 Chrome/120.0.0.0 Safari/537.36'
}
$resp = Invoke-WebRequest -Uri 'https://www.carlogos.org/car-brands/toyota-logo.html' -Headers $headers -TimeoutSec 20 -UseBasicParsing
$html = $resp.Content
$lines = $html -split "`n" | Where-Object { $_ -match 'png|jpg|logo|img' } | Select-Object -First 30
$lines | Out-File -FilePath "debug_output.txt" -Encoding utf8
Write-Host "Done. Check debug_output.txt"
Write-Host "Total HTML length: $($html.Length)"
