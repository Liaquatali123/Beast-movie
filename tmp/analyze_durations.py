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

req = urllib.request.Request(base_url + "wefeed-h5api-bff/home?host=movie-box.co", headers=headers)
with urllib.request.urlopen(req) as resp:
    raw = resp.read()
    if resp.headers.get("content-encoding") == "gzip":
        raw = gzip.decompress(raw)
    home_data = json.loads(raw.decode("utf-8"))

subjects = []
for op in home_data.get("data", {}).get("operatingList", []):
    for sub in op.get("subjects", []):
        subjects.append(sub)

durations = []
for s in subjects:
    trailer = s.get("trailer") or {}
    va = trailer.get("videoAddress") or {}
    dur = va.get("duration", 0)
    durations.append((dur, s.get("title"), s.get("subjectId"), s.get("subjectType"), s.get("detailPath"), va.get("url")))

durations.sort(reverse=True)
print(f"Total subjects: {len(subjects)}")
print("Top 15 longest durations in trailer/videoAddress:")
for d, title, sid, stype, dp, url in durations[:15]:
    print(f"  {d}s ({d//60}m {d%60}s) - {title} (id: {sid}, type: {stype})")
    print(f"    url: {url}")
