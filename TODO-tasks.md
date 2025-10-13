# 담당 도메인 추가 작업 목록

## 담당 영역

- 시큐리티 (Auth)
- 유저 (User)
- 주소 (Address)

---

## Task 1: 입력값 검증 강화 (Validation)

### 1.1 User 도메인

- [x] 이메일 형식 검증 추가 (`@Email` 어노테이션)
- [x] 전화번호 형식 검증 추가 (정규식 패턴)
- [x] 비밀번호 정책 검증 추가
  - 최소 8자 이상
  - 영문, 숫자, 특수문자 조합
  - 현재 비밀번호와 새 비밀번호 동일 여부 체크
- [x] 닉네임 중복 체크 추가
- [x] 이름 길이 및 형식 검증 (한글/영문만 허용)

### 1.2 Address 도메인

- [x] 우편번호 형식 검증 (5자리 숫자)
- [x] 전화번호 형식 검증
- [x] 주소 필수 필드 검증 강화
- [x] 주소 길이 제한 검증

### 1.3 Auth 도메인

- [ ] 로그인 시도 횟수 제한 (Rate Limiting) _(Task 3에서 처리)_
- [ ] IP 기반 로그인 제한 _(Task 3에서 처리)_
- [x] 이메일/비밀번호 입력값 공백 체크

---

## Task 2: 예외 처리 추가

### 2.1 User 도메인

- [x] 탈퇴한 사용자 접근 시도 예외 처리
- [x] BANNED 상태 사용자 예외 처리
- [x] 동시성 문제 예외 처리 (이메일 중복 체크)
- [x] 잘못된 사용자 상태 전환 예외 처리

### 2.2 Address 도메인

- [x] 삭제된 주소 접근 예외 처리
- [x] 주소 개수 제한 초과 예외 처리 (예: 최대 10개)
- [x] 기본 배송지 삭제 시도 예외 처리
- [x] 다른 사용자의 주소 접근 예외 처리 강화

### 2.3 Auth 도메인

- [x] 토큰 만료 예외 처리 세분화
- [x] 토큰 변조 예외 처리
- [ ] Refresh Token 재사용 방지 _(Task 3에서 처리 - Redis 필요)_
- [ ] 동시 로그인 처리 _(Task 3에서 처리)_

### 2.4 전역 예외 처리

- [x] ErrorCode에 새로운 에러 코드 추가
  - `USER_DEACTIVATED`: 탈퇴한 사용자
  - `USER_BANNED`: 차단된 사용자
  - `USER_WITHDRAWN`: 탈퇴 처리된 사용자
  - `INVALID_USER_STATUS`: 잘못된 사용자 상태
  - `INVALID_TOKEN`: 유효하지 않은 토큰
  - `TOKEN_EXPIRED`: 만료된 토큰
  - `TOKEN_TAMPERED`: 변조된 토큰
  - `ADDRESS_DELETED`: 삭제된 주소
  - `ADDRESS_LIMIT_EXCEEDED`: 주소 개수 초과
  - `DEFAULT_ADDRESS_DELETE_NOT_ALLOWED`: 기본 배송지 삭제 불가
  - `INVALID_EMAIL_FORMAT`: 잘못된 이메일 형식 _(Task 1에서 완료)_
  - `INVALID_PASSWORD_FORMAT`: 잘못된 비밀번호 형식 _(Task 1에서 완료)_
  - `TOO_MANY_LOGIN_ATTEMPTS`: 로그인 시도 횟수 초과 _(예약)_

---

## Task 3: 시큐리티 고도화

### 3.1 인증/인가 강화

- [x] Refresh Token 블랙리스트 구현 (Redis)
- [x] Access Token 블랙리스트 구현 (로그아웃 시)
- [x] 토큰 갱신 정책 개선
  - Refresh Token Rotation 구현
  - Refresh Token 재사용 감지
- [x] CORS 설정 추가
- [ ] CSRF 보호 설정 _(REST API는 불필요 - 현재 설정 유지)_

### 3.2 권한 관리

- [x] Role 기반 접근 제어 강화
  - `@PreAuthorize` 어노테이션 활용 ✅
  - USER, OWNER, MANAGER, MASTER 역할별 권한 설정 ✅
  - `@EnableMethodSecurity(prePostEnabled = true)` 활성화 ✅
- [x] 본인 정보만 조회/수정 가능하도록 권한 체크
  - `@PreAuthorize("isAuthenticated()")` 적용 ✅
  - UserController: `/v1/users/me` 엔드포인트에 본인 인증 강제 ✅
  - AddressController: 모든 엔드포인트에 본인 인증 강제 ✅
- [x] 관리자 전용 API 추가
  - 사용자 목록 조회 (페이징) ✅
  - 사용자 상세 조회 ✅
  - 사용자 상태 변경 (ACTIVE/BANNED/WITHDRAWN) ✅
  - 사용자 강제 탈퇴 ✅
  - 제재된 사용자 목록 조회 ✅
  - 탈퇴한 사용자 목록 조회 ✅

### 3.3 보안 설정

- [ ] JWT Secret Key 환경변수로 분리
- [ ] 비밀번호 암호화 강도 설정 (BCrypt strength)
- [ ] 보안 헤더 추가
  - X-Content-Type-Options
  - X-Frame-Options
  - X-XSS-Protection
- [ ] 로그인 이력 저장 (로그인 시간, IP, User-Agent)

---

## Task 4: 기능 추가

### 4.1 User 도메인

