import urllib.request
import re

req = urllib.request.Request(
    "https://movie-box.co/movies/forgotten-island-67AJfvji0o",
    headers={
        "User-Agent": "Mozilla/5.0 (Linux; Android 11; vivo 1951 Build/RP1A.200720.012) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/154.0.8037.107 Mobile Safari/537.36"
    }
)
with urllib.request.urlopen(req) as resp:
    html = resp.read().decode("utf-8")

scripts = re.findall(r'<script[^>]*>(.*?)</script>', html, re.DOTALL)
s3 = scripts[3]
print("S3 content:")
# Look for any url or api
matches = re.findall(r'https?://[^\s",\'`\]]+', s3)
print("URLs in S3:", set(matches))

# Look for resource in s3
idx = s3.find('"resource"')
if idx != -1:
    print("Around resource:", s3[idx:idx+1000])

# Look for watchTimeLimit
idx2 = s3.find('"watchTimeLimit"')
if idx2 != -1:
    print("Around watchTimeLimit:", s3[idx2:idx2+500])

# Look for path or api
idx3 = s3.find('/wefeed-h5api-bff')
if idx3 != -1:
    print("Around api:", s3[idx3:idx3+500])
