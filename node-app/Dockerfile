FROM node:18
WORKDIR /app

# 最初のビルド時だけ初期化とexpress導入を試みる
RUN test -f package.json || (npm init -y && npm install express)

COPY . /app

CMD ["node", "index.js"]

