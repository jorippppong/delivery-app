# 🚀 환경 설정 빠른 가이드

## ⚡ 빠른 시작

### 개발 환경 (localhost)

```bash
# 1. .env 파일 생성
cat > .env << EOF
SPRING_PROFILES_ACTIVE=dev
DB_HOST=localhost
DB_PORT=3308
DB_USERNAME=foodorder
DB_PASSWORD=foodorder123
REDIS_HOST=localhost
REDIS_PORT=6379
JWT_SECRET_KEY=dev-secret-key-at-least-32-chars
JWT_REFRESH_KEY=dev-refresh-key-at-least-32-chars
AI_GEMINI_API_KEY=your-api-key
EOF

# 2. Docker 실행
docker-compose up -d

# 3. 앱 실행
./gradlew bootRun

# 4. Swagger 접속
# http://localhost:8080/swagger-ui/index.html
```

### 프로덕션 환경 (서버)

```bash
# 1. .env 파일 생성 (보안 키 변경 필수!)
cat > .env << EOF
SPRING_PROFILES_ACTIVE=prod
DB_HOST=mysql
DB_PORT=3306
DB_USERNAME=foodorder
DB_PASSWORD=CHANGE_THIS_STRONG_PASSWORD
REDIS_HOST=redis
REDIS_PORT=6379
JWT_SECRET_KEY=$(openssl rand -base64 32)
JWT_REFRESH_KEY=$(openssl rand -base64 32)
AI_GEMINI_API_KEY=your-api-key
EOF

# 2. Docker Compose로 배포
docker-compose -f docker-compose.prod.yml up -d

# 3. Swagger 접속
# http://15.165.159.66:8080/swagger-ui/index.html
```

---

## 📋 주요 변경 사항

### ✅ 해결된 문제

1. **Swagger URL 자동 전환**

   - 개발: `http://localhost:8080`
   - 프로덕션: `http://15.165.159.66:8080`

2. **쿠키 설정 자동 조정**

   - 개발: `secure=false` (HTTP 가능)
   - 프로덕션: `secure=true` (HTTPS 전용)

3. **환경별 설정 분리**
   - `application-dev.yml` / `application-prod.yml`

---

## 🔧 환경 확인

```bash
# 현재 profile 확인
grep SPRING_PROFILES_ACTIVE .env

# 로그에서 확인
# 출력: The following 1 profile is active: "dev"
```

---

## 💡 주요 설정

| 항목              | 개발(dev)      | 프로덕션(prod)     |
| ----------------- | -------------- | ------------------ |
| **Swagger URL**   | localhost:8080 | 15.165.159.66:8080 |
| **쿠키 secure**   | false          | true               |
| **쿠키 sameSite** | Lax            | Lax                |
| **SQL 로그**      | DEBUG          | INFO               |
| **JPA ddl-auto**  | update         | validate           |

---

## 🚨 중요 사항

1. **.env 파일은 Git에 커밋하지 마세요**
2. **프로덕션 JWT 키는 반드시 변경하세요**
3. **프로덕션에서는 HTTPS를 사용하세요** (secure=true)

---

더 자세한 내용은 [ENVIRONMENT_CONFIG.md](./ENVIRONMENT_CONFIG.md)를 참고하세요.
