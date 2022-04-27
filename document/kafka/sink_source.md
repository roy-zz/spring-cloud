[이전 장(링크)](https://imprint.tistory.com/233) 에서는 `Kafka의 Connect`를 설치하는 방법에 대해서 알아보았다.
이번 장에서는 `Source Connect`와 `Sink Connect`를 사용하는 방법에 대해서 알아본다.
모든 소스 코드는 [깃 허브 (링크)](https://github.com/roy-zz/spring-cloud) 에 올려두었다.

---

우리는 MariaDB의 `Connect Source`와 `Connect Sink`를 사용하여 아래와 같은 구조를 구축할 것이다.

![](sink_source_image/connector-flow.png)

`Connect Source`는 데이터를 제공하는 쪽으로부터 데이터를 전달받아 Kafka Cluster로 전달하는 역할을 하고 `Connect Sink`는 Kafka Cluster를 대상 저장소에 전달하는 역할을 한다.
이전에 Producer와 Consumer를 사용하여 Kafka Cluster를 통해서 데이터를 제공하고 소비하는 작업을 진행했었다.
이번에는 Connect Source와 Connect Sink를 사용하여 데이터를 제공하고 소비하는 작업을 진행해본다.

### Source Connect

우리는 Postman을 사용하여 `connect`의 connectors API를 호출하여 새로운 정보를 등록할 것이다. 

Postman에 전달하는 데이터 중에서 `table.whitelist`라는 항목에 우리가 원하는 테이블의 이름을 지정한다.
`connect`는 평소에 대기하고 있다가 우리가 지정한 테이블에 새로운 데이터가 입력되면 새로 입력된 데이터를 가져오는 역할을 한다.
`topic.prefix`는 `table.whitelist`에서 지정한 테이블에 새로운 데이터가 들어왔을 때 데이터를 전달하기 위해 사용되는 토픽의 Prefix이며 데이터가 전달되는 토픽의 이름은 우리가 지정한 테이블의 이름과 합쳐져서 `roy_topic_users`가 될 것이다.
결국 `Source Connect`는 우리가 지정한 테이블의 변경을 감지하고 있다가 변경이 감지되면 우리가 지정한 토픽으로 데이터를 전달해주는 역할을 한다.

1. POST /connectors API 호출

아래의 JSON 파일을 HTTP Body에 넣어서 `connect`의 /connectors API를 호출한다.

```json
{
    "name": "my-source-connect",
    "config": {
        "connector.class": "io.confluent.connect.jdbc.JdbcSourceConnector",
        "connection.url": "jdbc:mysql://localhost:3306/mydb",
        "connection.user": "root",
        "connection.password": "root",
        "mode": "incrementing",
        "incrementing.column.name": "id",
        "table.whitelist": "users",
        "topic.prefix": "roy_topic_",
        "tasks.max" : "1"
    }
}
```

아래와 같이 201 코드가 반환된다면 정상적으로 `Source Connect`가 등록된 것이다.
혹시라도 500 오류가 발생한다면 이전 장에서 설정을 완료하고 나서 Connect를 재실행시키지 않아서 발생하는 오류일 수 있으므로 Connect를 재실행시켜본다.

![](sink_source_image/post-connectors-api.png)

**참고**

GET http://localhost:8083/connectors 를 호출하면 등록되어 있는 connect 목록을 확인할 수 있다.

![](sink_source_image/get-connectors-api.png)

GET http://localhost:8083/connectors/my-source-connect 를 호출하면 connect의 상세정보를 확인할 수 있다.

![](sink_source_image/get-connectors-detail-api.png)

2. Consumer 등록

`Source Connect`가 produce하는 데이터를 확인하는 Consumer를 실행시킨다.

```bash
$ ./bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic roy_topic_users --from-beginning
```

![](sink_source_image/register-consumer.png)

3. 사용자 정보 Insert

새로운 사용자 정보가 Insert 되었을 때 우리가 예상한 데이터가 전달되는지 확인하기 위해 MariaDB 콘솔로 접속하여 새로운 사용자를 등록해본다.

```sql
INSERT INTO users (user_id, password, name) VALUES ('roy', 'password', 'roy choi');
```

Consumer가 정상적으로 데이터를 수신하고 있다.
좌측 창이 Consumer이며 우측 창에서 데이터를 INSERT하고 있다.

![](sink_source_image/insert-user-receiver-source-connect.png)


**참고**

1, 2, 3 단계를 진행하여도 Consumer가 데이터를 수신하지 못하는 경우가 발생하였다.
필자의 경우 아래와 같은 방법으로 해결하였으니 혹시라도 같은 문제가 발생한다면 참고하도록 한다.

문제상황: Consumer에서 데이터를 수신하지 못하며 connect 로그에서 SQL Exception이 발생하는 상황.
해결방법:
- 아래의 이미지와 같이 mariadb-connector.jar 파일을 추가한 디렉토리에 myslq-connector.jar파일을 추가한다. (mysql.jar 파일은 [여기](https://dev.mysql.com/downloads/file/?id=510648)에서 다운로드 한다.)

![](sink_source_image/add-mysql-connector.png)

- connect의 API를 호출하여 `Source Connect`를 추가할 때 접속주소를 mariadb가 아닌 mysql로 수정한다.

```json
{
    "name": "roy-source-connect",
    "config": {
        "connector.class": "io.confluent.connect.jdbc.JdbcSourceConnector",
        "connection.url": "jdbc:mysql://localhost:3306/mydb",
        "connection.user": "root",
        "connection.password": "root",
        "mode": "incrementing",
        "incrementing.column.name": "id",
        "table.whitelist": "users",
        "topic.prefix": "roy_topic_",
        "tasks.max" : "1"
    }
}
```

아마 문제가 발생하였다면 같은 방식으로 해결될 것으로 예상된다.

---






---

**참고한 강의:**

- https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81-%ED%81%B4%EB%9D%BC%EC%9A%B0%EB%93%9C-%EB%A7%88%EC%9D%B4%ED%81%AC%EB%A1%9C%EC%84%9C%EB%B9%84%EC%8A%A4