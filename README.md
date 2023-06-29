# 書籍管理システムAPI
API仕様は、「REAME_API.md」のご参照をお願いいたします。

# プロジェクトのセットアップ
## 開発環境の前提条件
- Dockerがインストール済み

## 動作確認
1. ソースをダウンロード  
1. mysqlのコンテナを起動  
   ~~~bash
   docker-compose up -d mysql
   ~~~
1. apiのコンテナを起動
   ~~~bash
   docker-compose up -d api
   ~~~
1. apiのコンテナに入る
   ~~~bash
   docker-compose exec api bash
   ~~~
1. マイグレーション実行
   ~~~bash
   # apiコンテナ内部 /appにて
   ./gradlew flywayMigrate
   ~~~
1. プロジェクト開始
   ~~~bash
   # apiコンテナ内部 /appにて
   ./gradlew bootRun
   ~~~

1. http://localhost:8080/booksにGETリクエストを送り、結果が帰って来れば成功
   ~~~bash
   $ curl -X GET http://localhost:8080/books
   []
   ~~~

## テストの実行
1. mysql-testのコンテナを起動
   ~~~bash
   docker-compose up -d mysql-test
   ~~~
1. apiのコンテナを起動
   ~~~bash
   docker-compose up -d api
   ~~~
1. apiのコンテナに入る
   ~~~bash
   docker-compose exec api bash
   ~~~
1. テスト用mysqlコンテナにマイグレーション実行
   ~~~bash
   # apiコンテナ内部 /appにて
   ./gradlew migrateTestDB
   ~~~
1. テスト用のコマンドを実行
   ~~~bash
   # apiコンテナ内部 /appにて
   ./gradlew test
   ~~~
