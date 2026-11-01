import urllib.request
import re

for chunk in ['DtXhuNIC', 'C9OOlBFS', 'dgZhthLs']:
    url = f"https://h5-static.aoneroom.com/ssrStatic/movieboxco/public/_nuxt/{chunk}.js"
    r = urllib.request.Request(url, headers={"User-Agent": "Mozilla/5.0"})
    with urllib.request.urlopen(r) as resp:
        code = resp.read().decode("utf-8")
        print(f"\n=================== {chunk}.js ===================")
        for ep in ['/detail', '/subject/play', '/subject/caption', '/subject/download']:
            for m in re.finditer(re.escape(ep), code):
                pos = m.start()
                start = max(0, pos - 300)
                end = min(len(code), pos + 400)
                print(f"--- MATCH {ep} ---")
                print(code[start:end])
