import urllib.request
import re

for chunk in ['DtXhuNIC', 'dgZhthLs']:
    url = f"https://h5-static.aoneroom.com/ssrStatic/movieboxco/public/_nuxt/{chunk}.js"
    r = urllib.request.Request(url, headers={"User-Agent": "Mozilla/5.0"})
    with urllib.request.urlopen(r) as resp:
        code = resp.read().decode("utf-8")
        # search for /subject/play
        for m in re.finditer(r'/subject/play', code):
            pos = m.start()
            print(f"Match in {chunk}:", code[max(0, pos-200):min(len(code), pos+200)])
