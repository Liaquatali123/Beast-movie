import urllib.request
import json
import gzip

base_url = "https://h5-api.aoneroom.com/"
token = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1aWQiOjcyOTgzNzU2MDM0MzU0OTI5MTIsImF0cCI6MywiZXh0IjoiMTc5MjI1NTMwMSIsImV4cCI6MTgwMDAzMTMwMSwiaWF0IjoxNzkyMjU1MDAxfQ.yebzJAiss-jxfhberxN1Zeb12D079M9yYoFyPiUdF_g"

for vip in ["0", "1"]:
    for high_risk in ["0", "1"]:
        headers = {
            "user-agent": "Mozilla/5.0 (Linux; Android 11; vivo 1951 Build/RP1A.200720.012) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/154.0.8037.107 Mobile Safari/537.36",
            "referer": "https://movie-box.co/",
            "origin": "https://movie-box.co",
            "accept": "application/json",
            "x-u-aid": "2f1e4a25-3cd1-46b3-96e8-10d8d2723039",
            "x-client-info": '{"timezone":"America/New_York"}',
            "x-request-lang": "en",
            "x-vip-restrict": vip,
            "x-no-high-risk-restrict": high_risk,
            "authorization": token,
            "accept-encoding": "gzip"
        }
        url = f"{base_url}wefeed-h5api-bff/subject/play?subjectId=324954574010339504&se=0&ep=0&detailPath=forgotten-island-67AJfvji0o&host=movie-box.co"
        try:
            req = urllib.request.Request(url, headers=headers)
            with urllib.request.urlopen(req) as resp:
                raw = resp.read()
                if resp.headers.get("content-encoding") == "gzip":
                    raw = gzip.decompress(raw)
                d = json.loads(raw.decode("utf-8")).get("data", {})
                sc = len(d.get("streams", []))
                print(f"vip={vip}, hr={high_risk} -> streams: {sc}, limited: {d.get('limited')}, vipLocked: {d.get('vipLocked')}")
        except Exception as e:
            print(f"vip={vip}, hr={high_risk} -> {e}")
