with open("tmp/inspect_movie_page.py") as f:
    pass

import urllib.request
import re

req = urllib.request.Request(
    "https://movie-box.co/movies/forgotten-island-67AJfvji0o",
    headers={
        "User-Agent": "Mozilla/5.0 (Linux; Android 11; vivo 1951 Build/RP1A.200720.012) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/154.0.8037.107 Mobile Safari/537.36"
    }
)
with urllib.request.urlopen(req) as resp:
    html = resp.read().decode("utf-8")

# Let us find video tags, player elements, or window.__NUXT__ keys
mp4s = re.findall(r'https?://[^\s"\'`)]+\.mp4[^\s"\'`)]*', html)
print("MP4s found in HTML:", len(mp4s))
for m in mp4s[:10]:
    print(" ", m)

# Let us check any json blocks
scripts = re.findall(r'<script[^>]*>(.*?)</script>', html, re.DOTALL)
for i, s in enumerate(scripts):
    if "macdn.aoneroom.com" in s or "play" in s or "video" in s:
        print(f"Script #{i} length {len(s)}:")
        # print first 500 chars
        print(s[:500])
