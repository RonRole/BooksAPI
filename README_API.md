# 著者検索
## エンドポイント

```
[GET] /authors
```

## リクエストパラメータ
|パラメータ名|説明|必須|データ型|
|----------|---|----|------|
|name|指定された場合、この文字列を含む名前の著者を抽出します|No|文字列|
|sort|指定された場合、指定した順でレスポンスを並び替えます</br>指定する項目名に"-"を含めた場合、その項目は降順になります|No|文字列(id,name)のカンマ区切り</br>例):/books?sort=-name</br>著者名の降順で取得
|limit|出力件数を指定します|No|0以上の整数 デフォルト10|
|offset|検索結果から、指定した件数だけ前方から削除します|No|0以上の整数 デフォルト0|

## リクエスト例
```
[GET] /authors?name=author&sort=id
```

## レスポンス

```json
[
    {
        "id": 1,
        "name": "author 1"
    },
    {
        "id": 2,
        "name": "author 2"
    }
]
```

# 著者登録
## エンドポイント

```
[POST] /authors
```

## リクエストボディ

以下のパラメータを含むJSON形式

|パラメータ名|説明|必須|データ型|
|----------|---|---|-------|
|name|著者名|Yes|文字列|

リクエストボディ例
```json
{
  "name": "author 1 name"
}
```

## レスポンス
```json
{
    "id": 1,
    "name": "author 1 name"
}
```

## エラーレスポンス
| ステータスコード | 説明              |
| ------------ | ------------------- |
| 400          | パラメータの形式が誤っています |
| 409          | 同じ名前の著者が登録済みです  |
| 500          | サーバーエラー |

# 著者取得
## エンドポイント

```
[GET] /authors/{id}
```

## リクエスト例

```
[GET] /authors/1
```

## レスポンス

```json
{
    "id": 1,
    "name": "author 1 name"
}
```

## エラーレスポンス
| ステータスコード | 説明              |
| ------------ | ------------------- |
| 404          | 対象のIDの著者が見つかりませんでした |
| 500          | サーバーエラー |


# 著者更新
## エンドポイント

```
[PATCH] /authors/{id}
```

## リクエストボディ

以下のパラメータを含むJSON形式  
パラメータとして渡された項目のみ更新する

|パラメータ名|説明|必須|データ型|
|----------|---|---|-------|
|name|著者名|No|文字列|

リクエストボディ例
```json
{
  "name": "patched author name"
}
```

## レスポンス
200 No Content

## エラーレスポンス
| ステータスコード | 説明              |
| ------------ | ------------------- |
| 404          | 対象のIDの著者が見つかりませんでした |
| 500          | サーバーエラー |

# 著者に紐づく書籍の取得
## エンドポイント

```
[GET] /authors/{id}/books
```

## リクエスト例
```
[GET] /authors/1/books
```

## レスポンス

```json
[
    {
        "id": 1,
        "title": "book 1 title",
        "authorId" : 1,
        "publishedAt" : "2020-02-01"
    },
    {
        "id": 2,
        "title": "book 2 title",
        "authorId" : 1,
        "publishedAt" : "2021-06-03"
    },
]
```


## エラーレスポンス
| ステータスコード | 説明              |
| ------------ | ------------------- |
| 404          | 対象のIDの著者が見つかりませんでした |
| 500          | サーバーエラー |

# 書籍検索
## エンドポイント

```
[GET] /books
```

## リクエストパラメータ

|パラメータ名|説明|必須|データ型|
|----------|---|---|-------|
|title|指定された場合、この文字列を含むタイトルの書籍を抽出します|No|文字列|
|author-id|指定された場合、著者IDがこれと一致する書籍を抽出します|No|整数|
|published-from|指定された場合、出版日がこれ以降の書籍を抽出します|No|文字列 yyyy-MM-dd形式|
|published-to|指定された場合、出版日がこれ以前の書籍を抽出します|No|文字列 yyyy-MM-dd形式|
|sort|指定された場合、指定した順でレスポンスを並び替えます</br>指定する項目名に"-"を含めた場合、その項目は降順になります|No|文字列(id,title,authorId,published-at)のカンマ区切り</br>例):/books?sort=authorId,-published-at</br>著者ID昇順,出版日降順で取得
|limit|出力件数を指定します|No|0以上の整数 デフォルト10
|offset|検索結果から、指定した件数だけ前方から削除します|No|0以上の整数 デフォルト0

## リクエスト例

```
[GET] /books/title=book&sort=id
```

## レスポンス

```json
[
    {
        "id": 1,
        "title": "book 1 title",
        "authorId" : 1,
        "publishedAt" : "2020-02-01"
    },
    {
        "id": 2,
        "title": "book 2 title",
        "authorId" : 1,
        "publishedAt" : "2021-06-03"
    },
]
```

## エラーレスポンス
| ステータスコード | 説明              |
| ------------ | ------------------- |
| 400          | パラメータの形式が誤っています |
| 500          | サーバーエラー |

# 書籍登録
## エンドポイント

```
[POST] /books
```

## リクエストボディ

以下のパラメータを含むJSON形式

|パラメータ名|説明|必須|データ型|
|----------|---|---|-------|
|title|書籍タイトル|Yes|文字列|
|authorId|著者ID|Yes|整数|
|publishedAt|出版日|Yes|文字列 yyyy-MM-dd形式|

リクエストボディ例
```json
{
  "title" : "the first book",
  "authorId" : 1,
  "publishedAt" : "2018-02-01"
}
```

## レスポンス
```json
{
  "id" : 1, 
  "title" : "the first book",
  "authorId" : 1,
  "publishedAt" : "2018-02-01"
}
```

## エラーレスポンス
| ステータスコード | 説明              |
| ------------ | ------------------- |
| 400          | パラメータの形式が誤っています |
| 409          | 同じタイトル・著者の書籍が登録済みです |
| 500          | サーバーエラー |

# 書籍取得
## エンドポイント

```
[GET] /books/{id}
```

## リクエスト例

```
[GET] /books/1
```

## レスポンス

```json
{
    "id": 1,
    "title": "book 1 title",
    "authorId" : 1,
    "publishedAt" : "2020-02-01"
}
```

## エラーレスポンス
| ステータスコード | 説明              |
| ------------ | ------------------- |
| 404          | 対象のIDの書籍が見つかりませんでした |
| 500          | サーバーエラー |

# 書籍更新
## エンドポイント

```
[PATCH] /books/{id}
```

## リクエストボディ

以下のパラメータを含むJSON形式  
パラメータとして渡された項目のみ更新する

|パラメータ名|説明|必須|データ型|
|----------|---|---|-------|
|title|書籍タイトル|No|文字列|
|authorId|著者ID|No|整数|
|publishedAt|出版日|No|文字列 yyyy-MM-dd形式|

リクエストボディ例
```json
{
  "title": "the first book"
}
```

## レスポンス
200 No Content

## エラーレスポンス
| ステータスコード | 説明              |
| ------------ | ------------------- |
| 404          | 対象のIDの書籍が見つかりませんでした |
| 500          | サーバーエラー |