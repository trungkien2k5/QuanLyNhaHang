# Codebase Convention

## 1. Mục đích

File này định nghĩa quy ước code cho project `QuanLyNhaHang`.

AI và developer phải ưu tiên làm theo convention hiện có của codebase thay vì tự tạo cách tổ chức mới.

---

## 2. Kiến trúc

Project sử dụng kiến trúc Spring Boot theo các layer chính:

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
Database
```

Các thành phần bổ sung:

```text
Entity
DTO
Exception
Security
Event
Config
```

Không tự ý bỏ qua layer nếu không có lý do rõ ràng.

---

## 3. Package

Package chính:

```text
com.kien.quanlynhahang
```

Các package nên phân chia theo trách nhiệm:

```text
controller
service
repository
entity
dto
config
security
exception
event
mail
...
```

Không tạo package mới nếu có package hiện tại phù hợp.

---

## 4. Controller

Controller chỉ xử lý HTTP.

Controller chịu trách nhiệm:

* Nhận request.
* Validate input.
* Gọi Service.
* Trả response.

Không đặt business logic lớn trong Controller.

Ví dụ:

```java
@PostMapping
public ResponseEntity<?> create(@Valid @RequestBody MonAnDTO dto) {
    return ResponseEntity.ok(monAnService.themMon(dto));
}
```

Business logic phải nằm trong Service.

---

## 5. Service

Service chịu trách nhiệm xử lý business logic.

Ví dụ:

```java
@Service
@RequiredArgsConstructor
public class MonAnService {
}
```

Ưu tiên:

* `@RequiredArgsConstructor`
* Dependency Injection qua constructor.
* `@Transactional` khi xử lý transaction.
* `@Cacheable`, `@CacheEvict` khi logic cần caching.

Không viết query database trực tiếp trong Service nếu Repository có thể xử lý.

---

## 6. Repository

Repository chịu trách nhiệm truy cập database.

Ưu tiên Spring Data JPA:

```java
public interface MonAnRepository extends JpaRepository<MonAn, Integer> {
}
```

Query đặc biệt chỉ tạo khi method có sẵn của JPA không đủ.

Không đưa business logic vào Repository.

---

## 7. Entity

Entity đại diện cho database table.

Sử dụng:

```java
@Entity
@Table(name = "...")
```

Khóa chính sử dụng annotation JPA phù hợp.

Không dùng Entity trực tiếp làm request DTO nếu API cần validation hoặc dữ liệu đầu vào khác Entity.

---

## 8. DTO

DTO dùng để nhận/trả dữ liệu API.

Ví dụ:

```java
public class MonAnDTO {
}
```

Validation đặt ở DTO:

```java
@NotBlank
private String tenMon;
```

Không đưa password hoặc dữ liệu nhạy cảm vào response nếu không cần thiết.

---

## 9. Validation

Sử dụng Bean Validation:

```java
@Valid
@NotBlank
@NotNull
@Min
@Max
@Email
```

Không tự viết validation trùng với annotation có sẵn nếu không cần.

---

## 10. Exception

Không dùng `try-catch` lặp lại trong từng Controller chỉ để xử lý business exception.

Ưu tiên:

```java
@RestControllerAdvice
```

Exception phải trả response rõ ràng cho client.

---

## 11. Security

Logic authentication/authorization phải nằm trong security layer.

Các thành phần liên quan JWT/Spring Security phải tách riêng khỏi business logic.

Không hard-code:

* JWT secret
* Password
* Database password
* API key

Các giá trị cấu hình phải lấy từ configuration/environment.

---

## 12. Kafka

Kafka dùng cho event/asynchronous communication khi cần.

Event đặt trong package:

```text
event
```

Tên event phải mô tả hành động đã xảy ra.

Ví dụ:

```java
OrderCreatedEvent
```

Không đưa business logic phức tạp vào event object.

---

## 13. Redis / Cache

Cache chỉ dùng cho dữ liệu phù hợp.

Ví dụ:

```java
@Cacheable(value = "monan")
```

Khi dữ liệu thay đổi phải xem xét invalidate cache:

```java
@CacheEvict(value = "monan", allEntries = true)
```

Không cache mọi API một cách tùy tiện.

---

## 14. Naming

Class:

```text
MonAnService
MonAnController
MonAnRepository
MonAnDTO
```

Method dùng camelCase:

```text
themMon()
suaMon()
xoaMon()
getById()
```

Constant dùng:

```text
UPPER_SNAKE_CASE
```

Tên phải mô tả đúng trách nhiệm.

Không dùng tên kiểu:

```text
data()
test()
handle()
doSomething()
```

nếu có thể đặt tên cụ thể hơn.

---

## 15. BigDecimal

Tiền tệ sử dụng:

```java
BigDecimal
```

Không sử dụng `double` hoặc `float` cho giá tiền.

Ví dụ:

```java
BigDecimal tongTien;
BigDecimal donGia;
```

---

## 16. Logging

Dùng logger thay vì:

```java
System.out.println(...)
```

Không log:

* Password
* JWT
* Secret
* Thông tin nhạy cảm

---

## 17. Code mới

Khi thêm chức năng mới:

1. Tìm code tương tự trong project.
2. Tái sử dụng pattern hiện tại.
3. Giữ naming và package nhất quán.
4. Hạn chế tạo abstraction mới nếu chưa cần.
5. Không refactor hàng loạt nếu task chỉ yêu cầu một chức năng.

---

## 18. Nguyên tắc cho AI

AI phải:

* Đọc code hiện tại trước khi sửa.
* Ưu tiên pattern đang tồn tại.
* Không tự ý đổi kiến trúc.
* Không tự ý đổi tên class/package/API.
* Không xóa code đang hoạt động nếu không liên quan task.
* Không thêm thư viện nếu chưa cần.
* Không sửa nhiều file ngoài phạm vi cần thiết.
* Giữ backward compatibility nếu có thể.

Khi code hiện tại chưa hoàn hảo, không tự động refactor toàn bộ project chỉ để "đẹp hơn".

---

## 19. Ưu tiên

Thứ tự ưu tiên khi đưa ra quyết định:

```text
1. Đúng yêu cầu
2. Không phá code hiện tại
3. Nhất quán với codebase
4. Dễ bảo trì
5. Đơn giản
6. Tối ưu khi thực sự cần
```
