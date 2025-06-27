# Hướng dẫn Authentication với JWT Access Token và Refresh Token

## Tổng quan

Hệ thống sử dụng **JWT (JSON Web Token)** với 2 loại token:

- **Access Token**: Token ngắn hạn (15 phút) để truy cập API
- **Refresh Token**: Token dài hạn (7 ngày) để làm mới Access Token

## Kiến trúc tổng thể

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Frontend      │    │   Backend       │    │   Database      │
│   (React)       │    │   (Spring Boot) │    │   (PostgreSQL)  │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         │ 1. Login Request      │                       │
         │──────────────────────▶│                       │
         │                       │ 2. Validate User      │
         │                       │──────────────────────▶│
         │                       │◀──────────────────────│
         │                       │ 3. Generate Tokens    │
         │                       │                       │
         │ 4. Access Token       │                       │
         │◀──────────────────────│                       │
         │ 5. Refresh Token      │                       │
         │ (HttpOnly Cookie)     │                       │
         │◀──────────────────────│                       │
```

## Chi tiết từng file

### 1. Backend - Entity Layer

#### `RefreshToken.java`

```java
@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {
    @Column(unique = true, nullable = false, length = 500)
    private String token;  // JWT refresh token

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;     // User sở hữu token

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;  // Thời gian hết hạn

    @Column(name = "revoked")
    private Boolean revoked = false;  // Token có bị vô hiệu hóa không

    @Column(name = "device_info")
    private String deviceInfo;  // Thông tin thiết bị

    @Column(name = "ip_address")
    private String ipAddress;   // IP address
}
```

**Chức năng:**

- Lưu trữ refresh token trong database
- Theo dõi token nào thuộc user nào
- Kiểm soát thời gian hết hạn và trạng thái revoked
- Lưu thông tin thiết bị và IP để bảo mật

### 2. Backend - Repository Layer

#### `RefreshTokenRepository.java`

```java
@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    List<RefreshToken> findByUserAndRevokedFalse(User user);

    @Modifying
    @Query("UPDATE RefreshToken rt SET rt.revoked = true WHERE rt.user = :user")
    void revokeAllUserTokens(@Param("user") User user);

    @Modifying
    @Query("UPDATE RefreshToken rt SET rt.revoked = true WHERE rt.token = :token")
    void revokeToken(@Param("token") String token);
}
```

**Chức năng:**

- Tìm refresh token theo token string
- Tìm tất cả token chưa revoked của user
- Revoke (vô hiệu hóa) token cụ thể hoặc tất cả token của user

### 3. Backend - Service Layer

#### `JwtService.java`

```java
@Service
public class JwtService {
    public String generateAccessToken(User user) {
        // Tạo JWT với thời gian sống ngắn (15 phút)
        // Chứa thông tin: userId, email, role, exp, iat
    }

    public String generateRefreshToken(User user) {
        // Tạo JWT với thời gian sống dài (7 ngày)
        // Chứa thông tin: userId, tokenType="refresh", exp, iat
    }

    public boolean validateToken(String token) {
        // Kiểm tra tính hợp lệ của JWT
    }
}
```

**Chức năng:**

- Tạo và validate JWT tokens
- Access token: ngắn hạn, chứa thông tin user
- Refresh token: dài hạn, chỉ dùng để refresh

#### `AuthServiceImpl.java`

```java
@Service
public class AuthServiceImpl implements AuthService {

    // 1. Đăng nhập
    public AuthResponse login(LoginRequest request, HttpServletResponse response) {
        // Xác thực user
        // Tạo access token và refresh token
        // Lưu refresh token vào DB
        // Set refresh token vào HttpOnly cookie
        // Trả về access token
    }

    // 2. Refresh token
    public AuthResponse refreshToken(String oldRefreshToken, HttpServletResponse response) {
        // Kiểm tra refresh token có hợp lệ không
        // Kiểm tra user có active không
        // Tạo access token và refresh token mới
        // Revoke token cũ, lưu token mới
        // Set cookie mới
    }

    // 3. Lưu refresh token
    private void saveRefreshToken(User user, String token) {
        // Revoke tất cả token cũ của user
        // Lưu token mới vào DB
    }
}
```

**Chức năng:**

- Xử lý logic đăng nhập, đăng ký, refresh token
- Quản lý vòng đời của tokens
- Đảm bảo mỗi user chỉ có 1 refresh token active

### 4. Backend - Controller Layer

#### `AuthController.java`

```java
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        // Gọi service login
        // Trả về access token
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(@CookieValue("refreshToken") String refreshToken, HttpServletResponse response) {
        // Gọi service refresh token
        // Trả về access token mới
    }
}
```

**Chức năng:**

- Expose REST API endpoints
- Nhận request từ frontend
- Gọi service layer xử lý logic
- Trả về response cho frontend

### 5. Backend - Security Layer

#### `JwtAuthenticationFilter.java`

```java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        // Lấy access token từ Authorization header
        // Validate token
        // Set authentication vào SecurityContext
    }
}
```

**Chức năng:**

- Intercept tất cả requests
- Kiểm tra access token trong header
- Validate token và set authentication
- Cho phép/deny access vào protected endpoints

### 6. Frontend - Authentication Manager

#### `auth.js`

```javascript
class AuthManager {
  constructor() {
    this.accessToken = localStorage.getItem("accessToken") || null;
    this.tokenExpirationTime =
      localStorage.getItem("tokenExpirationTime") || null;
  }

