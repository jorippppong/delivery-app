알겠습니다! Security와 User 역할을 명확히 분리해서 파일을 재구성하겠습니다.

먼저 현재 파일 상태를 확인하고 정리하겠습니다:

[2 tools called]

이제 역할 분리에 맞게 파일을 정리하겠습니다:

## 1단계: Security에서 회원가입 관련 제거 (User로 이동)

[2 tools called]

## 2단계: Security 파일 정리 (인증/인가만)

[6 tools called]

## 3단계: User 도메인에 필요한 파일 추가

[9 tools called]

## 4단계: 최종 구조 확인

[1 tool called]

## ✅ 역할 분리 완료!

### 📁 Security (인증/인가만 담당)

```
security/
├── application/
│   ├── dto/
│   │   ├── LoginRequestDto.java       # 로그인 요청
│   │   └── LoginResponseDto.java      # 토큰 응답
│   └── AuthService.java               # 인증 서비스 (UserRepository 의존)
├── domain/
│   ├── AuthException.java             # 인증 예외
│   ├── AuthService.java               # 도메인 서비스 인터페이스
│   └── AuthToken.java                 # RefreshToken 엔티티
├── infrastructure/
│   └── JwtTokenProvider.java          # JWT 토큰 생성/검증
└── presentation/
    └── AuthController.java            # /auth/login, /auth/logout, /auth/refresh
```

**Security 담당:**

- ✅ 로그인/로그아웃
- ✅ JWT 토큰 발급/검증
- ✅ 권한 확인

---

### 👤 User (사용자 정보 관리)

```
domain/user/
├── application/
│   ├── dto/
│   │   ├── SignupRequestDto.java      # 회원가입 요청
│   │   ├── UserResponseDto.java       # 사용자 응답
│   │   └── UpdatePasswordRequestDto.java # 비밀번호 변경
│   └── UserService.java               # 사용자 서비스
├── domain/
│   ├── User.java                      # 사용자 엔티티
│   ├── UserRole.java                  # 역할 enum
│   ├── UserStatus.java                # 상태 enum
│   └── UserService.java               # 도메인 서비스 인터페이스
├── infrastructure/
│   ├── UserRepository.java            # 리포지토리 인터페이스
│   ├── UserJpaRepository.java         # JPA 리포지토리
│   ├── UserRepositoryImpl.java        # 리포지토리 구현체
│   └── PasswordEncoderImpl.java       # 비밀번호 암호화
└── presentation/
    └── UserController.java            # /users (회원가입, 프로필 관리)
```

**User 담당:**

- ✅ 회원가입
- ✅ 프로필 조회/수정
- ✅ 비밀번호 변경
- ✅ 사용자 정보 관리

---

### 🔗 의존 관계

```
Security → User (의존)
  AuthService가 UserRepository 사용해서 사용자 조회
```

깔끔하게 역할이 분리되었습니다!

현재 코드를 분석해서 JWT 인증 플로우의 시퀀스 다이어그램을 그려드리겠습니다.

[1 tool called]

시퀀스 다이어그램을 텍스트로 작성해드리겠습니다:

## JWT 인증 플로우 시퀀스 다이어그램

### 1. 로그인 플로우

```
Client -> AuthController: POST /v1/auth/login
AuthController -> AuthServiceImpl: login(LoginRequestDto)
AuthServiceImpl -> UserRepository: findByUserEmail(email)
UserRepository -> AuthServiceImpl: User 엔티티 반환
AuthServiceImpl -> PasswordEncoderImpl: matches(password, encodedPassword)
PasswordEncoderImpl -> AuthServiceImpl: boolean 결과
AuthServiceImpl -> JwtTokenizer: createAccessToken(id, email, status, roles)
JwtTokenizer -> AuthServiceImpl: accessToken 반환
AuthServiceImpl -> JwtTokenizer: createRefreshToken(id, email, status, roles)
JwtTokenizer -> AuthServiceImpl: refreshToken 반환
AuthServiceImpl -> AuthController: LoginResponseDto 반환
AuthController -> Client: HTTP 200 + 토큰들
```

### 2. API 요청 인증 플로우

