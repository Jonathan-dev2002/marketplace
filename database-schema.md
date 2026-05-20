# 🗄️ E-Commerce Marketplace: Database Schema

เอกสารนี้รวบรวมโครงสร้างฐานข้อมูล (DDL) สำหรับระบบ E-Commerce Multi-vendor ออกแบบมาสำหรับ **PostgreSQL** โดยใช้ `UUID` (แนะนำเป็น Version 7) เป็น Primary Key เพื่อประสิทธิภาพในการทำ Indexing และรองรับการทำ Soft Delete รวมถึง Audit Logs ในทุกตารางหลัก

---

## 📦 1. Module: Users, Auth & RBAC (ระบบผู้ใช้และสิทธิ์)

```sql
-- 1. ผู้ใช้งาน (Users)
CREATE TABLE users (
    id UUID PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    phone VARCHAR(20),
    is_verified BOOLEAN DEFAULT FALSE,
    status VARCHAR(20) DEFAULT 'ACTIVE', -- ACTIVE, DEACTIVATED, SUSPENDED, BANNED
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    created_by UUID,
    updated_by UUID,
    deleted_at TIMESTAMP
);

-- 2. ยืนยันตัวตน KYC (User Verifications)
CREATE TABLE user_verifications (
    id UUID PRIMARY KEY,
    user_id UUID REFERENCES users(id),
    id_card_number VARCHAR(255), 
    card_image_url TEXT,
    face_image_url TEXT,
    status VARCHAR(20) DEFAULT 'PENDING', -- PENDING, APPROVED, REJECTED
    admin_note TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    created_by UUID,
    updated_by UUID
);

-- 3. สมุดที่อยู่ (User Addresses)
CREATE TABLE user_addresses (
    id UUID PRIMARY KEY,
    user_id UUID REFERENCES users(id),
    label VARCHAR(100),
    recipient_name VARCHAR(150) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    address_line1 VARCHAR(255) NOT NULL,
    address_line2 VARCHAR(255),
    sub_district VARCHAR(100),
    district VARCHAR(100),
    province VARCHAR(100),
    postal_code VARCHAR(20) NOT NULL,
    country VARCHAR(100) DEFAULT 'Thailand' NOT NULL,
    is_default BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    created_by UUID,
    updated_by UUID,
    deleted_at TIMESTAMP
);

CREATE UNIQUE INDEX uq_user_addresses_default
ON user_addresses(user_id)
WHERE is_default = TRUE AND deleted_at IS NULL;

-- 4. ตำแหน่ง (Roles)
CREATE TABLE roles (
    id UUID PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    shop_id UUID, -- NULL = Role ระบบกลาง, Not NULL = Role ของร้านค้านั้นๆ
    description TEXT,
    is_system_defined BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 5. สิทธิ์ (Permissions)
CREATE TABLE permissions (
    id UUID PRIMARY KEY,
    slug VARCHAR(100) UNIQUE NOT NULL, -- เช่น product:create
    module VARCHAR(50),
    description TEXT
);

-- 6. การจับคู่ Role และ Permission
CREATE TABLE role_permissions (
    role_id UUID REFERENCES roles(id),
    permission_id UUID REFERENCES permissions(id),
    PRIMARY KEY (role_id, permission_id)
);

-- 7. การกำหนด Role ให้ User ในแต่ละร้านค้า
CREATE TABLE user_shop_roles (
    user_id UUID REFERENCES users(id),
    shop_id UUID, -- จะผูก FK กับ shops ภายหลัง
    role_id UUID REFERENCES roles(id),
    PRIMARY KEY (user_id, shop_id, role_id)
);
```
---

## 🛍️ 2. Module: Shop & Product Catalog (ร้านค้าและสินค้า)

