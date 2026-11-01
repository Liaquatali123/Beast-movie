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

# 1. First get detail of The Terminal List to find its detailPath
url_d = f"{base_url}wefeed-h5api-bff/detail?subjectId=6377932688309244960&host=movie-box.co"
req_d = urllib.request.Request(url_d, headers=headers)
with urllib.request.urlopen(req_d) as resp:
    raw = resp.read()
    if resp.headers.get("content-encoding") == "gzip":
        raw = gzip.decompress(raw)
    data_d = json.loads(raw.decode("utf-8"))

sub = data_d.get("data", {}).get("subject", {})
detail_path = sub.get("detailPath")
print("Terminal List detailPath:", detail_path)

# 2. Now call play with se=1, ep=1, detailPath, streamSignType=1
url_p = f"{base_url}wefeed-h5api-bff/subject/play?subjectId=6377932688309244960&se=1&ep=1&detailPath={detail_path}&streamSignType=1&host=movie-box.co"
req_p = urllib.request.Request(url_p, headers=headers)
with urllib.request.urlopen(req_p) as resp:
    raw = resp.read()
    if resp.headers.get("content-encoding") == "gzip":
        raw = gzip.decompress(raw)
    data_p = json.loads(raw.decode("utf-8"))

print("PLAY RESPONSE:")
d = data_p.get("data", {})
print("Streams:", len(d.get("streams", [])), "HLS:", len(d.get("hls", [])), "DASH:", len(d.get("dash", [])))
print("hasResource:", d.get("hasResource"), "limited:", d.get("limited"), "vipLocked:", d.get("vipLocked"))
for s in d.get("streams", []):
    print("Stream:", s.get("resolutions"), s.get("url")[:80])
for h in d.get("hls", []):
    print("HLS:", h.get("resolutions"), h.get("url")[:80])
for da in d.get("dash", []):
    print("DASH:", da.get("resolutions"), da.get("url")[:80])

# Also check download
url_dl = f"{base_url}wefeed-h5api-bff/subject/download?subjectId=6377932688309244960&se=1&ep=1&detailPath={detail_path}&host=movie-box.co"
req_dl = urllib.request.Request(url_dl, headers=headers)
with urllib.request.urlopen(req_dl) as resp:
    raw = resp.read()
    if resp.headers.get("content-encoding") == "gzip":
        raw = gzip.decompress(raw)
    data_dl = json.loads(raw.decode("utf-8"))
print("DOWNLOAD RESPONSE downloads:", len(data_dl.get("data", {}).get("downloads", [])))
for dl in data_dl.get("data", {}).get("downloads", []):
    print("Download item:", dl.get("resolutions"), dl.get("url"))
