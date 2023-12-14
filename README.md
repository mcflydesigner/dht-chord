# Distributed Hash Table (DHT) based on Chord protocol 

Distributed Hash Table (DHT) based on the Chord architecture with the access by REST API model written in Java.

It is a decentralized distributed system that provides a lookup service similar to a hash table. 
Key, value pairs are stored in a DHT, and any participating node can retrieve the value associated with a given key. 
All the nodes communicate with each other through the HTTP and HTTPs protocols and REST-ful APIs. 
The nodes authenticate each other requests using basic access authentication method.

## Features

- Lookup for a key takes at most `O(log N)` hops where N is the number of nodes in the cluster.
- Dynamic node join in the cluster topology (knowing one arbitrary node endpoint address is enough)
- Implemented stabilization protocol for the cluster.
- Secure communication between the nodes using HTTPs (if configured).
- The nodes use basic authentication method.

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
```

## How to run?

There are two ways to run our project: plain Java process and Docker container.

### Run in Docker

```bash
docker build -t dht .
docker run -p 8080:8080 dht
```

### Building Jar

```bash
/mvnw clean install
java -jar ./target/dht-chord-0.0.1-SNAPSHOT.jar
```

## Contribution
You can contribute to our project - we are glad to new ideas and fixes.

## License

The project is released and distributed under [MIT License](https://en.wikipedia.org/wiki/MIT_License).

