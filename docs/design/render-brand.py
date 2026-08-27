"""Rasterise l'identite AquaSensus depuis sa geometrie canonique.

Sources de verite : `aquasensus-mark.svg` (symbole) et `aquasensus-logo.svg`
(verrouillage horizontal). Ce script ne fait que produire les rasters utilises
par les applications : logo par defaut, favicon, icones PWA, icone Android.

Usage : python docs/design/render-brand.py

Typographie : Inter est la police de la charte. Si les fichiers Inter sont
vendorises dans `docs/design/fonts/`, ils sont utilises ; sinon le script
retombe sur le repli systeme declare par la charte (Segoe UI, puis Arial).
"""

from __future__ import annotations

import math
from pathlib import Path

from PIL import Image, ImageDraw, ImageFont

# --- Tokens (docs/CHARTE-GRAPHIQUE.md) -------------------------------------

BLUE = (16, 109, 153, 255)        # blue-600
DEEP = (11, 71, 99, 255)          # blue-800
NIGHT = (10, 58, 82, 255)         # blue-900
GREY = (81, 96, 111, 255)         # neutral-600
LIGHT_GREY = (167, 189, 203, 255) # texte secondaire sur fond sombre
WHITE = (255, 255, 255, 255)

# --- Geometrie du symbole (viewBox 64 x 64) --------------------------------

VIEWBOX = 64.0
APEX = (32.0, 4.5)
CENTER = (32.0, 39.0)
RADIUS = 19.5
PULSE = [(19.5, 40.5), (25.7, 40.5), (29.6, 32.1), (34.7, 47.3), (38.6, 40.5), (44.5, 40.5)]
PULSE_WIDTH = 3.4

# --- Geometrie du verrouillage horizontal (viewBox 320 x 72) ---------------

LOCKUP = (320.0, 72.0)
MARK_ORIGIN = (4.0, 4.0)
WORDMARK_POS = (80.0, 36.0)       # x, baseline
WORDMARK_SIZE = 28.0
TAGLINE_POS = (81.0, 55.0)
TAGLINE_SIZE = 12.0
TAGLINE = "Anticiper la panne, garder l'eau."

FONT_CANDIDATES = {
    "bold": [
        "docs/design/fonts/Inter-Bold.ttf",
        "C:/Windows/Fonts/segoeuib.ttf",
        "C:/Windows/Fonts/arialbd.ttf",
        "/usr/share/fonts/truetype/inter/Inter-Bold.ttf",
        "/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf",
    ],
    "regular": [
        "docs/design/fonts/Inter-Medium.ttf",
        "C:/Windows/Fonts/segoeui.ttf",
        "C:/Windows/Fonts/arial.ttf",
        "/usr/share/fonts/truetype/inter/Inter-Medium.ttf",
        "/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf",
    ],
}

ROOT = Path(__file__).resolve().parents[2]
OUT = Path(__file__).resolve().parent


def load_font(weight: str, size: float) -> ImageFont.FreeTypeFont:
    for candidate in FONT_CANDIDATES[weight]:
        path = Path(candidate)
        if not path.is_absolute():
            path = ROOT / path
        if path.exists():
            return ImageFont.truetype(str(path), int(round(size)))
    raise FileNotFoundError(f"Aucune police disponible pour la graisse '{weight}'.")


def _sweep_passes_bottom(start: float, end: float) -> bool:
    """Le point bas du cercle est a theta = pi/2 (axe y oriente vers le bas)."""
    while end <= start:
        end += 2 * math.pi
    bottom = math.pi / 2
    while bottom < start:
        bottom += 2 * math.pi
    return bottom <= end


