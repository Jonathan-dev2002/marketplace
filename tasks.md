# 🛍️ E-Commerce Marketplace: Project Task Backlog

เอกสารนี้รวบรวมรายการงาน (Tasks) ทั้งหมดสำหรับการพัฒนาระบบ E-Commerce แบบ Multi-vendor โดยแบ่งออกเป็น 5 ระยะ (Phases) เพื่อให้สามารถติดตามความคืบหน้าและจัดการ MVP (Minimum Viable Product) ได้อย่างมีประสิทธิภาพ

---

## 📦 Phase 1: Foundation & Authentication (รากฐานและความปลอดภัย)

### [EM-001] Project Initialization & Infrastructure
* **Feature:** Project Setup & Environment
* **Concept:** เตรียมโครงสร้างโปรเจกต์ Spring Boot แบบ Modular Layered Architecture และตั้งค่า Service ต่างๆ ให้พร้อมรัน
* **Checklist:**
  - [ ] สร้างโปรเจกต์ Spring Boot (Java 21+)
  - [ ] จัดโครงสร้าง Folder (controller, service, repository, entity, dto, common)
  - [ ] ตั้งค่า Docker Compose (PostgreSQL, Redis, Meilisearch)
  - [x] Config Redis connection properties สำหรับ token blacklist และ future cache usage
  - [x] ตั้งค่า Swagger (OpenAPI) สำหรับทำ API Docs
* **Priority:** 🔴 High
* **Difficulty:** ⚡ Medium

### [EM-021] Media Storage & Cloudinary Configuration
* **Feature:** File Upload & Media Storage
* **Concept:** ตั้งค่า Cloudinary สำหรับจัดเก็บไฟล์รูปภาพของระบบ เช่น KYC documents, shop logo, product images และ media อื่นๆ
* **Checklist:**
  - [ ] เพิ่ม Cloudinary dependency และ configuration properties (`cloud_name`, `api_key`, `api_secret`)
  - [ ] สร้าง Cloudinary config bean และ media storage service กลาง
  - [ ] กำหนด upload folders แยกตาม module เช่น `kyc`, `shops`, `products`
  - [ ] เพิ่ม validation สำหรับ file type, file size, และ allowed image formats
  - [ ] เพิ่ม response DTO สำหรับ upload result (`url`, `publicId`, `secureUrl`)
  - [ ] เพิ่ม delete/replace media helper สำหรับเคสเปลี่ยนรูปหรือยกเลิกเอกสาร
  - [ ] เพิ่ม environment variable documentation สำหรับ Cloudinary ใน README
* **Priority:** 🟡 Medium
* **Difficulty:** ⚡ Medium

### [EM-002] Advanced Authentication (JWT)
* **Feature:** User Authentication & Security
* **Concept:** ระบบยืนยันตัวตนแบบ Stateless โดยใช้ JSON Web Token พร้อมระบบจัดการ Session ความปลอดภัยสูงผ่าน Redis
* **Checklist:**
  - [x] Implement Login / Register API (`POST /api/v1/auth/login`, `POST /api/v1/auth/register`)
  - [x] สร้าง JWT Provider สำหรับ Generate & Validate Access Token
  - [x] เพิ่ม Refresh Token Flow (`POST /api/v1/auth/refresh`) พร้อม rotate refresh token เดิม
  - [x] เชื่อมต่อ Redis ทำ Token Blacklist สำหรับ Logout (`POST /api/v1/auth/logout`)
  - [x] เพิ่ม `jti` claim ใน JWT เพื่อใช้ revoke token แบบระบุตัวตนได้
  - [x] สร้าง Security Config & Filter Chain แบบ Stateless
  - [x] จำกัด public auth endpoint เฉพาะ register/login/refresh และบังคับ auth endpoint ที่เหลือ
  - [x] Map roles เป็น Spring Security authorities (`ROLE_*`) เพื่อรองรับ `@PreAuthorize`
  - [x] ตรวจ account status เพื่อบล็อก user ที่ไม่ใช่ `ACTIVE` ทั้งตอน login และตอนใช้ token
  - [x] เพิ่ม API เปลี่ยนรหัสผ่าน (`POST /api/v1/auth/change-password`)
  - [x] ปรับ Auth Response ให้คืน `userId`, `roles`, `authorities`, `expiresIn`, `accessToken`, และ `refreshToken`
  - [x] ปรับ JWT Filter ให้ตรวจ account status ด้วย query เฉพาะ `existsByIdAndStatus(...)`
  - [ ] Future: เพิ่ม cache สำหรับ account status lookup ใน JWT Filter พร้อม invalidation เมื่อ user ถูก deactivate/suspend/ban
