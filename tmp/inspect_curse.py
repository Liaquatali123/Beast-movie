import urllib.request
import re

url = "https://h5-static.aoneroom.com/ssrStatic/movieboxco/public/_nuxt/dgZhthLs.js"
r = urllib.request.Request(url, headers={"User-Agent": "Mozilla/5.0"})
with urllib.request.urlopen(r) as resp:
    code = resp.read().decode("utf-8")

for m in re.finditer(r'curSe', code):
    pos = m.start()
    print("curSe snippet:", code[max(0, pos-150):min(len(code), pos+200)])
