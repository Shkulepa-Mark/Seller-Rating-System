# Project Phases Estimation

1.  **Phase 0: Project Setup (6h)**
    * Setup Maven WAR project from archetype (`maven-archetype-webapp`).
    * Manually configure `pom.xml` with all dependencies (Spring MVC, Spring Data JPA, Spring Security, PostgreSQL, HikariCP, Jackson, JWT, Liquibase, Lombok).
    * Configure `AppInitializer` (to replace `web.xml`).
    * Configure `WebConfig` (for `@EnableWebMvc`, scan `controller`).
    * Configure `AppConfig` (scan `service`, `security`; configure Redis).
    * Configure `PersistenceConfig` (scan `repository`; configure `DataSource`, JPA `EntityManagerFactory`, `TransactionManager`, `Liquibase`).
    * Configure external Tomcat server for deployment.

2.  **Phase 1: Database & Entities (6h)**
    * Create all 5 JPA `@Entity` classes (User, Comment, Game, GameObject, SellerRating).
    * Create all 5 `JpaRepository` interfaces.
    * Set up Liquibase changelog (`db.changelog-master.xml`, `001-create-initial-tables.xml`) to create all tables and relations.
    * Configure `db.properties` for PostgreSQL connection.

3.  **Phase 2: Registration & Auth (10h)**
    * Implement `UserDetailsService` (`UserDetailsServiceImpl`).
    * Implement `JwtTokenProvider` (create and validate tokens).
    * Implement `JwtTokenFilter` (extends `OncePerRequestFilter`).
    * Implement `SecurityConfig` (configure `SecurityFilterChain`, `AuthenticationManager`, `PasswordEncoder`).
    * Implement `AuthService` logic:
        * `registerSeller`: Creates `User` (`status=PENDING_EMAIL`), saves token to **Redis**, simulates email (logs code).
        * `confirmEmail`: Validates token from **Redis**, updates `User.status` to `PENDING_APPROVAL`.
        * `login`: Authenticates user, returns JWT.
    * Implement `AuthController` (REST endpoints: `/auth/register`, `/auth/login`, `/auth/confirm`).

4.  **Phase 3: Admin Functionality (8h)**
    * Implement `AdminService` (or add to `UserService`):
        * `approveSeller(Long userId)`: Sets `User.status = ACTIVE`.
        * `declineSeller(Long userId)`
    * Implement `CommentService`:
        * `getPendingComments()`
        * `approveComment(Long commentId)`
        * `declineComment(Long commentId)`
    * Implement `AdminController` (endpoints for all admin actions, secured with `@PreAuthorize("hasRole('ADMIN')")`).

5.  **Phase 4: Comment & Rating Logic (10h)**
    * Implement `RatingService` (SOLID):
        * `updateSellerRating(Long sellerId)`: Calculates `AVG(rating)` from `commentRepository.findAllBySellerIdAndStatus(..., "APPROVED")` and saves/updates `SellerRating` entity.
    * Modify `CommentService.approveComment` to **trigger** `ratingService.updateSellerRating(...)`.
    * Implement `CommentService.submitComment(Long sellerId, CommentRequest dto)`: (Creates `Comment` with `status=PENDING`).
    * Implement `CommentController` (endpoints `POST /sellers/{id}/comments`, `GET /sellers/{id}/comments`).

6.  **Phase 5: Public & Seller Endpoints (8h)**
    * Implement `SellerController`: `GET /sellers` (with filtering by game/rating), `GET /sellers/{id}`.
    * Implement `GameObjectService`: `create`, `update`, `delete` (with author check).
    * Implement `GameObjectController`: `POST`, `PUT`, `DELETE` (secured with `@PreAuthorize("hasRole('SELLER')")`).

7.  **Phase 6: Testing (10h)**
    * Write 2 Unit Tests (e.g., `RatingServiceTest`, `AuthServiceTest`) using `@ExtendWith(MockitoExtension.class)` (fast, no Spring context).
    * Write 2 Integration Tests (e.g., `AuthControllerTest`, `CommentControllerTest`) using `@WebAppConfiguration`, `@ContextConfiguration`, and `MockMvc`.