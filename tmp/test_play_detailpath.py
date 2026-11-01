import urllib.request
import json

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
    "authorization": token
}

m_id = "324954574010339504" # Forgotten Island
detail_path = "forgotten-island-67AJfvji0o"

# Test /subject/play with detailPath and streamSignType=0
url1 = f"{base_url}wefeed-h5api-bff/subject/play?subjectId={m_id}&se=0&ep=0&detailPath={detail_path}&streamSignType=0&host=movie-box.co"
req1 = urllib.request.Request(url1, headers=headers)
with urllib.request.urlopen(req1) as resp:
    data1 = json.loads(resp.read().decode("utf-8"))

print("=== /subject/play with detailPath & streamSignType=0 ===")
print("Code:", data1.get("code"), "Msg:", data1.get("message"))
d = data1.get("data", {})
print("Streams:", len(d.get("streams", [])), "HLS:", len(d.get("hls", [])), "DASH:", len(d.get("dash", [])))
if d.get("streams"):
    print("Stream 0:", d["streams"][0])
if d.get("hls"):
    print("HLS 0:", d["hls"][0])
if d.get("dash"):
    print("DASH 0:", d["dash"][0])

# Also test /subject/download:
url_dl = f"{base_url}wefeed-h5api-bff/subject/download?subjectId={m_id}&se=0&ep=0&detailPath={detail_path}&host=movie-box.co"
req_dl = urllib.request.Request(url_dl, headers=headers)
with urllib.request.urlopen(req_dl) as resp:
    data_dl = json.loads(resp.read().decode("utf-8"))
print("\n=== /subject/download ===")
print("DL data keys:", list(data_dl.get("data", {}).keys()))
downloads = data_dl.get("data", {}).get("downloads", [])
print("Downloads count:", len(downloads))
for dl in downloads:
    print(f"  DL: res={dl.get('resolutions')}, url={dl.get('url')[:60]}..., size={dl.get('size')}, id={dl.get('id')}")
