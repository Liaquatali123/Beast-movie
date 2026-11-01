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

endpoints = [
    ("GET", "wefeed-h5api-bff/home?host=movie-box.co"),
    ("GET", "wefeed-h5api-bff/subject/trending?page=1&perPage=10&host=movie-box.co"),
    ("GET", "wefeed-h5api-bff/subject/everyone-search?host=movie-box.co"),
    ("GET", "wefeed-h5api-bff/detail?subjectId=324954574010339504&host=movie-box.co"), # Forgotten Island
    ("GET", "wefeed-h5api-bff/detail?subjectId=6377932688309244960&host=movie-box.co"), # The Terminal List
    ("GET", "wefeed-h5api-bff/subject/play?subjectId=324954574010339504&se=0&ep=0&detailPath=forgotten-island-67AJfvji0o&host=movie-box.co"),
    ("GET", "wefeed-h5api-bff/subject/play?subjectId=6377932688309244960&se=1&ep=1&detailPath=the-terminal-list-YMJKCBXZ8B7&host=movie-box.co"),
    ("GET", "wefeed-h5api-bff/subject/download?subjectId=324954574010339504&se=0&ep=0&detailPath=forgotten-island-67AJfvji0o&host=movie-box.co"),
    ("GET", "wefeed-h5api-bff/subject/detail-rec?subjectId=324954574010339504&page=1&perPage=6&host=movie-box.co"),
]

for method, path in endpoints:
    url = base_url + path
    clean_path = path.split("?")[0]
    try:
        req = urllib.request.Request(url, headers=headers, method=method)
        with urllib.request.urlopen(req) as resp:
            raw = resp.read()
            if resp.headers.get("content-encoding") == "gzip":
                raw = gzip.decompress(raw)
            data = json.loads(raw.decode("utf-8"))
            data_val = data.get("data")
            keys = list(data_val.keys()) if isinstance(data_val, dict) else str(type(data_val))
            print(f"[{method}] {clean_path} -> Code: {data.get('code')}, Msg: {data.get('message')}, Data: {keys}")
    except Exception as e:
        print(f"[{method}] {clean_path} -> ERROR: {e}")
