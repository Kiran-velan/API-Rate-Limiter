# ⚡ API-Rate-Limiter
![Java](https://img.shields.io/badge/Java-23-blue)
![Spring Boot](https://img.shields.io/badge/Spring--Boot-3.2.4-green)
![Maven](https://img.shields.io/badge/Maven-3.9.9-orange)

A lightweight API Gateway that limits request traffic per user using rate limiting algorithms like Fixed Window and Token Bucket. Comes with Redis-backed persistence and an Admin Dashboard for real-time analytics.

---

## 🚀 Features

- Multiple rate-limiting algorithms
- Admin dashboard with charts
- Redis for persistence
- User plans with configurable limits
- Request analytics and logs

---

## 📐 User Plans

| Plan Name | Limit | Interval | Algorithm |
|-----------|-------|----------|----------|
| FREE     | 5     | per 10s  | Fixed Window |
| PRO       | 100    | per 60s  | Token Bucket |

- Plans can be modified via enum in `PlanType`.
- Easy to add new algorithms via `RateLimiterStrategy` interface. For more details read [Design.md](Design.md)

---

## 📸 Screenshots
### Login
![Screenshot 2025-06-27 010345](https://github.com/user-attachments/assets/5ef539c5-59d3-4cf9-9123-dd1bdf6f7923)
### Admin Home Page
![Screenshot 2025-06-27 010256](https://github.com/user-attachments/assets/06f4069d-c7b9-4a90-b1ff-62e833e88401)
### Dashboard
![Screenshot 2025-06-27 105835](https://github.com/user-attachments/assets/2c200cb0-5bd7-42cf-b594-db7df0bf03cd)
### Visual Analytics
![Screenshot 2025-06-27 105636](https://github.com/user-attachments/assets/a72de46b-c48e-4b49-8600-79c016abf02d)
### User Request Logs
![Screenshot 2025-06-27 010313](https://github.com/user-attachments/assets/2ac92a78-3bea-4f7b-8068-42a4173c75d2)

---

## 🧪 How to Test with Postman

1. Set request header: `X-User-Id: <userId>`
2. Send request to: `GET localhost:8081/hello`
3. View response: `Hello, you're allowed!`
![image](https://github.com/user-attachments/assets/8835d30f-5c15-4caf-b6e6-feec09bd0971)
4. Check logs: in Ui `http://localhost:8081/login`
5. UI Login username: `admin` and passwaord = `test`

---

## 🧰 Redis Setup & Configuration

### 1. Installation (WSL/Ubuntu)
```bash
wsl --install -d Ubuntu
sudo apt update && sudo apt install redis-server
```
### 2. Essential Commands
```bash
# Start/Stop
sudo service redis-server start  # Start
sudo service redis-server stop   # Stop

# Verify
redis-cli ping                 # Should return "PONG"
sudo service redis-server status # Check running status
```
### Enable Persistence
```bash
sudo nano /etc/redis/redis.conf
```
Change:
appendonly no → appendonly yes

## Redis Command Results

![Screenshot 2025-06-27 141709](https://github.com/user-attachments/assets/31bdf340-9783-4547-a3b2-b87568c266ee)

![Screenshot 2025-06-27 142609](https://github.com/user-attachments/assets/f7cf83f9-0fe3-4540-b076-1adf0e69a36b)

![Screenshot 2025-06-27 142547](https://github.com/user-attachments/assets/1e918049-d695-4ad9-a327-bb9de3d3be92)

![Screenshot 2025-06-27 142641](https://github.com/user-attachments/assets/582b41b4-3692-472f-a55a-7034f01a880c)

![Screenshot 2025-06-27 142707](https://github.com/user-attachments/assets/0828100f-cf88-48f6-9965-2123219dd08e)

![Screenshot 2025-06-27 142737](https://github.com/user-attachments/assets/6641f9dd-1f92-4c13-a095-07afe9999413)

![Screenshot 2025-06-27 142503](https://github.com/user-attachments/assets/fe9ea0c8-4171-43dc-95ee-ecc9d7e7f65c)

![Screenshot 2025-06-27 142517](https://github.com/user-attachments/assets/5928bdbd-f116-41b6-b048-841e7361725b)

---

## 🚀 How to Run

### 1. Clone the Repository

```bash
git clone https://github.com/Kiran-velan/API-Rate-Limiter.git
```

### 2. Install Dependencies
```bash
mvn clean install
mvn spring-boot:run
```

### 3. API Endpoint
```
http://localhost:8081/admin/login
```
- username: admin
- password: test
