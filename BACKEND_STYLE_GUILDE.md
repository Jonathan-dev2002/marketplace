# Backend Style Guide

Use this guide when creating a new Java Spring Boot backend project that should follow the same enterprise backend style and structure.

The goal is to keep project structure, naming, layering, validation, transaction handling, security, and API responses consistent across backend projects.

## Tech Stack

- Java 21
- Spring Boot 3.x
- Maven
- Spring Web MVC
- Spring Security with JWT
- Spring Validation
- Spring Data JPA
- JDBC Template for custom SQL queries
- PostgreSQL
- Lombok
- JUnit 5 / Spring Boot Test

## Project Structure

Use this package structure for a backend module:

```text
src/main/java/{basePackage}
├── common/
├── config/
├── constant/
├── controller/
├── entity/
├── exception/
├── model/
│   ├── dto/
│   │   ├── request/
│   │   └── response/
│   ├── enums/
│   └── mapper/
├── repository/
├── security/
├── service/
├── specification/
└── utils/
```

For shared code, create a common module when the project needs reusable entities, utilities, exceptions, response models, validators, cache services, XML helpers, or cross-service configuration.

Before creating new helper methods, utilities, validators, response models, exceptions, or shared services, check whether an existing implementation already exists. Reuse existing code when it fits the requirement.

## Request Flow

Use this flow for API requests:

```text
HTTP Request
-> Controller
-> Service Interface
-> Service Implementation
-> Repository
-> Database / External Service
-> Response Wrapper
```

Controllers should stay thin. Business logic belongs in services.

## Layering Rules

### Controller

Controllers should:

- Define REST endpoint mappings.
- Validate incoming request bodies with `@Valid`.
- Extract headers, query params, path variables, request body, multipart files, and authentication principal.
- Call the service layer.
- Return a standard response wrapper.
- Use `@PreAuthorize` for endpoint-level permission checks when needed.

Controllers should not:

- Contain business rules.
- Query repositories directly.
- Return JPA entities directly.
- Inject service implementation classes when an interface exists.

Prefer this:

```java
private final CountryService countryService;
```

Avoid this:

```java
private final CountryServiceImpl countryService;
```

### Service

Services should:

- Own business rules.
- Validate required business conditions.
- Check duplicates.
- Manage status transitions.
- Convert entities or DAOs into response DTOs.
- Control transactions with `@Transactional`.
- Throw `BusinessException` for expected business failures.

Use service interfaces for feature services:

```text
service/interfaces/CountryService.java
service/CountryServiceImpl.java
```

### Repository

Repositories should:

- Use Spring Data JPA for normal CRUD.
- Use `JpaSpecificationExecutor` for dynamic search filters.
- Use JDBC Template for custom SQL, complex joins, reports, or manual result mapping.
- Keep SQL/data access logic out of services when it grows.
- Select only required columns instead of loading unnecessary fields.

Repositories should not contain business rules.

### Entity

Entities should:

- Map database tables.
- Use `@Entity`, `@Table`, `@Id`, `@GeneratedValue`, and `@Column`.
- Extend a shared base entity when the project has common audit fields.

Entities should not be used as API request or response objects.

Always keep validation rules, entity column length, and database schema aligned. For example, if a code must be 3 characters, then the entity and database column should also allow 3 characters.

### DTO / DAO

Use separate model classes for different purposes:

- `Request`: incoming API payload.
- `Response`: outgoing API payload.
- `Dto`: internal transfer model or nested response item.
- `Dao`: custom query result from JDBC Template or repository projection.

## Naming Conventions

Use these suffixes consistently:

- `XxxController`
- `XxxService`
- `XxxServiceImpl`
- `XxxRepository`
- `XxxJpaRepository`
- `XxxRepositoryImpl`
- `XxxEntity`
- `XxxRequest`
- `XxxResponse`
- `XxxDto`
- `XxxDao`
- `XxxSpecification`
- `XxxMapper`

Example feature structure:

```text
controller/CountryController.java
service/interfaces/CountryService.java
service/CountryServiceImpl.java
repository/interfaces/CountryRepository.java
repository/CountryRepositoryImpl.java
entity/CountryEntity.java
model/rest/master/request/CountryRequest.java
model/rest/master/response/SearchCountryResponse.java
specification/CountrySpecification.java
```

