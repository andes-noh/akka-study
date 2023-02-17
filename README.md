docker run --name cassandra -d -p 9042:9042 cassandra:3

docker exec -it my_cassandra cqlsh


describe keyspaces;
use akka;

describe tables;
select * from messages