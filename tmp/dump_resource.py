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

url = f"{base_url}wefeed-h5api-bff/detail?subjectId=324954574010339504&host=movie-box.co"
req = urllib.request.Request(url, headers=headers)
with urllib.request.urlopen(req) as resp:
    raw = resp.read()
    if resp.headers.get("content-encoding") == "gzip":
        raw = gzip.decompress(raw)
    data = json.loads(raw.decode("utf-8"))

d = data.get("data", {})
res = d.get("resource", {})
print("RESOURCE KEYS:", list(res.keys()))
for k, v in res.items():
    if isinstance(v, list):
        print(f"  {k} (list len {len(v)}):", v[:2])
    elif isinstance(v, dict):
        print(f"  {k} (dict keys):", list(v.keys()))
    else:
        print(f"  {k}: {v}")

# Also test for series (The Terminal List)
url_s = f"{base_url}wefeed-h5api-bff/detail?subjectId=6377932688309244960&host=movie-box.co"
req_s = urllib.request.Request(url_s, headers=headers)
with urllib.request.urlopen(req_s) as resp:
    raw = resp.read()
    if resp.headers.get("content-encoding") == "gzip":
        raw = gzip.decompress(raw)
    data_s = json.loads(raw.decode("utf-8"))

print("\nSERIES RESOURCE KEYS:")
res_s = data_s.get("data", {}).get("resource", {})
print(list(res_s.keys()))
for k, v in res_s.items():
    if isinstance(v, list):
        print(f"  {k} (list len {len(v)}):", v[:2])
    elif isinstance(v, dict):
        print(f"  {k} (dict keys):", list(v.keys()))
    else:
        print(f"  {k}: {v}")
