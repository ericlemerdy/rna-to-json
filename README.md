rnatojson
---------

### Compile

1. Run maven build

```bash
$ mvn clean install
```

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

```bash
$ java -jar target/rna-to-json-1.0-SNAPSHOT.jar AssociationsIndex
```

3. Index documents from CSV

```bash
$ java -jar target/rna-to-json-1.0-SNAPSHOT.jar RNAIndexer
```

### Deploy Front-end.

1. Start maven

```bash
docker run -it --rm --name my-maven-project -v "$PWD":/usr/src/mymaven -w /usr/src/mymaven maven /bin/bash
```

2. Create `known_host` file

```bash
mkdir /root/.ssh/
touch /root/.ssh/known_hosts
```

3. Deploy with maven

```bash
mvn wagon:upload
```

4. Test

Access [http://assoc-io.ddns.net/](http://assoc-io.ddns.net/)