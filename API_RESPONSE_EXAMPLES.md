# API Response Examples

## 1. JSON Parsing Error (Khi gửi giá trị enum sai)

**Request:**

```json
PUT /api/v1/user/admin-update/1
{
  "status": "ACTIVE",
  "role": "ADMI"
}
```

**Response:**

```json
{
  "success": false,
  "message": "Invalid role value 'ADMI'. Valid values are: ADMIN, CHA_XU, GIAO_DAN, TRUONG_HOI",
  "data": null,
  "timestamp": "2024-01-15T10:30:00",
  "status": 400
}
```

## 2. Validation Error Response (Khi status không hợp lệ)

**Request:**

```json
PUT /api/v1/user/admin-update/1
{
  "status": "INVALID_STATUS"
}
```

**Response:**

```json
{
  "success": false,
  "message": "Validation failed",
  "data": {
    "status": "Status must be one of: [ACTIVE, INACTIVE, BLOCKED, PENDING]"
  },
  "timestamp": "2024-01-15T10:30:00",
  "status": 400
}
```

## 3. Success Response

**Request:**

```json
PUT /api/v1/user/admin-update/1
{
  "status": "ACTIVE",
  "role": "ADMIN"
}
```

**Response:**

```json
{
  "success": true,
  "message": "User update successfully!",
  "data": {
    "id": 1,
    "username": "admin",
    "email": "admin@example.com",
    "status": "ACTIVE",
    "role": "ADMIN"
  },
  "timestamp": "2024-01-15T10:30:00",
  "status": 200
}
```

## 4. User Not Found Error

**Response:**

```json
{
  "success": false,
  "message": "User not found: User with id 999 not found",
  "data": null,
  "timestamp": "2024-01-15T10:30:00",
  "status": 404
}
```

## 5. User Already Exists Error

**Response:**

```json
{
  "success": false,
  "message": "User already exists: User with email admin@example.com already exists",
  "data": null,
  "timestamp": "2024-01-15T10:30:00",
  "status": 409
}
```

## Các giá trị hợp lệ:

### UserRole:

- `ADMIN` - Quản trị viên
- `CHA_XU` - Cha xứ
- `GIAO_DAN` - Giáo dân
- `TRUONG_HOI` - Trưởng hội

### UserStatus:

- `ACTIVE` - Người dùng đang hoạt động
- `INACTIVE` - Người dùng không hoạt động
- `BLOCKED` - Người dùng bị chặn
- `PENDING` - Người dùng đang chờ xác nhận