```
Client -> JwtAuthFilter: HTTP 요청 + Authorization 헤더/쿠키
JwtAuthFilter -> JwtAuthFilter: getAccessToken(request)
JwtAuthFilter -> JwtAuthFilter: getRefreshToken(request)

alt AccessToken이 있는 경우
    JwtAuthFilter -> JwtAuthenticationProvider: getAuthentication(accessToken)
    JwtAuthenticationProvider -> JwtTokenizer: parseAccessToken(token)
    JwtTokenizer -> JwtAuthenticationProvider: Claims 반환
    JwtAuthenticationProvider -> AuthService: findByEmailWithRoles(userEmail)
    AuthService -> JwtAuthenticationProvider: User 엔티티 반환
    JwtAuthenticationProvider -> JwtAuthenticationProvider: getUserFromClaims(claims)
    JwtAuthenticationProvider -> JwtAuthenticationProvider: 사용자 상태 검증
    JwtAuthenticationProvider -> JwtAuthenticationProvider: GrantedAuthority 생성
    JwtAuthenticationProvider -> JwtAuthFilter: JwtAuthenticationToken 반환
    JwtAuthFilter -> SecurityContextHolder: setAuthentication(authentication)
    JwtAuthFilter -> Controller: 요청 전달
    Controller -> Client: 응답 반환

else AccessToken 없고 RefreshToken만 있는 경우
    JwtAuthFilter -> JwtAuthenticationProvider: genNewAccessToken(refreshToken)
    JwtAuthenticationProvider -> JwtTokenizer: parseRefreshToken(token)
    JwtTokenizer -> JwtAuthenticationProvider: Claims 반환
    JwtAuthenticationProvider -> AuthService: findByEmailWithRoles(userEmail)
    AuthService -> JwtAuthenticationProvider: User 엔티티 반환
    JwtAuthenticationProvider -> AuthService: genAccessToken(user)
    AuthService -> JwtAuthenticationProvider: 새 accessToken 반환
    JwtAuthenticationProvider -> JwtAuthFilter: 새 accessToken 반환
    JwtAuthFilter -> Client: 새 accessToken을 쿠키로 설정
    JwtAuthFilter -> Controller: 요청 전달 (새 토큰으로 인증)
    Controller -> Client: 응답 반환

else 토큰이 없는 경우
    JwtAuthFilter -> Controller: 인증 없이 요청 전달
    Controller -> Client: 응답 반환 (인증 필요시 401)
```

### 3. 사용자 정보 조회 플로우 (SecurityUtil 사용)

```
Controller -> SecurityUtil: getCurrentUser()
SecurityUtil -> SecurityContextHolder: getContext().getAuthentication()
SecurityContextHolder -> SecurityUtil: Authentication 반환
SecurityUtil -> CustomUserDetails: getUser()
CustomUserDetails -> SecurityUtil: User 엔티티 반환
SecurityUtil -> Controller: User 엔티티 반환
```

### 4. 주요 컴포넌트 역할

**JwtAuthFilter**:

- 모든 요청을 가로채서 JWT 토큰 검증
- 토큰 재발급 처리
- SecurityContext에 인증 정보 설정

**JwtAuthenticationProvider**:

- JWT 토큰 파싱 및 검증
- 사용자 상태 확인
- Authentication 객체 생성

**JwtTokenizer**:

- JWT 토큰 생성/파싱
- 토큰 유효성 검증

**JwtAuthenticationToken**:

- Spring Security의 Authentication 객체를 JWT용으로 커스터마이징한 클래스
- AbstractAuthenticationToken을 상속받아 JWT 전용 인증 토큰 구현
- 인증된 사용자 정보(principal), 권한(authorities), 자격 증명(credentials) 저장
- JWT 토큰을 Spring Security의 인증 시스템과 연결하는 다리 역할
- SecurityContext에 저장되어 Controller에서 현재 사용자 정보 접근 가능

**CustomUserDetails**:

- User 엔티티를 Spring Security UserDetails로 래핑
- 권한 정보 제공

**AuthServiceImpl**:

- 로그인 로직 처리
- 비밀번호 검증
- 토큰 생성 요청

이 구조로 JWT 기반의 stateless 인증이 구현되어 있습니다!
