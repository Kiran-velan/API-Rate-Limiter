# API Rate Limiter Gateway - Design Document

## 1. Overview

This document describes the architectural design, key components, file responsibilities, and strategies used in the API Rate Limiting Gateway. The project supports multiple algorithms, Redis integration, an Admin UI dashboard, and user-based configuration with real-time analytics.

---

## Architecture Overview

- **Backend**: Java 24, Spring Boot 3.2.4
- **Frontend**: Thymeleaf + Chart.js
- **Storage**: In-memory & Redis
- **Build Tool**: Maven 3.9.9
- **Metrics**: Prometheus + Spring Actuator
- **Security**: Spring Security (admin login)

---

## 2. Core Components

### 2.1 Configuration (`config/`)
- `RateLimitConfig`: Central configuration for rate limiting rules

### 2.2 Controllers (`controller/`)
- `TestController`: API endpoints for testing rate limiting
- `AdminController`: Main Admin endpoints for monitoring and management
- `AdminUIController`: Endpoint for dashboard to show or update users and their plans

### 2.3 Middleware (`middleware/`)
- `RateLimiterMiddleware`: Handles rate limit logic and response formatting

### 2.4 Models (`model/`)
- `PlanType`: enum class type to configure different plans
- `UserPlan`: Standardized API response format

### 2.5 Security (`security/`)
- `SecurityConfig`: Spring Security configuration

### 2.6 Services (`service/`)
- `LogRequestService`: Manages rate limiting logs
- `RequestMetricsService`: Redis Backed History for Metrics Visualization
- `UserPlanService`: Redis-backed rate limiting implementation

### 2.7 Strategies (`strategy/`)
- `RateLimitStrategy`: Interface for different rate limiting algorithms
- `TokenBucketStrategy`: Token bucket implementation
- `FixedWindowStrategy`: Fixed window counter implementation
- `RedisTokenBucketStrategy`: Redis backed Token bucket implementation
- `RedisFixedWindowStrategy`: Redis backed Fixed window implementation
- `StrategyFactory`: Maps the user with the plan basedstrategy

---

## 3. Supported Rate Limiting Strategies
- Configure the rates in PlanType enum
### To add new algorithms:
  - Create a new file in strategy/
  - Implement(In-memory/Redis backed) algorithm into the file
  - Add a Key:value map in strategy/StrategyFactory.java
  - Create a new model/PlanType.java and map the algorithm and its limits 

1. **Token Bucket**
   - Configurable token refill rate
   - Burst handling
   - Memory efficient

2. **Fixed Window Counter**
   - Simple implementation
   - Low memory footprint
   - Potential for boundary condition issues

| Strategy       | Status      | Description                                  |
|----------------|-------------|----------------------------------------------|
| Fixed Window   |  Done      | Limits requests per fixed time window        |
| Token Bucket   |  Done      | Tokens refilled over time, request uses one  |
| Leaky Bucket   |  If needed   | Queues and processes requests at a rate      |
| Sliding Window |  If needed   | Time-bucketed count over rolling intervals   |

Each strategy implements a common interface:  
```java
interface RateLimiterStrategy {
    RateLimitResponse isAllowed(String userId);
}
```
---

## 4. Middleware Design

### 4.1 Request Flow
1. Request enters the `RateLimiterMiddleware`
2. Client identification (User Id) is extracted
3. Maps user → plan → appropriate rate-limiting strategy
4. Request is processed by the appropriate strategy
5. Response is modified with rate limit headers
6. Each request is logged (timestamp, status, userId)

### 4.2 Security Middleware
- IP whitelisting/blacklisting
- Request validation adn Filtering

---

## 5. Redis Integration

### 5.1 Data Structure
- **Key Formats**:
  - user:{userId}:tokens → token count
  - user:{userId}:requests:{timestamp} → request logs
  - user:{userId}:logs → recent activity for UI display
- **Value**: Current token count or request count
- **TTL**: Automatically expires based on window size

### 5.2 Redis Operations
- Persistence: Redis stores per-user counters, token buckets, and logs
- Durability: Enabled via appendonly yes and proper shutdown scripts

---

## 6. Admin UI

### 6.1 Technology Stack
- Frontend: html, css, js (Thymeleaf)
- State Management: Redis
- Charts: charts.js

### 6.2 Metrics and Monitoring
- Real-time request rate
- Rate limit violations
- Historical data visualization
- User wise logs history

---

## 7. Dashboard and Visuals

The Admin Dashboard provides comprehensive monitoring and management capabilities through two main views:

### 7.1 Dashboard View (`dashboard.html`)

**Key Features:**
- **User Management Table**
  - Displays all registered UserIDs and their current plan

- **Plan Management**
  - Dropdown selector to upgrade/downgrade user plans
  - One-click plan modification with confirmation dialog
  - Real-time plan change reflection in the system

### 7.2 Analytics View (`chart.html`)

**Interactive Visualizations:**

1. **Request Timeline (Line Chart)**
   - Time-series display of total requests
   - Hover tooltips showing exact counts per minute

2. **User Activity Breakdown (Bar Chart)**
   - Horizontal bars comparing users' request volumes
   - Dual-color bars showing allowed (green) vs blocked (red) requests

3. **Plan Distribution (Pie Chart)**
   - Visual representation of plan adoption
   - Exploded view on hover showing exact user counts
   - Dynamic updates when plans are modified

### 7.2 Logs View (`logs.html`)

- Shows Logs History of all the users

---

## 8. Data Structures

### In-Memory
- Map<String, FixedWindowBucket> fixedWindowMap
- Map<String, TokenBucket> tokenBucketMap
- Map<String, List<UsageLog>> usageLogsMap

### Redis Keys
- user:{id}:tokens → int
- user:{id}:logs → list of json
- user:{id}:window → timestamp count

---

## 9. Project Structure

```text
src/
└── main/
    ├── java/com/example/ratelimiter/
    │   ├── config/            # Configures endpoints for rate limiting
    │   ├── controller/        # Handles REST and Admin UI endpoints
    │   ├── middleware/        # Intercepts and validates requests
    │   ├── strategy/          # Rate limit algorithms (Fixed, Token etc.)
    │   ├── service/           # Business logic services
    │   ├── model/             # Data transfer objects and entities
    │   └── security/          # Authentication and authorization
    └── resources/
        └── templates/         # Thymeleaf admin UI templates
```
