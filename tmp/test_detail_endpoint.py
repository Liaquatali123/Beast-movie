import urllib.request
import json
import gzip

base_url = "https://h5-api.aoneroom.com/"
token = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1aWQiOjcyOTgzNzU2MDM0MzU0OTI5MTIsImF0cCI6MywiZXh0IjoiMTc5MjI1NTMwMSIsImV4cCI6MTgwMDAzMTMwMSwiaWF0IjoxNzkyMjU1MDAxfQ.yebzJAiss-jxfhberxN1Zeb12D079M9yYoFyPiUdF_g"
headers = {
    "user-agent": "Mozilla/5.0 (Linux; Android 11; vivo 1951 Build/RP1A.200720.012) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/154.0.8037.107 Mobile Safari/537.36",
    "referer": "https://movie-box.co/",
    "origin": "https://movie-box.co",
    "accept": "application/json",
    "x-u-aid": "2f1e4a25-3cd1-46b3-96e8-10d8d2723039",
    "x-client-info": '{"timezone":"America/New_York"}',
    "x-request-lang": "en",
    "x-vip-restrict": "1",
    "x-no-high-risk-restrict": "0",
    "authorization": token,
    "accept-encoding": "gzip"
}

# Test /wefeed-h5api-bff/detail with various params
tests = [
    "wefeed-h5api-bff/detail?detailPath=forgotten-island-67AJfvji0o&host=movie-box.co",
    "wefeed-h5api-bff/detail?subjectId=324954574010339504&host=movie-box.co",
    "wefeed-h5api-bff/detail?id=324954574010339504&host=movie-box.co",
    "wefeed-h5api-bff/subject/detail?detailPath=forgotten-island-67AJfvji0o&host=movie-box.co",
    "wefeed-h5api-bff/subject/detail?subjectId=324954574010339504&host=movie-box.co",
]

for t in tests:
    try:
        req = urllib.request.Request(base_url + t, headers=headers)
        with urllib.request.urlopen(req) as resp:
            raw = resp.read()
            if resp.headers.get("content-encoding") == "gzip":
                raw = gzip.decompress(raw)
            data = json.loads(raw.decode("utf-8"))
            print(f"SUCCESS {t} -> Code: {data.get('code')}")
            if data.get("data"):
                d = data["data"]
                print("  Keys:", list(d.keys()) if isinstance(d, dict) else type(d))
                # look for resource or streams
                for k in ["resource", "streams", "subject", "downloads", "epList", "seasonList"]:
                    if k in d:
                        print(f"    {k}: {type(d[k])}")
    except urllib.error.HTTPError as e:
        print(f"HTTP {e.code} for {t}")
    except Exception as e:
        print(f"ERR {t}: {e}")
