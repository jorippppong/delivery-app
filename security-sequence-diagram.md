# JWT 인증 시스템 시퀀스 다이어그램

## 1. 로그인 시퀀스

```mermaid
sequenceDiagram
    participant Client as 클라이언트
    participant AuthController as AuthController
    participant AuthService as AuthServiceImpl
    participant UserRepository as UserRepository
    participant PasswordEncoder as PasswordEncoderImpl
    participant JwtTokenizer as JwtTokenizer
    participant Database as 데이터베이스

    Client->>AuthController: POST /auth/login<br/>(LoginRequestDto)
    AuthController->>AuthService: login(requestDto)

    AuthService->>UserRepository: findByUserEmail(email)
    UserRepository->>Database: SELECT * FROM user WHERE email = ?
    Database-->>UserRepository: User 엔티티
    UserRepository-->>AuthService: Optional<User>

    alt 사용자가 존재하지 않는 경우
        AuthService-->>AuthController: AuthException(USER_NOT_FOUND)
        AuthController-->>Client: 401 Unauthorized
    end

    AuthService->>PasswordEncoder: matches(password, user.password)
    PasswordEncoder-->>AuthService: boolean

    alt 비밀번호가 일치하지 않는 경우
        AuthService-->>AuthController: AuthException(INVALID_PASSWORD)
        AuthController-->>Client: 401 Unauthorized
    end

    alt 사용자 상태가 ACTIVE가 아닌 경우
        AuthService-->>AuthController: AuthException(UNAUTHORIZED_USER)
        AuthController-->>Client: 401 Unauthorized
    end

    AuthService->>JwtTokenizer: createAccessToken(id, email, status, roles)
    JwtTokenizer-->>AuthService: AccessToken (30분 만료)

    AuthService->>JwtTokenizer: createRefreshToken(id, email, status, roles)
    JwtTokenizer-->>AuthService: RefreshToken (7일 만료)

    AuthService-->>AuthController: LoginResponseDto(accessToken, refreshToken, email, role)
    AuthController-->>Client: 200 OK (LoginResponseDto)
```

## 2. 로그아웃 시퀀스

```mermaid
sequenceDiagram
    participant Client as 클라이언트
    participant AuthController as AuthController
    participant AuthService as AuthServiceImpl
    participant JwtTokenizer as JwtTokenizer
    participant UserRepository as UserRepository

    Client->>AuthController: POST /auth/logout<br/>(Authorization: Bearer refreshToken)
    AuthController->>AuthService: logout(refreshToken)

    AuthService->>JwtTokenizer: parseAccessToken(refreshToken)
    JwtTokenizer-->>AuthService: Claims

    AuthService->>AuthService: claims.get("userEmail")
    AuthService->>UserRepository: findByUserEmail(userEmail)
    UserRepository-->>AuthService: Optional<User>

    alt 사용자가 존재하지 않는 경우
        AuthService-->>AuthController: IllegalStateException
        AuthController-->>Client: 500 Internal Server Error
    end

    AuthService->>UserRepository: save(user)
    UserRepository-->>AuthService: User

    AuthService-->>AuthController: void
    AuthController-->>Client: 200 OK
```

## 3. 토큰 갱신 시퀀스

```mermaid
sequenceDiagram
    participant Client as 클라이언트
    participant AuthController as AuthController
    participant AuthService as AuthServiceImpl
    participant JwtTokenizer as JwtTokenizer
    participant UserRepository as UserRepository

    Client->>AuthController: POST /auth/refresh<br/>(Authorization: Bearer refreshToken)
    AuthController->>AuthService: refresh(refreshToken)

    AuthService->>JwtTokenizer: parseRefreshToken(refreshToken)

    alt RefreshToken이 유효하지 않은 경우
        JwtTokenizer-->>AuthService: Exception
        AuthService-->>AuthController: AuthException(UNAUTHORIZED_USER)
        AuthController-->>Client: 401 Unauthorized
    end

    JwtTokenizer-->>AuthService: Claims

    AuthService->>AuthService: claims.getSubject() (userEmail)
    AuthService->>UserRepository: findByUserEmail(userEmail)
    UserRepository-->>AuthService: Optional<User>

    alt 사용자가 존재하지 않는 경우
        AuthService-->>AuthController: AuthException(USER_NOT_FOUND)
        AuthController-->>Client: 401 Unauthorized
    end

    alt 사용자 상태가 ACTIVE가 아닌 경우
        AuthService-->>AuthController: AuthException(UNAUTHORIZED_USER)
        AuthController-->>Client: 401 Unauthorized
    end

    AuthService->>JwtTokenizer: createAccessToken(id, email, status, roles)
    JwtTokenizer-->>AuthService: 새로운 AccessToken (30분 만료)

    AuthService-->>AuthController: LoginResponseDto(newAccessToken, refreshToken, email, role)
    AuthController-->>Client: 200 OK (LoginResponseDto)
```

