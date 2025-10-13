# Spring Security isAuthenticated() 완전 가이드 🔐

## 📚 목차

1. [isAuthenticated()란?](#isauthenticated란)
2. [어디서 나온 것인가?](#어디서-나온-것인가)
3. [어떻게 작동하나요?](#어떻게-작동하나요)
4. [사용 가능한 SpEL 표현식들](#사용-가능한-spel-표현식들)
5. [실제 사용 예시](#실제-사용-예시)
6. [우리 프로젝트에서의 활용](#우리-프로젝트에서의-활용)
7. [Before vs After 비교](#before-vs-after-비교)

---

## isAuthenticated()란? 🤔

### 🎯 **정의**

`isAuthenticated()`는 **Spring Security의 SpEL (Spring Expression Language) 표현식**입니다.

### 📝 **역할**

- 사용자가 **로그인되어 있는지** 확인
- 메서드 레벨에서 **접근 권한을 제어**
- `@PreAuthorize` 어노테이션과 함께 사용

### 🔍 **기본 동작**

```java
// 이 표현식이 true를 반환하면 메서드 실행 허용
@PreAuthorize("isAuthenticated()")
public ResponseEntity<UserResponseDto> getMyInfo(...)
```

---

## 어디서 나온 것인가? 🏗️

### 1️⃣ **Spring Security Core에서 제공**

```java
// Spring Security 내부 클래스
public class SecurityExpressionRoot {

    /**
     * 사용자가 인증되었는지 확인
     * @return true if the user is not anonymous
     */
    public boolean isAuthenticated() {
        return authentication != null &&
               !(authentication instanceof AnonymousAuthenticationToken);
    }

    /**
     * 익명 사용자인지 확인
     */
    public boolean isAnonymous() {
        return authentication instanceof AnonymousAuthenticationToken;
    }

    /**
     * 완전히 인증된 사용자인지 확인 (Remember-me 제외)
     */
    public boolean isFullyAuthenticated() {
        return authentication != null &&
               !(authentication instanceof AnonymousAuthenticationToken) &&
               !(authentication instanceof RememberMeAuthenticationToken);
    }
}
```

### 2️⃣ **우리 프로젝트에서 활성화**

```java
// SecurityConfig.java
@Configuration
@EnableMethodSecurity(prePostEnabled = true)  // ⭐ 이 설정으로 활성화!
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity security) throws Exception {
        // JWT 기반 인증 설정
        return security.build();
    }
}
```

### 3️⃣ **활성화되는 어노테이션들**

```java
@EnableMethodSecurity(prePostEnabled = true)
// 다음 어노테이션들이 사용 가능해집니다:

@PreAuthorize("isAuthenticated()")    // 메서드 실행 전 권한 확인
@PostAuthorize("hasRole('ADMIN')")    // 메서드 실행 후 결과 확인
@PreFilter("filterObject.owner == authentication.name")  // 컬렉션 필터링
@PostFilter("filterObject.status == 'ACTIVE'")          // 결과 필터링
```

---

## 어떻게 작동하나요? ⚙️

### 🔄 **요청 처리 흐름**

```
1. 사용자 요청
   ↓
2. JWT 토큰 검증 (JwtAuthFilter)
   ↓
3. SecurityContext에 Authentication 저장
   ↓
4. @PreAuthorize("isAuthenticated()") 실행
   ↓
5. SecurityExpressionRoot.isAuthenticated() 호출
   ↓
6. 권한 확인 결과
   ├─ true  → 메서드 실행 ✅
   └─ false → 403 Forbidden ❌
```

### 🧠 **내부 동작 원리**

```java
public boolean isAuthenticated() {
    // 1. SecurityContext에서 Authentication 가져오기
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();

    // 2. Authentication이 존재하고 익명이 아닌지 확인
    return auth != null &&
           !(auth instanceof AnonymousAuthenticationToken) &&
           auth.isAuthenticated();
}
```

### 📊 **Authentication 상태별 결과**

| Authentication 상태                   | isAuthenticated() 결과 | 설명               |
| ------------------------------------- | ---------------------- | ------------------ |
| `null`                                | `false`                | 인증 정보 없음     |
| `AnonymousAuthenticationToken`        | `false`                | 익명 사용자        |
| `UsernamePasswordAuthenticationToken` | `true`                 | 일반 로그인        |
| `RememberMeAuthenticationToken`       | `true`                 | Remember-me 로그인 |

---

## 사용 가능한 SpEL 표현식들 🎨

### 🔐 **인증 관련 표현식**

```java
// 기본 인증 확인
@PreAuthorize("isAuthenticated()")           // 로그인한 사용자
@PreAuthorize("isAnonymous()")               // 익명 사용자
@PreAuthorize("isFullyAuthenticated()")      // 완전 인증 (Remember-me 제외)
@PreAuthorize("isRememberMe()")             // Remember-me로 로그인
```

### 👥 **역할 및 권한 관련**

```java
// 역할 확인
@PreAuthorize("hasRole('USER')")             // USER 역할
@PreAuthorize("hasAnyRole('USER', 'ADMIN')") // USER 또는 ADMIN
@PreAuthorize("hasRole('ADMIN') and hasRole('MANAGER')") // 둘 다 필요

// 권한 확인
@PreAuthorize("hasAuthority('READ')")         // READ 권한
@PreAuthorize("hasAnyAuthority('READ', 'WRITE')") // READ 또는 WRITE
```

### 🔍 **사용자 정보 접근**

```java
// 사용자명 확인
@PreAuthorize("authentication.name == 'admin'")

// 파라미터와 비교
@PreAuthorize("authentication.principal.userId == #userId")

// 복합 조건
@PreAuthorize("isAuthenticated() and authentication.name == #username")
```

### 🎯 **고급 표현식**

```java
// 메서드 파라미터 활용
@PreAuthorize("hasRole('ADMIN') or authentication.name == #username")

// 컬렉션 필터링
@PreFilter("filterObject.owner == authentication.name")

// 결과 필터링
@PostFilter("filterObject.status == 'ACTIVE'")
```

---

## 실제 사용 예시 💡

### 1️⃣ **기본 인증 확인**

```java
@RestController
@RequestMapping("/v1/users")
public class UserController {

    // 로그인한 사용자만 접근 가능
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponseDto> getMyInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        // 본인 정보만 조회 가능
        return ResponseEntity.ok(userService.getUser(userDetails.getUsername()));
    }
}
```

### 2️⃣ **역할 기반 접근 제어**

```java
@RestController
@RequestMapping("/v1/admin/users")
@PreAuthorize("hasAnyRole('MASTER', 'MANAGER')")  // 클래스 레벨 권한
public class AdminUserController {

    // MANAGER, MASTER만 접근 가능
    @GetMapping
    public ResponseEntity<Page<UserResponseDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers(pageable));
    }

    // MASTER만 접근 가능
    @PatchMapping("/{userId}/status")
    @PreAuthorize("hasRole('MASTER')")
    public ResponseEntity<UserResponseDto> updateUserStatus(
            @PathVariable Long userId,
            @RequestBody UserStatusUpdateRequestDto request) {
        return ResponseEntity.ok(userService.updateUserStatus(userId, request));
    }
}
```

### 3️⃣ **복합 조건**

```java
// 인증 + 특정 사용자만
@PreAuthorize("isAuthenticated() and authentication.name == #username")

// 역할 + 파라미터 조건
@PreAuthorize("hasRole('ADMIN') or authentication.principal.userId == #userId")

// 여러 역할 중 하나
@PreAuthorize("hasAnyRole('USER', 'ADMIN', 'MANAGER')")
```

---

## 우리 프로젝트에서의 활용 🚀

### 📁 **파일별 적용 현황**

#### 1️⃣ **SecurityConfig.java**

```java
@Configuration
@EnableMethodSecurity(prePostEnabled = true)  // SpEL 활성화
public class SecurityConfig {
    // JWT 기반 인증 설정
}
```

#### 2️⃣ **UserController.java**

```java
@RestController
@RequestMapping("/v1/users")
public class UserController {

    // 회원가입 - 인증 불필요
    @PostMapping
    public ResponseEntity<UserResponseDto> signup(...)

    // 내 정보 조회 - 인증 필요
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponseDto> getMyInfo(...)

    // 프로필 수정 - 인증 필요
    @PutMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponseDto> updateProfile(...)
}
```

#### 3️⃣ **AddressController.java**

```java
@RestController
@RequestMapping("/v1/addresses")
@PreAuthorize("isAuthenticated()")  // 클래스 레벨 인증
public class AddressController {
    // 모든 엔드포인트에 인증 필요
}
```

#### 4️⃣ **AdminUserController.java**

```java
@RestController
@RequestMapping("/v1/admin/users")
@PreAuthorize("hasAnyRole('MASTER', 'MANAGER')")  // 관리자만 접근
public class AdminUserController {

    // MANAGER, MASTER 접근 가능
    @GetMapping
    public ResponseEntity<Page<UserResponseDto>> getAllUsers()

    // MASTER만 접근 가능
    @PatchMapping("/{userId}/status")
    @PreAuthorize("hasRole('MASTER')")
    public ResponseEntity<UserResponseDto> updateUserStatus(...)
}
```

### 🎯 **권한 레벨별 접근 제어**

| 권한 레벨       | 접근 가능한 API              | 표현식                            |
| --------------- | ---------------------------- | --------------------------------- |
| **인증 불필요** | 회원가입, 로그인             | `permitAll()`                     |
| **인증 필요**   | 내 정보 조회/수정, 주소 관리 | `isAuthenticated()`               |
| **MANAGER+**    | 사용자 목록 조회             | `hasAnyRole('MASTER', 'MANAGER')` |
| **MASTER만**    | 사용자 상태 변경, 강제 탈퇴  | `hasRole('MASTER')`               |

---

## Before vs After 비교 📊

### ❌ **Before (URL 기반 권한)**

```java
// 보안 취약점이 있는 방식
@GetMapping("/{userEmail}")
public ResponseEntity<UserResponseDto> getUser(@PathVariable String userEmail) {
    // 문제점:
    // 1. URL에 이메일 노출
    // 2. 다른 사용자 정보 조회 가능
    // 3. 권한 확인 로직 부재
}

// SecurityUtil 사용
@PostMapping
public ResponseEntity<AddressResponseDto> createAddress(@RequestBody AddressRequestDto request) {
    Long userId = SecurityUtil.getCurrentUserId();  // 반복적인 코드
    return addressService.createAddress(userId, request);
}
```

### ✅ **After (SpEL 기반 권한)**

```java
// 안전한 방식
@GetMapping("/me")
@PreAuthorize("isAuthenticated()")  // 명시적 권한 확인
public ResponseEntity<UserResponseDto> getMyInfo(
        @AuthenticationPrincipal CustomUserDetails userDetails) {
    // 장점:
    // 1. URL에서 이메일 제거
    // 2. 본인 정보만 조회 가능
    // 3. 메서드 레벨 권한 확인
}

// @AuthenticationPrincipal 사용
@PostMapping
@PreAuthorize("isAuthenticated()")
public ResponseEntity<AddressResponseDto> createAddress(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @RequestBody AddressRequestDto request) {
    return addressService.createAddress(userDetails.getUserId(), request);
}
```

### 🎯 **개선 효과**

| 항목           | Before                        | After                    |
| -------------- | ----------------------------- | ------------------------ |
| **보안성**     | ❌ URL 노출, 권한 우회 가능   | ✅ 메서드 레벨 권한 확인 |
| **코드 품질**  | ❌ 반복적인 SecurityUtil 호출 | ✅ 명시적 의존성 주입    |
| **유지보수성** | ❌ 권한 로직 분산             | ✅ 어노테이션으로 중앙화 |
| **가독성**     | ❌ 권한 확인 로직 숨김        | ✅ 명시적 권한 표현      |

---

## 🎉 **정리**

### 🔑 **핵심 포인트**

1. **`isAuthenticated()`**는 Spring Security의 **표준 SpEL 표현식**
2. **`@EnableMethodSecurity(prePostEnabled = true)`**로 활성화
3. **메서드 레벨에서 권한 확인** 가능
4. **URL 기반 권한보다 안전하고 명시적**

### 🚀 **우리 프로젝트의 장점**

- ✅ **보안 강화**: URL에서 민감 정보 제거
- ✅ **권한 명확화**: 역할별 접근 제어
- ✅ **코드 간결화**: 반복적인 권한 확인 코드 제거
- ✅ **유지보수성**: 어노테이션 기반 권한 관리

### 📚 **추가 학습 자료**

- [Spring Security Method Security](https://docs.spring.io/spring-security/reference/servlet/authorization/method-security.html)
- [Spring Expression Language (SpEL)](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#expressions)
- [@PreAuthorize vs @PostAuthorize](https://www.baeldung.com/spring-security-method-security)

**Spring Security의 `isAuthenticated()`로 더 안전하고 깔끔한 코드를 작성할 수 있습니다!** 🛡️✨
