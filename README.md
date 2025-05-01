

```md
# doredemo 開発テンプレート

このリポジトリは、Laravel・Rails・Node.js・Java（Spring Boot）・Python（Flask）をすべて同じDocker環境で一括起動できる開発用テンプレートです。

## 📦 含まれる構成

| 言語       | フレームワーク | ポート | パス              |
|------------|----------------|--------|-------------------|
| PHP        | Laravel 12     | 80     | `/laravel/`       |
| Ruby       | Rails 7        | 3000   | `/rails/`         |
| JavaScript | Node.js + Express | 3001 | `/node/`          |
| Java       | Spring Boot    | 8080   | `/java/`          |
| Python     | Flask          | 5000   | `/python/`        |
| DB         | MySQL 8.0      | 3306   | 共通              |
| Proxy      | Nginx          | 80     | 各アプリにルーティング |

---

## 🚀 初回セットアップ手順

以下の手順は、初めてこの環境を立ち上げるときに一度だけ行ってください。

### 1. このリポジトリをクローン

```bash
git clone https://github.com/your-org/doredemo.git
cd doredemo
```

---

### 2. Laravel の `.env` ファイルを作成

Laravelは `.env` ファイルが必須です。

```bash
cp laravel-app/.env.example laravel-app/.env
```

---

### 3. Laravel の暗号キーを生成

Laravelは `.env` にある `APP_KEY` が未設定だと動作しません。

```bash
docker compose up -d laravel
docker compose exec laravel php artisan key:generate
```

---

### 4. Laravel のセッションテーブルを作成

Laravelは `SESSION_DRIVER=database` が設定されているため、テーブルを用意する必要があります。

```bash
docker compose exec laravel php artisan session:table
docker compose exec laravel php artisan migrate
```

---

## ▶️ 開発用サービスの起動

すべてのサービスを一括で起動するには以下を実行してください：

```bash
docker compose up --build
```

---

## 🌐 アクセスURL一覧

| アプリ名   | アクセスURL                  |
|------------|-------------------------------|
| Laravel    | http://localhost/laravel/     |
| Rails      | http://localhost/rails/       |
| Node.js    | http://localhost/node/        |
| Java       | http://localhost/java/        |
| Python     | http://localhost/python/      |

---

## 🔁 よく使うコマンド

### サービスを個別に再起動したいとき：

```bash
docker compose restart laravel  # ← Laravelだけ再起動
```

### ログを確認したいとき：

```bash
docker compose logs -f rails
```

### コンテナを完全に削除（ボリューム含む）：

```bash
docker compose down -v
```

---

## 🧼 `.gitignore` に含めているもの（すでに追加済）

- Laravel の `vendor`, `node_modules`, `storage`, `bootstrap/cache`
- Rails の `tmp`, `log`, `vendor`

---

## 👤 補足：Docker がまだインストールされていない場合

1. [Docker公式サイト](https://www.docker.com/products/docker-desktop/)から Docker Desktop をダウンロードしてインストール
2. Windows の場合は **WSL2（推奨）** を有効化してください

---

## 🧪 確認済みOS・バージョン

- Windows 10/11 + WSL2
- macOS Sonoma
- Docker Desktop 4.0+

---

## ✅ 初期動作確認済み

- Laravel 12 の Welcome ページ表示確認済
- Rails 起動確認済（`/rails/` にて表示）
- Node.js + Express 正常表示
- Java (Spring Boot) Welcome 表示済
- Python (Flask) 正常レスポンス返却済

---



## ✅ MySQLクライアントでDB接続・確認する手順（README追記用）

### 🔍 MySQL クライアントツールでの接続手順

このプロジェクトでは、Docker上に共通のMySQLサーバーを構築しています。  
`appuser` でログインすることで、Laravel / Rails / Node.js / Java / Python 各アプリ用のデータベースにアクセスできます。

#### 推奨ツール例（どれでもOK）：
- [HeidiSQL（Windows）](https://www.heidisql.com/)
- [DBeaver（マルチOS）](https://dbeaver.io/)
- [TablePlus（macに人気）](https://tableplus.com/)

---

### 🔧 接続情報（docker-compose準拠）

| 項目         | 値              |
|--------------|-----------------|
| ホスト名     | `127.0.0.1` または `localhost` |
| ポート       | `3306`          |
| ユーザー名   | `appuser`       |
| パスワード   | `secret`        |
| 使用DB名     | 任意（例：`laravel_db`）または空欄でもOK |

> Dockerが起動している状態であれば、上記情報でMySQLに接続できます。

---

### 📋 確認できるデータベース一覧（初期状態）

以下のデータベースが自動作成されています：

- `laravel_db`
- `rails_db`
- `node_db`
- `java_db`
- `python_db`

> これらは `/mysql/init.sql` により `docker-compose up` 時に初期作成されます。

---

### ✅ トラブルシューティング

- **接続できない場合**：
  - Dockerが起動していない → `docker ps` で確認
  - 他のMySQLがポート3306を使っている → `docker-compose.yml` でポート変更可
  - ボリューム初期化が必要な場合 → `docker volume rm <volume名>` → 再起動

---

必要であればこのまま `README.md` にコピペでOKです。  
もっと短く or コマンドベースでまとめたい場合もアレンジできますよ！どうしますか？