```sql
-- 8. ร้านค้า (Shops)
CREATE TABLE shops (
    id UUID PRIMARY KEY,
    owner_id UUID REFERENCES users(id),
    name VARCHAR(100) NOT NULL,
    slug VARCHAR(150) UNIQUE NOT NULL,
    description TEXT,
    logo_url TEXT,
    is_verified BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE,
    rating_avg DECIMAL(3,2) DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP
);

-- เพิ่ม FK ให้ user_shop_roles หลังจากสร้างตาราง shops
ALTER TABLE user_shop_roles ADD CONSTRAINT fk_usr_shop FOREIGN KEY (shop_id) REFERENCES shops(id);

-- 8.1 ห้องแชทภายในร้านค้า (Shop Chat Rooms)
CREATE TABLE shop_chat_rooms (
    id UUID PRIMARY KEY,
    shop_id UUID NOT NULL REFERENCES shops(id),
    name VARCHAR(100) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    created_by UUID,
    updated_by UUID,
    deleted_at TIMESTAMP
);

-- 8.2 ข้อความแชทภายในร้านค้า (Shop Chat Messages)
CREATE TABLE shop_chat_messages (
    id UUID PRIMARY KEY,
    shop_id UUID NOT NULL REFERENCES shops(id),
    room_id UUID NOT NULL REFERENCES shop_chat_rooms(id),
    sender_id UUID NOT NULL REFERENCES users(id),
    message_type VARCHAR(20) DEFAULT 'TEXT' NOT NULL,
    content VARCHAR(2000) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    created_by UUID,
    updated_by UUID,
    deleted_at TIMESTAMP
);

-- 9. หมวดหมู่สินค้า (Categories)
CREATE TABLE categories (
    id UUID PRIMARY KEY,
    parent_id UUID REFERENCES categories(id),
    name VARCHAR(100) NOT NULL,
    slug VARCHAR(150) UNIQUE NOT NULL,
    icon_url TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP
);

-- 10. สินค้าหลัก (Products)
CREATE TABLE products (
    id UUID PRIMARY KEY,
    shop_id UUID REFERENCES shops(id),
    category_id UUID REFERENCES categories(id),
    name VARCHAR(200) NOT NULL,
    description TEXT,
    base_price DECIMAL(10,2),
    status VARCHAR(20) DEFAULT 'DRAFT', -- DRAFT, PUBLISHED
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    created_by UUID,
    updated_by UUID,
    deleted_at TIMESTAMP
);

-- 11. หัวข้อตัวเลือกสินค้า (Product Options)
CREATE TABLE product_options (
    id UUID PRIMARY KEY,
    product_id UUID REFERENCES products(id),
    name VARCHAR(50) NOT NULL, -- เช่น "Color", "Size"
    position INT DEFAULT 0
);

-- 12. ค่าของตัวเลือก (Product Option Values)
CREATE TABLE product_option_values (
    id UUID PRIMARY KEY,
    option_id UUID REFERENCES product_options(id),
    value VARCHAR(50) NOT NULL -- เช่น "Red", "XL"
);

-- 13. สินค้าจริง (Product SKUs)
CREATE TABLE product_skus (
    id UUID PRIMARY KEY,
    product_id UUID REFERENCES products(id),
    sku_code VARCHAR(50) UNIQUE,
    price DECIMAL(15,2) NOT NULL,
    stock_quantity INT DEFAULT 0,
    image_url TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    version INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP,
    CONSTRAINT chk_stock_non_negative CHECK (stock_quantity >= 0) -- 🛡️ ปราการด่านสุดท้ายกันของติดลบ
);

-- 14. ความสัมพันธ์ SKU กับ ตัวเลือก (SKU Values)
CREATE TABLE product_sku_values (
    sku_id UUID REFERENCES product_skus(id),
    option_value_id UUID REFERENCES product_option_values(id),
    PRIMARY KEY (sku_id, option_value_id)
);

-- 15. ส่วนประกอบของสินค้าจัดเซ็ต (Bundle Items)
CREATE TABLE product_bundle_items (
    id UUID PRIMARY KEY,
    bundle_sku_id UUID REFERENCES product_skus(id),
    component_sku_id UUID REFERENCES product_skus(id),
    quantity INT DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```
---

## ⚙️ 3. Module: Inventory & Costing (คลังสินค้าและบัญชีต้นทุน)

```sql
-- 16. การจัดการล็อตสินค้า (Product Lots - FIFO Costing)
CREATE TABLE product_lots (
    id UUID PRIMARY KEY,
    shop_id UUID REFERENCES shops(id),
    sku_id UUID REFERENCES product_skus(id),
    lot_code VARCHAR(50),
    cost_price DECIMAL(10,2) NOT NULL,
    initial_quantity INT NOT NULL,
    remaining_quantity INT NOT NULL,
    manufacture_date DATE,
    expiry_date DATE,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    created_by UUID,
    updated_by UUID,
    CONSTRAINT chk_remaining_non_negative CHECK (remaining_quantity >= 0) -- 🛡️ กันสต็อกล็อตติดลบ
);

-- 17. บันทึกความเคลื่อนไหวสต็อก (Inventory Logs)
CREATE TABLE inventory_logs (
    id UUID PRIMARY KEY,
    shop_id UUID REFERENCES shops(id),
    sku_id UUID REFERENCES product_skus(id),
    lot_id UUID REFERENCES product_lots(id),
    action_type VARCHAR(50) NOT NULL, -- SALE, RESTOCK, RETURN, ADJUST
    change_amount INT NOT NULL,
    balance_after INT NOT NULL,
    reference_id VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by UUID
);
```
---
## 💳 4. Module: Order, Cart & Extra (ตะกร้า การซื้อขาย และส่วนเสริม)

