# Resident Management API Documentation

## Base URL

```
/api/v1/residents
```

## Endpoints

### 1. Create Resident

**POST** `/api/v1/residents`

**Request Body:**

```json
{
  "fullName": "Nguyễn Văn A",
  "dateOfBirth": "1990-01-01",
  "gender": "MALE",
  "phone": "0123456789",
  "address": "123 Đường ABC, Quận 1, TP.HCM",
  "baptized": true,
  "confirmed": false,
  "married": false,
  "relationToHead": "CHA",
  "familyId": 1
}
```

**Response:**

```json
{
  "success": true,
  "message": "Resident created successfully!",
  "data": {
    "id": 1,
    "fullName": "Nguyễn Văn A",
    "dateOfBirth": "1990-01-01",
    "gender": "MALE",
    "phone": "0123456789",
    "address": "123 Đường ABC, Quận 1, TP.HCM",
    "baptized": true,
    "confirmed": false,
    "married": false,
    "relationToHead": "CHA",
    "familyId": 1,
    "familyCode": "F001",
    "createdAt": "2024-01-15T10:30:00",
    "updatedAt": "2024-01-15T10:30:00"
  },
  "timestamp": "2024-01-15T10:30:00",
  "status": 201
}
```

### 2. Get Resident by ID

**GET** `/api/v1/residents/{id}`

**Response:**

```json
{
  "success": true,
  "message": "Resident retrieved successfully!",
  "data": {
    "id": 1,
    "fullName": "Nguyễn Văn A",
    "dateOfBirth": "1990-01-01",
    "gender": "MALE",
    "phone": "0123456789",
    "address": "123 Đường ABC, Quận 1, TP.HCM",
    "baptized": true,
    "confirmed": false,
    "married": false,
    "relationToHead": "CHA",
    "familyId": 1,
    "familyCode": "F001",
    "createdAt": "2024-01-15T10:30:00",
    "updatedAt": "2024-01-15T10:30:00"
  },
  "timestamp": "2024-01-15T10:30:00",
  "status": 200
}
```

### 3. Get All Residents (Paginated)

**GET** `/api/v1/residents?page=0&size=10&sortBy=fullName&sortDir=asc`

**Query Parameters:**

- `page` (default: 0) - Page number
- `size` (default: 10) - Page size
- `sortBy` (default: id) - Sort field
- `sortDir` (default: desc) - Sort direction (asc/desc)

**Response:**

```json
{
  "success": true,
  "message": "Residents retrieved successfully!",
  "data": {
    "content": [
      {
        "id": 1,
        "fullName": "Nguyễn Văn A",
        "dateOfBirth": "1990-01-01",
        "gender": "MALE",
        "phone": "0123456789",
        "address": "123 Đường ABC, Quận 1, TP.HCM",
        "baptized": true,
        "confirmed": false,
        "married": false,
        "relationToHead": "CHA",
        "familyId": 1,
        "familyCode": "F001",
        "createdAt": "2024-01-15T10:30:00",
        "updatedAt": "2024-01-15T10:30:00"
      }
    ],
    "pageable": {
      "sort": {
        "empty": false,
        "sorted": true,
        "unsorted": false
      },
      "offset": 0,
      "pageNumber": 0,
      "pageSize": 10,
      "paged": true,
      "unpaged": false
    },
    "last": true,
    "totalElements": 1,
    "totalPages": 1,
    "size": 10,
    "number": 0,
    "sort": {
      "empty": false,
      "sorted": true,
      "unsorted": false
    },
    "first": true,
    "numberOfElements": 1,
    "empty": false
  },
  "timestamp": "2024-01-15T10:30:00",
  "status": 200
}
```

### 4. Search Residents

**GET** `/api/v1/residents/search?keyword=Nguyễn&page=0&size=10`

**Query Parameters:**

- `keyword` (required) - Search term for fullName or phone
- `page` (default: 0) - Page number
- `size` (default: 10) - Page size

### 5. Get Residents by Baptized Status

**GET** `/api/v1/residents/baptized?baptized=true&page=0&size=10`

**Query Parameters:**

- `baptized` (required) - true/false
- `page` (default: 0) - Page number
- `size` (default: 10) - Page size

