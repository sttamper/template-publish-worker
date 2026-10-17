# template-publish-worker

template-publish-worker reconciles which engagements must be opened by engagement-mgmt-system, 
so this can emit the notification via kafka event for the engagement-index-api to evaluate 
whether a template update is pending. For the ones that do not need to be opened, the worker 
sends directly the notification via the same kafka topic.

It is one of the regional services described in
[`docs/home-task-design-document.pdf`](docs/home-task-design-document.pdf).

## How it works

The worker consumes `CurrentVersionChanged` events from `version-registry-topic` and launches the
`templatePublishedJob` Spring Batch job for each one:

```
               CurrentVersionChanged (version-registry-topic)
                                      │
                                      ▼
                         conciliateEngagementIdsStep
                                      │
                 ┌────────────────────┴────────────────────┐
                 ▼                                         ▼
publishTemplateUpdateAvailableStep                openEngagementStep
     known IDs, 20 in parallel                unknown IDs, 50 in parallel
                 │                                         │
         ┌───────┼───────┐                         ┌───────┼───────┐
         ▼       ▼       ▼                         ▼       ▼       ▼
       event   event   event ...                 REST    REST    REST  ...
         │       │       │                         │       │       │
         └───────┼───────┘                         └───────┼───────┘
                 ▼                                         ▼
     engagement-updates-topic                   engagement-mgmt-system
     (TemplateUpdateAvailable)                     (open engagement)
```

Note that once conciliation completes, both steps run in parallel. Inside each step, the events and the REST
calls are processed concurrently with `CompletableFuture`s on bounded thread pools (20 for publishing, 50 for
opening), which speeds up the run considerably compared with handling one engagement at a time.

## Implementation tradeoffs

* **Job runs on the Kafka listener thread.** This is simple and handles one event at a time per partition. But
  a run lasts longer than Kafka's default `max.poll.interval.ms` (5 minutes), so the consumer is evicted and the
  group rebalances during long runs. Launching the job asynchronously, or raising the timeout, would fix this.
* **Conciliation results live in the job execution context.** The steps share state without extra tables. In
  exchange, the full known and unknown ID lists are serialized into the Spring Batch metadata tables. That is
  fine for thousands of IDs but not for millions.
* **Parallel split after conciliation.** Publishing and opening only read the conciliation results, so they run
  side by side. A run takes conciliation plus the slower of the two steps, instead of the sum of both.
* **Bounded thread pools with `CompletableFuture`.** Fixed pools of 50 (openings) and 20 (publishes) cap the load
  on `engagement-mgmt-system` and Kafka. The pools are singleton beans, so concurrent job runs share the limit
  instead of multiplying it.
* **All-or-nothing tasklets.** Each tasklet processes its whole list in one step execution and one transaction.
  If one item fails, the step fails and a rerun repeats every item. Chunk-oriented steps would allow restarting
  from where a run stopped, at the cost of more code.
* **A new job instance per event.** A random `runId` parameter makes each event start a fresh job, so a
  duplicate `CurrentVersionChanged` event triggers a full re-run. Using `templateId` and `version` as job
  parameters would let Spring Batch reject duplicates.

## Tech stack

* Java 21, Gradle (wrapper included)
* Spring Boot 4, Spring Batch 6, Spring Kafka
* Postgres 16 (engagement IDs and Spring Batch metadata), Flyway
* Kafka 7.5 (Confluent images) with ZooKeeper
