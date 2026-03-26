# 🚀 E-Commerce Marketplace: Architecture & Tech Stack

เอกสารนี้รวบรวมเทคโนโลยีที่เลือกใช้ (Tech Stack) และโครงสร้างของโปรเจกต์ (Project Structure) สำหรับระบบ E-Commerce Marketplace ที่เน้นการออกแบบสถาปัตยกรรมฝั่ง Backend ที่รองรับสเกลการทำงานจริง มีความปลอดภัยสูง และจัดการข้อมูลที่ซับซ้อนได้อย่างมีประสิทธิภาพ

---

## 🛠️ 1. Tech Stack (เทคโนโลยีที่ใช้)

โปรเจกต์นี้เลือกใช้ชุดเทคโนโลยีระดับ Enterprise เพื่อความเสถียรและประสิทธิภาพสูงสุด โดยเน้นการพัฒนา Backend เป็นหลัก

### ⚙️ Backend Core
* **Language:** Java 21 (LTS) - ใช้ฟีเจอร์ใหม่ๆ เช่น Record สำหรับทำ DTO และเปิดใช้งาน Virtual Threads (Project Loom) เพื่อรับมือกับ High Concurrency I/O แบบไม่กิน Memory เพิ่ม (Performance Booster)
* **Framework:** Spring Boot 3.x - เฟรมเวิร์กหลักสำหรับการสร้าง RESTful API
* **Build Tool:** Maven / Gradle *(เลือกตามความถนัด)*

### 🗄️ Database & Caching
* **Primary Database:** PostgreSQL - ระบบฐานข้อมูลเชิงสัมพันธ์ที่แข็งแกร่ง จัดการ Transaction และ Data Consistency ได้ดีเยี่ยม (รองรับ JSONB)
* **In-memory Cache:** Redis - ใช้สำหรับ Caching ข้อมูลที่เรียกบ่อย, จัดการ JWT Blacklist และเก็บ OTP ชั่วคราว เพื่อลดภาระของ Database หลัก
* **Search Engine:** Meilisearch - ระบบค้นหาความเร็วสูง (Typo Tolerance) สำหรับค้นหาสินค้าและฟิลเตอร์ข้อมูล

### 🔒 Security & API
* **Authentication:** JSON Web Tokens (JWT) + Spring Security - ยืนยันตัวตนแบบ Stateless และจัดการสิทธิ์แบบ Dynamic RBAC
* **API Documentation:** Swagger (OpenAPI 3.0) - สำหรับสร้างเอกสาร API อัตโนมัติและใช้ทดสอบระบบระหว่างการพัฒนา

### 🧩 Utilities & Infrastructure
* **Object Mapping:** MapStruct - แปลงข้อมูลระหว่าง Entity และ DTO อย่างรวดเร็วและปลอดภัย (Compile-time type-safe)
* **Boilerplate Reduction:** Lombok - ลดการเขียนโค้ด Getter/Setter/Builder
* **Containerization:** Docker & Docker Compose - สำหรับจำลอง Environment ของ Database, Redis และ Meilisearch ให้นักพัฒนาใช้งานได้ทันที

### 💻 Frontend (Planned)
* **Framework:** Next.js (React) - สำหรับการสร้างหน้าเว็บฝั่ง Client ในเฟสสุดท้าย

---

## 📂 2. Project Structure (โครงสร้างโปรเจกต์)

โครงสร้างโฟลเดอร์ถูกออกแบบมาในรูปแบบ **Modular Layered Architecture** โดยจัดกลุ่มไฟล์ข้อมูล (Data Objects) ไว้ที่ศูนย์กลาง และแยกส่วนของ Business Logic อย่างชัดเจน เพื่อให้ง่ายต่อการดูแลรักษาเมื่อโปรเจกต์มีขนาดใหญ่ขึ้น

```text
com.jo.marketplace
├── common/              # คลาสหรือฟังก์ชันกลางที่ใช้ร่วมกันทั้งระบบ (เช่น BaseResponse, Pagination Details)
├── config/              # ตั้งค่าระบบและ External Services (RedisConfig, SwaggerConfig, SecurityConfig)
├── constant/            # จัดเก็บค่าคงที่ (Constants) เช่น ข้อความ Error, ตัวเลข Default ต่างๆ
├── controller/          # ชั้นรับ Request และส่ง Response (REST API Endpoints)
├── entity/              # คลาสที่ Map กับตารางใน Database (@Entity) รวมถึง BaseEntity
├── exception/           # จัดการ Error ระดับ Global (GlobalExceptionHandler, Custom Exceptions)
├── model/               # 📦 ศูนย์รวม Data Objects ของระบบ
│   ├── dto/             # Data Transfer Objects
│   │   ├── request/     # รูปแบบข้อมูลที่รับเข้ามาจาก Client
│   │   └── response/    # รูปแบบข้อมูลที่ส่งกลับไปให้ Client
│   ├── enums/           # ค่าสถานะต่างๆ (เช่น OrderStatus, Role, ActionType)
│   └── mapper/          # MapStruct Interfaces สำหรับแปลง Entity <-> DTO
├── repository/          # ชั้นติดต่อฐานข้อมูล (Spring Data JPA Interfaces)
├── security/            # ลอจิกด้านความปลอดภัย (JWT Filters, UserDetailsService)
├── service/             # ชั้นประมวลผล Business Logic (ศูนย์รวมความซับซ้อนของระบบ)
├── specification/       # ลอจิกสำหรับการทำ Dynamic Query/Filter (JPA Specification)
└── utils/               # ฟังก์ชันตัวช่วยอรรถประโยชน์ (เช่น DateUtil, StringUtil, HashUtil)