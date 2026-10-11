# reshmamart — Sprint Retrospective Log (RETRO.md)

This log records weekly sprint retrospectives across the 11-week Anna University R2025 Capstone development cycle (Jul 24 – Oct 10, 2026).

---

| Sprint / Week | Dates | What Worked | What Didn't | Action Item / Change for Next Sprint |
| :--- | :--- | :--- | :--- | :--- |
| **Kickoff** | Jul 24 – Jul 27 | Architecture package skeleton and D2 Use Case diagram drafted cleanly. | Initial port 8080 collided with local background system processes. | Switched default development port to 8085 with dynamic fallback. |
| **Week 1** | Jul 27 – Aug 2 | jBCrypt password hashing and HikariCP connection pool set up without issue. | Session timeout was initially inconsistent across restarts. | Standardized explicit session timeout configuration in `web.xml` and `config.properties`. |
| **Week 2** | Aug 3 – Aug 9 | Core shopping journey (browse → cart → checkout) functioning end-to-end. | Floats for product price caused slight decimal inaccuracies. | Refactored all currency fields to `DECIMAL(10,2)` in `schema.sql` and entity models. |
| **Week 3** | Aug 10 – Aug 16 | DTO separation (`UserResponseDTO`) prevented password hash leakage. | Inconsistent JSON envelopes between custom endpoints. | Standardized generic `{success, data, error}` `ApiResponse<T>` envelope for all `/api/` routes. |
| **Week 4** | Aug 17 – Aug 23 | Seller portal and Admin dashboard moderation workflows completed smoothly. | Seller order view missed buyer contact metadata. | Enhanced `OrderDTO` to include buyer name, email, and shipping destination. |
| **Week 5** | Aug 24 – Aug 30 | O2 order status state machine (`PENDING` → `CONFIRMED` → `SHIPPED` → `DELIVERED`) verified. | Search filter reset category dropdown selection on submit. | Added sticky category and keyword state bindings in JSP search inputs. |
| **Week 6** | Aug 31 – Sep 6 | F8 review submission with star ratings and average score aggregation complete. | Empty cart checkout attempt previously resulted in an uncaught SQL error. | Implemented guard validation throwing `ValidationException` before touching DAO. |
| **Week 7** | Sep 7 – Sep 13 | 100% PreparedStatement verification; custom error pages prevented stack trace leaks. | H2 auto-increment sequences did not advance past explicitly seeded IDs. | Added `ALTER TABLE ... RESTART WITH 100` sequence reset statements in `seed.sql`. |
| **Week 8** | Sep 14 – Sep 20 | Dockerfile multi-stage build succeeded; public live deployment verified on Render. | Render initially failed to locate `Dockerfile` before it was pushed to GitHub. | Pushed deployment blueprint to GitHub origin and established automatic CI/CD deployment. |
| **Week 9** | Sep 21 – Sep 27 | Strategy pattern `ChatProvider` with Mock and Gemini implementations created cleanly. | Outbound API call risks hanging thread if LLM provider has high latency. | Configured strict 5-second socket timeout and automatic fallback to `MockChatProvider`. |
| **Week 10** | Sep 28 – Oct 4 | Floating interactive chat widget integrated across all pages with quick suggestion chips. | Dynamic `<jsp:include>` lost JSTL taglibs on older servlet engines. | Declared standard taglib directives explicitly at the top of each individual JSP file. |
| **Week 11** | Oct 5 – Oct 10 | 30/30 unit tests passing; all three architecture diagrams (D1, D2, D3, D4) and final reports complete. | Manual demo rehearsal required quick test accounts setup. | Documented exact click paths and demo credentials in `docs/DEMO_SCRIPT.md`. |
