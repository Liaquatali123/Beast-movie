import urllib.request
import re

req = urllib.request.Request(
    "https://movie-box.co/",
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
        print("Scripts found:", len(scripts))
        for s in scripts:
            print(" ", s)
        # Also look for any inline config or api routes
        apis = re.findall(r'https?://[a-zA-Z0-9.-]+\.aoneroom\.com[^\s"\'`)]*', html)
        print("Aoneroom urls in HTML:", set(apis))
except Exception as e:
    print("Err loading movie-box.co:", e)
