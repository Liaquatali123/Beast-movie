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
url = f"{base_url}wefeed-h5api-bff/subject/play?subjectId={m_id}&host=movie-box.co"
req = urllib.request.Request(url, headers=headers)
with urllib.request.urlopen(req) as resp:
    play_data = json.loads(resp.read().decode("utf-8"))

print("=== MOVIE PLAY RESPONSE ===")
data = play_data.get("data", {})
print("Top-level keys:", list(data.keys()))
print("limited:", data.get("limited"), "limitedCode:", data.get("limitedCode"), "freeNum:", data.get("freeNum"), "vipLocked:", data.get("vipLocked"))

streams = data.get("streams", [])
print(f"Streams count: {len(streams)}")
for i, s in enumerate(streams):
    print(f"Stream #{i}:")
    for k in ["id", "url", "resolutions", "format", "quality", "duration", "audioTrackList", "subtitleList", "fps", "bitrate", "size", "sign"]:
        if k in s:
            print(f"  {k}: {s.get(k)}")

print("hls:", data.get("hls"))
print("dash:", data.get("dash"))

# Now let us check series: how to get episodes and play episode?
# Let us check series endpoints:
s_id = "6377932688309244960" # The Terminal List
url_s = f"{base_url}wefeed-h5api-bff/subject/play?subjectId={s_id}&se=1&ep=1&host=movie-box.co"
req_s = urllib.request.Request(url_s, headers=headers)
with urllib.request.urlopen(req_s) as resp:
    s_play = json.loads(resp.read().decode("utf-8"))
print("=== SERIES PLAY RESPONSE (se=1&ep=1) ===")
print("Series play data keys:", list(s_play.get("data", {}).keys()))
if s_play.get("data", {}).get("streams"):
    print("Series streams[0] sample:", list(s_play["data"]["streams"][0].keys()))
    for k in ["url", "duration", "audioTrackList", "subtitleList", "resolutions"]:
        if k in s_play["data"]["streams"][0]:
            print(f"  {k}: {s_play['data']['streams'][0].get(k)}")
