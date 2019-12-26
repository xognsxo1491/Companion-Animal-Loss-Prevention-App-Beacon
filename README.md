# Companion-Animal-Loss-Prevention-App-Beacon

해당 애플리케이션은 비콘을 활용하여 반려동물의 위치를 찾아주는 애플리케이션입니다.

## 주요 기능

- 반려동물 실종 등록을 통해 반려동물의 위치를 나만의 지도에 나타내줍니다.
- 애플리케이션 사용자들끼리 커뮤니티를 이용한 정보 교환이 가능합니다.
- 백그라운드에서 비콘을 동작시켜 놓으면 실종상태인 다른 유저의 반려동물을 찾아내는 것이 가능합니다.

## 필요 라이브러리 

- Google Firebase Messaging
- Google Firebase Core
- Google Location
- Google Maps
- Altbeacon Library

## 주의사항 및 기타

- cafe24의 서버를 호스팅하기 때문에 호스팅 기간 만료시 동작에 오류가 생깁니다.
- 장소에 따라 비콘 동작이 원활하게 수행되지 않을 수 있습니다.
- 백그라운드 비콘 탐지를 원하지 않는 경우, 메인 페이지에 on/off 기능을 조작하시면 됩니다.
