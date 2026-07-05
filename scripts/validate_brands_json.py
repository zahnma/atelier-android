#!/usr/bin/env python3
"""Validate brands.json schema and data integrity."""

from __future__ import annotations

import json
import sys
from datetime import date
from pathlib import Path


def parse_date(value: str) -> date:
    return date.fromisoformat(value)


def validate_brands(data: dict) -> list[str]:
    errors: list[str] = []

    if data.get("schemaVersion") != 1:
        errors.append("schemaVersion must be 1")

    data_version = data.get("dataVersion")
    if not data_version or not isinstance(data_version, str):
        errors.append("dataVersion must be a non-empty string")

    brands = data.get("brands")
    if not isinstance(brands, list) or not brands:
        errors.append("brands must be a non-empty list")
        return errors

    brand_ids: set[str] = set()
    for index, brand in enumerate(brands):
        prefix = f"brands[{index}]"
        brand_id = brand.get("id")
        if not brand_id or not isinstance(brand_id, str):
            errors.append(f"{prefix}: missing id")
            continue
        if brand_id in brand_ids:
            errors.append(f"{prefix}: duplicate brand id '{brand_id}'")
        brand_ids.add(brand_id)

        for field in ("name", "country", "category"):
            if not brand.get(field):
                errors.append(f"{prefix} ({brand_id}): missing {field}")

        if not isinstance(brand.get("founded"), int):
            errors.append(f"{prefix} ({brand_id}): founded must be an integer")

        directors = brand.get("directors")
        if not isinstance(directors, list) or not directors:
            errors.append(f"{prefix} ({brand_id}): directors must be a non-empty list")
            continue

        person_ids: set[str] = set()
        for d_index, director in enumerate(directors):
            d_prefix = f"{prefix}.directors[{d_index}]"
            person_id = director.get("personId")
            if not person_id:
                errors.append(f"{d_prefix}: missing personId")
            elif person_id in person_ids:
                errors.append(f"{d_prefix}: duplicate personId '{person_id}'")
            else:
                person_ids.add(person_id)

            for field in ("name", "role", "startDate"):
                if not director.get(field):
                    errors.append(f"{d_prefix}: missing {field}")

            start_raw = director.get("startDate")
            end_raw = director.get("endDate")
            try:
                start = parse_date(start_raw)
            except (TypeError, ValueError):
                errors.append(f"{d_prefix}: invalid startDate '{start_raw}'")
                continue

            end = None
            if end_raw is not None:
                try:
                    end = parse_date(end_raw)
                except (TypeError, ValueError):
                    errors.append(f"{d_prefix}: invalid endDate '{end_raw}'")
                    continue
                if end < start:
                    errors.append(f"{d_prefix}: endDate before startDate")

            is_current = director.get("isCurrent")
            if is_current is not None and not isinstance(is_current, bool):
                errors.append(f"{d_prefix}: isCurrent must be boolean when present")

            derived_current = end_raw is None
            if is_current is not None and is_current != derived_current:
                errors.append(
                    f"{d_prefix}: isCurrent ({is_current}) does not match endDate nullability",
                )

    return errors


def main() -> int:
    path = Path(sys.argv[1] if len(sys.argv) > 1 else "data/brands.json")
    if not path.exists():
        print(f"File not found: {path}", file=sys.stderr)
        return 1

    with path.open(encoding="utf-8") as handle:
        data = json.load(handle)

    errors = validate_brands(data)
    if errors:
        print(f"Validation failed for {path}:", file=sys.stderr)
        for error in errors:
            print(f"  - {error}", file=sys.stderr)
        return 1

    brand_count = len(data["brands"])
    director_count = sum(len(b["directors"]) for b in data["brands"])
    print(f"OK: {brand_count} brands, {director_count} director entries")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