## 4. 토큰 만료 시 자동 갱신 시퀀스 (JwtAuthFilter)

```mermaid
sequenceDiagram
    participant Client as 클라이언트
    participant JwtAuthFilter as JwtAuthFilter
    participant JwtAuthenticationProvider as JwtAuthenticationProvider
    participant JwtTokenizer as JwtTokenizer
    participant UserRepository as UserRepository
    participant SecurityContextHolder as SecurityContextHolder

    Client->>JwtAuthFilter: HTTP Request (AccessToken 만료됨, RefreshToken 존재)

    JwtAuthFilter->>JwtAuthFilter: getAccessToken(request) → null
    JwtAuthFilter->>JwtAuthFilter: getRefreshToken(request) → refreshToken

    JwtAuthFilter->>JwtAuthenticationProvider: genNewAccessToken(refreshToken)

    JwtAuthenticationProvider->>JwtTokenizer: parseRefreshToken(refreshToken)

    alt RefreshToken이 유효하지 않은 경우
        JwtTokenizer-->>JwtAuthenticationProvider: Exception
        JwtAuthenticationProvider-->>JwtAuthFilter: RuntimeException
        JwtAuthFilter->>JwtAuthFilter: 로그만 남기고 계속 진행
    end

    JwtTokenizer-->>JwtAuthenticationProvider: Claims

    JwtAuthenticationProvider->>JwtAuthenticationProvider: getUserFromClaims(claims)
    JwtAuthenticationProvider->>UserRepository: findByUserEmail(userEmail)
    UserRepository-->>JwtAuthenticationProvider: User

    JwtAuthenticationProvider->>JwtTokenizer: createAccessToken(id, email, status, roles)
    JwtTokenizer-->>JwtAuthenticationProvider: 새로운 AccessToken

    JwtAuthenticationProvider-->>JwtAuthFilter: 새로운 AccessToken

    JwtAuthFilter->>JwtAuthFilter: 새 AccessToken을 쿠키에 설정
    JwtAuthFilter->>JwtAuthFilter: X-Token-Refreshed: true 헤더 설정

    JwtAuthFilter->>JwtAuthenticationProvider: getAuthentication(newAccessToken)
    JwtAuthenticationProvider->>JwtTokenizer: parseAccessToken(newAccessToken)
    JwtTokenizer-->>JwtAuthenticationProvider: Claims

    JwtAuthenticationProvider->>JwtAuthenticationProvider: getUserFromClaims(claims)
    JwtAuthenticationProvider->>JwtAuthenticationProvider: CustomUserDetails 생성
    JwtAuthenticationProvider-->>JwtAuthFilter: JwtAuthenticationToken

    JwtAuthFilter->>SecurityContextHolder: setAuthentication(authentication)

    JwtAuthFilter-->>Client: HTTP Response (새로운 AccessToken 쿠키 포함)
```

## 5. 인증이 필요한 API 접근 시퀀스

