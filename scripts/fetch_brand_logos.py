#!/usr/bin/env python3
"""Download brand logos using a curated Wikimedia URL map."""

from __future__ import annotations

import json
import time
import urllib.error
import urllib.request
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
ASSETS_DIR = ROOT / "app" / "src" / "main" / "assets" / "logos"
BRANDS_PATH = ROOT / "data" / "brands.json"

# Curated direct Wikimedia Commons URLs (stable upload paths).
LOGO_URLS: dict[str, str] = {
    "chanel": "https://upload.wikimedia.org/wikipedia/commons/3/35/Chanel_logo.svg",
    "dior": "https://upload.wikimedia.org/wikipedia/commons/7/77/Dior_Logo_2022.svg",
    "louis-vuitton": "https://upload.wikimedia.org/wikipedia/commons/7/76/Louis_Vuitton_logo_and_wordmark.svg",
    "gucci": "https://upload.wikimedia.org/wikipedia/commons/e/e0/Gucci_logo.svg",
    "prada": "https://upload.wikimedia.org/wikipedia/commons/b/b8/Prada-Logo.svg",
    "hermes": "https://upload.wikimedia.org/wikipedia/commons/8/88/Herm%C3%A8s_Logo.svg",
    "balenciaga": "https://upload.wikimedia.org/wikipedia/commons/8/8f/Balenciaga_logo.svg",
    "saint-laurent": "https://upload.wikimedia.org/wikipedia/commons/8/81/Saint_Laurent_logo.svg",
    "bottega-veneta": "https://upload.wikimedia.org/wikipedia/commons/5/5c/Bottega_Veneta_logo.svg",
    "burberry": "https://upload.wikimedia.org/wikipedia/commons/8/86/Burberry_Logo.svg",
    "versace": "https://upload.wikimedia.org/wikipedia/commons/4/48/Versace_logo.svg",
    "valentino": "https://upload.wikimedia.org/wikipedia/commons/5/5f/Valentino_logo.svg",
    "givenchy": "https://upload.wikimedia.org/wikipedia/commons/6/6e/Givenchy_logo.svg",
    "fendi": "https://upload.wikimedia.org/wikipedia/commons/5/5e/Fendi_logo.svg",
    "loewe": "https://upload.wikimedia.org/wikipedia/commons/4/4a/Loewe_logo.svg",
    "celine": "https://upload.wikimedia.org/wikipedia/commons/1/1c/Celine_logo.svg",
    "miu-miu": "https://upload.wikimedia.org/wikipedia/commons/5/5d/Miu_Miu_logo.svg",
    "tom-ford": "https://upload.wikimedia.org/wikipedia/commons/4/4e/Tom_Ford_logo.svg",
    "alexander-mcqueen": "https://upload.wikimedia.org/wikipedia/commons/6/6a/Alexander_McQueen_logo.svg",
    "jacquemus": "https://upload.wikimedia.org/wikipedia/commons/9/9a/Jacquemus_logo.svg",
    "alaia": "https://upload.wikimedia.org/wikipedia/commons/4/4b/Ala%C3%AFa_logo.svg",
    "moschino": "https://upload.wikimedia.org/wikipedia/commons/4/4c/Moschino_logo.svg",
    "armani": "https://upload.wikimedia.org/wikipedia/commons/4/44/Giorgio_Armani_logo.svg",
    "dolce-gabbana": "https://upload.wikimedia.org/wikipedia/commons/4/42/Dolce_%26_Gabbana_logo.svg",
    "moncler": "https://upload.wikimedia.org/wikipedia/commons/5/5a/Moncler_logo.svg",
}


def download(url: str, destination: Path) -> None:
    request = urllib.request.Request(url, headers={"User-Agent": "AtelierAndroid/1.0"})
    for attempt in range(4):
        try:
            with urllib.request.urlopen(request, timeout=30) as response:
                destination.write_bytes(response.read())
            return
        except urllib.error.HTTPError as error:
            if error.code in {429, 503} and attempt < 3:
                time.sleep(2 ** (attempt + 1))
                continue
            raise


def main() -> int:
    ASSETS_DIR.mkdir(parents=True, exist_ok=True)
    with BRANDS_PATH.open(encoding="utf-8") as handle:
        data = json.load(handle)

    missing: list[str] = []
    for brand in data["brands"]:
        brand_id = brand["id"]
        url = LOGO_URLS.get(brand_id)
        if not url:
            missing.append(brand_id)
            continue

        destination = ASSETS_DIR / f"{brand_id}.svg"
        try:
            download(url, destination)
            brand["logoUrl"] = url
            brand["logoAsset"] = f"logos/{brand_id}.svg"
            print(f"OK  {brand_id}")
        except Exception as error:  # noqa: BLE001
            missing.append(brand_id)
            print(f"FAIL {brand_id}: {error}")
        time.sleep(1)

    data["dataVersion"] = "2026-07-05-logos"

    with BRANDS_PATH.open("w", encoding="utf-8") as handle:
        json.dump(data, handle, indent=2, ensure_ascii=False)
        handle.write("\n")

    seed_path = ROOT / "app" / "src" / "main" / "assets" / "brands_seed.json"
    seed_path.write_text(BRANDS_PATH.read_text(encoding="utf-8"), encoding="utf-8")

    if missing:
        print("Missing:", ", ".join(missing))
        return 1
    print(f"All {len(data['brands'])} logos downloaded.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
