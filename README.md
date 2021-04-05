# SafeBundle📦  
## 목적
이 라이브러리의 목적은 **Activity/Fragment**간 데이터를 전달할때 사용하는 Bundle에 값을 올바르게 넣고, 그것에 맞게 꺼내서 사용하는지 컴파일 타임에 확인하여 **런타임 에러를 미연에 방지 하는 것**

```kotlin
class MyActivity1: AppCompatActivity() {

    ...
    
    fun startMyActivity2() {
        val intent = Intent(this, MyActivity2::class.java).apply {
            putExtra("param", "str")
        }
        startActivity(intent)
    }
    
    ...
    
}

class MyActivity2: AppCompatActivity() {
    ...
    
    override fun onCreate(savedInstanceState: Bundle?) {
        // wrong key value
        val param = intent.extras?.get("wrong key") as String -> Runtime Error
        
        // wrong type casting
        val param = intent.extras?.get("param") as Int -> Runtime Error
    }
}
```
> 위의 같이 개발자가 실수를 할 경우 런타임에 버그를 알 수 있으므로 위험하다.

## 사용법
### 1. 전달 받을 인자 설정
`Activity`에서 받을 데이터를 인자들을 멤버 변수 정의하고 `by bundle<Type>()`로 property delegation 한다.

```kotlin
class ReceiveActivity: AppCompatActivity() {

    private val p1 by bundle<Int>()
    
    private val p2 by bundle<String?>()
}
```
### 2. Activity를 시작 하는 Navgator정의
`Activity`를 어떤 인자로 시작할지 정의한 `Navigator`를 작성한다.

```kotlin
// Navigator는 interface여야 하며 ContextBasedCreatable을 확장해야 한다.
interface Navigator : ContextBasedCreatable {
    // ReciveActivity에서 정의한 멤버 변수들의 변수명과 타입과 일치해야 한다.
    // p1 - Int, p2 - String?
    fun start(p1: Int, p2: String?) // return 타입은 Unit이여야 한다.
    
    // p2는 Nullable하기 때문에 인자를 아예 안쓰는 함수 정의도 가능하다.
    fun start(p1: Int)
    
    // 받는쪽의 p2는 Nullable하기 때문에 충분 조건인 p2가 NonNull한 함수 정의도 가능하다.
    fun start(p1: Int, p2: String)
}
```
### 3. Activity와 Navgator 연결
`Activity`와 해당 `Activity`가 어떤 인자들로 시작될 수 있는지 정의한 `Navigator`를 `SafeBundle` 어노테이션으로 연결해주어야 컴파일 타임에 정합성 체크를 할 수 있다.

```kotlin
@SafeBundle(Navigator::class) // 이줄 추가
class ReceiveActivity: AppCompatActivity() {

    private val p1 by bundle<Int>()
    
    private val p2 by bundle<String?>()
}
```

### 4. Navgator를 통해 Activity 실행

```kotlin
class MainActivity: AppCompatActivity() {
    
    private val navigator = create<Navigator>() // Activity Extension function인 create() 사용
    
    fun startReceiveActivity() {
        navigator.start(1, "p2")
    }
    
    ...
}
```