```mermaid
sequenceDiagram
    participant Client as 클라이언트
    participant JwtAuthFilter as JwtAuthFilter
    participant JwtAuthenticationProvider as JwtAuthenticationProvider
    participant JwtTokenizer as JwtTokenizer
    participant UserRepository as UserRepository
    participant SecurityContextHolder as SecurityContextHolder
    participant Controller as Controller

    Client->>JwtAuthFilter: HTTP Request (Authorization: Bearer accessToken)

    JwtAuthFilter->>JwtAuthFilter: getAccessToken(request) → accessToken

    JwtAuthFilter->>JwtAuthenticationProvider: getAuthentication(accessToken)

    JwtAuthenticationProvider->>JwtTokenizer: parseAccessToken(accessToken)

    alt AccessToken이 만료된 경우
        JwtTokenizer-->>JwtAuthenticationProvider: ExpiredJwtException
        JwtAuthenticationProvider-->>JwtAuthFilter: RuntimeException("토큰이 만료되었습니다")
        JwtAuthFilter->>JwtAuthFilter: SecurityContextHolder.clearContext()
        JwtAuthFilter-->>Client: 401 Unauthorized
    end

    alt AccessToken이 유효하지 않은 경우
        JwtTokenizer-->>JwtAuthenticationProvider: Exception
        JwtAuthenticationProvider-->>JwtAuthFilter: RuntimeException("유효하지 않은 토큰입니다")
        JwtAuthFilter->>JwtAuthFilter: SecurityContextHolder.clearContext()
        JwtAuthFilter-->>Client: 401 Unauthorized
    end

    JwtTokenizer-->>JwtAuthenticationProvider: Claims

    JwtAuthenticationProvider->>JwtAuthenticationProvider: getUserFromClaims(claims)
    JwtAuthenticationProvider->>UserRepository: findByUserEmail(userEmail)
    UserRepository-->>JwtAuthenticationProvider: User

    alt 사용자 상태가 ACTIVE가 아닌 경우
        JwtAuthenticationProvider-->>JwtAuthFilter: RuntimeException("계정 상태 오류")
        JwtAuthFilter->>JwtAuthFilter: SecurityContextHolder.clearContext()
        JwtAuthFilter-->>Client: 401 Unauthorized
    end

    JwtAuthenticationProvider->>JwtAuthenticationProvider: CustomUserDetails 생성
    JwtAuthenticationProvider->>JwtAuthenticationProvider: GrantedAuthority 컬렉션 생성
    JwtAuthenticationProvider-->>JwtAuthFilter: JwtAuthenticationToken

    JwtAuthFilter->>SecurityContextHolder: setAuthentication(authentication)

    JwtAuthFilter->>Controller: 요청 전달
    Controller-->>JwtAuthFilter: 응답
    JwtAuthFilter-->>Client: HTTP Response
```

## 주요 컴포넌트 설명

### 1. JwtTokenizer

- **역할**: JWT 토큰 생성, 파싱, 검증
- **주요 메서드**:
  - `createAccessToken()`: AccessToken 생성 (30분 만료)
  - `createRefreshToken()`: RefreshToken 생성 (7일 만료)
  - `parseAccessToken()`: AccessToken 파싱 및 검증
  - `parseRefreshToken()`: RefreshToken 파싱 및 검증

### 2. AuthServiceImpl

- **역할**: 인증 비즈니스 로직 처리
- **주요 메서드**:
  - `login()`: 로그인 처리 및 토큰 발급
  - `logout()`: 로그아웃 처리
  - `refresh()`: RefreshToken으로 새로운 AccessToken 발급

### 3. JwtAuthenticationProvider

- **역할**: JWT 기반 인증 처리
- **주요 메서드**:
  - `getAuthentication()`: AccessToken으로 Authentication 객체 생성
  - `genNewAccessToken()`: RefreshToken으로 새로운 AccessToken 생성
  - `getUserFromClaims()`: JWT 클레임에서 User 정보 추출 및 상태 검증

### 4. JwtAuthFilter

- **역할**: HTTP 요청 필터링 및 자동 토큰 갱신
- **주요 기능**:
  - 인증이 필요 없는 경로 스킵
  - AccessToken 만료 시 RefreshToken으로 자동 갱신
  - SecurityContext에 인증 정보 설정

### 5. 토큰 만료 시간

- **AccessToken**: 30분 (1000 _ 60 _ 30L)
- **RefreshToken**: 7일 (7 _ 24 _ 60 _ 60 _ 1000L)

### 6. 사용자 상태 검증

- **ACTIVE**: 정상 사용자
- **INACTIVE**: 비활성화된 계정
- **PENDING**: 승인 대기 중
- **BANNED**: 제재된 계정
- **WITHDRAWN**: 탈퇴한 계정