* **Priority:** 🔴 High
* **Difficulty:** ⚡ Medium

### [EM-003] Dynamic RBAC System (Role & Permission)
* **Feature:** Role-Based Access Control
* **Concept:** ระบบจัดการสิทธิ์ที่ยืดหยุ่น แยกตาราง Role และ Permission ออกจากกัน เพื่อรองรับการที่ร้านค้าสามารถสร้างตำแหน่งพนักงาน (Custom Role) ได้เอง
* **Checklist:**
  - [x] สร้าง Entity พื้นฐาน: `User`, `Role`, `Permission`, `RolePermission`, `UserShopRole`
  - [x] สร้าง Data Seeder สำหรับ Default Roles (`ADMIN`, `SELLER`, `BUYER`)
  - [x] กำหนด Permission Slug มาตรฐานของระบบ เช่น `SHOP_VIEW`, `SHOP_UPDATE`, `SHOP_EMPLOYEE_MANAGE`, `PRODUCT_CREATE`, `PRODUCT_UPDATE`, `ORDER_VIEW`
  - [x] เพิ่ม Data Seeder สำหรับ Default Permissions และ Mapping ระหว่าง Default Roles กับ Permissions
  - [x] เพิ่ม Repository Query สำหรับตรวจ role/permission ระดับร้านค้า (`userId`, `shopId`, `permissionSlug`)
  - [x] สร้าง Permission Guard/Evaluator สำหรับใช้กับ `@PreAuthorize` เช่น `@shopSecurity.hasPermission(#shopId, 'SHOP_UPDATE')`
  - [x] ปรับ EM-006 Shop APIs ให้ใช้ Permission Guard แทนการเช็ค owner แบบ hardcoded ในทุกจุดที่เหมาะสม
  - [x] เพิ่ม API จัดการ Custom Role ภายในร้าน (`POST/GET/PATCH/DELETE /api/v1/shops/{shopId}/roles`)
  - [x] เพิ่ม API จัดการ Permission ของ Role (`PUT /api/v1/shops/{shopId}/roles/{roleId}/permissions`)
  - [x] เพิ่ม API ดู Permission ของผู้ใช้ในร้าน (`GET /api/v1/shops/{shopId}/permissions/me`)
  - [x] เพิ่ม Business Rules: ห้ามแก้ไข/ลบ system role โดยตรง, custom role ต้องอยู่ในร้านของตัวเอง, และห้าม assign permission นอกขอบเขตร้าน
  - [x] ปรับ JWT/Auth Response หรือ endpoint `/permissions/me` ให้ frontend รู้สิทธิ์ของ user ตาม shop ที่เลือก
* **Priority:** 🔴 High
* **Difficulty:** 🔥 Hard

### [EM-004] User Profile & Address Book
* **Feature:** User Data Management
* **Concept:** จัดการข้อมูลส่วนตัวและสมุดที่อยู่สำหรับจัดส่งสินค้า ซึ่งผู้ใช้ 1 คนสามารถมีได้หลายที่อยู่
* **Checklist:**
  - [x] API ดู Profile ของตัวเอง (`GET /api/v1/users/me`)
  - [x] API แก้ไข Profile ของตัวเอง (`PATCH /api/v1/users/me`)
  - [x] API ปิดบัญชีของตัวเอง (`PATCH /api/v1/users/me/deactivate`)
  - [x] สร้าง Entity และ Repository สำหรับ Address Book
  - [x] API ดูรายการที่อยู่ของตัวเอง (`GET /api/v1/addresses`)
  - [x] API เพิ่มที่อยู่ (`POST /api/v1/addresses`)
  - [x] API ดูรายละเอียดที่อยู่ (`GET /api/v1/addresses/{addressId}`)
  - [x] API แก้ไขที่อยู่ (`PATCH /api/v1/addresses/{addressId}`)
  - [x] API ลบที่อยู่แบบ Soft Delete (`DELETE /api/v1/addresses/{addressId}`)
  - [x] API ตั้งค่า Default Address (`PATCH /api/v1/addresses/{addressId}/default`)
  - [x] Logic บังคับให้ผู้ใช้มี Default Address ได้แค่ 1 รายการ
  - [x] Logic ตั้ง address แรกเป็น default อัตโนมัติ
  - [x] Business Rules: ห้ามเข้าถึง/แก้ไข/ลบ address ของผู้ใช้อื่น
