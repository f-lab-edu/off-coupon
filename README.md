# off-coupon 프로젝트 개요

<img width="789" alt="image" src="https://github.com/f-lab-edu/off-coupon/assets/101460733/d2b9c662-3e0c-44d1-965a-0b1a15b54ac4">  
<br> 이 프로젝트는 네고왕 선착순 이벤트를 참고하여 개발된 서비스로, 선착순 쿠폰 발행 및 사용 기능을 중점적으로 다루고 있습니다. 

# 프로젝트 목표
- 고가용성 선착순 쿠폰 발행 및 사용 서비스를 구현하는 것이 목표입니다.
- 객체지향 원칙에 준수하며 유지 보수 용이한 코드를 작성하는 것을 목표로 합니다.
- Slice Test를 활용하여 각 모듈의 기능을 단위별로 테스트하는 것이 목표입니다.
- 대규모 트래픽에도 데이터 정합성을 유지하고 서비스 안정성을 보장하는것이 목표입니다.

# Git Flow

✅ master : 릴리스 버전을 관리하는 메인 브랜치  
✅ develop : 개발이 진행되는 통합 브랜치  
✅ feature : 새로운 기능을 개발하는 브랜치  
✅ hotfix : 실제 프로덕션에서 발생한 버그를 수정하는 브랜치  