## API Response Pattern

All APIs should return a standard response wrapper.

Example:

```java
return ResponseUtil.success(StatusCode.SUCCESS_CREATED.getCode(), StatusCode.SUCCESS_CREATED.getMessage());
```

For successful responses with data:

```java
return ResponseUtil.success(StatusCode.SUCCESS_SEARCH.getCode(), StatusCode.SUCCESS_SEARCH.getMessage(), result);
```

For business errors, throw `BusinessException` and let the global exception handler convert it into the standard error response.

## Exception Handling

Create a centralized exception handler with `@RestControllerAdvice`.

Handle at least:

- `BusinessException`
- `AuthenticationException`
- `AccessDeniedException`
- `MethodArgumentNotValidException`
- `HttpMessageNotReadableException`
- `MethodArgumentTypeMismatchException`
- generic `Exception`

Use expected business exceptions for cases such as:

- required field missing
- invalid format
- duplicate code/name
- data not found
- unauthorized business operation
- invalid status transition

Do not expose raw exception details to API clients.

## Validation Rules

Use Jakarta Validation for simple request-level validation:

- `@Valid`
- `@Validated`
- `@NotBlank`
- `@NotNull`
- `@NotEmpty`
- `@Size`
- `@Pattern`
- custom validation annotations when needed

Use service-layer validation for business rules:

- duplicate code
- duplicate name
- cross-table validation
- status validation
- current user permission logic
- file content validation

Keep field naming consistent between upload headers, DTO fields, validation messages, and API contracts. For example, if the uploaded CSV header is `currency_code`, avoid returning an error message that says only `currencyCode` unless the frontend expects that format.

## Transaction Rules

Use `@Transactional` on service methods that modify data.

Example:

```java
@Transactional(rollbackFor = Exception.class)
public void addFeature(FeatureRequest request) {
    ...
}
```

Use `readOnly = true` for read-only methods when appropriate:

```java
@Transactional(readOnly = true)
public PaginationResponse<SearchCountryResponse> searchCountry(...) {
    ...
}
```

Use special rollback rules only when there is a clear reason:

```java
@Transactional(noRollbackFor = BusinessException.class)
```

## Security Rules

Use stateless JWT authentication.

Security configuration should:

- Define a `SecurityFilterChain`.
- Disable CSRF for stateless APIs.
- Use `SessionCreationPolicy.STATELESS`.
- Permit public endpoints explicitly.
- Authenticate all other endpoints.
- Add the JWT filter before `UsernamePasswordAuthenticationFilter`.
- Use a custom authentication entry point for unauthorized responses.

Example permission check:

```java
@PreAuthorize("@authorizationService.hasAuthorities('FEATURE_CODE','PERMISSION_CODE')")
```

JWT filter should:

- Read the `Authorization` header.
- Validate `Bearer` token format.
- Skip configured public paths.
- Load user principal from token/session.
- Set `SecurityContextHolder`.
- Continue the filter chain.

## Master Data Feature Template

For each master data feature, create these APIs when applicable:

- `POST /{feature}/search`
- `POST /{feature}/add`
- `PATCH /{feature}/update`
- `DELETE /{feature}/delete`
- `POST /{feature}/upload`
- `POST /{feature}/upload/submit`

Search should support:

- pagination
- sorting
- filtering
- active status filtering when needed

Add should:

- validate required fields
- validate format
- validate duplicate code/name
- set audit fields
- set `isActive`
- save through repository

Delete should:

- validate identifier
- check existing data
- decide whether to hard delete or soft delete based on the feature requirement
- throw `BusinessException` when data is not found

Upload should:

- validate file size
- validate file type
- support CSV and Excel only when required
- validate headers
- parse rows
- skip empty rows
- validate duplicate rows inside the uploaded file
- return row-level status and remark

Upload submit should:

- validate the submitted list again
- check duplicates inside the request list
- fetch existing rows in bulk
- update existing rows
- insert new rows
- save in bulk with `saveAll`

## Pagination And Sorting

Use Spring Data pagination:

