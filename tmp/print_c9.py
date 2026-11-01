import urllib.request

url = "https://h5-static.aoneroom.com/ssrStatic/movieboxco/public/_nuxt/C9OOlBFS.js"
r = urllib.request.Request(url, headers={"User-Agent": "Mozilla/5.0"})
with urllib.request.urlopen(r) as resp:
    code = resp.read().decode("utf-8")

print(code)
