# Virality Engine

A Spring Boot microservice implementing Redis-based virality scoring, atomic guardrails, concurrency protection, and notification batching.

---

# Project Overview

This project was built as a backend engineering assignment focused on:

* High-performance REST APIs
* Redis atomic operations
* Concurrency-safe backend design
* Distributed state management
* Event-driven notification batching
* PostgreSQL persistence
* Stateless Spring Boot architecture

The system uses PostgreSQL as the persistent source of truth and Redis as the real-time guardrail and throttling engine.

---

# Tech Stack

* Java 17
* Spring Boot 3
* Spring Data JPA
* Spring Data Redis
* PostgreSQL
* Redis
* Docker Compose
* Maven

---

# Features

## Phase 1 — Core API & Database

Implemented REST APIs:

* Create Post
* Add Comment
* Like Post

Entities:

* User
* Bot
* Post
* Comment

---

<img width="1920" height="1080" alt="image" src="https://github.com/user-attachments/assets/4630b323-ec0b-4a23-bea2-696dfa410f6b" />



## Phase 2 — Redis Virality Engine & Atomic Locks

### Virality Score

Virality scores are maintained in Redis in real time.

Scoring rules:

| Interaction   | Points |
| ------------- | ------ |
| Bot Reply     | +1     |
| Human Like    | +20    |
| Human Comment | +50    |

Redis key format:

```text
post:{id}:virality_score
```

---

### Atomic Locks & Guardrails

Implemented using Redis atomic operations.

#### Horizontal Cap

A post cannot receive more than 100 bot replies.

Redis key:

```text
post:{id}:bot_count
```

If the limit exceeds 100, the API returns:

```text
429 TOO MANY REQUESTS
```

---

#### Vertical Cap

Comment threads cannot exceed depth level 20.

Requests with:

```text
depth_level > 20
```

are rejected.

---

#### Cooldown Cap

A bot cannot interact with the same human more than once within 10 minutes.

Redis TTL-based cooldown key:

```text
cooldown:bot_{id}:human_{id}
```

---

## Phase 3 — Notification Engine

### Notification Throttling

If a user has already received a notification within the last 15 minutes:

* notifications are batched in Redis
* immediate notification is skipped

Redis pending notification list:

```text
user:{id}:pending_notifs
```

---

### Scheduled Notification Sweeper

A scheduled Spring task runs every 5 minutes and:

* scans pending notifications
* summarizes interactions
* logs batched notifications
* clears Redis lists

---

## Phase 4 — Concurrency & Data Integrity

### Race Condition Protection

The system handles concurrent bot requests safely using Redis atomic increment operations.

Even under 200 simultaneous requests:

* only the first 100 bot replies succeed
* remaining requests are rejected
* database consistency is preserved

---

### Stateless Architecture

The application remains fully stateless.

No in-memory storage mechanisms were used such as:

* HashMap
* static counters
* ConcurrentHashMap
* ArrayList caches

All counters, cooldowns, and notification queues are maintained in Redis.

---

### Data Integrity

Redis guardrails are validated BEFORE PostgreSQL transactions are committed.

Flow:

```text
Validate Redis Guardrails
        ↓
Save Entity in PostgreSQL
        ↓
Update Virality Score
        ↓
Process Notifications
```

This guarantees that invalid requests never persist inconsistent database records.

---

# Thread Safety & Atomic Locks

Thread safety was guaranteed using Redis atomic operations through Spring Data Redis.

The Horizontal Cap was implemented using Redis atomic increment operations:

```java
redisTemplate.opsForValue().increment(key);
```

Redis executes increment operations atomically, ensuring thread-safe counter updates even under heavy concurrency.

If the bot reply count exceeds 100:

* the request is rejected
* HTTP 429 is returned
* the increment is rolled back using Redis decrement operations

Cooldown protection was implemented using:

```java
setIfAbsent(key, value, Duration.ofMinutes(10))
```

which guarantees distributed cooldown enforcement using Redis TTL.

Because Redis operates independently from application memory, the backend remains fully stateless and concurrency-safe.

---

# API Endpoints

## Create Post

```http
POST /api/posts
```

---

## Add Comment

```http
POST /api/posts/{postId}/comments
```

---

## Like Post

```http
POST /api/posts/{postId}/like
```

---

# Example Bot Comment Request

```json
{
  "authorId": 10,
  "content": "AI generated reply",
  "depthLevel": 1,
  "isBot": true,
  "botId": "bot1",
  "humanId": "human1"
}
```

---

# Running The Project

## Clone Repository

```bash
git clone <repository-url>
```

---

## Start Redis Using Docker Compose

```bash
docker-compose up -d
```

---

## PostgreSQL Setup

PostgreSQL was configured locally on the system.

Update database credentials inside:

```text
src/main/resources/application.properties
```

---

## Run Spring Boot Application

```bash
mvn spring-boot:run
```

---

# Redis Configuration

Redis runs on:

```text
localhost:6379
```

---

# Docker Compose

```yaml
services:

  redis:
    image: redis:latest
    ports:
      - "6379:6379"
```

---

# Testing

The APIs can be tested using the included Postman collection.

Included test scenarios:

* Create posts
* Add comments
* Like posts
* Test cooldown restrictions
* Test horizontal cap
* Verify Redis virality scores
* Verify notification batching
* Concurrency testing

---

# Concurrency Testing

The system was tested using concurrent requests to validate:

* atomic Redis counters
* race condition prevention
* exact 100 bot reply enforcement
* database consistency

Expected result:

* 100 successful bot replies
* remaining requests rejected with HTTP 429

---

# Postman Collection

The repository includes:

```text
virality-engine.postman_collection.json
```

for easy API testing.

---

# Author

Himanshu Tiwari
