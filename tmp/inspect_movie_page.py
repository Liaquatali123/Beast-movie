import urllib.request
import re

req = urllib.request.Request(
    "https://movie-box.co/movies/forgotten-island-67AJfvji0o",
    headers={
        "User-Agent": "Mozilla/5.0 (Linux; Android 11; vivo 1951 Build/RP1A.200720.012) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/154.0.8037.107 Mobile Safari/537.36"
    }
)
try:
    with urllib.request.urlopen(req) as resp:
        html = resp.read().decode("utf-8")
        print("HTML length:", len(html))
        # Look for script tags
        scripts = re.findall(r'<script[^>]+src="([^"]+)"', html)
        print("Scripts on movie page:", len(scripts))
        for s in scripts:
            print(" ", s)
        # Check window.__NUXT__ or initial data
        nuxt_match = re.search(r'window\.__NUXT__\s*=\s*(.*?);</script>', html, re.DOTALL)
        if nuxt_match:
            print("Found NUXT data length:", len(nuxt_match.group(1)))
            # search for mp4 or m3u8 or streams or api calls
            streams = re.findall(r'https?://[^\s"\'`)]+\.(?:mp4|m3u8)[^\s"\'`)]*', nuxt_match.group(1))
            print("Streams in NUXT:", streams[:5])
        # Find all api occurrences
        apis = re.findall(r'(/wefeed-h5api-bff/[^"\'`\s\?]+)', html)
        print("APIs in page:", set(apis))
except Exception as e:
    print("Err:", e)
