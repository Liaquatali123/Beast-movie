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

search_url = base_url + "wefeed-h5api-bff/subject/search?host=movie-box.co"
req_data = json.dumps({"keyword": "Demon Slayer", "page": 1, "perPage": 5}).encode("utf-8")
req = urllib.request.Request(search_url, data=req_data, headers={**headers, "content-type": "application/json"}, method="POST")
with urllib.request.urlopen(req) as resp:
    raw = resp.read()
    if resp.headers.get("content-encoding") == "gzip":
        raw = gzip.decompress(raw)
    data = json.loads(raw.decode("utf-8"))
    items = data.get("data", {}).get("items", [])
    print(f"Found {len(items)} items for Demon Slayer")
    for item in items:
        print(f"ID: {item.get('subjectId')}, Title: {item.get('title')}, detailPath: {item.get('detailPath')}")
        sub_id = item.get('subjectId')
        d_path = item.get('detailPath', '')
        play_url = f"{base_url}wefeed-h5api-bff/subject/play?subjectId={sub_id}&se=1&ep=1&detailPath={d_path}&host=movie-box.co"
        req2 = urllib.request.Request(play_url, headers=headers, method="GET")
        try:
            with urllib.request.urlopen(req2) as resp2:
                raw2 = resp2.read()
                if resp2.headers.get("content-encoding") == "gzip":
                    raw2 = gzip.decompress(raw2)
                p_data = json.loads(raw2.decode("utf-8"))
                print(f"Play response for {item.get('title')}:", json.dumps(p_data, indent=2))
        except Exception as e:
            print(f"Play error: {e}")
        break
