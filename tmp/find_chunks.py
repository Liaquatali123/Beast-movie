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

# Find all _nuxt chunk names
chunks = re.findall(r'/ssrStatic/movieboxco/public/_nuxt/([a-zA-Z0-9_\-\.]+)\.js', html)
print("Chunks mentioned in HTML:", set(chunks))

# In DLq7l5jf.js, let us find chunk mapping or import() statements
r = urllib.request.Request("https://h5-static.aoneroom.com/ssrStatic/movieboxco/public/_nuxt/DLq7l5jf.js", headers={"User-Agent": "Mozilla/5.0"})
with urllib.request.urlopen(r) as resp:
    main_js = resp.read().decode("utf-8")

all_js = re.findall(r'["\']([a-zA-Z0-9_\-]+\.js)["\']', main_js)
print("Additional JS files referenced:", len(all_js), all_js[:20])

# Let us search for subject detail or movie detail endpoint in main_js
bffs_in_main = re.findall(r'(/wefeed-h5api-bff/[^"\'`\s\?]+)', main_js)
print("All bffs in main:", set(bffs_in_main))