def droplet_polygon(steps: int = 720) -> list[tuple[float, float]]:
    """Goutte = sommet + deux tangentes vers le cercle inferieur + arc."""
    ax, ay = APEX
    cx, cy = CENTER
    d = math.hypot(cx - ax, cy - ay)
    base = math.atan2(cy - ay, cx - ax)
    spread = math.asin(RADIUS / d)
    reach = math.sqrt(d * d - RADIUS * RADIUS)

    tangents = []
    for sign in (1, -1):
        angle = base + sign * spread
        px = ax + math.cos(angle) * reach
        py = ay + math.sin(angle) * reach
        tangents.append(math.atan2(py - cy, px - cx))

    # Retenir l'arc qui passe par le bas du cercle, pas le raccourci par le haut.
    start, end = tangents
    if not _sweep_passes_bottom(start, end):
        start, end = end, start
    while end <= start:
        end += 2 * math.pi

    points = [APEX]
    for i in range(steps + 1):
        theta = start + (end - start) * i / steps
        points.append((cx + RADIUS * math.cos(theta), cy + RADIUS * math.sin(theta)))
    return points


def draw_mark(
    draw: ImageDraw.ImageDraw,
    scale: float,
    origin: tuple[float, float] = (0.0, 0.0),
    fill: tuple[int, int, int, int] = BLUE,
    pulse: tuple[int, int, int, int] = WHITE,
) -> None:
    ox, oy = origin

    def place(point: tuple[float, float]) -> tuple[float, float]:
        return ((ox + point[0]) * scale, (oy + point[1]) * scale)

    draw.polygon([place(p) for p in droplet_polygon()], fill=fill)

    width = max(1, round(PULSE_WIDTH * scale))
    scaled = [place(p) for p in PULSE]
    draw.line(scaled, fill=pulse, width=width, joint="curve")
    for x, y in scaled:  # terminaisons arrondies
        r = width / 2
        draw.ellipse((x - r, y - r, x + r, y + r), fill=pulse)


def render_mark(size: int, supersample: int = 4) -> Image.Image:
    scale = size * supersample / VIEWBOX
    canvas = Image.new("RGBA", (size * supersample,) * 2, (0, 0, 0, 0))
    draw_mark(ImageDraw.Draw(canvas), scale)
    return canvas.resize((size, size), Image.LANCZOS)


def render_logo(width: int, supersample: int = 3, inverse: bool = False) -> Image.Image:
    scale = width * supersample / LOCKUP[0]
    size = (int(LOCKUP[0] * scale), int(LOCKUP[1] * scale))
    canvas = Image.new("RGBA", size, (0, 0, 0, 0))
    draw = ImageDraw.Draw(canvas)

    mark_fill, pulse_fill = (WHITE, NIGHT) if inverse else (BLUE, WHITE)
    prefix_color = WHITE if inverse else DEEP
    suffix_color = WHITE if inverse else BLUE
    tagline_color = LIGHT_GREY if inverse else GREY

    draw_mark(draw, scale, origin=MARK_ORIGIN, fill=mark_fill, pulse=pulse_fill)

    wordmark = load_font("bold", WORDMARK_SIZE * scale)
    tagline = load_font("regular", TAGLINE_SIZE * scale)

    x = WORDMARK_POS[0] * scale
    baseline = WORDMARK_POS[1] * scale
    draw.text((x, baseline), "Aqua", font=wordmark, fill=prefix_color, anchor="ls")
    x += draw.textlength("Aqua", font=wordmark)
    draw.text((x, baseline), "Sensus", font=wordmark, fill=suffix_color, anchor="ls")

    draw.text(
        (TAGLINE_POS[0] * scale, TAGLINE_POS[1] * scale),
        TAGLINE,
        font=tagline,
        fill=tagline_color,
        anchor="ls",
    )

    return canvas.resize((width, int(width * LOCKUP[1] / LOCKUP[0])), Image.LANCZOS)


if __name__ == "__main__":
    # Logo par defaut de toutes les applications AquaSensus.
    render_logo(1280).save(OUT / "aquasensus-logo.png")
    render_logo(640).save(OUT / "aquasensus-logo-640.png")
    render_logo(1280, inverse=True).save(OUT / "aquasensus-logo-inverse.png")

    # Symbole seul : favicon, manifeste PWA, icone adaptative Android.
    render_mark(512).save(OUT / "aquasensus-mark.png")
    for px in (16, 32, 192, 512):
        render_mark(px).save(OUT / f"aquasensus-mark-{px}.png")

    used = load_font("bold", 28).getname()
    print(f"Rasters ecrits dans {OUT}")
    print(f"Police du nom : {' '.join(used)}")
