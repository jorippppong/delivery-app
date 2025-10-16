# 개발/운영 환경 분리 가이드

## 개요

Spring Boot 프로젝트의 개발 환경과 운영 환경을 분리하여 효율적으로 관리하는 방법을 설명합니다.

## 핵심 개념

### 환경 분리 이유

- **개발**: IntelliJ에서 직접 실행, 디버깅 편의성
- **운영**: Docker 컨테이너로 실행, 안정성과 확장성

### 환경변수 관리

- `.env` 파일로 민감 정보 분리
- `spring-dotenv` 라이브러리로 자동 로드

---

## 1. 프로젝트 구조

```
delivery-app/
├── .env                           # 환경변수 (Git 제외)
├── .env.example                   # 환경변수 예시 (Git 포함)
├── docker-compose.dev.yml         # 개발용 (MySQL, Redis만)
├── docker-compose.prod.yml        # 운영용 (MySQL, Redis, App)
├── Dockerfile                     # 운영용 이미지 빌드
├── config/
│   └── application.yml            # 환경변수 사용
└── build.gradle                   # spring-dotenv 의존성 포함
```

---

## 2. 환경변수 설정

### `.env` 파일 (프로젝트 루트)

```env
# 데이터베이스
DB_HOST=localhost
DB_PORT=3308
DB_USERNAME=foodorder
DB_PASSWORD=foodorder123

# Redis
REDIS_HOST=localhost
REDIS_PORT=6379

# JWT 키 (실제 값)
JWT_SECRET_KEY=aHR0cHM6Ly93d3cueW91dHViZS5jb20vd2F0Y2g/dj1kUXc0dzlXZ1hjUQ==
JWT_REFRESH_KEY=aHR0cHM6Ly93d3cueW91dHViZS5jb20vd2F0Y2g/dj1kUXc0dzlXZ1hjUQ==

# AI API 키
AI_GEMINI_API_KEY=AIzaSyC2X0UoiwzsRZWn-VNrQLNQzH-Xy7g5OgQ
```

### `application.yml` 설정

```yaml
spring:
  datasource:
    url: jdbc:mysql://${DB_HOST}:${DB_PORT}/foodorder?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
    driver-class-name: com.mysql.cj.jdbc.Driver

  data:
    redis:
      host: ${REDIS_HOST}
      port: ${REDIS_PORT}
      timeout: 3000ms

jwt:
  secretKey: ${JWT_SECRET_KEY}
  refreshKey: ${JWT_REFRESH_KEY}

ai:
  providers:
    gemini:
      api-key: ${AI_GEMINI_API_KEY}
```

---

## 3. 의존성 설정

### `build.gradle`

```gradle
dependencies {
    // 기존 의존성들...

    // .env 파일 자동 로드
    implementation 'me.paulschwarz:spring-dotenv:4.0.0'
}
```

---

## 4. Docker Compose 설정

### 개발용 (`docker-compose.dev.yml`)

```yaml
version: "3.8"

# 개발 환경용: MySQL, Redis만 실행
# 앱은 IntelliJ에서 직접 실행

services:
  mysql:
    image: mysql:8.0
    container_name: foodorder-mysql
    environment:
      MYSQL_ROOT_PASSWORD: root123
      MYSQL_DATABASE: foodorder
      MYSQL_USER: foodorder
      MYSQL_PASSWORD: foodorder123
    ports:
      - "3308:3306"
    volumes:
      - mysql_data:/var/lib/mysql
    command: --default-authentication-plugin=mysql_native_password
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost"]
      timeout: 20s
      retries: 10

  redis:
    image: redis:latest
    container_name: foodorder-redis
    ports:
      - "6379:6379"
    volumes:
      - redis_data:/data
    command: redis-server --appendonly yes
    healthcheck:
      test: ["CMD", "redis-cli", "ping"]
      timeout: 10s
      retries: 5

volumes:
  mysql_data:
  redis_data:
```

### 운영용 (`docker-compose.prod.yml`)

