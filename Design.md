### Which strategies will you support?

### How middleware is used?

### Redis integration plan

### Admin UI plan (tech used, metrics to show)

### Data structure (in-memory maps, Redis keys format)


# API Rate Limiting Gateway — Design Doc

## 🎯 Objective
To build a flexible, extensible API gateway that limits incoming traffic per user or plan using different algorithms like:
- Fixed Window
- Sliding Window
- Token Bucket
- Leaky Bucket

Supports:
- Strategy selection per user
- In-memory + Redis storage
- Admin UI to configure and monitor limits

---

## 🏗️ Architecture

1. **Incoming Request**
2. **Rate Limiter Middleware** (filter)
3. **Rate Limit Strategy** (pluggable interface)
4. **Decision: Allow or Block**
5. **Controller Logic** (if allowed)
6. **Optional Logging / Metrics**

---

## 📦 Components

### Middleware
- Intercepts requests
- Extracts user info (via headers or tokens)
- Applies appropriate rate limiting logic

### Strategy Interface
```java
boolean allowRequest(String userId);