  setAccessToken(token, expiresIn) {
    // Lưu access token và thời gian hết hạn vào localStorage
  }

  getAccessToken() {
    // Lấy access token, kiểm tra hết hạn
  }

  isTokenExpired() {
    // Kiểm tra token có hết hạn chưa
  }

  shouldRefreshToken() {
    // Kiểm tra có nên refresh token không (1 phút trước khi hết hạn)
  }

  async ensureValidToken() {
    // Nếu token sắp hết hạn, gọi API refresh
    // Cập nhật access token mới
  }
}
```

**Chức năng:**

- Quản lý access token ở frontend
- Kiểm tra thời gian hết hạn
- Tự động refresh token khi cần

### 7. Frontend - API Interceptor

#### `api.js`

```javascript
const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  withCredentials: true,
});

api.interceptors.request.use(async (config) => {
  // Trước mỗi request, đảm bảo access token còn hợp lệ
  await authManager.ensureValidToken();
  config.headers.Authorization = `Bearer ${authManager.getAccessToken()}`;
  return config;
});

api.interceptors.response.use(
  (response) => response,
  async (error) => {
    if (error.response?.status === 401) {
      // Token hết hạn, logout user
      authManager.clearTokens();
      window.location.href = "/login";
    }
    return Promise.reject(error);
  }
);
```

**Chức năng:**

- Tự động thêm access token vào header của mọi request
- Tự động refresh token khi cần
- Xử lý lỗi 401 (unauthorized)

### 8. Frontend - Token Refresh Hook

#### `useTokenRefresh.js`

```javascript
const useTokenRefresh = () => {
  useEffect(() => {
    const checkAndRefreshToken = async () => {
      if (authManager.isAuthenticated()) {
        try {
          if (authManager.isTokenExpired()) {
            logout();
          } else if (authManager.shouldRefreshToken()) {
            await authManager.ensureValidToken();
            await syncAuth();
          }
        } catch (error) {
          logout();
        }
      }
    };

    // Kiểm tra token mỗi 15 giây
    const interval = setInterval(checkAndRefreshToken, 15000);
    return () => clearInterval(interval);
  }, []);
};
```

**Chức năng:**

- Chạy background để kiểm tra token
- Tự động refresh token trước khi hết hạn
- Logout user nếu refresh thất bại

## Luồng hoạt động

### 1. Đăng nhập

```
1. User nhập email/password
2. Frontend gửi POST /api/auth/login
3. Backend validate credentials
4. Backend tạo access token (15 phút) và refresh token (7 ngày)
5. Backend lưu refresh token vào DB
6. Backend set refresh token vào HttpOnly cookie
7. Backend trả về access token
8. Frontend lưu access token vào localStorage
```

### 2. Truy cập API

```
1. Frontend gửi request với Authorization: Bearer <access_token>
2. JwtAuthenticationFilter intercept request
3. Filter validate access token
4. Nếu valid: cho phép access
5. Nếu invalid: trả về 401
```

### 3. Refresh Token

```
1. Frontend phát hiện access token sắp hết hạn
2. Frontend gửi POST /api/auth/refresh với refresh token từ cookie
3. Backend validate refresh token
4. Backend tạo access token và refresh token mới
5. Backend revoke token cũ, lưu token mới
6. Backend set cookie mới
7. Backend trả về access token mới
8. Frontend cập nhật access token
```

### 4. Logout

```
1. User click logout
2. Frontend gửi POST /api/auth/logout với refresh token
3. Backend revoke refresh token
4. Frontend xóa access token khỏi localStorage
5. Frontend redirect về login page
```

## Bảo mật

### 1. Access Token

- **Thời gian sống ngắn** (15 phút)
- **Lưu trong localStorage** (có thể bị XSS attack)
- **Chứa thông tin user** (userId, email, role)

### 2. Refresh Token

- **Thời gian sống dài** (7 ngày)
- **Lưu trong HttpOnly cookie** (không thể truy cập bằng JavaScript)
- **Lưu trong database** với thông tin device/IP
- **Có thể revoke** khi cần

### 3. Các biện pháp bảo mật khác

- **HTTPS** để bảo vệ transmission
- **SameSite cookie** để chống CSRF
- **IP tracking** để phát hiện suspicious activity
- **Token rotation** (refresh token mới mỗi lần refresh)

## Troubleshooting

### Lỗi thường gặp

1. **Duplicate key constraint violation**

   - Nguyên nhân: Refresh token bị trùng
   - Giải pháp: Revoke token cũ trước khi lưu token mới

2. **Token expired**

   - Nguyên nhân: Access token hết hạn
   - Giải pháp: Tự động refresh hoặc redirect login

3. **Invalid refresh token**

   - Nguyên nhân: Refresh token bị revoke hoặc hết hạn
   - Giải pháp: Yêu cầu user đăng nhập lại

4. **CORS issues**
   - Nguyên nhân: Cookie không được gửi cross-origin
   - Giải pháp: Cấu hình CORS với credentials: true
