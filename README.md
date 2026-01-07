# 🌍 TravelMates - Backend API

TravelMates is a complete platform for managing group trips. It allows groups of friends to organize trips, manage shared expenses with automatic debt calculation, plan activities, share documents, and communicate through shared notes.

## 📋 Table of Contents

- [Features](#-features)
- [Technologies](#-technologies)
- [Technical Requirements](#-technical-requirements)
- [Database Architecture](#-database-architecture)
- [Project Setup](#-project-setup)
- [API Endpoints](#-api-endpoints)
- [Testing with Postman](#-testing-with-postman)
- [External Integrations](#-external-integrations)

---

## ✨ What You Can Do with TravelMates

### 🔐 Account and Security
I've implemented a solid authentication system with JWT. No plain text passwords (using BCrypt) and three permission levels:
- **ADMIN** - for platform management
- **ORGANIZER** - for creating and organizing trips
- **TRAVELER** - for all participants

The JWT token lasts 24 hours, long enough not to force you to re-login every 5 minutes but not so long as to be dangerous.

### 🏖️ Trip Management
Create your trip with name, dates, budget, and destination. You can also upload a nice cover photo (thanks to Cloudinary!).

Invite your friends by assigning them different roles - organizers can modify everything, participants can add expenses and activities but can't delete other people's stuff (avoid drama 😅).

### 💰 The Heart of the System: Expenses
This is the part I'm most proud of. You have two types of expenses:

**Shared Expenses**: like a group dinner. You pay €100, but there were 5 of you. The app automatically divides and knows that everyone owes you €20. You can also make custom divisions if someone had lobster while others had pizza 🦞

**Personal Expenses**: did you buy the train ticket for your friend who forgot their card? Register it as a personal expense and the system keeps track that they owe you that money.

Expenses have categories (FOOD, TRANSPORT, ACCOMMODATION, etc.) and you can also attach the receipt photo.

### 🧮 Automatic Debt Calculation
Here's the cool part: the app automatically calculates who owes what to whom, optimizing transactions to reduce them to a minimum. Instead of making 10 different payments, maybe 3 are enough.

Practical example:
- Mario owes €50 to Luca
- Luca owes €50 to Sara
- Result: Mario pays €50 directly to Sara, done.

### 📅 Activity Planning
Museum tomorrow at 10? Hotel check-in at 3 PM? Return flight on Sunday? Put everything in the shared calendar.

I created 8 activity categories (museums, restaurants, flights, trains, excursions...) and you can confirm or cancel reservations. So everyone always knows what's happening and when.

### 💸 Payment System
When it's time to settle accounts, you create a payment request. The system also sends an automatic email (via Mailgun) to remind your friend that they owe you money 😉

Available states:
- PENDING - waiting
- COMPLETED - paid, we're good!
- CANCELLED - forget it

### 📄 Shared Documents
Never again "but where did you put the booking PDF?!". Upload everything here: tickets, bookings, identity documents, insurance, maps, itineraries.

There's also a full-text search so if you only remember "it said Ryanair" you'll find it right away.

### 💬 Group Chat
Ok, technically I called it "Notes" but it's basically a chat. Write messages, pin them if they're important (like "GUYS THE FLIGHT LEAVES AT 6 AM"), and communicate without having to open WhatsApp.

---

## 🛠️ Technology Stack

I chose modern and reliable technologies:

**Backend & Framework:**
- **Java 21** - latest LTS version, modern and performant
- **Spring Boot 4.0.1** - the Java framework par excellence
- **Spring Security** - authentication and authorization management
- **JWT (jjwt)** - for stateless and scalable tokens
- **Spring Data JPA** - because writing SQL by hand is for masochists 😅
- **PostgreSQL** - robust and reliable database
- **Lombok** - less boilerplate, more useful code

**External Services:**
- **Cloudinary** - to upload and optimize images (automatic WebP conversion, fast CDN)
- **Mailgun** - to send beautiful emails without configuring SMTP

**Tools & Testing:**
- **Maven** - build and dependency management
- **Postman** - I prepared 7 complete collections to test everything
- **Dotenv** - environment variable management (never commit passwords!)

---

## 📊 Database Structure

I designed a database with **11 tables** (the minimum requirement was 8, but I got carried away 😄):

### Main Tables

```
users                    → Who registers on the platform
trips                    → Created trips
trip_members             → Who participates in which trip (many-to-many)
expenses                 → Base table for expenses (used with inheritance)
  ├── shared_expenses    → Expenses shared among multiple people
  └── personal_expenses  → One-to-one expenses
expense_splits           → How a shared expense is divided
activities               → Planned activities
documents                → Uploaded documents
settlements              → Payments between members
trip_notes               → Group messages/notes
```

### Interesting Patterns

**Inheritance Strategy**: I used `JOINED` for expenses. `Expense` is the abstract base class, then `SharedExpense` and `PersonalExpense` extend by adding their specific fields. This way I have reusable code but also flexibility.

**Enums Everywhere**: To avoid dirty data I use enums for everything (roles, categories, states). The database is happy and so am I.

### Main Relationships

A **User** can:
- Be a member of N trips (through TripMember)
- Create expenses, activities, documents, notes
- Receive and send payments

A **Trip** has:
- Members with different roles
- Expenses, activities, documents, notes, settlements

All `@ManyToOne` relationships with lazy loading to avoid loading the entire database every time.

---

## 🚀 How to Start the Project

### What You Need

- **Java 21** or higher
- **PostgreSQL 15+** (or the version you prefer, but I tested with this one)
- **Maven** (you probably already have it if you use Java)
- A **Cloudinary** account (it's free for personal use: https://cloudinary.com)
- A **Mailgun** account (this also has a free tier: https://mailgun.com)

### Step-by-Step Setup

**1. Database**

Create the database on PostgreSQL:
```sql
CREATE DATABASE travelmates_db;
```

**2. Environment Variables**

Copy the `.env.example` file to `.env` and fill it with your data:

```bash
# Database - use your PostgreSQL credentials
DB_URL=jdbc:postgresql://localhost:5432/travelmates_db
DB_USERNAME=postgres
DB_PASSWORD=your_password_here

# JWT - generate a long random string for security
JWT_SECRET=a_very_long_and_secure_string_at_least_256_bits

# Cloudinary - get these from your Cloudinary dashboard
CLOUDINARY_CLOUD_NAME=your_cloud_name
CLOUDINARY_API_KEY=your_api_key
CLOUDINARY_API_SECRET=your_api_secret

# Mailgun - find everything in the Mailgun dashboard
MAILGUN_API_KEY=your_mailgun_key
MAILGUN_DOMAIN=your_domain.mailgun.org
MAILGUN_FROM_EMAIL=noreply@yourdomain.com
MAILGUN_FROM_NAME=TravelMates
```

**Pro tip**: For JWT_SECRET you can generate a secure random string with:
```bash
openssl rand -base64 32
```

**3. Install and Start**

```bash
# Install dependencies
mvn clean install

# Start the application
mvn spring-boot:run
```

If everything goes well, you should see in the logs something like:
```
Started BackendTravelmatesApplication in X seconds
```

The server will be available at **http://localhost:8080** 🎉

### 4. Quick First Test

Open Postman and try to register:

```http
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
  "email": "test@example.com",
  "password": "password123",
  "firstName": "John",
  "lastName": "Doe"
}
```

If you receive a 201 Created with user data, you're ready! 🚀

---

## 📡 API Endpoints in Detail

I created a complete RESTful API. Here's a quick summary:

### 🔐 Authentication `/api/auth`
- `POST /register` - Create a new account
- `POST /login` - Get your JWT token

### 👤 Users `/api/users`
- `GET /me` - Your profile
- `PUT /me` - Update your data
- `GET /{id}` - See someone's public profile
- `GET /` - List all (ADMIN only)

### 🏖️ Trips `/api/trips`
- `POST /` - Create a trip
- `GET /` - Your trips
- `GET /{id}` - Trip details
- `PUT /{id}` - Modify (ORGANIZER only)
- `DELETE /{id}` - Delete (ORGANIZER only)
- `POST /{id}/members/invite` - Invite friends
- `DELETE /{id}/members/{memberId}` - Remove someone (ORGANIZER)

### 💰 Expenses `/api/trips/{tripId}/expenses`
- `POST /shared` - Add shared expense
- `POST /personal` - Add personal expense
- `GET /` - All trip expenses
- `GET /{id}` - Expense details
- `PUT /{id}` - Modify (creator only)
- `DELETE /{id}` - Delete (creator only)
- `GET /balance` - Who owes what to whom (magic! ✨)

### 📅 Activities `/api/trips/{tripId}/activities`
- `POST /` - Create activity (ORGANIZER)
- `GET /` - List activities
- `GET /{id}` - Details
- `PUT /{id}` - Modify (ORGANIZER)
- `DELETE /{id}` - Delete (ORGANIZER)
- `PUT /{id}/confirm` - Confirm booking
- `PUT /{id}/cancel` - Cancel booking

### 📄 Documents `/api/trips/{tripId}/documents`
- `POST /` - Upload a document
- `GET /` - List documents
- `GET /{id}` - Details
- `DELETE /{id}` - Delete (uploader or ORGANIZER)
- `GET /search?query=ticket` - Search in filenames

### 💸 Settlements `/api/trips/{tripId}/settlements`
- `POST /` - "Hey, pay me!"
- `GET /` - List payments
- `PUT /{id}/complete` - "Received, thanks!"
- `DELETE /{id}` - Cancel (ORGANIZER)
- `GET /balance` - Optimized debt calculation

### 💬 Notes `/api/trips/{tripId}/notes`
- `POST /` - Write a message
- `GET /` - All messages (with pagination)
- `GET /{id}` - Single message
- `PUT /{id}` - Modify (yours only or if ORGANIZER)
- `DELETE /{id}` - Delete (yours only or if ORGANIZER)

### 🖼️ Images `/api/images`
- `POST /upload/profile` - Profile photo
- `POST /upload/trip-cover` - Trip cover
- `POST /upload/receipt` - Expense receipt
- `POST /upload/document` - Generic document
- `DELETE /{publicId}` - Delete image
- `GET /optimized/{publicId}` - Optimized URL (WebP, automatic resize)

**Note**: All APIs except register and login require the header:
```
Authorization: Bearer {your_jwt_token}
```

---

## 🧪 Testing with Postman

I prepared 7 complete Postman collections in the `postman/` folder. They're ready to use and include examples for each endpoint.

### Quick Setup

**1. Import the Environment**

File: `postman/environments/TravelMates_Local.postman_environment.json`

In Postman:
- Click on the gear icon ⚙️ in the top right
- Import → select the file
- Select "TravelMates Local" from the dropdown

**2. Import the Collections**

Drag all files from the `postman/collections/` folder into Postman.

You'll have:
1. **Auth** - Registration and login
2. **Trip** - Trip and member management
3. **Expenses** - Shared and personal expenses
4. **Activities** - Activity planning
5. **Documents** - Upload and document management
6. **Settlements** - Requests and payments
7. **Notes** - Group chat

**3. Recommended Test Workflow**

1. Register 2-3 users (Auth collection)
2. Login with the first user
3. Create a trip (Trip collection)
4. Invite other users to the trip
5. Add shared expenses (Expenses collection)
6. Check the balance - you'll see who owes what to whom!
7. Create a settlement and complete the payment
8. Plan some activities
9. Upload documents
10. Write messages in the chat

The collections have automatic scripts that save IDs in environment variables, so tests chain together nicely.

---

## 🔌 External Integrations

### Cloudinary - Image Management

I integrated Cloudinary because:
- Uploads images in Base64 (convenient for APIs)
- Automatically optimizes (WebP, compression, resize)
- Global CDN = speed
- I don't have to manage storage on my server

**What you can upload:**
- User profile photo (automatic circular crop)
- Trip cover (16:9 ratio)
- Expense receipts (OCR ready if you want to expand)
- Various documents (tickets, passports, etc.)

**How it works:**

Send the image in Base64:
```json
{
  "imageBase64": "data:image/jpeg;base64,/9j/4AAQSkZJRg..."
}
```

Get back:
```json
{
  "imageUrl": "https://res.cloudinary.com/...",
  "publicId": "unique-id",
  "width": 800,
  "height": 600,
  "format": "webp"
}
```

The URL is public and optimized. Cloudinary serves the image in the best format for the client's browser (WebP for Chrome, JPEG for old Safari, etc.).

### Mailgun - Automatic Emails

Mailgun allows me to send beautiful emails without going crazy with SMTP.

**Automatic emails I send:**

1. **Welcome** - When you register
2. **Trip Invitation** - When someone adds you to a trip
3. **New Expense** - When someone adds a shared expense that involves you
4. **Payment Request** - When someone wants their money 💸
5. **Payment Received** - Confirmation that you've paid

**Design:**
- Responsive HTML template
- Colored gradient header
- Clear CTA buttons
- Details formatted in tables

**Performance:**
Emails are sent asynchronously, so APIs respond immediately without waiting for Mailgun. If the email fails, the operation continues anyway (I don't want the trip not to be created just because Mailgun is down).

**Configuration:**
Just put the credentials in the `.env` and everything works. Mailgun has a free tier that covers 5000 emails/month, more than enough for testing.

---

## 🏗️ How the Code is Organized

I followed a classic but effective layered architecture:

```
com.santoprestandrea_s00007624.backend_travelmates/
├── config/              # Configurations (Security, Cloudinary, Mailgun)
├── controller/          # REST Controllers
├── dto/
│   ├── request/         # Request DTOs
│   └── response/        # Response DTOs
├── entity/              # JPA Entities
├── exception/           # Custom exceptions and global handler
├── mapper/              # Entity ↔ DTO conversions
├── repository/          # JPA Repositories
├── security/            # JWT Filter and utilities
└── service/             # Business logic
```

### Patterns Used

- **Layered Architecture**: Controller → Service → Repository
- **DTO Pattern**: Separation of entity from API contracts
- **Builder Pattern**: Lombok @Builder for object construction
- **Dependency Injection**: Spring @Autowired
- **Strategy Pattern**: Expense inheritance with different types
- **Observer Pattern**: Asynchronous email events

---

## 📈 Performance & Best Practices

### Database
- **Lazy Loading** for @OneToMany relationships
- **Fetch Joins** for N+1 queries
- **Pagination** for large lists
- **Indexes** on foreign keys

### API
- **Validation** input with Bean Validation
- **Error Handling** centralized
- **JWT stateless** for scalability
- **CORS** configured

### Security
- **BCrypt** for password hashing
- **JWT** with expiration
- **Role-based** access control
- **SQL Injection** prevented by JPA

---