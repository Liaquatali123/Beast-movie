import urllib.request
import re

js_urls = [
    "https://h5-static.aoneroom.com/ssrStatic/movieboxco/public/_nuxt/B9dpMIM0.js",
    "https://h5-static.aoneroom.com/ssrStatic/movieboxco/public/_nuxt/D3bojDws.js",
    "https://h5-static.aoneroom.com/ssrStatic/movieboxco/public/_nuxt/DlAUqK2U.js",
    "https://h5-static.aoneroom.com/ssrStatic/movieboxco/public/_nuxt/BIl4cyR9.js"
]

for url in js_urls:
    try:
        req = urllib.request.Request(url, headers={"User-Agent": "Mozilla/5.0"})
        with urllib.request.urlopen(req) as r:
            js = r.read().decode("utf-8")
            print(f"Downloaded {url.split('/')[-1]} ({len(js)} bytes)")
            # search for api paths
            matches = re.findall(r'["\'](/wefeed-h5api-bff/[^"\']+)["\']', js)
            if matches:
                print("  APIs:", set(matches))
            # search for play
            play_ctx = [m.start() for m in re.finditer(r'/subject/play', js)]
            for pos in play_ctx:
                snippet = js[max(0, pos-80):min(len(js), pos+120)]
                print("  Play snippet:", snippet)
            # search for play?
            play_q = [m.start() for m in re.finditer(r'subject/play\?', js)]
            for pos in play_q:
                snippet = js[max(0, pos-80):min(len(js), pos+120)]
                print("  Play? snippet:", snippet)
    except Exception as e:
        print("Err:", e)
