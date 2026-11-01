import urllib.request
import re

url = "https://h5-static.aoneroom.com/ssrStatic/movieboxco/public/_nuxt/C9OOlBFS.js"
r = urllib.request.Request(url, headers={"User-Agent": "Mozilla/5.0"})
with urllib.request.urlopen(r) as resp:
    code = resp.read().decode("utf-8")

pos = code.find("/subject/play")
print(code[max(0, pos-400):min(len(code), pos+800)])
