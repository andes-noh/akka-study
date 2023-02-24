docker run --name cassandra -d -p 9042:9042 cassandra:3

docker exec -it my_cassandra cqlsh


describe keyspaces;
use akka;

describe tables;
select * from messages

- Behaviors
  -  setup(): 액터 생성 및 정의, 초기에 사용
    - context => 
        ... Behaviors.empty
  - receive: 일반적 사용
  - receiveMessage

- EventSourcedBehavior
  - 이벤트 발생 로그 기록