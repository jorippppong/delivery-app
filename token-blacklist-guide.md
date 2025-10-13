# 토큰 블랙리스트란? 🛡️

## 📚 목차

1. [토큰이 뭔가요?](#토큰이-뭔가요)
2. [왜 블랙리스트가 필요해요?](#왜-블랙리스트가-필요해요)
3. [블랙리스트는 어떻게 작동해요?](#블랙리스트는-어떻게-작동해요)
4. [실제 예시로 이해하기](#실제-예시로-이해하기)
5. [우리 프로젝트에서는 어떻게 사용해요?](#우리-프로젝트에서는-어떻게-사용해요)

---

## 토큰이 뭔가요? 🎫

### 🎪 놀이공원 입장권에 비유해보세요!

```
🏰 놀이공원 = 우리 웹사이트
🎫 입장권 = 토큰 (JWT)
👮‍♂️ 검표원 = 서버
```

**입장권의 특징:**

- 입장권에는 "어떤 놀이기구를 탈 수 있는지" 적혀있어요
- 검표원은 입장권만 보고 "통과!" 또는 "안 돼!"라고 해요
- 입장권이 유효한지 확인하는 것만 해요

**문제점:**

- 입장권을 잃어버려도 다른 사람이 주워서 사용할 수 있어요 😱
- 놀이공원을 나가도 입장권은 계속 유효해요 😰

---

## 왜 블랙리스트가 필요해요? 🚨

### 🏠 집 열쇠로 생각해보세요!

#### ❌ 블랙리스트가 없다면?

```
1. 집 열쇠를 잃어버렸어요 🔑
2. 하지만 문은 여전히 열려요! 😱
3. 도둑이 열쇠를 주워서 집에 들어올 수 있어요 🏃‍♂️💨
```

#### ✅ 블랙리스트가 있다면?

```
1. 집 열쇠를 잃어버렸어요 🔑
2. 즉시 "이 열쇠는 사용하면 안 돼요!" 목록에 추가 📝
3. 도둑이 열쇠를 사용해도 문이 안 열려요! 🛡️
```

### 🎮 게임에서도 비슷해요!

**게임 아이템 도둑질 시나리오:**

```
👤 플레이어: "게임에서 나갈게요" (로그아웃)
🎮 게임: "아이템을 안전하게 보관해드릴게요"

😈 해커: "도둑질한 아이템을 사용해볼게요"
🎮 게임: "이 아이템은 블랙리스트에 있어요! 사용 불가!" ❌
```

---

## 블랙리스트는 어떻게 작동해요? ⚙️

### 🏪 편의점 포인트 카드로 이해해보세요!

#### 1️⃣ **정상적인 사용**

```
👤 고객: 포인트 카드로 결제
🏪 편의점: 카드 확인 → 유효함 → 결제 완료 ✅
```

#### 2️⃣ **카드 분실 신고**

```
👤 고객: "카드를 잃어버렸어요!"
🏪 편의점: "블랙리스트에 등록해드릴게요" 📝
```

#### 3️⃣ **도둑이 카드 사용 시도**

```
😈 도둑: 도둑질한 카드로 결제 시도
🏪 편의점: "블랙리스트 확인 중..." 🔍
🏪 편의점: "이 카드는 사용할 수 없어요!" ❌
```

### 🔄 **우리 시스템에서는 이렇게 작동해요:**

```
📱 사용자 로그아웃
    ↓
🖥️ 서버: "이 토큰은 더 이상 사용하면 안 돼요!"
    ↓
📝 Redis: 블랙리스트에 토큰 저장
    ↓
😈 해커: 탈취한 토큰 사용 시도
    ↓
🖥️ 서버: "블랙리스트 확인 중..."
    ↓
❌ 서버: "접근 거부!"
```

---

## 실제 예시로 이해하기 🎯

### 📱 **시나리오 1: 로그아웃 후 보안**

#### 상황

```
👤 민수: "카카오톡에서 로그아웃할게요"
📱 카카오톡: "로그아웃 완료!"
😈 해커: 민수의 토큰을 탈취했어요
```

#### 블랙리스트 없이

```
😈 해커: 민수의 토큰으로 메시지 보내기 시도
📱 카카오톡: "유효한 토큰이네요! 메시지 전송 완료!" ✅
👤 민수: "어? 내가 보낸 메시지가 아니잖아!" 😱
```

#### 블랙리스트 있이

```
😈 해커: 민수의 토큰으로 메시지 보내기 시도
📱 카카오톡: "블랙리스트 확인 중..."
📱 카카오톡: "이 토큰은 사용할 수 없어요!" ❌
👤 민수: "안전해!" 😌
```

### 🔄 **시나리오 2: 토큰 갱신 공격**

#### 상황

```
👤 지영: 정상적으로 토큰 갱신
🖥️ 서버: 새로운 토큰 발급
😈 해커: 이전 토큰을 재사용 시도
```

#### 블랙리스트 없이

```
😈 해커: 이전 토큰으로 갱신 요청
🖥️ 서버: "유효한 토큰이네요! 새 토큰 발급!" ✅
😈 해커: "성공! 계속 사용할 수 있어!" 😈
```

#### 블랙리스트 있이

```
😈 해커: 이전 토큰으로 갱신 요청
🖥️ 서버: "블랙리스트 확인 중..."
🖥️ 서버: "이 토큰은 이미 사용됐어요!" ❌
🖥️ 서버: "보안 위협 감지! 모든 토큰 무효화!" 🚨
😈 해커: "실패! 더 이상 사용할 수 없어!" 😤
```

---

## 우리 프로젝트에서는 어떻게 사용해요? 🏗️

### 📁 **파일 구조**

```
src/main/java/com/sparta/foodorder/
├── domain/auth/infrastructure/
│   ├── TokenBlacklistService.java     # 블랙리스트 관리자
│   └── RefreshTokenService.java       # 토큰 저장소 관리자
└── global/config/
    └── RedisConfig.java               # Redis 설정
```

### 🔧 **주요 기능들**

#### 1️⃣ **TokenBlacklistService** (블랙리스트 관리자)

```java
// 블랙리스트에 추가하기
tokenBlacklistService.addAccessTokenToBlacklist(token, 만료시간);

// 블랙리스트에 있는지 확인하기
boolean isBlacklisted = tokenBlacklistService.isAccessTokenBlacklisted(token);
```

**역할:**

- 🚫 나쁜 토큰들을 기억하고 있어요
- 🔍 토큰이 나쁜지 좋은지 확인해요
- ⏰ 시간이 지나면 자동으로 잊어버려요

#### 2️⃣ **RefreshTokenService** (토큰 저장소 관리자)

```java
// 토큰 저장하기
refreshTokenService.saveRefreshToken(사용자이메일, 토큰, 만료시간);

// 토큰 확인하기
boolean isValid = refreshTokenService.validateRefreshToken(사용자이메일, 토큰);
```

**역할:**

- 💾 좋은 토큰들을 안전하게 보관해요
- 🔄 토큰을 새로 바꿔줄 때 도와줘요
- 🚨 같은 토큰을 두 번 사용하면 알려줘요

### 🎮 **실제 동작 과정**

#### 로그인할 때

```
1. 👤 사용자: "로그인해주세요"
2. 🖥️ 서버: "비밀번호 확인 중..."
3. 🖥️ 서버: "맞아요! 토큰 만들어드릴게요"
4. 💾 Redis: "토큰을 안전하게 보관할게요"
5. 📱 사용자: "로그인 완료!"
```

#### 로그아웃할 때

```
1. 👤 사용자: "로그아웃해주세요"
2. 🖥️ 서버: "토큰을 블랙리스트에 추가할게요"
3. 🚫 Redis: "이 토큰은 더 이상 사용하면 안 돼요!"
4. 📱 사용자: "로그아웃 완료!"
```

#### 해커가 공격할 때

```
1. 😈 해커: "탈취한 토큰으로 접근해볼게요"
2. 🖥️ 서버: "블랙리스트 확인 중..."
3. 🚫 Redis: "이 토큰은 블랙리스트에 있어요!"
4. 🖥️ 서버: "접근 거부!" ❌
5. 😈 해커: "실패했어요!" 😤
```

---

## 🎉 **정리하면**

### 🛡️ **블랙리스트의 역할**

- **보안 경비**: 나쁜 토큰들이 들어오는 것을 막아요
- **기억력**: 어떤 토큰이 나쁜지 기억하고 있어요
- **자동 정리**: 시간이 지나면 자동으로 잊어버려요

### 🎯 **왜 중요한가요?**

- **도둑 방지**: 해커가 토큰을 훔쳐도 사용할 수 없어요
- **안전한 로그아웃**: 로그아웃하면 정말로 안전해져요
- **똑똑한 감지**: 같은 토큰을 두 번 사용하면 알려줘요

### 🚀 **결과**

```
✅ 사용자: 안전하게 웹사이트를 사용할 수 있어요
✅ 개발자: 보안 걱정 없이 개발할 수 있어요
✅ 해커: 토큰을 훔쳐도 사용할 수 없어요
```

**블랙리스트는 우리 웹사이트의 보안 경비원이에요!** 🛡️👮‍♂️

---

## 💡 **더 알아보기**

### 🔗 **관련 기술**

- **JWT**: 토큰의 종류
- **Redis**: 토큰을 저장하는 창고
- **Spring Security**: 보안을 담당하는 경비 시스템

### 📚 **비슷한 예시**

- 🏦 은행 카드 블랙리스트
- 🎮 게임 아이템 블랙리스트
- 🏪 편의점 포인트 카드 블랙리스트

**모든 곳에서 보안을 위해 블랙리스트를 사용해요!** 🌟

---

토큰 블랙리스트가 왜 필요한지 자세히 설명드리겠습니다! 🔐

## 🤔 **JWT의 근본적인 문제점**

### 📋 **JWT의 특징**

- **Stateless**: 서버에 상태를 저장하지 않음
- **Self-contained**: 토큰 자체에 모든 정보 포함
- **서명 검증**: 서버는 토큰의 유효성만 확인

### ⚠️ **문제 상황들**

#### 1. **로그아웃 후에도 토큰이 유효한 문제**

```javascript
// 사용자가 로그아웃했지만...
const token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...";

// 이 토큰은 여전히 유효함! 😱
fetch("/api/protected", {
  headers: { Authorization: `Bearer ${token}` },
});
// → 서버는 "유효한 토큰"이라고 판단하고 접근 허용
```

#### 2. **토큰 탈취 시나리오**

```javascript
// 해커가 토큰을 탈취했다면...
const stolenToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...";

// 사용자가 로그아웃해도 해커는 계속 사용 가능 😰
fetch("/api/user/profile", {
  headers: { Authorization: `Bearer ${stolenToken}` },
});
```

#### 3. **Refresh Token 재사용 공격**

```javascript
// 정상적인 토큰 갱신
POST /api/auth/refresh
Authorization: Bearer valid_refresh_token
→ 새로운 access_token + refresh_token 발급

// 해커가 이전 refresh_token을 재사용 시도
POST /api/auth/refresh
Authorization: Bearer old_refresh_token
→ 서버는 이를 감지하지 못함 (JWT는 stateless)
```

---

## 🛡️ **블랙리스트의 해결책**

### 🔴 **Access Token 블랙리스트**

#### **로그아웃 시**

```java
// AuthServiceImpl.java
public String logout(HttpServletRequest request, HttpServletResponse response) {
    String accessToken = getCookieValue(request, "accessToken");

    if (accessToken != null) {
        // 1. 토큰에서 만료 시간 추출
        Claims claims = jwtTokenizer.parseAccessToken(accessToken);
        long expirationTime = claims.getExpiration().getTime() - System.currentTimeMillis();

        // 2. 블랙리스트에 추가 (만료될 때까지)
        tokenBlacklistService.addAccessTokenToBlacklist(accessToken, expirationTime / 1000);
    }
}
```

#### **인증 필터에서 검증**

```java
// JwtAuthFilter.java (추가 구현 필요)
if (accessToken != null) {
    // 블랙리스트 확인
    if (tokenBlacklistService.isAccessTokenBlacklisted(accessToken)) {
        log.warn("블랙리스트된 토큰 사용 시도");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        return;
    }

    // 정상적인 토큰 검증
    Authentication auth = jwtAuthenticationProvider.getAuthentication(accessToken);
    SecurityContextHolder.getContext().setAuthentication(auth);
}
```

### 🔄 **Refresh Token 블랙리스트**

#### **Token Rotation 시**

```java
// AuthServiceImpl.java
public LoginResponseDto refresh(String token) {
    String refreshToken = token.replace("Bearer ", "");

    // 1. 블랙리스트 확인
    if (tokenBlacklistService.isRefreshTokenBlacklisted(refreshToken)) {
        throw new AuthException(ErrorCode.INVALID_TOKEN);
    }

    // 2. 재사용 감지
    if (!refreshTokenService.validateRefreshToken(userEmail, refreshToken)) {
        // 보안 위협! 모든 토큰 무효화
        tokenBlacklistService.addRefreshTokenToBlacklist(refreshToken, expiration);
        throw new AuthException(ErrorCode.REFRESH_TOKEN_REUSED);
    }

    // 3. 새로운 토큰 발급
    String newAccessToken = createAccessToken(...);
    String newRefreshToken = createRefreshToken(...);

    // 4. 기존 토큰 블랙리스트 추가 (재사용 방지)
    tokenBlacklistService.addRefreshTokenToBlacklist(refreshToken, expiration);

    return new LoginResponseDto(newAccessToken, newRefreshToken, ...);
}
```

---

## 🎯 **실제 보안 시나리오**

### 📱 **시나리오 1: 사용자 로그아웃**

```
1. 사용자: "로그아웃해줘"
2. 서버: Access Token을 블랙리스트에 추가
3. 해커: 탈취한 토큰으로 접근 시도
4. 서버: "블랙리스트에 있네요" → 접근 거부 ✅
```

### 🔐 **시나리오 2: 토큰 재사용 공격**

```
1. 정상 사용자: 토큰 갱신 → 새 토큰 발급
2. 해커: 이전 토큰으로 갱신 시도
3. 서버: "이 토큰은 이미 사용됨" → 재사용 감지 ✅
4. 서버: 모든 토큰 무효화 → 보안 위협 차단 ✅
```

### 🚨 **시나리오 3: 계정 탈취 감지**

```
1. 해커: A지역에서 로그인
2. 사용자: B지역에서 로그인 (동시 접근)
3. 서버: "같은 Refresh Token이 두 곳에서 사용됨"
4. 서버: 모든 세션 무효화 → 계정 보호 ✅
```

---

## 💡 **블랙리스트의 장점**

### ✅ **보안 강화**

- 로그아웃한 토큰 즉시 무효화
- 토큰 재사용 공격 방지
- 계정 탈취 시나리오 대응

### ⚡ **성능 최적화**

- Redis 기반으로 빠른 조회
- 자동 만료로 메모리 효율적
- 분산 환경에서 상태 공유

### 🔄 **유연성**

- 필요에 따라 토큰 수동 무효화 가능
- 관리자 도구로 보안 이벤트 대응
- 세밀한 접근 제어

---

## 🎭 **블랙리스트 없이 vs 블랙리스트 있이**

### ❌ **블랙리스트 없이**

```javascript
// 로그아웃 후에도...
const token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...";
fetch("/api/user/profile", { headers: { Authorization: `Bearer ${token}` } });
// → 200 OK (여전히 접근 가능!) 😱
```

### ✅ **블랙리스트 있이**

```javascript
// 로그아웃 후...
const token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...";
fetch("/api/user/profile", { headers: { Authorization: `Bearer ${token}` } });
// → 401 Unauthorized (접근 거부!) 🛡️
```

---

## 🏗️ **구현된 아키텍처**

```
┌─────────────────┐    ┌──────────────────┐    ┌─────────────────┐
│   Spring Boot   │    │      Redis       │    │   JWT Token     │
│                 │    │                  │    │                 │
│ ┌─────────────┐ │    │ ┌──────────────┐ │    │ ┌─────────────┐ │
│ │AuthService  │◄┼────┼►│Blacklist    │ │    │ │Access Token │ │
│ └─────────────┘ │    │ │Service      │ │    │ └─────────────┘ │
│                 │    │ └──────────────┘ │    │                 │
│ ┌─────────────┐ │    │ ┌──────────────┐ │    │ ┌─────────────┐ │
│ │JwtAuthFilter│◄┼────┼►│Refresh Token │ │    │ │Refresh Token│ │
│ └─────────────┘ │    │ │Service      │ │    │ └─────────────┘ │
└─────────────────┘    └──────────────────┘    └─────────────────┘
```

**결론**: 블랙리스트는 JWT의 Stateless 특성으로 인한 보안 취약점을 해결하는 핵심 메커니즘입니다! 🚀

====================

아니요! 설명이 약간 틀렸습니다. 정확한 시퀀스를 설명드리겠습니다! 🔍

## ❌ **잘못된 이해**

```
로그인 후 > 블랙리스트 등록 ❌
로그아웃 후 > 블랙리스트 삭제 ❌
```

## ✅ **올바른 이해**

```
로그인 후 > 정상 토큰 사용
로그아웃 후 > 블랙리스트 등록 ✅
토큰 만료 시 > 자동 삭제 ✅
```

이제 정확한 타임시퀀스 다이어그램으로 설명하겠습니다!

[1 tool called]

타임시퀀스 다이어그램을 텍스트로 설명하겠습니다!

## 🕐 **정확한 타임시퀀스**

### 📱 **시나리오 1: 정상적인 로그인/로그아웃**

```
시간축: 0초 → 30분 → 60분 → 90분

👤 사용자                    🖥️ 서버                    💾 Redis
    |                          |                          |
    |-- 로그인 요청 ----------->|                          |
    |                          |-- 토큰 생성 ------------->|
    |<-- 로그인 성공 -----------|                          |
    |                          |                          |
    |-- API 요청 (토큰) ------>|                          |
    |<-- 응답 성공 ------------|                          |
    |                          |                          |
    |-- 로그아웃 요청 -------->|                          |
    |                          |-- 토큰 블랙리스트 등록 -->|
    |<-- 로그아웃 완료 --------|                          |
    |                          |                          |
    |                          |                          |-- 토큰 만료 시 자동 삭제
```

### 😈 **시나리오 2: 해커 공격**

```
시간축: 0초 → 30분 → 60분 → 90분

😈 해커                      🖥️ 서버                    💾 Redis
    |                          |                          |
    |-- 탈취한 토큰으로 요청 ->|                          |
    |                          |-- 블랙리스트 확인 ------->|
    |                          |<-- "블랙리스트에 있음" ---|
    |<-- 401 Unauthorized -----|                          |
    |                          |                          |
    |-- 다른 토큰으로 시도 --->|                          |
    |                          |-- 블랙리스트 확인 ------->|
    |                          |<-- "블랙리스트에 있음" ---|
    |<-- 401 Unauthorized -----|                          |
```

## 🔄 **상세한 플로우**

### 1️⃣ **로그인 시 (블랙리스트 등록 ❌)**

```
👤 사용자 → 🖥️ 서버: "로그인해주세요"
🖥️ 서버 → 💾 Redis: "새로운 Refresh Token 저장"
🖥️ 서버 → 👤 사용자: "로그인 성공! 토큰 발급"
```

### 2️⃣ **정상 사용 시**

```
👤 사용자 → 🖥️ 서버: "API 요청 (토큰 포함)"
🖥️ 서버 → 💾 Redis: "블랙리스트 확인"
💾 Redis → 🖥️ 서버: "블랙리스트에 없음"
🖥️ 서버 → 👤 사용자: "요청 처리 완료"
```

### 3️⃣ **로그아웃 시 (블랙리스트 등록 ✅)**

```
👤 사용자 → 🖥️ 서버: "로그아웃해주세요"
🖥️ 서버 → 💾 Redis: "Access Token을 블랙리스트에 추가"
🖥️ 서버 → 💾 Redis: "Refresh Token 삭제"
🖥️ 서버 → 👤 사용자: "로그아웃 완료"
```

### 4️⃣ **해커 공격 시**

```
😈 해커 → 🖥️ 서버: "탈취한 토큰으로 요청"
🖥️ 서버 → 💾 Redis: "블랙리스트 확인"
💾 Redis → 🖥️ 서버: "블랙리스트에 있음!"
🖥️ 서버 → 😈 해커: "401 Unauthorized (접근 거부)"
```

## 📊 **Redis 데이터 상태 변화**

### 로그인 후

```
Redis:
├── refresh_token:user@email.com → "valid_refresh_token"
└── (블랙리스트는 비어있음)
```

### 로그아웃 후

```
Redis:
├── blacklist:access:eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9... → "blacklisted"
└── (refresh_token은 삭제됨)
```

### 토큰 만료 후 (자동 정리)

```
Redis:
└── (블랙리스트도 자동 삭제됨)
```

## 🎯 **핵심 포인트**

### ✅ **올바른 이해**

- **로그인**: 토큰 발급 + Redis에 Refresh Token 저장
- **로그아웃**: Access Token을 블랙리스트에 추가
- **자동 정리**: 토큰 만료 시 블랙리스트에서 자동 삭제

### ❌ **잘못된 이해**

- ~~로그인 시 블랙리스트 등록~~ ❌
- ~~로그아웃 시 블랙리스트 삭제~~ ❌

## 🛡️ **보안 효과**

```
정상 사용자: 로그인 → 사용 → 로그아웃 → 블랙리스트 등록
해커: 탈취한 토큰 사용 시도 → 블랙리스트 확인 → 접근 거부 ✅
```

**결론**: 블랙리스트는 **로그아웃 후에 등록**되어 **해커의 접근을 차단**하는 보안 메커니즘입니다! 🚀

====

네! 로그아웃 시에 Refresh Token도 함께 삭제됩니다! 확인해보겠습니다.

[1 tool called]

네! 맞습니다! 로그아웃 시에 Refresh Token도 함께 삭제됩니다! 🎯

## 📍 **로그아웃 시 토큰 처리**

### 📁 **파일 위치**

```
src/main/java/com/sparta/foodorder/domain/auth/application/AuthServiceImpl.java
```

### 📝 **정확한 위치**

- **메서드**: `logout()` 메서드
- **라인**: **100번째 줄**

### 🔍 **해당 코드**

```java
// 97번째 줄: Access Token 블랙리스트 추가
tokenBlacklistService.addAccessTokenToBlacklist(accessToken, expirationSeconds);

// 100번째 줄: Refresh Token을 Redis에서 삭제 ✅
refreshTokenService.deleteRefreshToken(userEmail);

// 102-106번째 줄: Refresh Token도 블랙리스트에 추가
if (refreshToken != null && !refreshToken.isEmpty()) {
    long refreshExpiration = JwtTokenizer.REFRESH_TOKEN_EXPIRE_COUNT / 1000;
    tokenBlacklistService.addRefreshTokenToBlacklist(refreshToken, refreshExpiration);
}
```

## 🔄 **로그아웃 시 토큰 처리 과정**

### 1️⃣ **Access Token 처리**

```java
// 블랙리스트에 추가 (만료될 때까지 차단)
tokenBlacklistService.addAccessTokenToBlacklist(accessToken, expirationSeconds);
```

### 2️⃣ **Refresh Token 처리**

```java
// Redis에서 완전 삭제
refreshTokenService.deleteRefreshToken(userEmail);

// 블랙리스트에도 추가 (이중 보안)
tokenBlacklistService.addRefreshTokenToBlacklist(refreshToken, refreshExpiration);
```

## 🛡️ **이중 보안 메커니즘**

### ✅ **Refresh Token 삭제**

- Redis에서 완전히 제거
- 더 이상 토큰 갱신 불가능

### ✅ **Refresh Token 블랙리스트**

- 혹시 남아있는 토큰도 차단
- 재사용 공격 방지

## 📊 **Redis 상태 변화**

### 로그인 후

```
Redis:
└── refresh_token:user@email.com → "valid_refresh_token"
```

### 로그아웃 후

```
Redis:
├── blacklist:access:eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9... → "blacklisted"
└── blacklist:refresh:eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9... → "blacklisted"
```

**결론**: 로그아웃 시 Refresh Token은 **Redis에서 삭제**되고 **블랙리스트에도 추가**되어 이중으로 보호됩니다! 🚀
