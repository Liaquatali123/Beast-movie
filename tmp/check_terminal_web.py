import urllib.request
import re

req = urllib.request.Request(
    "https://movie-box.co/movies/the-terminal-list-YMJKCBXZ8B7",
    headers={
        "User-Agent": "Mozilla/5.0 (Linux; Android 11; vivo 1951 Build/RP1A.200720.012) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/154.0.8037.107 Mobile Safari/537.36"
    }
)
with urllib.request.urlopen(req) as resp:
    html = resp.read().decode("utf-8")

print("Length:", len(html))
# search for mp4 or stream or play
mp4s = re.findall(r'https?://[^\s"\'`)]+\.mp4[^\s"\'`)]*', html)
print("MP4s in Terminal List HTML:", mp4s)

# check script data
scripts = re.findall(r'<script[^>]*>(.*?)</script>', html, re.DOTALL)
for s in scripts:
    if "trailer" in s:
        m = re.findall(r'"trailer"\s*:\s*\{.*?\}', s)
        print("Trailer in script:", m[:2])
    if "curEp" in s:
        print("curEp in script snippet:", s[s.find("curEp")-50:s.find("curEp")+200])