* **Priority:** 🔴 High
* **Difficulty:** 🧊 Easy

### [EM-005] KYC Verification System
* **Feature:** Identity Verification
* **Concept:** ระบบยืนยันตัวตนสำหรับผู้ที่จะเปิดร้านค้า โดยให้อัปโหลดภาพเอกสารเพื่อให้ระบบหลังบ้านตรวจสอบ
* **Checklist:**
  - [ ] สร้าง Entity และ Table `user_verifications`
  - [ ] สร้าง enum `VerificationStatusEnum` (`PENDING`, `APPROVED`, `REJECTED`)
  - [ ] API ส่งคำขอ KYC ของ user (`POST /api/v1/verifications/me`)
  - [ ] API ดูสถานะ KYC ของตัวเอง (`GET /api/v1/verifications/me`)
  - [ ] API แก้ไข/ส่ง KYC ใหม่เมื่อถูก reject (`PATCH /api/v1/verifications/me`)
  - [ ] API Upload รูปบัตรประชาชนและรูปยืนยันตัวตน
  - [ ] Admin API list คำขอ KYC (`GET /api/v1/admin/verifications`)
  - [ ] Admin API ดูรายละเอียด KYC (`GET /api/v1/admin/verifications/{verificationId}`)
  - [ ] Admin API approve/reject KYC (`PATCH /api/v1/admin/verifications/{verificationId}/status`)
  - [ ] เพิ่ม business rules: ห้าม submit ซ้ำตอน pending, ห้ามแก้เมื่อ approved, reject ต้องมีเหตุผล
  - [ ] เพิ่ม request/response DTO และ validation สำหรับข้อมูล KYC
  - [ ] เพิ่ม status code enum สำหรับ KYC success/error cases
  - [ ] เพิ่ม permission/admin guard สำหรับ KYC review endpoints
* **Priority:** 🟡 Medium
* **Difficulty:** 🧊 Easy

---

## 🛍️ Phase 2: Shop & Product Catalog (ร้านค้าและสินค้า)

### [EM-006] Shop Management System
* **Feature:** Multi-vendor Shop Management
* **Concept:** ระบบจัดการข้อมูลร้านค้า และการจัดการพนักงานภายในร้านสำหรับแพลตฟอร์มแบบหลายผู้ขาย
* **Checklist:**
  - [x] API สร้างร้านค้า (ผูกกับ User เจ้าของร้าน)
  - [x] API แก้ไขข้อมูลร้าน (Logo, Description)
  - [x] API เพิ่มพนักงานเข้าร้าน (Assign Role ในระดับร้านค้า)
  - [x] API ดูรายละเอียดร้านค้า (`GET /shops/{shopId}`)
  - [x] API ดูร้านค้าของฉัน (`GET /shops/me`) ทั้งร้านที่เป็นเจ้าของและร้านที่เป็นพนักงาน
  - [x] API ลบร้านค้าแบบ Soft Delete (`DELETE /shops/{shopId}`) โดย set `deleted_at` และปิด `is_active`
  - [x] API เปิด/ปิดสถานะร้านค้า (`PATCH /shops/{shopId}/status`) แยกจากการลบร้าน
  - [x] API ดูรายชื่อพนักงานในร้าน (`GET /shops/{shopId}/employees`)
  - [x] API ลบพนักงานออกจากร้าน (`DELETE /shops/{shopId}/employees/{userId}`) และห้ามลบเจ้าของร้าน
  - [x] API เปลี่ยน Role ของพนักงาน (`PATCH /shops/{shopId}/employees/{userId}/role`)
  - [x] กำหนด Slug Policy: ตอนสร้างร้าน generate slug จากชื่อร้าน, ตอนแก้ชื่อร้านไม่เปลี่ยน slug อัตโนมัติ
  - [x] API แก้ไข slug แยกต่างหาก (`PATCH /shops/{shopId}/slug`) พร้อมตรวจ slug ซ้ำ
  - [x] เพิ่ม Response DTO สำหรับข้อมูลร้านค้าและพนักงาน ห้ามคืน Entity ตรงๆ
  - [x] เพิ่ม Business Rules: ตรวจ owner permission, กันเพิ่มพนักงานซ้ำ, ห้าม assign role ที่ไม่เหมาะสม, และห้ามเพิ่ม owner เป็น employee ซ้ำ
