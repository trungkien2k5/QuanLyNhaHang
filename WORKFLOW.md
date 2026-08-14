# AI Development Workflow

## 1. Mục đích

AI không được lập tức sửa code ngay khi nhận task.

AI phải phân tích codebase trước, đưa ra plan, chờ review, sau đó mới implement.

---

## 2. Bước 1 — Đọc codebase

Trước khi thay đổi code, AI phải kiểm tra:

* Cấu trúc project.
* Các package liên quan.
* Controller liên quan.
* Service liên quan.
* Repository liên quan.
* Entity/DTO liên quan.
* Security nếu task có authentication.
* Config nếu task liên quan infrastructure.
* Test hiện có.

Không đoán cấu trúc nếu có thể đọc trực tiếp source code.

---

## 3. Bước 2 — Phân tích ảnh hưởng

AI phải xác định:

```text
Task ảnh hưởng file nào?
API nào?
Database nào?
Business logic nào?
Có ảnh hưởng authentication không?
Có ảnh hưởng cache không?
Có ảnh hưởng Kafka không?
Có cần test không?
```

Phân biệt rõ:

```text
File bắt buộc phải sửa
File có thể phải sửa
File không cần sửa
```

---

## 4. Bước 3 — Viết PLAN

Trước khi implement, AI phải trình bày:

### Mục tiêu

Task cần đạt được gì.

### Phân tích hiện trạng

Code hiện tại đang hoạt động như thế nào.

### Các file sẽ thay đổi

Ví dụ:

```text
src/.../MonAnController.java
src/.../MonAnService.java
src/.../MonAnRepository.java
```

### Thay đổi dự kiến

Mô tả ngắn từng thay đổi.

### Risk

Nêu các rủi ro có thể ảnh hưởng code hiện tại.

### Test

Nêu cách kiểm tra sau khi implement.

---

## 5. Bước 4 — REVIEW

AI phải dừng trước bước implementation.

Chỉ implement khi user đã review/đồng ý plan.

Nếu user yêu cầu implement ngay mà không cần review, có thể bỏ qua bước chờ review.

---

## 6. Bước 5 — IMPLEMENT

Khi implement:

* Chỉ sửa phạm vi cần thiết.
* Giữ nguyên architecture hiện tại.
* Tuân thủ `CONVENTION.md`.
* Không tự ý refactor không liên quan.
* Không thay đổi API hiện tại nếu task không yêu cầu.
* Không xóa functionality đang hoạt động.

Ưu tiên sửa ít file nhất có thể.

---

## 7. Bước 6 — TEST

Sau khi implement, kiểm tra:

### Compile

```bash
mvn clean test
```

hoặc Maven Wrapper:

```bash
./mvnw test
```

Windows:

```bash
mvnw.cmd test
```

### API

Kiểm tra:

* HTTP status.
* Request.
* Response.
* Validation.
* Exception.

### Database

Kiểm tra:

* CRUD.
* Relationship.
* Transaction.
* Query.

### Infrastructure

Nếu có thay đổi liên quan:

```text
Docker
Kafka
Redis
SQL Server
```

phải kiểm tra tương ứng.

---

## 8. Bước 7 — REVIEW CODE

Sau khi code xong AI phải tự kiểm tra:

```text
[ ] Có compile không?
[ ] Có lỗi import không?
[ ] Có lỗi logic không?
[ ] Có vi phạm convention không?
[ ] Có code dư không?
[ ] Có ảnh hưởng API cũ không?
[ ] Có cần update test không?
[ ] Có hard-code secret không?
[ ] Có thay đổi ngoài phạm vi task không?
```

---

## 9. Bước 8 — Báo cáo kết quả

Sau khi hoàn thành, AI phải báo:

### Đã thay đổi

Liệt kê file và chức năng đã sửa.

### Logic chính

Giải thích ngắn flow xử lý.

### Test

Nêu đã chạy test gì và kết quả.

### Vấn đề còn lại

Nếu có lỗi hoặc phần chưa kiểm tra được phải nói rõ.

---

## 10. Khi gặp lỗi

Không sửa ngẫu nhiên.

Quy trình:

```text
1. Đọc stack trace
2. Xác định nguyên nhân
3. Kiểm tra code liên quan
4. Đưa ra nguyên nhân
5. Đề xuất cách sửa
6. Sửa
7. Test lại
```

Không che lỗi bằng cách:

* Xóa exception.
* Bỏ validation.
* Tắt security.
* Disable test.
* Hard-code dữ liệu để chạy qua.

---

## 11. Khi task lớn

Nếu task ảnh hưởng nhiều module:

```text
Task
  ↓
Phân tích
  ↓
Chia nhỏ
  ↓
Plan
  ↓
Review
  ↓
Implement từng phần
  ↓
Test
  ↓
Review tổng thể
```

Không sửa hàng chục file cùng lúc nếu có thể chia thành các bước nhỏ.

---

## 12. Khi không chắc

AI phải nói rõ:

```text
Không chắc
```

hoặc:

```text
Cần kiểm tra thêm file X
```

Không tự bịa API, database schema hoặc business rule.

---

## 13. Quy tắc quan trọng nhất

```text
READ → ANALYZE → PLAN → REVIEW → IMPLEMENT → TEST → REPORT
```

AI phải ưu tiên hiểu codebase hiện tại trước khi tạo code mới.
