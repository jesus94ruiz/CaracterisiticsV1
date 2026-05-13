import urllib.request
import re
import os
import time

headers = {
    'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 Chrome/120.0 Safari/537.36',
    'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8',
}

BRANDS = [
    'toyota', 'bmw', 'mercedes-benz', 'audi', 'volkswagen', 'ford', 'honda',
    'porsche', 'tesla', 'ferrari', 'lamborghini', 'subaru', 'hyundai', 'jeep',
    'dodge', 'jaguar', 'maserati', 'chevrolet', 'nissan', 'mazda', 'kia',
    'volvo', 'peugeot', 'renault', 'seat', 'fiat', 'alfa-romeo', 'lexus',
    'bugatti', 'bentley', 'rolls-royce', 'aston-martin', 'land-rover', 'mclaren',
    'mitsubishi', 'skoda', 'citroen', 'opel', 'infiniti', 'cadillac', 'mini',
    'dacia', 'acura', 'lincoln', 'buick', 'chrysler', 'smart', 'genesis',
    'pagani', 'koenigsegg',
]

out_dir = os.path.join('app', 'src', 'main', 'res', 'drawable')
os.makedirs(out_dir, exist_ok=True)

results = {}

for slug in BRANDS:
    url = f'https://www.carlogos.org/car-brands/{slug}-logo.html'
    try:
        req = urllib.request.Request(url, headers=headers)
        with urllib.request.urlopen(req, timeout=20) as resp:
            html = resp.read().decode('utf-8', errors='ignore')

        # Find all PNG image URLs from carlogos.org/car-logos/
        imgs = re.findall(r'https://www\.carlogos\.org/car-logos/[^\s\'"<>]+\.png', html)
        if not imgs:
            # Try relative paths
            imgs_rel = re.findall(r'/car-logos/[^\s\'"<>]+\.png', html)
            imgs = ['https://www.carlogos.org' + r for r in imgs_rel]

        if imgs:
            # Prefer the most recent (last in page) - deduplicate preserving order
            seen = set()
            unique_imgs = []
            for img in imgs:
                if img not in seen:
                    seen.add(img)
                    unique_imgs.append(img)
            png_url = unique_imgs[-1]
            results[slug] = png_url
            print(f'[FOUND] {slug}: {png_url}')
        else:
            print(f'[NOPNG] {slug}: no PNG found in page')

        time.sleep(0.7)
    except Exception as e:
        print(f'[ERROR] {slug}: {e}')
        time.sleep(1)

# Download the found logos
print('\n--- Downloading logos ---')
for slug, png_url in results.items():
    safe_name = slug.replace('-', '_')
    dest = os.path.join(out_dir, f'brand_{safe_name}.png')
    try:
        req = urllib.request.Request(png_url, headers=headers)
        with urllib.request.urlopen(req, timeout=20) as resp:
            data = resp.read()
        with open(dest, 'wb') as f:
            f.write(data)
        size = len(data)
        print(f'[DL OK] brand_{safe_name}.png ({size} bytes)')
    except Exception as e:
        print(f'[DL ERR] {slug}: {e}')
    time.sleep(0.5)

print('\n=== DONE ===')
print('Downloaded brands:', list(results.keys()))

# Write mapping file for BrandLogoProvider
mapping_file = 'brand_logo_mapping.txt'
with open(mapping_file, 'w') as f:
    for slug, url in results.items():
        safe_name = slug.replace('-', '_')
        f.write(f'{slug}|{safe_name}|{url}\n')
print(f'Mapping saved to {mapping_file}')