* **Priority:** 🔴 High
* **Difficulty:** ⚡ Medium

### [EM-007] Recursive Category System
* **Feature:** Product Categories
* **Concept:** ระบบหมวดหมู่สินค้าที่สามารถซ้อนกันได้แบบไม่จำกัดชั้น (Tree Structure)
* **Checklist:**
  - [ ] สร้าง Entity `CategoryEntity` แบบ self-referencing (`parent`, `children`)
  - [ ] สร้าง Repository สำหรับ query category active/non-deleted
  - [ ] API สร้างหมวดหมู่ (`POST /api/v1/categories`)
  - [ ] API ดูรายละเอียดหมวดหมู่ (`GET /api/v1/categories/{categoryId}`)
  - [ ] API แก้ไขหมวดหมู่ (`PATCH /api/v1/categories/{categoryId}`)
  - [ ] API ลบหมวดหมู่แบบ soft delete (`DELETE /api/v1/categories/{categoryId}`)
  - [ ] API เปิด/ปิดหมวดหมู่ (`PATCH /api/v1/categories/{categoryId}/status`)
  - [ ] API ดึง category tree (`GET /api/v1/categories/tree`)
  - [ ] API ดึง children ของหมวดหมู่ (`GET /api/v1/categories/{categoryId}/children`)
  - [ ] เพิ่ม slug policy: generate slug จากชื่อ และตรวจ slug ซ้ำ
  - [ ] เพิ่ม rule กัน circular reference เช่น ห้ามย้าย category ไปอยู่ใต้ลูกของตัวเอง
  - [ ] เพิ่ม rule ห้ามลบ category ที่ยังมี child หรือ product ผูกอยู่
  - [ ] เพิ่ม Redis cache สำหรับ category tree และ clear cache เมื่อ create/update/delete/status change
  - [ ] เพิ่ม request/response DTO และ validation
  - [ ] เพิ่ม status code enum สำหรับ category success/error cases
  - [ ] เพิ่ม admin/permission guard สำหรับ create/update/delete category
* **Priority:** 🔴 High
* **Difficulty:** ⚡ Medium

### [EM-008] Product Variants & SKU System
* **Feature:** Complex Product Catalog
* **Concept:** ระบบจัดการสินค้าที่มีหลายตัวเลือก (เช่น สี, ไซส์) โดยแยกการจัดการสต็อกและราคาตาม SKU จริง
* **Checklist:**
  - [ ] ออกแบบและสร้าง Entity `Product`, `ProductOption`, `ProductSku`
  - [ ] API สร้างสินค้าพร้อม Variant (จัดการ Transaction ให้บันทึกลงหลายตารางพร้อมกัน)
  - [ ] API เปิด/ปิด การมองเห็นสินค้า (`isActive`)
* **Priority:** 🔴 High
* **Difficulty:** 🔥 Hard

### [EM-009] Meilisearch Integration
* **Feature:** High-Performance Search Engine
* **Concept:** ระบบค้นหาสินค้าหน้าบ้านที่รวดเร็ว รองรับการค้นหาแบบพิมพ์ผิด (Typos Tolerance) และการทำ Filter แบบ Dynamic
* **Checklist:**
  - [ ] Config Meilisearch Client
  - [ ] สร้าง Sync Service: Push ข้อมูลไป Meilisearch เมื่อมีการอัปเดต Product
  - [ ] API Search สินค้า (รับ Keyword & Filter แล้วยิงไปหา Meili)
