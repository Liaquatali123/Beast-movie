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

# Let us find all endpoints in DLq7l5jf.js or any other script
scripts = re.findall(r'<script[^>]+src="([^"]+)"', html)
for s_url in scripts:
    print("Checking script:", s_url)
    r = urllib.request.Request(s_url, headers={"User-Agent": "Mozilla/5.0"})
    with urllib.request.urlopen(r) as resp:
        content = resp.read().decode("utf-8")
        bffs = re.findall(r'["\'](/wefeed-h5api-bff/[a-zA-Z0-9_\-\/]+)["\']', content)
        print("BFF endpoints:", set(bffs))
