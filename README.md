# Getting Started

### Reference Documentation

For further reference, please consider the following sections:

* [Official Gradle documentation](https://docs.gradle.org)
* [Spring Boot Gradle Plugin Reference Guide](https://docs.spring.io/spring-boot/4.1.1/gradle-plugin)
* [Create an OCI image](https://docs.spring.io/spring-boot/4.1.1/gradle-plugin/packaging-oci-image.html)
* [Spring Batch](https://docs.spring.io/spring-boot/4.1.1/how-to/batch.html)
* [Spring Batch JDBC](https://docs.spring.io/spring-boot/4.1.1/how-to/batch.html)

### Guides

The following guides illustrate how to use some features concretely:

* [Creating a Batch Service](https://spring.io/guides/gs/batch-processing/)

### Additional Links

These additional references should also help you:

* [Gradle Build Scans – insights for your project's build](https://scans.gradle.com#gradle)

### Producing a test message to `version-registry-topic`

Use the `kafka-console-producer` bundled in the Kafka container to check that `VersionRegistryListener` consumes `CurrentVersionChanged` events.

1. Start the local stack (Postgres, ZooKeeper, Kafka) from the project root:

   ```sh
   podman compose up -d
   ```

2. Check that the containers are up:

   ```sh
   podman compose ps
   ```

3. Start the application (from the IDE, or with `./gradlew bootRun`) and wait for the log line
   `partitions assigned: [version-registry-topic-0]`.

4. Produce a `CurrentVersionChanged` message:

   ```sh
   podman exec kafka75-1 bash -c 'echo "{\"templateId\":\"tpl-123\",\"version\":\"7\"}" | kafka-console-producer --bootstrap-server localhost:9092 --topic version-registry-topic'
   ```

   Or open an interactive producer, type one JSON message per line, and press `Ctrl+C` to quit:

   ```sh
   podman exec -it kafka75-1 kafka-console-producer --bootstrap-server localhost:9092 --topic version-registry-topic
   ```

   ```json
   {"templateId":"tpl-123","version":"7"}
   ```

5. The application log should show:

   ```
   Received CurrentVersionChanged from version-registry-topic: CurrentVersionChanged(templateId=tpl-123, version=7)
   ```

Optionally, read back everything on the topic to confirm the message was written (`Ctrl+C` to quit):

```sh
podman exec -it kafka75-1 kafka-console-consumer --bootstrap-server localhost:9092 --topic version-registry-topic --from-beginning
```

Notes:

* Inside the container the broker is `localhost:9092`; tools running on your machine use `localhost:9093`.
* Messages must be JSON with `templateId` and `version` fields.

