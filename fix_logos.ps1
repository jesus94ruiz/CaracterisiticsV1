# Fix script: re-download logos that got wrong file (ford-logo.png = 14695 bytes)
# Uses og:image meta tag for reliable logo URL

$headers = @{
    'User-Agent' = 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 Chrome/120.0.0.0 Safari/537.36'
    'Accept'     = 'text/html,application/xhtml+xml'
}

$baseUrl = 'https://www.carlogos.org'
$outDir  = "app\src\main\res\drawable"
$wrongSize = 14695  # ford-logo.png size

# All 50 brands
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

foreach ($slug in $brands) {
    $safeName = $slug -replace '-','_'
    $dest     = "$outDir\brand_$safeName.png"

    # Check if file needs fixing (doesn't exist or wrong size)
    $needsFix = $false
    if (-not (Test-Path $dest)) {
        $needsFix = $true
        Write-Host "MISSING: $slug"
    } else {
        $size = (Get-Item $dest).Length
        if ($size -eq $wrongSize) {
            $needsFix = $true
            Write-Host "WRONG: $slug ($size bytes) - will fix"
        } else {
            Write-Host "OK: $slug ($size bytes)"
            continue
        }
    }

    if ($needsFix) {
        $url = "$baseUrl/car-brands/$slug-logo.html"
        try {
            $resp = Invoke-WebRequest -Uri $url -Headers $headers -TimeoutSec 20 -UseBasicParsing
            $html = $resp.Content

            # Extract og:image content
            $ogMatch = [regex]::Match($html, '<meta property="og:image" content="([^"]+\.png)"')
            if ($ogMatch.Success) {
                $relPath = $ogMatch.Groups[1].Value
                # Make absolute if relative
                if ($relPath.StartsWith('/')) {
                    $pngUrl = $baseUrl + $relPath
                } else {
                    $pngUrl = $relPath
                }
                Write-Host "  OG-IMAGE: $pngUrl"
                Invoke-WebRequest -Uri $pngUrl -Headers $headers -OutFile $dest -TimeoutSec 20 -UseBasicParsing
                $newSize = (Get-Item $dest).Length
                Write-Host "  DL OK: brand_$safeName.png - $newSize bytes"
            } else {
                Write-Host "  NO og:image for $slug"
                # Try secure_url
                $secureMatch = [regex]::Match($html, '<meta property="og:image:secure_url" content="([^"]+\.png)"')
                if ($secureMatch.Success) {
                    $relPath = $secureMatch.Groups[1].Value
                    $pngUrl = if ($relPath.StartsWith('/')) { $baseUrl + $relPath } else { $relPath }
                    Write-Host "  SECURE-URL: $pngUrl"
                    Invoke-WebRequest -Uri $pngUrl -Headers $headers -OutFile $dest -TimeoutSec 20 -UseBasicParsing
                    $newSize = (Get-Item $dest).Length
                    Write-Host "  DL OK: brand_$safeName.png - $newSize bytes"
                }
            }
        } catch {
            Write-Host "  ERROR: $slug - $_"
        }
        Start-Sleep -Milliseconds 700
    }
}

Write-Host ""
Write-Host "=== FIX DONE ==="
Write-Host "--- Final file listing ---"
Get-ChildItem "$outDir\brand_*.png" | ForEach-Object {
    Write-Host "$($_.Name) - $($_.Length) bytes"
}
