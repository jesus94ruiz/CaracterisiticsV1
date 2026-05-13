$headers = @{
    'User-Agent' = 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 Chrome/120.0.0.0 Safari/537.36'
    'Accept'     = 'text/html,application/xhtml+xml'
}

$brands = @(
    'toyota','bmw','mercedes-benz','audi','volkswagen','ford','honda',
    'porsche','tesla','ferrari','lamborghini','subaru','hyundai','jeep',
    'dodge','jaguar','maserati','chevrolet','nissan','mazda','kia',
    'volvo','peugeot','renault','seat','fiat','alfa-romeo','lexus',
    'bugatti','bentley','rolls-royce','aston-martin','land-rover','mclaren',
    'mitsubishi','skoda','citroen','opel','infiniti','cadillac','mini',
    'dacia','acura','lincoln','buick','chrysler','smart','genesis',
    'pagani','koenigsegg'
)

$baseUrl  = 'https://www.carlogos.org'
$outDir   = "app\src\main\res\drawable"
if (-not (Test-Path $outDir)) { New-Item -ItemType Directory -Path $outDir | Out-Null }

$results = @{}

# Pattern to match src="/car-logos/something.png"
$srcPattern = 'src="(/car-logos/[^"]+\.png)"'

foreach ($slug in $brands) {
    $url = "$baseUrl/car-brands/$slug-logo.html"
    try {
        $resp = Invoke-WebRequest -Uri $url -Headers $headers -TimeoutSec 20 -UseBasicParsing
        $html = $resp.Content

        # Try to find the "present" block first (most recent logo)
        $presentMatch = [regex]::Match($html, 'class="present"[^>]*>.*?src="(/car-logos/[^"]+\.png)"')
        if ($presentMatch.Success) {
            $pngUrl = $baseUrl + $presentMatch.Groups[1].Value
            $results[$slug] = $pngUrl
            Write-Host "FOUND (present): $slug => $pngUrl"
        } else {
            # Fall back: find all src matches, take the last one
            $allMatches = [regex]::Matches($html, $srcPattern)
            if ($allMatches.Count -gt 0) {
                $pngUrl = $baseUrl + $allMatches[$allMatches.Count - 1].Groups[1].Value
                $results[$slug] = $pngUrl
                Write-Host "FOUND (fallback): $slug => $pngUrl"
            } else {
                Write-Host "NOPNG: $slug - no PNG found"
            }
        }
    } catch {
        Write-Host "ERROR: $slug - $_"
    }
    Start-Sleep -Milliseconds 700
}

Write-Host ""
Write-Host "--- Downloading logos ---"
foreach ($slug in $results.Keys) {
    $pngUrl   = $results[$slug]
    $safeName = $slug -replace '-','_'
    $dest     = "$outDir\brand_$safeName.png"
    try {
        Invoke-WebRequest -Uri $pngUrl -Headers $headers -OutFile $dest -TimeoutSec 20 -UseBasicParsing
        $size = (Get-Item $dest).Length
        Write-Host "DL OK: brand_$safeName.png - $size bytes"
    } catch {
        Write-Host "DL ERR: $slug - $_"
    }
    Start-Sleep -Milliseconds 500
}

$mappingLines = $results.GetEnumerator() | ForEach-Object {
    $safeName = $_.Key -replace '-','_'
    "$($_.Key)|$safeName|$($_.Value)"
}
$mappingLines | Out-File -FilePath "brand_logo_mapping.txt" -Encoding utf8

Write-Host ""
Write-Host "=== DONE ==="
Write-Host "Downloaded: $($results.Count) brands"
Write-Host "Mapping saved to brand_logo_mapping.txt"
