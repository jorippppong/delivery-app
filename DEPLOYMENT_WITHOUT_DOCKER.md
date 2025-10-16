# Docker 없는 배포 가이드

## 🚀 방법 1: JAR 파일 직접 실행 (권장)

### 1-1. 로컬에서 JAR 빌드

```bash
# 프로덕션용 JAR 빌드
./gradlew clean build -x test

# JAR 파일 위치 확인
ls -la build/libs/foodorder-0.0.1-SNAPSHOT.jar
```

### 1-2. 서버에 JAR 업로드

```bash
# SCP로 서버에 업로드
scp build/libs/foodorder-0.0.1-SNAPSHOT.jar user@15.165.159.66:/home/user/

# 또는 SFTP 사용
sftp user@15.165.159.66
put build/libs/foodorder-0.0.1-SNAPSHOT.jar
```

### 1-3. 서버에서 실행

```bash
# 서버 접속
ssh user@15.165.159.66

# 환경변수 설정
export SPRING_PROFILES_ACTIVE=prod
export DB_HOST=your-mysql-host
export DB_PORT=3306
export DB_USERNAME=foodorder
export DB_PASSWORD=your-password
export REDIS_HOST=your-redis-host
export REDIS_PORT=6379
export JWT_SECRET_KEY=your-secret-key
export JWT_REFRESH_KEY=your-refresh-key
export AI_GEMINI_API_KEY=your-api-key

# JAR 실행
java -jar foodorder-0.0.1-SNAPSHOT.jar
```

---

## 🔧 방법 2: 환경변수 파일 사용

### 2-1. 서버에 .env 파일 생성

```bash
# 서버에서
cat > .env << 'EOF'
SPRING_PROFILES_ACTIVE=prod
DB_HOST=your-mysql-host
DB_PORT=3306
DB_USERNAME=foodorder
DB_PASSWORD=your-password
REDIS_HOST=your-redis-host
REDIS_PORT=6379
JWT_SECRET_KEY=your-secret-key
JWT_REFRESH_KEY=your-refresh-key
AI_GEMINI_API_KEY=your-api-key
EOF
```

### 2-2. 환경변수 로드 후 실행

```bash
# 환경변수 로드
source .env

# JAR 실행
java -jar foodorder-0.0.1-SNAPSHOT.jar
```

---

## 🛠️ 방법 3: systemd 서비스 등록 (프로덕션 권장)

### 3-1. 서비스 파일 생성

```bash
sudo nano /etc/systemd/system/foodorder.service
```

### 3-2. 서비스 설정

```ini
[Unit]
Description=Food Order Application
After=network.target

[Service]
Type=simple
User=ubuntu
WorkingDirectory=/home/ubuntu
EnvironmentFile=/home/ubuntu/.env
ExecStart=/usr/bin/java -jar /home/ubuntu/foodorder-0.0.1-SNAPSHOT.jar
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
```

### 3-3. 서비스 시작

```bash
# 서비스 등록
sudo systemctl daemon-reload
sudo systemctl enable foodorder
sudo systemctl start foodorder

# 상태 확인
sudo systemctl status foodorder

# 로그 확인
sudo journalctl -u foodorder -f
```

---

## 📋 필요한 서버 환경

### 필수 설치

```bash
# Java 17 설치
sudo apt update
sudo apt install openjdk-17-jdk

# Java 버전 확인
java -version
```

### 외부 서비스 필요

- **MySQL**: AWS RDS 또는 외부 MySQL 서버
- **Redis**: AWS ElastiCache 또는 외부 Redis 서버

---

## 🔍 환경변수 확인

### 실행 전 확인

```bash
# 환경변수 확인
echo $SPRING_PROFILES_ACTIVE
echo $DB_HOST
echo $JWT_SECRET_KEY
```

### 애플리케이션 로그 확인

```bash
# 실행 시 로그에서 확인
# "The following 1 profile is active: prod"
```

---

## 🚨 주의사항

1. **포트 열기**: 서버 방화벽에서 8080 포트 열기
2. **보안**: JWT 키는 반드시 강력하게 설정
3. **백업**: 정기적인 데이터베이스 백업
4. **모니터링**: 로그 모니터링 설정

---

## 💡 추천 방법

**프리티어에서는 방법 1 (JAR 직접 실행)이 가장 간단합니다!**

```bash
# 한 번에 실행
export SPRING_PROFILES_ACTIVE=prod && \
export DB_HOST=your-host && \
export JWT_SECRET_KEY=your-key && \
java -jar foodorder-0.0.1-SNAPSHOT.jar
```
