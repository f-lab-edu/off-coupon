# 프로젝트 개요 off-coupon
<img width="789" alt="image" src="https://github.com/f-lab-edu/off-coupon/assets/101460733/d2b9c662-3e0c-44d1-965a-0b1a15b54ac4">

선착순 쿠폰 발행 서비스 입니다. (네고왕 이벤트 참고 - 추후 설명 추가 예정)

# Git Flow

✅ master : 릴리스 버전을 관리하는 메인 브랜치  
✅ develop : 개발이 진행되는 통합 브랜치  
✅ feature : 새로운 기능을 개발하는 브랜치  
✅ hotfix : 실제 프로덕션에서 발생한 버그를 수정하는 브랜치  

Reference : [우아한 형제들 기술블로그 : gitFlow](https://techblog.woowahan.com/2553/)

# 프로젝트 기술 스택

- ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.2-6DB33F?logo=spring%20boot&logoColor=6DB33F)
- ![MyBatis](https://img.shields.io/badge/MyBatis-3.0.2-000000?logo=&logoColor=000000)
- ![Gradle](https://img.shields.io/badge/Gradle-8.5-02303A?logo=gradle&logoColor=02303A)
- ![IntelliJ](https://img.shields.io/badge/IntelliJ-2023.1-000000?logo=intellijidea&logoColor=000000)

# Commit Convention

- feat : 새로운 기능 추가  
- fix : 버그 수정  
- docs : 문서 수정  
- style : 코드 포맷팅, 세미콜론 누락, 코드 변경이 없는 경우  
- refactor : 코드 리팩토링  
- test : 테스트 코드, 리팩토링 테스트 코드  
- chore : 빌드 업무 수정, 패키지 매니저 수정  

## 쿠폰 발행 Flow

<img width="844" alt="image" src="https://github.com/f-lab-edu/off-coupon/assets/101460733/0fb0fc6d-c985-40ab-8819-0e857da24101">

## 동시성 테스트
[쿠폰 발급에 대한 동시성 처리 (1) - synchronized, pessimisti Lock, optimistic Lock](https://strong-park.tistory.com/entry/%EC%BF%A0%ED%8F%B0-%EB%B0%9C%EA%B8%89%EC%97%90-%EB%8C%80%ED%95%9C-%EB%8F%99%EC%8B%9C%EC%84%B1-%EC%B2%98%EB%A6%AC-1-synchronized-pessimisti-Lock-optimistic-Lock)  
[쿠폰 발급에 대한 동시성 처리 (2) - MySQL의 NamedLock, Redis의 분산락(Lettuce, Redisson)](https://strong-park.tistory.com/entry/%EC%BF%A0%ED%8F%B0-%EB%B0%9C%EA%B8%89%EC%97%90-%EB%8C%80%ED%95%9C-%EB%8F%99%EC%8B%9C%EC%84%B1-%EC%B2%98%EB%A6%AC-2-MySQL%EC%9D%98-NamedLock-Redis%EC%9D%98-%EB%B6%84%EC%82%B0%EB%9D%BDLettuce-Redisson)


## 회원가입

[@RequestBody는 어떻게 바인딩 되는걸까? (with. 디버깅 과정)](https://strong-park.tistory.com/entry/RequestBody%EB%8A%94-%EC%96%B4%EB%96%BB%EA%B2%8C-%EB%B0%94%EC%9D%B8%EB%94%A9-%EB%90%98%EB%8A%94%EA%B1%B8%EA%B9%8C-with-%EB%94%94%EB%B2%84%EA%B9%85-%EA%B3%BC%EC%A0%95)  
[Request에 대한 validation과 Exception 처리에 대한 고찰](https://strong-park.tistory.com/entry/%ED%9A%8C%EC%9B%90%EA%B0%80%EC%9E%85-%EC%8B%9C-Request-validation%EA%B3%BC-Exception-%EC%B2%98%EB%A6%AC%EC%97%90-%EB%8C%80%ED%95%9C-%EA%B3%A0%EC%B0%B0)  
[DATETIME vs TIMESTAMP 둘 중 어느것이 더 나을까?](https://strong-park.tistory.com/entry/DATETIME-vs-TIMESTAMP-%EB%91%98-%EC%A4%91-%EC%96%B4%EB%8A%90%EA%B2%83%EC%9D%B4-%EB%8D%94-%EB%82%98%EC%9D%84%EA%B9%8C)  

## 트러블 슈팅
[private 메소드를 테스트하려 했지만, 문제는 테스트 코드 로직이었다.](https://strong-park.tistory.com/entry/%ED%8A%B8%EB%9F%AC%EB%B8%94-%EC%8A%88%ED%8C%85-private-%EB%A9%94%EC%86%8C%EB%93%9C%EB%A5%BC-%ED%85%8C%EC%8A%A4%ED%8A%B8%ED%95%98%EB%A0%A4-%ED%96%88%EC%A7%80%EB%A7%8C-%EB%AC%B8%EC%A0%9C%EB%8A%94-%ED%85%8C%EC%8A%A4%ED%8A%B8-%EC%BD%94%EB%93%9C%EC%98%80%EB%8B%A4)  
[Junit 테스트 중 Lock wait timeout exceeded 에러 발생](https://strong-park.tistory.com/entry/%ED%8A%B8%EB%9F%AC%EB%B8%94-%EC%8A%88%ED%8C%85-Junit-%ED%85%8C%EC%8A%A4%ED%8A%B8%EB%A5%BC-%ED%95%98%EB%8B%A4%EA%B0%80-Lock-wait-timeout-exceeded%EA%B0%80-%EB%96%B4%EB%8B%A4)
