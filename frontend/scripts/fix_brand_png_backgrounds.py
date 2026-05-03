"""
Remove baked-in checkerboard / flat light backgrounds from brand PNGs.
- logo-wide, logo-ico: aggressive achromatic light -> transparent
- illus-*, adm-*: edge flood fill through achromatic bright pixels only
- logo-sq: navy icon on checker; edge flood removes checker around rounded card
"""
from __future__ import annotations

from collections import deque
from pathlib import Path

from PIL import Image

BRAND_DIR = Path(__file__).resolve().parent.parent / "public" / "assets" / "brand"


def achromatic_bright(r: int, g: int, b: int, chroma_max: int = 28, lum_min: int = 195) -> bool:
    if max(r, g, b) - min(r, g, b) > chroma_max:
        return False
    return (r + g + b) // 3 >= lum_min


def flood_transparent_edge(im: Image.Image, chroma_max: int = 28, lum_min: int = 195) -> int:
    w, h = im.size
    px = im.load()
    vis = [[False] * w for _ in range(h)]
    q: deque[tuple[int, int]] = deque()

    def try_seed(x: int, y: int) -> None:
        if not (0 <= x < w and 0 <= y < h):
            return
        r, g, b, a = px[x, y]
        if a and achromatic_bright(r, g, b, chroma_max, lum_min):
            vis[y][x] = True
            q.append((x, y))

    for x in range(w):
        try_seed(x, 0)
        try_seed(x, h - 1)
    for y in range(h):
        try_seed(0, y)
        try_seed(w - 1, y)

    while q:
        x, y = q.popleft()
        for nx, ny in ((x + 1, y), (x - 1, y), (x, y + 1), (x, y - 1)):
            if 0 <= nx < w and 0 <= ny < h and not vis[ny][nx]:
                r, g, b, a = px[nx, ny]
                if a and achromatic_bright(r, g, b, chroma_max, lum_min):
                    vis[ny][nx] = True
                    q.append((nx, ny))

    n = 0
    for y in range(h):
        for x in range(w):
            if vis[y][x]:
                r, g, b, _ = px[x, y]
                px[x, y] = (r, g, b, 0)
                n += 1
    return n


def aggressive_logo_wide(im: Image.Image) -> int:
    """Navy + red only on light checker; safe to strip all bright achromatic."""
    w, h = im.size
    px = im.load()
    n = 0
    for y in range(h):
        for x in range(w):
            r, g, b, a = px[x, y]
            if not a:
                continue
            if achromatic_bright(r, g, b, chroma_max=32, lum_min=185):
                px[x, y] = (r, g, b, 0)
                n += 1
    return n


def process_file(path: Path) -> tuple[str, int]:
    im = Image.open(path).convert("RGBA")
    name = path.name.lower()
    if name == "logo-wide.png":
        n = aggressive_logo_wide(im)
    else:
        n = flood_transparent_edge(im, chroma_max=28, lum_min=195)
    im.save(path, optimize=True)
    return path.name, n


def main() -> None:
    files = sorted(BRAND_DIR.glob("*.png"))
    if not files:
        print("No PNGs in", BRAND_DIR)
        return
    for p in files:
        name, n = process_file(p)
        print(f"{name}: transparent pixels set: {n}")


if __name__ == "__main__":
    main()