- [ ] 사용자 검색 기능 (이메일, 이름, 닉네임)
- [ ] 사용자 목록 조회 (페이징)
- [ ] 프로필 이미지 업로드 기능
- [ ] 이메일 인증 기능
- [ ] 비밀번호 찾기 기능
- [ ] 회원 탈퇴 기능 (soft delete 강화)
  - isDeleted 플래그 활용
  - 탈퇴 사유 저장
  - 재가입 제한 정책
- [ ] 사용자 통계 조회
  - 가입 일자
  - 마지막 로그인 시간
  - 주문 횟수

### 4.2 Address 도메인

- [ ] 주소 검색 기능 (주소명, 우편번호)
- [ ] 최근 사용한 주소 조회
- [ ] 주소 사용 횟수 추적
- [ ] 주소 유효성 검증 API 연동 (카카오/네이버 주소 API)
- [ ] 배송 가능 지역 체크 기능

### 4.3 Auth 도메인

- [ ] 소셜 로그인 추가
  - 카카오 로그인
  - 구글 로그인
  - 네이버 로그인
- [ ] 2단계 인증 (2FA)
- [ ] 로그인 알림 기능 (이메일/SMS)
- [ ] 토큰 갱신 API 개선
- [ ] 로그아웃 시 모든 디바이스에서 로그아웃 기능

---

## Task 5: 리팩토링

### 5.1 코드 구조 개선

- [x] UserController에서 `@AuthenticationPrincipal` 사용
  - PathVariable 대신 SecurityContext에서 사용자 정보 가져오기
  - URL에서 userEmail 제거 → `/v1/users/me`로 변경
- [x] AddressController에서 `@AuthenticationPrincipal` 사용
  - SecurityUtil 대신 직접 UserDetails 주입
  - 보안 강화 및 코드 간결화
- [x] Service 레이어의 공통 메서드 추출
  - `findUserByEmail` → UserService로 이동 (public 메서드)
  - `findUserById` → UserService로 이동 (public 메서드)
- [x] DTO 검증 로직 강화 _(Task 1에서 완료)_
  - Custom Validator 생성 ✅
  - 비밀번호 정책 검증기 ✅
  - 이메일 형식 검증기 ✅
- [ ] 응답 DTO 통일 _(선택사항 - 추후 구현)_
  - 공통 Response Wrapper 생성
  - 성공/실패 응답 형식 통일

### 5.2 성능 최적화

- [ ] User-Address 연관관계 최적화
  - N+1 문제 해결
  - Fetch Join 활용
- [ ] 쿼리 최적화
  - 인덱스 설정 (user_email, postal_code 등)
  - 페이징 쿼리 최적화
- [ ] 캐싱 전략 수립
  - 사용자 정보 캐싱 (Redis)
  - 기본 배송지 캐싱

### 5.3 코드 품질 개선

- [ ] 매직 넘버 상수화
  - 토큰 만료 시간
  - 주소 개수 제한
  - 비밀번호 최소 길이
- [ ] 로깅 전략 개선
  - 민감 정보 마스킹 (비밀번호, 토큰)
  - 로그 레벨 조정
  - 구조화된 로깅
- [ ] 테스트 코드 작성
  - 단위 테스트
  - 통합 테스트
  - 시큐리티 테스트

### 5.4 문서화

- [ ] API 문서 보완
  - Swagger 상세 설명 추가
  - 요청/응답 예시 추가
  - 에러 응답 예시 추가
- [ ] 주석 추가
  - 복잡한 비즈니스 로직 설명
  - 보안 관련 주의사항

---

## Task 6: 데이터베이스 개선

### 6.1 스키마 개선

- [ ] User 테이블
  - `last_login_at` 컬럼 추가
  - `login_attempt_count` 컬럼 추가
  - `locked_until` 컬럼 추가 (계정 잠금)
  - `email_verified` 컬럼 추가
  - `email_verified_at` 컬럼 추가
- [ ] Address 테이블
  - `used_count` 컬럼 추가 (사용 횟수)
  - `last_used_at` 컬럼 추가
- [ ] 새 테이블 추가
  - `login_history` 테이블 (로그인 이력)
  - `token_blacklist` 테이블 (토큰 블랙리스트)

### 6.2 인덱스 추가

- [ ] `user_email` 인덱스 (이미 unique)
- [ ] `user_phone` 인덱스
- [ ] `postal_code` 인덱스
- [ ] `user_id, is_default` 복합 인덱스 (Address)
- [ ] `user_id, is_deleted` 복합 인덱스

---

## 우선순위

### 🔥 High Priority (필수)

1. Task 2: 예외 처리 추가
2. Task 1: 입력값 검증 강화
3. Task 3.1: 인증/인가 강화 (토큰 관리)
4. Task 5.1: 코드 구조 개선 (SecurityContext 활용)

### 🔸 Medium Priority (권장)

1. Task 3.2: 권한 관리 강화
2. Task 4.1: User 기능 추가 (회원 탈퇴, 비밀번호 찾기)
3. Task 5.2: 성능 최적화
4. Task 6.1: 스키마 개선

### 🔹 Low Priority (선택)

1. Task 4.3: 소셜 로그인
2. Task 5.3: 테스트 코드
3. Task 5.4: 문서화

---

## 참고사항

- 모든 작업은 별도의 브랜치에서 진행
- 각 Task는 독립적으로 PR 생성
- 컨벤션 규칙 준수
- 리뷰어 approval 필수