* **Priority:** 🟡 Medium
* **Difficulty:** ⚡ Medium

---

## ⚙️ Phase 3: Inventory & Data Consistency (คลังสินค้าและบัญชี)

### [EM-010] Real-time Stock Management
* **Feature:** Concurrency Inventory Control
* **Concept:** ระบบตัดสต็อกที่แม่นยำ ป้องกันปัญหาการขายของเกิน (Overselling) เมื่อมีผู้ใช้งานแย่งกันกดซื้อจำนวนมาก
* **Checklist:**
  - [ ] Implement Atomic Update Query
  - [ ] สร้าง Stock Service สำหรับ Validate จำนวนสินค้าก่อนสร้างออเดอร์
* **Priority:** 🔴 High
* **Difficulty:** 🔥 Hard

### [EM-011] Inventory Ledger (Stock Card)
* **Feature:** Inventory Audit Trail
* **Concept:** สมุดบัญชีคุมสต็อกที่บันทึกประวัติการเปลี่ยนแปลงทุกครั้ง (เข้า/ออก/ปรับปรุง) เพื่อให้ตรวจสอบย้อนหลังได้อย่างโปร่งใส
* **Checklist:**
  - [ ] สร้าง Entity `InventoryLog` (มีฟิลด์ `lot_id` ด้วย)
  - [ ] Implement Logic บังคับ Insert Log ทุกครั้งที่มีการแก้ไขสต็อก
  - [ ] API เรียกดูประวัติสต็อกแยกตาม SKU
* **Priority:** 🟡 Medium
* **Difficulty:** ⚡ Medium

### [EM-020] Lot Tracking & FIFO Costing System
* **Feature:** Advanced ERP-Grade Costing
* **Concept:** ระบบจัดการสินค้าแบบแยกล็อตการผลิต และอัลกอริทึมการตัดสต็อกข้ามล็อตแบบ First-In, First-Out (FIFO) เพื่อคำนวณต้นทุน/กำไรที่แท้จริง
* **Checklist:**
  - [ ] สร้าง Entity `ProductLot`
  - [ ] เขียน FIFO Algorithm Service: วนลูปหา Lot ที่เก่าที่สุดและตัดสต็อกลดหลั่นลงไป
  - [ ] เชื่อมต่อ Service นี้เข้ากับการสร้าง Order และบันทึก Log
* **Priority:** 🟡 Medium
* **Difficulty:** 🔥 Hard

---

## 💳 Phase 4: Order & Transaction (การซื้อขาย)

### [EM-012] Shopping Cart System
* **Feature:** Multi-shop Cart
* **Concept:** ตะกร้าสินค้าที่รองรับการเก็บสินค้าจากหลายร้านค้าไว้รวมกัน และคำนวณราคาสรุปเบื้องต้น
* **Checklist:**
  - [ ] API Add/Remove/Update สินค้าในตะกร้า
  - [ ] Logic คำนวณราคารวมของตะกร้า
* **Priority:** 🔴 High
* **Difficulty:** ⚡ Medium

### [EM-013] Order Management & Splitting
* **Feature:** Order Routing & Lifecycle
* **Concept:** ระบบสร้างคำสั่งซื้อที่จะทำการ "แยกออเดอร์ตามร้านค้า" โดยอัตโนมัติเมื่อกด Checkout และจัดการวงจรชีวิตของสถานะออเดอร์
* **Checklist:**
  - [ ] Logic Checkout: รับตะกร้า -> Group by Shop -> สร้าง Order แยกใบ
  - [ ] จัดการ Status Flow (PENDING -> PAID -> SHIPPED -> COMPLETED)
* **Priority:** 🔴 High
* **Difficulty:** 🔥 Hard

### [EM-014] Mock Payment Gateway
* **Feature:** Payment Simulation
* **Concept:** ระบบจำลองการรับชำระเงินเพื่อทดสอบ Flow การตัดเงิน, การคืนสต็อก (ถ้ายกเลิก), และการเปลี่ยนสถานะ
* **Checklist:**
  - [ ] สร้าง Endpoint `/payment/mock`
  - [ ] Logic: จำลองการจ่ายสำเร็จ -> Update สถานะ Order เป็น PAID -> เรียก Inventory Service ให้คอนเฟิร์มการจองสต็อก
