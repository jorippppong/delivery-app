# 환경 설정 가이드

## 개요

이 프로젝트는 **Spring Profile**을 사용하여 개발(dev)과 프로덕션(prod) 환경을 분리합니다.

## 환경별 차이점

| 설정 항목     | 개발(dev)             | 프로덕션(prod)            |
| ------------- | --------------------- | ------------------------- |
| Swagger URL   | http://localhost:8080 | http://15.165.159.66:8080 |
| 쿠키 secure   | false                 | true                      |
| 쿠키 sameSite | Lax                   | Lax                       |
| JPA ddl-auto  | update                | validate                  |
| SQL 로그      | DEBUG                 | INFO                      |

## 설정 방법

### 1. 개발 환경 (로컬)

#### 1-1. .env 파일 생성

```bash
cp .env.example .env
```

#### 1-2. .env 파일 수정

```properties
# .env
SPRING_PROFILES_ACTIVE=dev

DB_HOST=localhost
DB_PORT=3308
DB_USERNAME=foodorder
DB_PASSWORD=foodorder123

REDIS_HOST=localhost
REDIS_PORT=6379

JWT_SECRET_KEY=your-dev-secret-key
JWT_REFRESH_KEY=your-dev-refresh-key
AI_GEMINI_API_KEY=your-gemini-api-key
```

#### 1-3. Docker로 MySQL/Redis 실행

```bash
docker-compose up -d
```

#### 1-4. 애플리케이션 실행

```bash
./gradlew bootRun
```

또는 IDE에서:

- VM Options: `-Dspring.profiles.active=dev`
- 또는 Environment Variables: `SPRING_PROFILES_ACTIVE=dev`

#### 1-5. Swagger 접속

- http://localhost:8080/swagger-ui/index.html
- 서버 URL이 자동으로 `http://localhost:8080`으로 설정됨

---

### 2. 프로덕션 환경 (배포)

#### 2-1. .env 파일 생성

```bash
cp .env.prod.example .env
```

#### 2-2. .env 파일 수정 (보안 키 변경 필수!)

```properties
# .env
SPRING_PROFILES_ACTIVE=prod

DB_HOST=mysql
DB_PORT=3306
DB_USERNAME=foodorder
DB_PASSWORD=STRONG_PRODUCTION_PASSWORD

REDIS_HOST=redis
REDIS_PORT=6379

JWT_SECRET_KEY=PRODUCTION_SECRET_KEY_CHANGE_THIS
JWT_REFRESH_KEY=PRODUCTION_REFRESH_KEY_CHANGE_THIS
AI_GEMINI_API_KEY=your-production-gemini-api-key
```

#### 2-3. Docker Compose로 배포

```bash
docker-compose -f docker-compose.prod.yml up -d
```

#### 2-4. Swagger 접속

- http://15.165.159.66:8080/swagger-ui/index.html
- 서버 URL이 자동으로 `http://15.165.159.66:8080`으로 설정됨

---

## Profile 확인

애플리케이션 실행 시 로그에서 확인 가능:

```
The following 1 profile is active: "dev"
```

또는

```
The following 1 profile is active: "prod"
```

---

## 주요 설정 파일

```
delivery-app/
├── .env                              # 환경 변수 (gitignore됨)
├── .env.example                      # 개발 환경 예시
├── .env.prod.example                 # 프로덕션 환경 예시
├── src/main/resources/
│   ├── application.yml               # 공통 설정
│   ├── application-dev.yml           # 개발 환경 설정
│   └── application-prod.yml          # 프로덕션 환경 설정
```

---

## 쿠키 설정 동작

### 개발 환경 (dev)

- `secure=false` → HTTP에서도 쿠키 전송
- `sameSite=Lax` → CSRF 방어 + Swagger UI 호환

### 프로덕션 환경 (prod)

- `secure=true` → HTTPS에서만 쿠키 전송
- `sameSite=Lax` → CSRF 방어 + 일반적인 사용 패턴 지원

**중요:** 로그아웃 시 쿠키 삭제는 생성 시와 **동일한 설정**을 사용합니다.

---

## 트러블슈팅

### Swagger에서 프로덕션 서버로 요청이 가는 경우

**원인:** Profile이 prod로 설정됨

**해결:**

```bash
# .env 파일 확인
SPRING_PROFILES_ACTIVE=dev
```

### 쿠키가 삭제되지 않는 경우

**원인:** 생성 시와 삭제 시 쿠키 속성이 다름

**해결:** 이제 자동으로 환경 설정을 따릅니다.

### Profile이 적용되지 않는 경우

**해결:**

```bash
# 캐시 삭제
./gradlew clean

# 다시 빌드
./gradlew build
```

---

## 보안 권장사항

1. **프로덕션 JWT 키는 반드시 변경**

   - 최소 32자 이상
   - 무작위 생성 (openssl rand -base64 32)

2. **데이터베이스 비밀번호 강화**

   - 최소 16자 이상
   - 특수문자, 숫자 포함

3. **.env 파일은 절대 Git에 커밋하지 않음**

   - `.gitignore`에 추가됨

4. **프로덕션에서는 HTTPS 사용**
   - `secure=true`이므로 HTTP에서는 쿠키 전송 안 됨
