import urllib.request
import re

chunks = ['DtXhuNIC', 'DhIEUwo4', 'B4R0QfrL', 'B9dpMIM0', 'CMg-wuhF', 'D1Ffo8tH', 'CK0xgBL7', 'BIl4cyR9', 'lUWqs3Ra', 'CpP1Oekm', 'C0xLxHQ9', 'BxtYpgve', 'BFnG_YRJ', 'BxV2FMNE', 'BtjSVPGn', 'BJ98Y55J', 'BoffzofY', 'C5XbmQ38', 'DlAUqK2U', 'kpE16meZ', 'j1MBGrJO', 'C5uQ6hsT', 'XVmepSSf', 'D3bojDws', 'C9OOlBFS', 'dgZhthLs', 'uovGEL-L', 'NS6JTQ3y', 'C64z3ONE', 'T-uMGqEm', 'DojM1zFk', 'Bebe14kg', 'Qxg1FapD']

found_bffs = set()
for c in chunks:
    url = f"https://h5-static.aoneroom.com/ssrStatic/movieboxco/public/_nuxt/{c}.js"
    try:
        r = urllib.request.Request(url, headers={"User-Agent": "Mozilla/5.0"})
        with urllib.request.urlopen(r) as resp:
            code = resp.read().decode("utf-8")
            matches = re.findall(r'(/wefeed-h5api-bff/[a-zA-Z0-9_\-\/]+)', code)
            if matches:
                found_bffs.update(matches)
                print(f"In {c}: {set(matches)}")
            # search for play or stream
            if "play" in code.lower() or "video" in code.lower() or "player" in code.lower():
                pass
    except Exception as e:
        pass

print("\nALL FOUND BFFs:")
for b in sorted(found_bffs):
    print(" ", b)