### 6. Get Residents by Confirmed Status

**GET** `/api/v1/residents/confirmed?confirmed=true&page=0&size=10`

**Query Parameters:**

- `confirmed` (required) - true/false
- `page` (default: 0) - Page number
- `size` (default: 10) - Page size

### 7. Get Residents by Married Status

**GET** `/api/v1/residents/married?married=false&page=0&size=10`

**Query Parameters:**

- `married` (required) - true/false
- `page` (default: 0) - Page number
- `size` (default: 10) - Page size

### 8. Get Residents by Family ID

**GET** `/api/v1/residents/family/{familyId}`

**Response:**

```json
{
  "success": true,
  "message": "Family members retrieved successfully!",
  "data": [
    {
      "id": 1,
      "fullName": "Nguyễn Văn A",
      "dateOfBirth": "1990-01-01",
      "gender": "MALE",
      "phone": "0123456789",
      "address": "123 Đường ABC, Quận 1, TP.HCM",
      "baptized": true,
      "confirmed": false,
      "married": false,
      "relationToHead": "CHA",
      "familyId": 1,
      "familyCode": "F001",
      "createdAt": "2024-01-15T10:30:00",
      "updatedAt": "2024-01-15T10:30:00"
    }
  ],
  "timestamp": "2024-01-15T10:30:00",
  "status": 200
}
```

### 9. Update Resident

**PUT** `/api/v1/residents/{id}`

**Request Body:**

```json
{
  "fullName": "Nguyễn Văn A (Updated)",
  "dateOfBirth": "1990-01-01",
  "gender": "MALE",
  "phone": "0123456789",
  "address": "456 Đường XYZ, Quận 2, TP.HCM",
  "baptized": true,
  "confirmed": true,
  "married": true,
  "relationToHead": "CHA",
  "familyId": 1
}
```

**Response:**

```json
{
  "success": true,
  "message": "Resident updated successfully!",
  "data": {
    "id": 1,
    "fullName": "Nguyễn Văn A (Updated)",
    "dateOfBirth": "1990-01-01",
    "gender": "MALE",
    "phone": "0123456789",
    "address": "456 Đường XYZ, Quận 2, TP.HCM",
    "baptized": true,
    "confirmed": true,
    "married": true,
    "relationToHead": "CHA",
    "familyId": 1,
    "familyCode": "F001",
    "createdAt": "2024-01-15T10:30:00",
    "updatedAt": "2024-01-15T10:35:00"
  },
  "timestamp": "2024-01-15T10:35:00",
  "status": 200
}
```

### 10. Delete Resident

**DELETE** `/api/v1/residents/{id}`

**Response:**

```json
{
  "success": true,
  "message": "Resident deleted successfully!",
  "data": "Resident deleted successfully!",
  "timestamp": "2024-01-15T10:40:00",
  "status": 200
}
```

## Data Models

### Gender Enum

- `MALE` - Nam
- `FEMALE` - Nữ

### RelationToHead Enum

- `CHA` - Cha
- `ME` - Mẹ
- `CON` - Con
- `ONG` - Ông
- `BA` - Bà
- `KHAC` - Khác

## Error Responses

### Validation Error

```json
{
  "success": false,
  "message": "Validation failed",
  "data": {
    "fullName": "Full name is required",
    "phone": "Phone number must be 10-11 digits"
  },
  "timestamp": "2024-01-15T10:30:00",
  "status": 400
}
```

### Resident Not Found

```json
{
  "success": false,
  "message": "Resident not found: Resident with id 999 not found",
  "data": null,
  "timestamp": "2024-01-15T10:30:00",
  "status": 404
}
```

### Invalid Enum Value

```json
{
  "success": false,
  "message": "Invalid gender value 'UNKNOWN'. Valid values are: MALE, FEMALE",
  "data": null,
  "timestamp": "2024-01-15T10:30:00",
  "status": 400
}
```

## Validation Rules

- `fullName`: Required, not blank
- `dateOfBirth`: Must be in the past
- `gender`: Required, must be MALE or FEMALE
- `phone`: Must be 10-11 digits (optional)
- `relationToHead`: Required, must be one of: CHA, ME, CON, ONG, BA, KHAC
- `familyId`: Optional, must reference existing family
