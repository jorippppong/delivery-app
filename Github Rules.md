**Commit/PR 접두사 규칙**

- `feature` : 새로운 기능 추가
- `refactor` : 기능이나 동작은 변경하지 않지만, 코드의 가독성, 유지보수성 등을 향상하기 위해 코드를 수정
  - 함수나 메서드를 더 작은 단위로 분리
  - 변수 이름을 명확하게 변경
  - 중복 코드를 제거하는 등의 작업
- `test` : 테스트 코드 추가
- `fix` : 버그 수정
- `infra` : 시스템의 기본 구조와 운영 환경을 설정
- `docs` : 문서 수정
- `style` : 코드 포맷팅등 코드 변경이 없는 경우
  - 줄바꿈, 공백을 정리 등
- `chore` : 프로젝트 구조 변경
  - 빌드 업무 수정, 패키지 매니저 수정 등
- `merge` : 브랜치 병합, 병합 충돌 해결

“feature: ~ 기능 개발”

“fix: ~ 수정”

**✔️ Branch 규칙**

기능 개발 시 : `feature/`

- 예시 : `feature/DB-1`
- git-flow 브랜치 전략을 따른다.

1. **이슈**
   1. feature/190
2. 이름
3. 도메인

**✔️ Commit 규칙**

- 예시 : `feature: ~ 기능 개발`

**✔️ PR 규칙**

- 예시 : `[feature/이슈번호] ~ 개발`

1. 하나의 PR 에 너무 많은 작업을 담는 것을 피한다.

**✔️ Merge 규칙**

1. merge commit 메시지는 PR 이력을 파악하기 쉽도록 PR 제목과 동일하게 작성하며, 추적이 용이하도록 PR 번호를 추가한다.
   - 예시 : `[feature/DB-1]: 로그 설정 (#1)`
2. squash and merge 하여 커밋 그래프를 단순하게 가져가고 의미 있는 커밋들로 관리한다.
3. 본인 이외 PR에 기여한 사람이 있다면 merge message에 공동작업자로 추가한다.

- `Co-Authored-By: jorippppong <duelee75@gmail.com>`
- `Co-Authored-By: rjswjddn <>`

1. 리뷰어가 모두 approve 해야 merge 가능하도록 제한한다.
   - 1명 (pair)
2. 리뷰가 끝나고 appove가 완료된 후 PR을 올린사람이 merge 한다.
3. merge conflict 가 발생할 경우, PR을 올린 사람이 충돌을 해결한다.
