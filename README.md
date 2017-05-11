rnatojson
---------

### Run

1. Linux: increase map count.

```bash
$ sudo sysctl -w vm.max_map_count=262144
```

2. Start docker container

```bash
$ docker run \
    -p 9200:9200 \
    -p 9300:9300 \
    -p 5601:5601 \
    ibeauvais/elasticsearch-kibana:5.2
```

4. Create schema

$ java -jar target/rna-to-json-1.0-SNAPSHOT.jar AssociationsIndex

3. Convert documents from CSV to JSON

$ java -jar target/rna-to-json-1.0-SNAPSHOT.jar RNAIndexer

At the end, it will print the output file.

Ex. `/tmp/rna8887075158028002758.json`

4. Index the file

```bash
$ curl \
    -v
    -X PUT \
    --data-binary @/tmp/rna8887075158028002758.json \
    http://localhost:9200/rna/associations/_bulk
```