* **Priority:** 🔴 High
* **Difficulty:** ⚡ Medium

### [EM-015] Product Review System
* **Feature:** Verified Buyer Reviews
* **Concept:** ระบบให้คะแนนและรีวิวสินค้าที่อนุญาตเฉพาะผู้ที่ซื้อสินค้านั้นและสถานะออเดอร์เสร็จสมบูรณ์แล้วเท่านั้น
* **Checklist:**
  - [ ] API Submit Review (ตรวจสอบเงื่อนไขสถานะออเดอร์)
  - [ ] API Get Reviews สำหรับแสดงผลที่หน้าสินค้า
* **Priority:** 🟡 Medium
* **Difficulty:** 🧊 Easy

---

## 📊 Phase 5: Analytics & Extra Features (ส่วนเสริมและสถิติ)

### [EM-016] Seller Analytics Dashboard
* **Feature:** Shop Performance Metrics
* **Concept:** หน้าจอ API สรุปสถิติสำหรับแม่ค้าเพื่อดูยอดขายและสินค้าขายดี โดยใช้การเขียน Query ที่มีประสิทธิภาพ
* **Checklist:**
  - [ ] API Get Daily Sales (Aggregate Query)
  - [ ] API Get Top Selling Products
  - [ ] รับ Parameter แบบ POST method สำหรับ Filter ช่วงเวลา
* **Priority:** 🟡 Medium
* **Difficulty:** 🔥 Hard

### [EM-017] System Audit Logs
* **Feature:** System Security Trail
* **Concept:** ระบบบันทึกพฤติกรรมของ User/Admin เมื่อมีการเปลี่ยนแปลงข้อมูลสำคัญในระบบ (เก็บค่าก่อนหน้าและค่าใหม่)
* **Checklist:**
  - [ ] สร้าง Entity `AuditLog` (เก็บ OldValue/NewValue แบบ JSONB)
  - [ ] สร้าง Aspect/Listener ไปดักจับ Event การแก้ไขข้อมูลสำคัญ (เช่น ข้อมูลร้าน, ราคา)
* **Priority:** 🟢 Low
* **Difficulty:** ⚡ Medium

### [EM-022] Query Optimization & Backend Style Refactor
* **Feature:** Backend Code Quality & Query Optimization
* **Concept:** ปรับ service/repository ให้ตรงกับ `BACKEND_STYLE_GUILDE.md` โดยลดการโหลด Entity เกินจำเป็น, ใช้ projection/update query เมื่อเหมาะสม, และลบ dead code
* **Checklist:**
  - [ ] ปรับ `deactivateMyAccount()` ให้ update เฉพาะ `status` ด้วย repository update query แทนการโหลด `MasUserEntity` ทั้งก้อน
  - [ ] ปรับ `updateShopStatus()` ให้ update เฉพาะ `isActive` ด้วย repository update query
  - [ ] ปรับ `softDeleteShop()` ให้ update เฉพาะ `isActive` และ `deletedAt` ด้วย repository update query
  - [ ] ประเมิน `updateShopSlug()` ว่าควรใช้ update query เฉพาะ field หรือคง entity update เพื่อ validation flow
  - [ ] ปรับ `assignEmployee()` ให้ใช้ `existsById(...)` หรือ `existsByIdAndStatus(...)` แทนการโหลด user ทั้ง entity เมื่อใช้แค่ตรวจ existence/id
  - [ ] ปรับ `getMyShops()` ให้ query เฉพาะ shop ที่ต้องใช้ และไม่ fetch role ที่ไม่ได้ใช้
  - [ ] ปรับ `getEmployees()` ให้ลดการโหลด shop entity ทั้งก้อนเพื่อใช้แค่ `ownerId` หรือใช้ projection/query เดียวสำหรับ employee response
  - [ ] ประเมิน `deleteAddress()` และ `setDefaultAddress()` ว่าสามารถใช้ update query เฉพาะ field ได้โดยไม่เสีย business rule/default-address consistency
  - [ ] ประเมิน `lockUserAddressBook()` ให้ใช้ lock query ที่เลือกเฉพาะ id แทนการโหลด user entity ทั้งก้อน
  - [ ] ลบ dead code เช่น `validateShopOwner()` ถ้าไม่มี usage หลังย้ายไปใช้ permission guard
  - [ ] แก้ hardcoded/encoding error messages ใน `MasUserServiceImpl` ให้ใช้ `StatusCodeEnums` อย่างสม่ำเสมอ
  - [ ] ตรวจ permission string ใน `@PreAuthorize` และวางแผนย้ายเป็น constants/custom annotation ในอนาคต
  - [ ] ตรวจ `StatusCodeEnums` ภาษาไทยที่ encoding เพี้ยน และปรับให้ response message อ่านได้ถูกต้อง
