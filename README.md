docker run --name cassandra -d -p 9042:9042 cassandra:3

docker exec -it my_cassandra cqlsh