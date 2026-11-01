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

req = urllib.request.Request(base_url + "wefeed-h5api-bff/home?host=movie-box.co", headers=headers)
with urllib.request.urlopen(req) as resp:
    home_data = json.loads(resp.read().decode("utf-8"))

subjects = []
for op in home_data.get("data", {}).get("operatingList", []):
    for sub in op.get("subjects", []):
        subjects.append(sub)

movie = next(s for s in subjects if s.get("subjectType") == 1)
series = next(s for s in subjects if s.get("subjectType") == 2)
m_id = movie.get("subjectId")
s_id = series.get("subjectId")

print("Movie:", movie.get("title"), "id:", m_id, "detailPath:", movie.get("detailPath"))
print("Movie raw keys and sample:")
for k in ["duration", "trailer", "detailPath", "subtitles", "dubs", "season", "hasResource"]:
    print(f"  {k}: {movie.get(k)}")

# Also let's check what endpoints movie-box.co uses on web!
# When visiting movie-box.co, what does detailPath or watching a movie hit?
endpoints = [
    f"wefeed-h5api-bff/subject/detail?subjectId={m_id}&host=movie-box.co",
    f"wefeed-h5api-bff/subject/detail?id={m_id}&host=movie-box.co",
    f"wefeed-h5api-bff/subject/info?subjectId={m_id}&host=movie-box.co",
    f"wefeed-h5api-bff/subject/play?subjectId={m_id}&host=movie-box.co",
    f"wefeed-h5api-bff/subject/play-sources?subjectId={m_id}&host=movie-box.co",
    f"wefeed-h5api-bff/subject/sources?subjectId={m_id}&host=movie-box.co",
    f"wefeed-h5api-bff/subject/resource?subjectId={m_id}&host=movie-box.co",
    f"wefeed-h5api-bff/subject/resources?subjectId={m_id}&host=movie-box.co",
    f"wefeed-h5api-bff/subject/get-resource?subjectId={m_id}&host=movie-box.co",
    f"wefeed-h5api-bff/subject/seasons?subjectId={s_id}&host=movie-box.co",
    f"wefeed-h5api-bff/subject/episodes?subjectId={s_id}&host=movie-box.co",
    f"wefeed-h5api-bff/subject/episode-list?subjectId={s_id}&host=movie-box.co",
    f"wefeed-h5api-bff/subject/season-list?subjectId={s_id}&host=movie-box.co",
    f"wefeed-h5api-bff/subject/stream?subjectId={m_id}&host=movie-box.co",
    f"wefeed-h5api-bff/subject/get-play-info?subjectId={m_id}&host=movie-box.co",
    f"wefeed-h5api-bff/media/play-info?subjectId={m_id}&host=movie-box.co",
    f"wefeed-h5api-bff/media/stream?subjectId={m_id}&host=movie-box.co",
    f"wefeed-h5api-bff/subject/detail-sources?subjectId={m_id}&host=movie-box.co",
    f"wefeed-h5api-bff/subject/detail-season-list?subjectId={s_id}&host=movie-box.co"
]

for ep in endpoints:
    try:
        r = urllib.request.Request(base_url + ep, headers=headers)
        with urllib.request.urlopen(r) as resp:
            data = json.loads(resp.read().decode("utf-8"))
            print(f"SUCCESS {ep} -> code: {data.get('code')}")
            if data.get('data'):
                print("  data keys:", list(data['data'].keys()) if isinstance(data['data'], dict) else type(data['data']))
    except urllib.error.HTTPError as e:
        if e.code != 404:
            print(f"HTTP {e.code} for {ep}")
    except Exception as e:
        print(f"ERR {ep}: {e}")
