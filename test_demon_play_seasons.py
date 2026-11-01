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

sub_id = "7231184498325966096"
detail_path = "demon-slayer-kimetsu-no-yaiba-english-cK8E2dUTaC8"

# Test different se and ep combinations for Demon Slayer
for se in [1, 2, 3]:
    for ep in [1, 2]:
        play_url = f"{base_url}wefeed-h5api-bff/subject/play?subjectId={sub_id}&se={se}&ep={ep}&detailPath={detail_path}&host=movie-box.co"
        req = urllib.request.Request(play_url, headers=headers, method="GET")
        try:
            with urllib.request.urlopen(req) as resp:
                raw = resp.read()
                if resp.headers.get("content-encoding") == "gzip":
                    raw = gzip.decompress(raw)
                data = json.loads(raw.decode("utf-8"))
                streams = data.get("data", {}).get("streams", [])
                hls = data.get("data", {}).get("hls", [])
                dash = data.get("data", {}).get("dash", [])
                hasRes = data.get("data", {}).get("hasResource")
                print(f"Se {se} Ep {ep} -> hasResource: {hasRes}, streams: {len(streams)}, hls: {len(hls)}, dash: {len(dash)}")
                if streams or hls or dash:
                    print(f"  FOUND STREAM URL!")
        except Exception as e:
            print(f"Se {se} Ep {ep} -> Error: {e}")
