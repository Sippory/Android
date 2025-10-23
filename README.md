# Sippory - 개인 술 기록 앱 📱🍷

Sippory는 개인이 소장한 술을 기록하고 관리할 수 있는 Android 앱입니다.

## 주요 기능 ✨

### 1. 술 컬렉션 관리
- 📷 **사진 추가**: 갤러리에서 술 사진을 선택하여 등록
- 📝 **상세 정보**: 이름, 종류, 도수(ABV), 원산지, 평점, 메모 기록
- ⭐ **평점 시스템**: 0.5~5.0 별점으로 술 평가

### 2. 직관적인 UI
- 🗄️ **와인 냉장고 스타일 그리드**: 2열 그리드로 컬렉션을 시각적으로 표시
- 🎨 **Material 3 디자인**: 최신 Material Design 3 테마 적용
- ✨ **애니메이션**: 항목 추가/삭제 시 부드러운 애니메이션

### 3. 검색 & 필터
- 🔍 **실시간 검색**: 이름이나 종류로 빠르게 검색
- 🏷️ **타입별 필터**: Wine, Whiskey, Vodka 등 종류별 필터링
- ⭐ **평점 필터**: 4점 이상 고평점 술만 보기

### 4. CRUD 기능
- ➕ **추가**: FAB 버튼으로 새 술 추가 (Bottom Sheet)
- 👁️ **조회**: 상세 화면에서 모든 정보 확인
- ✏️ **수정**: 상세 화면에서 정보 수정
- 🗑️ **삭제**: 확인 다이얼로그와 함께 안전한 삭제

## 기술 스택 🛠️

### Architecture
- **MVVM Pattern**: ViewModel + Repository + Room DAO
- **Clean Architecture**: Presentation, Domain, Data 레이어 분리

### Android Jetpack
- **Jetpack Compose**: 선언형 UI 프레임워크
- **Room Database**: 로컬 데이터베이스
- **Navigation Compose**: 화면 간 네비게이션
- **ViewModel & LiveData**: UI 상태 관리
- **Kotlin Coroutines & Flow**: 비동기 프로그래밍

### UI/UX
- **Material 3**: 최신 디자인 시스템
- **Coil**: 이미지 로딩 라이브러리
- **Animations**: Compose 애니메이션

## 프로젝트 구조 📁

```
app/src/main/java/net/sippory/
├── data/
│   ├── dao/
│   │   └── BottleDao.kt              # Room DAO
│   ├── entity/
│   │   └── BottleEntity.kt           # Room Entity
│   ├── repository/
│   │   └── BottleRepository.kt       # Repository Pattern
│   └── AppDatabase.kt                # Room Database
│
├── presentation/
│   ├── home/
│   │   ├── HomeScreen.kt             # 홈 화면 UI
│   │   └── HomeViewModel.kt          # 홈 ViewModel
│   ├── detail/
│   │   ├── DetailScreen.kt           # 상세 화면 UI
│   │   └── DetailViewModel.kt        # 상세 ViewModel
│   └── add/
│       ├── AddBottleSheet.kt         # 추가 Bottom Sheet UI
│       └── AddBottleViewModel.kt     # 추가 ViewModel
│
├── navigation/
│   └── NavGraph.kt                   # Navigation 설정
│
├── utils/
│   ├── BottleTypes.kt                # 술 종류 상수
│   └── BottleViewModelFactory.kt     # ViewModel Factory
│
├── ui/theme/
│   ├── Color.kt
│   ├── Theme.kt
│   └── Type.kt
│
└── MainActivity.kt                   # 앱 진입점
```

## 데이터베이스 스키마 💾

### BottleEntity
| 필드 | 타입 | 설명 |
|------|------|------|
| id | Int | Primary Key (auto-increment) |
| name | String | 술 이름 |
| type | String | 종류 (Wine, Whiskey, etc.) |
| abv | Float? | 알코올 도수 (선택) |
| country | String? | 원산지 (선택) |
| photoUri | String? | 사진 URI |
| rating | Float | 평점 (0.5~5.0) |
| note | String | 메모 |
| createdAt | Long | 생성 시간 |
| updatedAt | Long | 수정 시간 |

## 빌드 및 실행 🚀

### 요구사항
- Android Studio Hedgehog 이상
- Android SDK 26 이상
- Kotlin 2.0.21

### 빌드
```bash
./gradlew assembleDebug
```

### 실행
```bash
./gradlew installDebug
```

## 향후 계획 🎯

### Phase 2
- [ ] 사용자 인증 (Firebase Authentication)
- [ ] 클라우드 동기화 (Firebase Firestore)
- [ ] 소셜 기능 (친구와 공유)

### Phase 3
- [ ] 외부 API 연동 (TheCocktailDB 등)
- [ ] 바코드 스캔 자동 입력
- [ ] 통계 및 분석 (마신 술 통계)
- [ ] 추천 시스템

### Phase 4
- [ ] 위치 기반 주점 추천
- [ ] 커뮤니티 기능
- [ ] 다국어 지원

## 라이선스 📄

이 프로젝트는 학습 및 포트폴리오 목적으로 제작되었습니다.

## 개발자 👨‍💻

Sippory Team
