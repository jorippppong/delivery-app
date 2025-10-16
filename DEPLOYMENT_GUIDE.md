## 배포 가이드 (AWS EC2 + Docker)

본 문서는 `delivery-app` Spring Boot 백엔드의 AWS 배포 절차를 최신 스택 기준으로 단계별 정리합니다. 두 가지 운영 옵션을 제공합니다.

- 옵션 A: 호스트(EC2)에서 애플리케이션 실행 + Docker Compose로 MySQL/Redis만 컨테이너
- 옵션 B: 애플리케이션까지 Docker로 컨테이너 구성 (권장: 이식성/재현성 ↑)

사전 확인

- OS: Amazon Linux 2023 또는 Ubuntu 22.04 LTS 권장
- Java: Temurin/Corretto 17
- 포트: 앱 8080, MySQL 3306(노출 3308), Redis 6379
- 도메인/SSL: 필요 시 Route53 + Nginx(Reverse Proxy) + Certbot

주의 (현재 리포 상태)

- `docker-compose.yml`은 MySQL/Redis만 포함. 애플 컨테이너는 미정.
- `config/application.yml` → 빌드시 `src/main/resources/application.yml`로 복사됨.
- 현재 `application.yml`에 민감정보(API 키/JWT 키)가 하드코딩. 운영에선 환경변수/Secret으로 전환 필요.

---

## 1. AWS EC2 준비

1. EC2 생성 (Free Tier)

- 인스턴스: t2.micro/t3.micro
- AMI: Amazon Linux 2023 또는 Ubuntu 22.04
- Storage: 20GB 이상 권장
- 보안그룹 인바운드
  - 22/tcp: 본인 IP만
  - 80/tcp: HTTP (선택)
  - 443/tcp: HTTPS (선택)
  - 8080/tcp: 임시 오픈(초기 점검용). 운영에선 LB/NGINX 뒤로 숨김.
  - 3306, 6379: 외부 차단(로컬/내부만)

2. 접속 및 기본 세팅

```bash
ssh -i YOUR_KEY.pem ec2-user@EC2_PUBLIC_IP   # Amazon Linux
# 또는
ssh -i YOUR_KEY.pem ubuntu@EC2_PUBLIC_IP     # Ubuntu
```

- 패키지 업데이트 및 필수 설치

```bash
# Amazon Linux
sudo dnf update -y
sudo dnf install -y git docker

# Ubuntu
sudo apt update && sudo apt upgrade -y
sudo apt install -y git docker.io
```

- Docker 권한/서비스

```bash
sudo usermod -aG docker $USER
sudo systemctl enable --now docker
newgrp docker

# docker compose v2 설치 (필요 시)
sudo mkdir -p /usr/local/lib/docker/cli-plugins
sudo curl -SL https://github.com/docker/compose/releases/download/v2.29.7/docker-compose-linux-$(uname -m) -o /usr/local/lib/docker/cli-plugins/docker-compose
sudo chmod +x /usr/local/lib/docker/cli-plugins/docker-compose

docker compose version
```

- Java 17 설치

```bash
# Amazon Linux (Corretto)
sudo dnf install -y java-17-amazon-corretto
java -version

# Ubuntu (Temurin 예시)
sudo apt install -y wget
wget -O- https://packages.adoptium.net/artifactory/api/gpg/key/public | sudo tee /etc/apt/keyrings/adoptium.asc > /dev/null
echo "deb [signed-by=/etc/apt/keyrings/adoptium.asc] https://packages.adoptium.net/artifactory/deb $(. /etc/os-release; echo $VERSION_CODENAME) main" | sudo tee /etc/apt/sources.list.d/adoptium.list
sudo apt update && sudo apt install -y temurin-17-jdk
java -version
```

---

## 2. 프로젝트 가져오기

권장: Git 클론. FileZilla로 올려도 되지만, 재현성/업데이트가 어려움.

```bash
cd ~
git clone https://YOUR_GIT_REPO_URL.git delivery-app
cd delivery-app
```

환경변수/시크릿 관리 준비 (.env)

**방법**: 단일 `application.yml` + `.env` 파일로 환경별 설정 관리

```bash
# .env 파일 생성 (EC2에서 실행)
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
DDL_AUTO=validate
SHOW_SQL=false
SQL_LOG_LEVEL=WARN
BINDER_LOG_LEVEL=WARN
EOF

# 파일 권한 설정
chmod 600 .env

# 확인
cat .env
```

**참고**:

- `config/application.yml`은 빌드 시 `src/main/resources/application.yml`로 자동 복사됩니다.
- 로컬 개발 시에는 프로젝트 루트의 `.env` 파일 사용 (IntelliJ 자동 인식)
- 운영 배포 시에는 EC2에서 `.env` 파일 생성하여 환경변수 주입

---

## 3. 데이터베이스/Redis (Docker Compose)

리포의 `docker-compose.yml`은 MySQL/Redis만 포함합니다.

```bash
docker compose up -d
# mysql: 3308(호스트) → 3306(컨테이너)
# redis: 6379
```

보안 메모

- 보안그룹에서 3308/3306, 6379는 외부 차단.
- RDS/ElastiCache 고려 시 보안/확장성 ↑.

---

## 4A. 옵션 A: 호스트에서 Spring Boot 실행

장점: 단순. 단점: 이식성 낮음, 롤백/스케일 제약.

1. Gradle 빌드

```bash
./gradlew clean build -x test
```

2. 실행 (환경변수 적용 예시)