```sql
-- 18. ตะกร้าสินค้า (Carts)
CREATE TABLE carts (
    id UUID PRIMARY KEY,
    user_id UUID REFERENCES users(id),
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE cart_items (
    id UUID PRIMARY KEY,
    cart_id UUID REFERENCES carts(id),
    sku_id UUID REFERENCES product_skus(id),
    quantity INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 19. คำสั่งซื้อ (Orders)
CREATE TABLE orders (
    id UUID PRIMARY KEY,
    order_number VARCHAR(50) UNIQUE NOT NULL,
    user_id UUID REFERENCES users(id),
    shop_id UUID REFERENCES shops(id),
    status VARCHAR(20) DEFAULT 'PENDING', -- PENDING, PAID, SHIPPED, COMPLETED, CANCELLED
    total_amount DECIMAL(10,2) NOT NULL,
    discount_amount DECIMAL(10,2) DEFAULT 0.00,
    shipping_fee DECIMAL(10,2) DEFAULT 0.00,
    net_amount DECIMAL(10,2) NOT NULL,
    shipping_address JSONB NOT NULL,
    payment_method VARCHAR(50),
    payment_status VARCHAR(20) DEFAULT 'UNPAID',
    note TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    created_by UUID,
    updated_by UUID
);

-- 20. รายการสินค้าในคำสั่งซื้อ (Order Items)
CREATE TABLE order_items (
    id UUID PRIMARY KEY,
    order_id UUID REFERENCES orders(id),
    sku_id UUID REFERENCES product_skus(id),
    product_name_snapshot VARCHAR(200),
    sku_details_snapshot VARCHAR(200),
    price_per_unit DECIMAL(10,2) NOT NULL,
    quantity INT NOT NULL
);

-- 21. รีวิวสินค้า (Reviews)
CREATE TABLE reviews (
    id UUID PRIMARY KEY,
    user_id UUID REFERENCES users(id),
    order_id UUID REFERENCES orders(id),
    product_id UUID REFERENCES products(id),
    rating INT CHECK (rating BETWEEN 1 AND 5),
    comment TEXT,
    images JSONB,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP
);

-- 22. รายการโปรด (Favorites / Wishlist)
CREATE TABLE favorites (
    id UUID PRIMARY KEY,
    user_id UUID REFERENCES users(id),
    product_id UUID REFERENCES products(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 23. ระบบคูปองส่วนลด (Coupons)
CREATE TABLE coupons (
    id UUID PRIMARY KEY,
    shop_id UUID, -- NULL = คูปองแพลตฟอร์ม
    code VARCHAR(50) UNIQUE NOT NULL,
    discount_type VARCHAR(20), -- PERCENTAGE, FIXED_AMOUNT
    discount_value DECIMAL(10,2) NOT NULL,
    min_purchase DECIMAL(10,2) DEFAULT 0.00,
    max_discount DECIMAL(10,2),
    usage_limit INT,
    used_count INT DEFAULT 0,
    start_date TIMESTAMP,
    end_date TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP
);

-- 24. Audit Logs (บันทึกกิจกรรมระบบ)
CREATE TABLE audit_logs (
    id UUID PRIMARY KEY,
    user_id UUID,
    action VARCHAR(100) NOT NULL,
    resource_name VARCHAR(50),
    resource_id VARCHAR(100),
    old_value JSONB,
    new_value JSONB,
    ip_address VARCHAR(50),
    event_type VARCHAR(100),
    event_id UUID,
    topic VARCHAR(150),
    message_key VARCHAR(150),
    source VARCHAR(100),
    processed_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_audit_logs_user_id ON audit_logs(user_id);
CREATE INDEX idx_audit_logs_resource ON audit_logs(resource_name, resource_id);
CREATE INDEX idx_audit_logs_action_created_at ON audit_logs(action, created_at);
CREATE UNIQUE INDEX uq_audit_logs_event_id
ON audit_logs(event_id)
WHERE event_id IS NOT NULL;

-- ==========================================
-- ⚡ Performance Tuning: Foreign Key & Partial Indexes
-- ==========================================

-- 1. สร้าง Index ให้ Foreign Key ที่ใช้ JOIN หรือ Query บ่อยๆ (ป้องกัน Sequential Scan)
CREATE INDEX idx_orders_user_id ON orders(user_id);
CREATE INDEX idx_orders_shop_id ON orders(shop_id);
CREATE INDEX idx_products_shop_id ON products(shop_id);
CREATE INDEX idx_products_category_id ON products(category_id);
CREATE INDEX idx_product_skus_product_id ON product_skus(product_id);
CREATE INDEX idx_order_items_order_id ON order_items(order_id);
CREATE INDEX idx_shop_chat_rooms_shop_id ON shop_chat_rooms(shop_id);
CREATE INDEX idx_shop_chat_messages_room_id ON shop_chat_messages(room_id);

-- 2. สร้าง Partial Index สำหรับข้ามข้อมูลที่ถูก Soft Delete ไปแล้ว (ทำให้ Index เล็กและเร็ว)
CREATE INDEX idx_active_products ON products(shop_id) WHERE deleted_at IS NULL AND is_active = TRUE;
CREATE INDEX idx_active_skus ON product_skus(product_id) WHERE deleted_at IS NULL AND is_active = TRUE;
CREATE INDEX idx_active_shops ON shops(owner_id) WHERE deleted_at IS NULL AND is_active = TRUE;
CREATE INDEX idx_active_shop_chat_rooms ON shop_chat_rooms(shop_id) WHERE deleted_at IS NULL AND is_active = TRUE;
CREATE INDEX idx_active_shop_chat_messages ON shop_chat_messages(room_id, created_at) WHERE deleted_at IS NULL;