```yaml
version: "3.8"

# 운영 환경용 (EC2): MySQL, Redis, App 모두 실행

services:
  mysql:
    image: mysql:8.0
    container_name: foodorder-mysql
    environment:
      MYSQL_ROOT_PASSWORD: root123
      MYSQL_DATABASE: foodorder
      MYSQL_USER: foodorder
      MYSQL_PASSWORD: foodorder123
    ports:
      - "3308:3306"
    volumes:
      - mysql_data:/var/lib/mysql
    command: --default-authentication-plugin=mysql_native_password
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost"]
      timeout: 20s
      retries: 10

  redis:
    image: redis:latest
    container_name: foodorder-redis
    ports:
      - "6379:6379"
    volumes:
      - redis_data:/data
    command: redis-server --appendonly yes
    healthcheck:
      test: ["CMD", "redis-cli", "ping"]
      timeout: 10s
      retries: 5

  app:
    build: .
    container_name: foodorder-app
    depends_on:
      - mysql
      - redis
    env_file: .env
    environment:
      DB_HOST: mysql
      DB_PORT: 3306
      REDIS_HOST: redis
      REDIS_PORT: 6379
    ports:
      - "8080:8080"
    restart: always

volumes:
  mysql_data:
  redis_data:
```

---

## 5. 사용 방법

### 로컬 개발 환경

#### 1) 의존성 다운로드

```bash
./gradlew clean build
```

#### 2) 데이터베이스 실행

```bash
docker compose -f docker-compose.dev.yml up -d
```

#### 3) IntelliJ에서 실행

- **별도 설정 불필요**
- `.env` 파일이 자동으로 로드됨
- `localhost:3308` (MySQL), `localhost:6379` (Redis) 연결

#### 4) API 테스트

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- API 문서: `http://localhost:8080/v3/api-docs`

### 운영 환경 (EC2)

#### 1) 프로젝트 클론

```bash
git clone https://github.com/your-repo.git
cd delivery-app
```

#### 2) 환경변수 설정

```bash
cat > .env << EOF
DB_HOST=mysql
DB_PORT=3306
DB_USERNAME=foodorder
DB_PASSWORD=foodorder123
REDIS_HOST=redis
REDIS_PORT=6379
JWT_SECRET_KEY=your-production-secret-key
JWT_REFRESH_KEY=your-production-refresh-key
AI_GEMINI_API_KEY=your-production-api-key
EOF

chmod 600 .env
```

#### 3) 전체 서비스 실행

```bash
docker compose -f docker-compose.prod.yml up -d
```

#### 4) 서비스 확인

```bash
# 컨테이너 상태 확인
docker compose -f docker-compose.prod.yml ps

# 로그 확인
docker compose -f docker-compose.prod.yml logs app

# API 테스트
curl http://localhost:8080/v3/api-docs
```

---

## 6. 장점

### 개발 환경

- ✅ IntelliJ 디버깅 편의성
- ✅ 환경변수 자동 로드 (설정 불필요)
- ✅ 빠른 재시작
- ✅ 로컬 DB/Redis 사용

### 운영 환경

- ✅ 완전한 컨테이너화
- ✅ 환경별 설정 분리
- ✅ 확장성과 안정성
- ✅ 배포 자동화 가능

---

## 7. 주의사항

### 보안

- `.env` 파일은 절대 Git에 커밋하지 않기
- `.gitignore`에 `.env` 추가 확인
- 운영 환경에서는 실제 키 값 사용

### 환경별 차이점

- **개발**: `DB_HOST=localhost`, `DB_PORT=3308`
- **운영**: `DB_HOST=mysql`, `DB_PORT=3306`

### 트러블슈팅

- 환경변수 로드 안됨 → `spring-dotenv` 의존성 확인
- DB 연결 실패 → Docker 컨테이너 상태 확인
- 포트 충돌 → `docker compose ps`로 포트 사용 확인

---

## 8. 추가 팁

### 개발 편의성

```bash
# 개발용 DB만 재시작
docker compose -f docker-compose.dev.yml restart mysql

# 로그 실시간 확인
docker compose -f docker-compose.dev.yml logs -f mysql
```

### 운영 모니터링

```bash
# 리소스 사용량 확인
docker stats

# 컨테이너 내부 접속
docker compose -f docker-compose.prod.yml exec app sh
```

이제 개발과 운영 환경이 깔끔하게 분리되어 효율적으로 작업할 수 있습니다! 🚀
