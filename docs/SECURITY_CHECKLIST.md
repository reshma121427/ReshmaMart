# reshmamart — Security Compliance Checklist (Section 9 Audit)

This document tracks the systematic verification of every security control required by Section 9 of the Capstone specification for the **September 21 Full Build + Deploy Checkpoint**.

---

## Security Audit Matrix

| Requirement | Description | Implementation Details | Status |
|---|---|---|---|
| **SQL Injection Prevention** | Every query parameterized; never string-concatenated SQL queries | 100% of queries in `UserDAOImpl`, `ProductDAOImpl`, `CartDAOImpl`, `OrderDAOImpl`, and `ReviewDAOImpl` use `java.sql.PreparedStatement` with parameter indices (`?`). Verified via `grep -rn "Statement)" src/`. | ✅ PASS |
| **Password Security** | Passwords bcrypt-hashed with salt; never stored in plaintext; never logged | Handled by `PasswordUtil.java` using `jBCrypt` (`BCrypt.hashpw(password, BCrypt.gensalt(12))`). Passwords are never logged in any logger or console output. | ✅ PASS |
| **Session Protection** | Session fixation defense & timeout | `AuthServlet` calls `oldSession.invalidate()` followed by `req.getSession(true)` on every successful login. Session timeout explicitly configured to 30 minutes in `web.xml`. | ✅ PASS |
| **Access Control (RBAC)** | Protected routes enforced via `AuthFilter` | `AuthFilter.java` intercepts all requests. Routes starting with `/seller/*` require role `SELLER` or `ADMIN`. Routes starting with `/admin/*` strictly require `ADMIN`. Buyer routes (`/cart`, `/checkout`, `/orders`) require authenticated session. Unauthenticated requests are redirected with URL encoding. | ✅ PASS |
| **XSS Prevention** | User input escaped before HTML rendering | All JSP views use JSTL `<c:out value="..."/>` or `${fn:escapeXml(...)}` for all dynamic data (names, descriptions, addresses, comments, categories). Script injection tags like `<script>` are rendered harmlessly as escaped entities. | ✅ PASS |
| **Safe Error Handling** | Zero stack trace exposure | Custom error pages defined in `web.xml` for HTTP `400`, `403`, `404`, and `500`/`Throwable`. All error JSPs display user-friendly notices without leaking internal class names or stack traces. | ✅ PASS |
| **Credential Protection** | Sensitive configuration excluded from Git | `.gitignore` explicitly excludes `.env`, `data/`, `*.mv.db`, and local configurations. `.env.example` provided for safe environment variable overrides. | ✅ PASS |
| **Transactional Integrity** | Atomic multi-step operations with rollback | Order placement in `OrderDAOImpl` executes with `conn.setAutoCommit(false)`, locking/decrementing stock, inserting order and items, clearing cart, and committing or rolling back on failure. | ✅ PASS |