Reference : [우아한 형제들 기술블로그 : gitFlow](https://techblog.woowahan.com/2553/)

# 프로젝트 기술 스택
- ![Java](https://img.shields.io/badge/Java-17-007396?logo=java)
- ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.2-6DB33F?logo=spring%20boot&logoColor=6DB33F)
- ![MySQL](https://img.shields.io/badge/MySQL-8.0.32-4479A1?logo=mysql&logoColor=4479A1)
- ![Redis](https://img.shields.io/badge/Redis-7.2.4-DC382D?logo=redis&logoColor=white)  
- ![MyBatis](https://img.shields.io/badge/MyBatis-3.0.3-000000?logo=&logoColor=000000)  
- ![Gradle](https://img.shields.io/badge/Gradle-8.5-02303A?logo=gradle&logoColor=02303A)  
- ![JUnit](https://img.shields.io/badge/JUnit-5.8.2-25A162?logo=junit&logoColor=white)
- ![JaCoCo](https://img.shields.io/badge/JaCoCo-_-FF4088?logo=jacoco&logoColor=white)
- ![Docker](https://img.shields.io/badge/Docker-24.0.2-2496ED?logo=docker&logoColor=white)
- ![IntelliJ](https://img.shields.io/badge/IntelliJ-2023.1-000000?logo=intellijidea&logoColor=000000)
- ![JMeter](https://img.shields.io/badge/JMeter-5.6.2-D21717?logo=apache%20jmeter&logoColor=white)
- ![Jenkins](https://img.shields.io/badge/Jenkins-2.449-D24939?logo=jenkins&logoColor=white)
  

# 서비스 아키텍처

<img width="1021" alt="image" src="https://github.com/f-lab-edu/off-coupon/assets/101460733/1ccb49c7-91b4-4e28-b6b9-d529f794ee9b">


## 쿠폰 발행 Flow

![image](https://github.com/f-lab-edu/off-coupon/assets/101460733/14c085a4-caf2-4184-a97d-58061b0a9bb7)
<img width="1014" alt="image" src="https://github.com/f-lab-edu/off-coupon/assets/101460733/1a4fb15f-4c49-4098-85c3-669a331359ef">

## 동시성 테스트
[쿠폰 발급에 대한 동시성 처리 (1) - synchronized, pessimisti Lock, optimistic Lock](https://strong-park.tistory.com/entry/%EC%BF%A0%ED%8F%B0-%EB%B0%9C%EA%B8%89%EC%97%90-%EB%8C%80%ED%95%9C-%EB%8F%99%EC%8B%9C%EC%84%B1-%EC%B2%98%EB%A6%AC-1-synchronized-pessimisti-Lock-optimistic-Lock)  
[쿠폰 발급에 대한 동시성 처리 (2) - MySQL의 NamedLock, Redis의 분산락(Lettuce, Redisson)](https://strong-park.tistory.com/entry/%EC%BF%A0%ED%8F%B0-%EB%B0%9C%EA%B8%89%EC%97%90-%EB%8C%80%ED%95%9C-%EB%8F%99%EC%8B%9C%EC%84%B1-%EC%B2%98%EB%A6%AC-2-MySQL%EC%9D%98-NamedLock-Redis%EC%9D%98-%EB%B6%84%EC%82%B0%EB%9D%BDLettuce-Redisson)

## CICD

[NCP환경에서 Jenkins와 Docker로 CICD Pipeline 구축하기](https://strong-park.tistory.com/entry/NCP%ED%99%98%EA%B2%BD%EC%97%90%EC%84%9C-Jenkins%EC%99%80-Docker%EB%A1%9C-CICD-Pipeline-%EA%B5%AC%EC%B6%95%ED%95%98%EA%B8%B0)  

## 회원가입

[@RequestBody는 어떻게 바인딩 되는걸까? (with. 디버깅 과정)](https://strong-park.tistory.com/entry/RequestBody%EB%8A%94-%EC%96%B4%EB%96%BB%EA%B2%8C-%EB%B0%94%EC%9D%B8%EB%94%A9-%EB%90%98%EB%8A%94%EA%B1%B8%EA%B9%8C-with-%EB%94%94%EB%B2%84%EA%B9%85-%EA%B3%BC%EC%A0%95)  
[Request에 대한 validation과 Exception 처리에 대한 고찰](https://strong-park.tistory.com/entry/%ED%9A%8C%EC%9B%90%EA%B0%80%EC%9E%85-%EC%8B%9C-Request-validation%EA%B3%BC-Exception-%EC%B2%98%EB%A6%AC%EC%97%90-%EB%8C%80%ED%95%9C-%EA%B3%A0%EC%B0%B0)  
[DATETIME vs TIMESTAMP 둘 중 어느것이 더 나을까?](https://strong-park.tistory.com/entry/DATETIME-vs-TIMESTAMP-%EB%91%98-%EC%A4%91-%EC%96%B4%EB%8A%90%EA%B2%83%EC%9D%B4-%EB%8D%94-%EB%82%98%EC%9D%84%EA%B9%8C)  

## 트러블 슈팅
[private 메소드를 테스트하려 했지만, 문제는 테스트 코드 로직이었다.](https://strong-park.tistory.com/entry/%ED%8A%B8%EB%9F%AC%EB%B8%94-%EC%8A%88%ED%8C%85-private-%EB%A9%94%EC%86%8C%EB%93%9C%EB%A5%BC-%ED%85%8C%EC%8A%A4%ED%8A%B8%ED%95%98%EB%A0%A4-%ED%96%88%EC%A7%80%EB%A7%8C-%EB%AC%B8%EC%A0%9C%EB%8A%94-%ED%85%8C%EC%8A%A4%ED%8A%B8-%EC%BD%94%EB%93%9C%EC%98%80%EB%8B%A4)  
[Junit 테스트 중 Lock wait timeout exceeded 에러 발생](https://strong-park.tistory.com/entry/%ED%8A%B8%EB%9F%AC%EB%B8%94-%EC%8A%88%ED%8C%85-Junit-%ED%85%8C%EC%8A%A4%ED%8A%B8%EB%A5%BC-%ED%95%98%EB%8B%A4%EA%B0%80-Lock-wait-timeout-exceeded%EA%B0%80-%EB%96%B4%EB%8B%A4)  
[테스트코드도 코드이므로 합성을 통해 중복을 없애자](https://strong-park.tistory.com/entry/%ED%8A%B8%EB%9F%AC%EB%B8%94-%EC%8A%88%ED%8C%85-%ED%85%8C%EC%8A%A4%ED%8A%B8%EC%BD%94%EB%93%9C%EB%8F%84-%EC%BD%94%EB%93%9C%EC%9D%B4%EB%AF%80%EB%A1%9C-%EC%B6%94%EC%83%81%ED%99%94%EB%A5%BC-%ED%86%B5%ED%95%B4-%EC%A4%91%EB%B3%B5%EC%9D%84-%EC%97%86%EC%95%A0%EC%9E%90)
## Commit Convention

- feat : 새로운 기능 추가  
- fix : 버그 수정  
- docs : 문서 수정  
- style : 코드 포맷팅, 세미콜론 누락, 코드 변경이 없는 경우  
- refactor : 코드 리팩토링  
- test : 테스트 코드, 리팩토링 테스트 코드  
- chore : 빌드 업무 수정, 패키지 매니저 수정  