* **Priority:** 🟡 Medium
* **Difficulty:** ⚡ Medium

### [EM-023] Shop Internal Chat WebSocket
* **Feature:** Real-time Shop Employee Messaging
* **Concept:** ระบบแชทภายในร้านสำหรับ owner และ employee ใน shop เดียวกัน โดยใช้ WebSocket/STOMP และบันทึกข้อความลงฐานข้อมูล
* **Checklist:**
  - [x] เพิ่ม dependency `spring-boot-starter-websocket`
  - [x] Config WebSocket/STOMP endpoint (`/ws`) และ message broker
  - [x] สร้าง Entity `ShopChatRoom` และ `ShopChatMessage`
  - [x] เพิ่ม permission `SHOP_CHAT_VIEW`, `SHOP_CHAT_SEND`, `SHOP_CHAT_MANAGE`
  - [x] API list/create/manage chat rooms ในร้าน (`/api/v1/shops/{shopId}/chat/rooms`)
  - [x] API ดูประวัติข้อความแบบ pagination (`GET /api/v1/shops/{shopId}/chat/rooms/{roomId}/messages`)
  - [x] WebSocket send message endpoint เช่น `/app/shops/{shopId}/chat/rooms/{roomId}/messages`
  - [x] WebSocket subscribe topic แยกตาม `shopId` และ `roomId`
  - [x] ตรวจ JWT และ shop permission ตอน WebSocket connect/send
  - [x] ตรวจว่า room อยู่ใน shop เดียวกัน และ user เป็น owner/employee ของ shop นั้น
  - [x] บันทึก message ลง database ก่อน broadcast
  - [x] เพิ่ม validation เช่น message length, message type, และ empty content
  - [ ] เพิ่ม unread count/read receipt เป็น future enhancement
  - [ ] Add unread count and read receipt tracking per room/user
  - [ ] Add structured WebSocket error responses via user-specific queues such as `/user/queue/errors`
  - [ ] Add room member/visibility rules for private rooms, role-based rooms, or team-specific rooms
  - [ ] Add message edit/delete support with soft delete and permission rules
  - [ ] Add real database migration for `shop_chat_rooms` and `shop_chat_messages`
  - [ ] Add chat-specific limits and validation such as message rate limits and image payload rules
  - [ ] Define frontend reconnect and resubscribe flow for WebSocket disconnects and token refresh
* **Priority:** 🟡 Medium
* **Difficulty:** 🔥 Hard

### [EM-018] Wishlist Feature
* **Feature:** User Favorites
* **Concept:** ระบบรายการสินค้าโปรดที่ผู้ใช้สามารถกดบันทึกเก็บไว้ดูภายหลังได้
* **Checklist:**
  - [ ] สร้าง Entity `Favorite`
  - [ ] API Add/Remove Favorite
  - [ ] API Get Favorite List
* **Priority:** 🟢 Low
* **Difficulty:** 🧊 Easy

### [EM-019] Coupon System
* **Feature:** Promotional Vouchers
* **Concept:** ระบบส่วนลดที่มีเงื่อนไขซับซ้อน ทั้งรูปแบบส่วนลดของร้านค้าเอง และส่วนลดกลางของแพลตฟอร์ม
* **Checklist:**
  - [ ] สร้าง Entity `Coupon`
  - [ ] Logic ตรวจสอบเงื่อนไขคูปองตอน Checkout (ขั้นต่ำ, วันหมดอายุ)
  - [ ] Logic คำนวณส่วนลดท้ายบิล
* **Priority:** 🟢 Low
* **Difficulty:** 🔥 Hard