```bash
export SPRING_DATASOURCE_URL="jdbc:mysql://localhost:3308/foodorder?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true"
export SPRING_DATASOURCE_USERNAME="foodorder"
export SPRING_DATASOURCE_PASSWORD="foodorder123"
export SPRING_DATA_REDIS_HOST="localhost"
export SPRING_DATA_REDIS_PORT="6379"
# JWT/AI 키는 환경변수로 전달 권장
export JWT_SECRET_KEY="<set-in-ssm-or-env>"
export JWT_REFRESH_KEY="<set-in-ssm-or-env>"
export AI_GEMINI_API_KEY="<set-in-ssm-or-env>"

java -jar build/libs/foodorder-0.0.1-SNAPSHOT.jar --server.port=8080
```

3. 서비스 등록 (systemd)

```bash
sudo tee /etc/systemd/system/foodorder.service > /dev/null << 'UNIT'
[Unit]
Description=Foodorder Spring Boot
After=network.target docker.service

[Service]
User=ec2-user
WorkingDirectory=/home/ec2-user/delivery-app
Environment=SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3308/foodorder?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
Environment=SPRING_DATASOURCE_USERNAME=foodorder
Environment=SPRING_DATASOURCE_PASSWORD=foodorder123
Environment=SPRING_DATA_REDIS_HOST=localhost
Environment=SPRING_DATA_REDIS_PORT=6379
Environment=JWT_SECRET_KEY=<set-in-ssm-or-env>
Environment=JWT_REFRESH_KEY=<set-in-ssm-or-env>
Environment=AI_GEMINI_API_KEY=<set-in-ssm-or-env>
ExecStart=/usr/bin/java -jar build/libs/foodorder-0.0.1-SNAPSHOT.jar --server.port=8080
Restart=always
RestartSec=5

[Install]
WantedBy=multi-user.target
UNIT

sudo systemctl daemon-reload
sudo systemctl enable --now foodorder
sudo systemctl status foodorder
```

---

## 4B. 옵션 B: 애플리케이션도 Docker로 (권장)

1. Dockerfile 예시 (프로젝트 루트에 생성)

```dockerfile
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app
COPY . .
RUN ./gradlew clean bootJar -x test

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/build/libs/foodorder-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENV JAVA_OPTS=""
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
```

2. docker-compose.yml 확장 (app 서비스 추가)

```yaml
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

3. .env 파일 (루트)

```env
DB_HOST=mysql
DB_PORT=3306
DB_USERNAME=foodorder
DB_PASSWORD=foodorder123
REDIS_HOST=redis
REDIS_PORT=6379
JWT_SECRET_KEY=your-secret-key
JWT_REFRESH_KEY=your-refresh-key
AI_GEMINI_API_KEY=your-api-key
DDL_AUTO=validate
SHOW_SQL=false
SQL_LOG_LEVEL=WARN
BINDER_LOG_LEVEL=WARN
```

4. 빌드/실행

```bash
docker compose build --no-cache
docker compose up -d
```

5. 이미지 레지스트리 사용(선택)

- ECR 생성 → `docker build` → `docker tag` → `docker push`
- EC2에서 `docker pull` 후 `docker compose up -d`

---

## 5. 리버스 프록시/HTTPS (선택)

Nginx + Certbot

```bash
sudo dnf install -y nginx certbot python3-certbot-nginx
sudo systemctl enable --now nginx
# 서버 블록에서 80 → 8080 프록시, 도메인 연결
sudo certbot --nginx -d your.domain.com
```

기본 Nginx 설정 예시

```nginx
server {
  listen 80;
  server_name your.domain.com;

  location / {
    proxy_pass http://127.0.0.1:8080;
    proxy_set_header Host $host;
    proxy_set_header X-Real-IP $remote_addr;
    proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    proxy_set_header X-Forwarded-Proto $scheme;
  }
}
```

---

## 6. 운영 보안/시크릿

- 하드코딩 제거: `application.yml`의 JWT/AI 키 제거 → 환경변수 주입
- AWS SSM Parameter Store/Secrets Manager 도입 권장
- DB/Redis 포트 외부 차단, 보안그룹 최소화
- EC2에 UFW/Firewalld 설정, SSH 키 관리, `sudo` 감사로그

---

## 7. 점검/검증 체크리스트

- 헬스체크: `GET /actuator/health`(추가 권장) 또는 스웨거 확인
- DB 연결: `docker logs foodorder-mysql`, 애플 로그에서 Hikari 연결 확인
- Redis 연결: `docker logs foodorder-redis`
- API: 서버에서 `curl http://localhost:8080/v3/api-docs` 확인

---

## 8. 트러블슈팅

- 포트 충돌: EC2에서 이미 사용 중인 포트 확인 → `sudo lsof -i :8080`/`:3308`
- 메모리 부족: t2.micro OOM → `-Xms256m -Xmx512m` 제한, 스왑(임시)
- 타임존/로케일: 컨테이너 TZ 설정 필요 시 `TZ=Asia/Seoul`
- 마이그레이션: 현재 `ddl-auto=update` → 운영에선 Flyway/Liquibase 도입 권장

---

## 9. 운영 팁

- 로그: CloudWatch/Fluent Bit로 중앙집중화
- 백업: MySQL 볼륨 스냅샷 또는 RDS 전환
- 무중단: ALB + 2대 롤링 또는 Blue/Green(ECS/EKS) 검토

---

## 부록: 현재 리포 설정 상 유의점

- `config/application.yml`가 빌드 시 복사되므로, CI/CD에서 운영 profie 분리(`application-prod.yml`) + `--spring.profiles.active=prod` 권장
- 외부키(API/JWT)는 코드 저장소에 남기지 않기
- Compose의 MySQL 루트/유저 패스워드 즉시 변경 권장
