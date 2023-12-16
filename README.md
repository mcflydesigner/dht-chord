# DNS Server based on the Distributed Hash Table (DHT) following Chord protocol 

We aim to develop a DNS server based on the distributed hash table following the Chord protocol. 
It would help to build a scalable distributed platform for DNS. 
In our project, all DNS records are stored in the distributed cluster. 

DHT is a decentralized distributed system that provides a lookup service similar to a hash table. 
Key, value pairs are stored in a DHT, and any participating node can retrieve the value associated with a given key. 
All the nodes communicate with each other using the HTTP protocol (RESTful API). 
The nodes authenticate each other using the basic access authentication method.

## Features

- Lookup for a key takes at most `O(log N)` hops where N is the number of nodes in the cluster.
- Dynamic node join in the cluster topology (knowing one arbitrary node endpoint address is enough)
- Implemented stabilization protocol for the cluster.
- Data transfer for newly joined nodes.
- The nodes use basic authentication method.
- DNS server is able to resolve some types of the DNS queries.

## Configuration

There are the parameters that allows configuration of DHT server node
through the VM options:

```shell
-Dserver.port=<port> # Server node port
-Ddht.node.id=<id> # Server node ID (optional)
-Ddht.node.address="<host>:<port>" # Current server node address 
-Ddht.node.join.address="<host>:<port>" # Arbitrary node's number of the cluster topology (pass empty iff it is the first node of the cluster)  
-Ddht.node.security.username="<login>" # Username for basic auth (used by nodes in communication)
-Ddht.node.security.password="<password>" # Password for basic auth (used by nodes in communication)
-Ddns.enricher.enabled="<true | false>" # Filling the DHT cluster using data from the zone file
-Ddns.enricher.zonefile="<path>" # Path to the zone file (used if the flag dns.enricher.enabled is true)
```

## How to run?

There are two ways to build and run the project: JAR file and  Docker container.

### Run in Docker

Firstly, it is required to build the project using Docker and the following command below:
```bash
docker build -t dht-dns .
```

Finally, run the Docker container:
```bash
docker run -p 80:80 -e SERVER_PORT=80 -e DHT_NODE_ID="0" -e DHT_NODE_ADDRESS="localhost:80" -e DHT_NODE_JOIN_ADDRESS="<host>:<port>" -e DHT_NODE_SECURITY_USERNAME="user" -e DHT_NODE_SECURITY_PASSWORD="password" -e DNS_ENRICHER_ENABLED="true" -e DNS_ENRICHER_ZONEFILE="st7.sne23.ru.zone" dht-dns
```

### Building Jar

Firstly, it is required to build the project using Maven. 
In the root directory of the project run the following command:
```
/mvnw clean install
```

Finally, run the generated JAR file using the command below:
```
java -jar ./target/dht-chord-0.0.1-SNAPSHOT.jar -Dserver.port=80 -Ddht.node.join.address="<host>:<port>" -Ddht.node.address=localhost:80 -Ddht.node.id="0" -Ddht.node.security.username=user -Ddht.node.security.password=password -Ddns.enricher.enabled=true -Ddns.enricher.zonefile="st7.sne23.ru.zone"
```

### Technological Stack

- The project is written in Java 17.
- The base framework is Spring Framework (Spring Web and Spring Security).
- Maven is used to build the project.
- JUnit5 is used as a testing framework.

### Implementation

Our project mainly consists of two parts. 
The first part (package: `ru.dht.dhtchord`) contains the implementation details of the distributed hash table. 
The second part (package: `ru.dht.dnsserver`) contains the implementation details of the DNS server.

## Contribution
You can contribute to our project - we are glad to new ideas and fixes.

## License

The project is released and distributed under [MIT License](https://en.wikipedia.org/wiki/MIT_License).

