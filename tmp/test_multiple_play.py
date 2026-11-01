import urllib.request
import urllib.error
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

# Let us check 20 different movies from home to see if any have streams in /subject/play
req = urllib.request.Request(base_url + "wefeed-h5api-bff/home?host=movie-box.co", headers=headers)
with urllib.request.urlopen(req) as resp:
    home_data = json.loads(resp.read().decode("utf-8"))

subjects = []
for op in home_data.get("data", {}).get("operatingList", []):
    for sub in op.get("subjects", []):
        subjects.append(sub)

print(f"Testing play for multiple subjects...")
for s in subjects[:15]:
    sid = s.get("subjectId")
    title = s.get("title")
    stype = s.get("subjectType")
    # try GET
    url = f"{base_url}wefeed-h5api-bff/subject/play?subjectId={sid}&host=movie-box.co"
    try:
        r = urllib.request.Request(url, headers=headers)
        with urllib.request.urlopen(r) as resp:
            d = json.loads(resp.read().decode("utf-8"))
            data = d.get("data", {})
            sc = len(data.get("streams", []))
            hc = len(data.get("hls", []))
            dc = len(data.get("dash", []))
            if sc > 0 or hc > 0 or dc > 0:
                print(f"FOUND STREAM! {title} (type {stype}): streams={sc}, hls={hc}, dash={dc}")
                print("  Stream sample:", data.get("streams", [])[0] if sc else (data.get("hls") or data.get("dash")))
            else:
                pass
    except Exception as e:
        print(f"Err {title}: {e}")