```java
int pageIndex = request.getPage() - 1;
Pageable pageable = PageRequest.of(pageIndex, request.getItemPerPage(), Sort.by(direction, sortField));
Page<Entity> pageResult = repository.findAll(specification, pageable);
```

Validate or whitelist sort fields. Do not blindly pass arbitrary client input into entity field names.

Use a response wrapper such as:

```java
PaginationResponse.of(items, totalItems, page, itemPerPage);
```

Be consistent about whether `page` in the response is zero-based or one-based.

## Specification Pattern

Use `Specification` for dynamic JPA filters.

Example:

```java
Specification<CountryEntity> spec = CountrySpecification.filterIsActive(true);

if (request.getCountryCode() != null) {
    spec = spec.and(CountrySpecification.filterCountryCode(request.getCountryCode()));
}
```

Keep specification methods small and composable.

## JDBC Template Pattern

Use JDBC Template for custom SQL queries.

Recommended pattern:

```java
StringBuilder sql = new StringBuilder();
List<Object> params = new ArrayList<>();

sql.append("""
    SELECT country_code, country_name
    FROM %s
    WHERE is_active = true
    """);

if (countryCode != null) {
    sql.append(" AND country_code = ? ");
    params.add(countryCode);
}

return jdbcTemplate.query(sql.toString().formatted(MASTER_TABLE), mapper, params.toArray());
```

Rules:

- Do not use `SELECT *`.
- Select only the columns needed by the response, DAO, mapper, or business logic.
- Use query parameters instead of string concatenating user input.
- Keep table names in constants if the project uses table constants.
- Use row mappers for manual mapping.
- Do not place business validation in SQL mapper classes.

## JPA Query Rules

Avoid loading unnecessary columns or entities when only some fields are needed.

Do not use `SELECT *` in native queries.

Prefer explicit column selection for native queries:

```java
@Query(value = """
    SELECT country_code, country_name
    FROM master_country
    WHERE is_active = true
    """, nativeQuery = true)
List<CountryDao> findActiveCountries();
```

For Spring Data JPA, prefer DTO projection, interface projection, or focused query methods when the API does not need the full entity.

Prefer this:

```java
List<CountryOptionProjection> findByIsActiveTrue();
```

Avoid fetching a full entity list just to map one or two fields:

```java
List<CountryEntity> countries = countryRepository.findAll();
```

It is acceptable to load full entities when the use case needs the full aggregate, needs entity updates, or relies on JPA dirty checking.

## File Upload Rules

Use `try-with-resources` for files and streams.

Prefer this:

```java
try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
    ...
}
```

Avoid this:

```java
BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
```

For Excel:

- Use Apache POI.
- Use `DataFormatter` for reading cell values.
- Validate header row before parsing data rows.
- Skip empty rows.
- Close `Workbook` and `InputStream` with `try-with-resources`.

## Code Quality Rules

Prefer:

- constructor injection
- service interface injection in controllers
- small private validation helpers
- `try-with-resources` for streams
- clear DTO/entity separation
- consistent field naming
- constants for repeated strings
- constants for hardcoded business values
- whitelisted sort fields
- bulk fetch before bulk save
- centralized exception handling
- centralized response utilities
- reusable common utilities and shared services

Avoid:

- injecting `ServiceImpl` directly into controllers
- unused imports
- unused dependencies
- hardcoded magic strings scattered across files
- hardcoded business values inside controller, service, repository, or mapper logic
- business logic in controllers
- returning entities directly
- mismatched validation and entity column length
- opening streams without closing them
- duplicated validation logic without helper methods
- using repository calls repeatedly inside large loops when a bulk query is possible
- creating duplicate helpers when an existing common method already solves the same problem

## Reuse And Common Module Rules

Before writing new code, check for existing methods, utilities, validators, constants, base models, response wrappers, exception classes, repository helpers, and shared services.

Reuse existing code when:

- the behavior already matches the requirement
- only naming differs but the logic is the same
- the method is already used by other features
- the code is part of a shared/common module
- the project already has an established helper or utility for the same task

Create or move code into a common/shared module when:

- the same logic is needed by more than one feature
- the same validation appears in multiple services
- the same response or error model is reused across APIs
- the same file parsing helper is used by multiple upload flows
- the same date, string, XML, JSON, CSV, Excel, token, security, cache, or formatting logic is repeated
- the same base entity or audit fields are needed across modules

Common/shared code may include:

- constants
- utility classes
- base entities
- base request/response models
- exception classes
- validators
- annotations
- response helpers
- security helpers
- file parsing helpers
- cache helpers
- configuration properties
- reusable services

Do not move feature-specific business rules into common code. Common modules should contain reusable infrastructure, shared models, and generic helper behavior, not business logic that belongs to one feature only.

Prefer this:

```text
common/
  constant/
  exception/
  model/
  util/
  validator/
  configuration/
```

Avoid this:

```text
feature-a/service/DateFormatHelper.java
feature-b/service/DateFormatHelper.java
feature-c/service/DateFormatHelper.java
```

## Collaboration And Scope Rules

When working in a shared repository, keep changes strictly scoped to the assigned feature.

Rules:

- Modify only files related to the requested feature.
- Do not refactor unrelated code.
- Do not rename shared classes, methods, packages, endpoints, or constants unless explicitly requested.
- Do not modify another feature's controller, service, repository, DTO, entity, mapper, specification, or tests unless explicitly approved.
- Before changing common/shared code, explain why the change is necessary and ask for approval.
- Prefer feature-local code over changing common code when the reuse benefit is unclear.
- If existing shared code has a bug but affects multiple features, report it first instead of silently changing it.
- Before editing, list the files that will be modified.
- After editing, summarize exactly which files changed and why.
- If unrelated files are already modified in the working tree, do not revert or overwrite them.
- If a required change may affect other features, stop and ask for approval before editing.

## Constant Rules

Move hardcoded values into constants when they are reused, business-related, or meaningful outside a single local expression.

Use constants for:

- status values
- role codes
- permission codes
- menu codes
- header names
- query parameter names
- path fragments
- file extensions
- CSV or Excel header names
- validation length limits
- validation patterns
- default sort fields
- fixed error messages
- table names
- cache names or keys
- configuration keys
- repeated SQL fragments when appropriate

Keep one-time local values inline only when extracting them would reduce readability.

Prefer this:

```java
private static final int COUNTRY_CODE_LENGTH = 2;
private static final String COUNTRY_CODE = "country_code";
private static final String DEFAULT_SORT_FIELD = "countryName";
```

Avoid this:

```java
if (countryCode.length() != 2) {
    ...
}

headerMap.get("country_code");

Sort.by("countryName");
```

## Code Comment Rules

When generating or modifying code, comments must follow these rules:

- Write code comments in English only.
- Do not use emojis in code comments.
- Add comments only for important or non-obvious logic.
- Do not comment every line.
- Do not add comments that simply repeat what the code already says.
- Keep comments short and direct.
- Prefer concise phrases over full sentences.
- Prefer clear method and variable names over unnecessary comments.

Good comment example:

```java
// Preserve OTP request state for retry flow
```

Bad comment examples:

```java
// Set user id
user.setUserId(userId);

// Validate data ✅
validateRequest(request);
```

## Testing Guidelines

Add tests based on risk.

For simple features, test:

- service validation
- duplicate checks
- search mapping
- upload parser validation

For security features, test:

- JWT parsing
- public endpoint bypass
- unauthorized request handling
- permission failure

For repository logic, test:

- custom SQL mapper
- specification filters
- pagination and sorting behavior

Use `@SpringBootTest` only when the test needs Spring context. Prefer smaller unit tests when possible.

## Recommended Build Order For New Features

1. Create request and response DTOs.
2. Create entity.
3. Create JPA repository.
4. Create JDBC repository or mapper only if needed.
5. Create specification for dynamic search.
6. Create service interface.
7. Create service implementation.
8. Create controller.
9. Add exception/status code handling if needed.
10. Add focused tests.

## Minimal Feature Checklist

Before considering a feature complete, check:

- Controller uses service interface.
- Request DTO has validation annotations where appropriate.
- Service validates business rules.
- Service method has transaction annotation when modifying data.
- Entity mapping matches validation and database schema.
- Repository queries do not concatenate user input.
- Search supports expected pagination and sorting.
- API returns standard response wrapper.
- Business errors use `BusinessException`.
- File streams are closed correctly.
- Tests cover important validation or business rules.
