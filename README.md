# 🔄 Swapily — Android App

> **Swapily** is a peer-to-peer product swap platform built with Kotlin and Jetpack Compose. Users list items they own, discover what others are offering nearby, and propose swaps — all without exchanging money.

---

## ✨ Features

### For users
- 🔐 **Authentication** — Email/Password, Google Sign-In, and Facebook Login, with persistent sessions
- 🏠 **Home Feed** — Browse available products with search, category, and location filters
- 🤖 **Smart Match** — A matching engine that scores and ranks the best swap pairs based on your products' wanted categories vs. what others are offering
- ➕ **Add Product** — List items with multiple images, condition, category, estimated value, and location
- 🔄 **Swap Requests** — Send, accept, or reject swap proposals with real-time status updates (`PENDING`, `ACCEPTED`, `REJECTED`, `COMPLETED`)
- 💬 **In-App Chat** — Negotiate swap details directly with the other user, per swap conversation
- ❤️ **Favorites** — Save products you're interested in
- 👤 **Public Profiles** — View other users' listings, bio, location, and ratings
- ⭐ **Reviews & Ratings** — Rate swap partners after completed exchanges
- 🚩 **Report Users/Products** — Flag inappropriate listings or behavior for moderation
- 🔔 **Push Notifications** — Real-time and broadcast notifications via Firebase Cloud Messaging (FCM), with an in-app notification center
- 📍 **Location Filtering** — Filter products by city or auto-detect your current location

### Admin panel
A role-gated (`role: "ADMIN"`) dashboard, automatically routed to on login:
- 📊 **Dashboard** — Live counts of users, products, swaps, pending swaps, and reports, plus a recent activity feed
- 👥 **User Management** — View all users, block/unblock, or delete accounts
- 📦 **Product Management** — View, archive, or delete any listing
- 🔁 **Swap Oversight** — Monitor all swaps across the platform
- 🚩 **Report Handling** — Review reports and mark them as resolved or dismissed
- 📣 **Notifications** — Broadcast a notification to all users or target a single user

---

## 🏗️ Architecture

The app follows the **MVVM** (Model-View-ViewModel) pattern with a clean separation of concerns:

```
com.swapily.app
├── data/
│   ├── model/          # Product, User, Swap, Message, Review, Report, AppNotification, MatchResult
│   └── repository/     # AuthRepository, ProductRepository, SwapRepository, ReportsRepository, AdminRepository
├── ui/
│   ├── screens/
│   │   ├── auth/           # LoginScreen (sign up + sign in)
│   │   ├── home/            # HomeScreen
│   │   ├── addproduct/      # AddProductScreen
│   │   ├── productdetail/   # ProductDetailScreen
│   │   ├── chat/            # ChatScreen
│   │   ├── messages/        # MessagesScreen (swap list)
│   │   ├── profile/         # ProfileScreen, EditProfileScreen, EditProductScreen, PublicProfileScreen
│   │   ├── notifications/   # UserNotificationsScreen
│   │   └── admin/           # AdminMainScreen, AdminDashboardScreen, AdminUsersScreen,
│   │                         # AdminProductsScreen, AdminSwapsScreen, AdminReportsScreen, AdminNotificationsScreen
│   ├── components/     # Shared UI components: BottomBar
│   ├── Navigation/     # NavGraph & Screen sealed class
│   └── theme/          # Colors, Typography, Theme
├── viewmodel/          # AuthViewModel, ProductViewModel, SwapViewModel, SmartMatchViewModel,
│                        # UserNotificationsViewModel, Admin*ViewModel (per admin screen)
└── service/            # MyFirebaseMessagingService (FCM)
```

---

## 🛠️ Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Kotlin |
| UI | Jetpack Compose + Material 3 |
| Navigation | Navigation Compose |
| Architecture | MVVM + Kotlin Flow / StateFlow |
| Authentication | Firebase Auth (Email, Google, Facebook) |
| Database | Firebase Firestore (real-time listeners) |
| Image Storage | Cloudinary |
| Image Loading | Coil |
| Push Notifications | Firebase Cloud Messaging (FCM) |
| Location | Google Play Services (FusedLocationProvider + Geocoder) |
| Min SDK | 31 (Android 12) |
| Target / Compile SDK | 36 |
| Build System | Gradle (Kotlin DSL), AGP 9.2.1 |

---

## 🚀 Getting Started

### Prerequisites

- Android Studio (latest stable) with AGP 9.2.1 / Kotlin 2.2.10 support
- JDK 11
- A Firebase project with **Authentication**, **Firestore**, **Storage**, and **Cloud Messaging** enabled
- A Cloudinary account with an unsigned upload preset
- A Facebook Developer App (for Facebook Login)

### Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/alghemsaad/Projet-Android-Swapily.git
   cd Projet-Android-Swapily/front_end
   ```

2. **Add your Firebase config**
   Download your own `google-services.json` from the Firebase Console and replace the one at:
   ```
   front_end/app/google-services.json
   ```

3. **Configure Cloudinary**
   In `SwapilyApp.kt`, set your own Cloudinary `cloud_name`:
   ```kotlin
   val config = mapOf(
       "cloud_name" to "YOUR_CLOUD_NAME",
       "secure" to true
   )
   MediaManager.init(this, config)
   ```

4. **Configure Facebook Login**
   In `res/values/strings.xml`, set your own Facebook app credentials:
   ```xml
   <string name="facebook_app_id">YOUR_FB_APP_ID</string>
   <string name="facebook_client_token">YOUR_FB_CLIENT_TOKEN</string>
   <string name="fb_login_protocol_scheme">fbYOUR_FB_APP_ID</string>
   ```

5. **Build and run**
   Open the project in Android Studio and run on a device or emulator (API 31+). Alternatively, from the command line:
   ```bash
   ./gradlew assembleDebug
   ```

---

## 📋 Firestore Data Model

```
/users/{uid}
  - name, email, image, location, bio
  - publicProfile, showLocation
  - rating, swapsCount, reviewsCount
  - favorites: [productId]
  - fcmToken
  - role: "USER" | "ADMIN"
  - isBlocked

/products/{productId}
  - userId, title, description, category, wantedCategory, lookingFor
  - condition, estimatedValue, location
  - images: [url]
  - isAvailable, status: "available" | "swapped"

/swaps/{swapId}
  - senderId, senderName, senderImage, senderProductId, senderProductTitle, senderProductImage
  - receiverId, receiverName, receiverImage, receiverProductId, receiverProductTitle, receiverProductImage
  - status: "PENDING" | "ACCEPTED" | "REJECTED" | "COMPLETED"
  - lastMessage, lastSenderId, read, timestamp, acceptedAt

  /swaps/{swapId}/messages/{messageId}
    - senderId, text, timestamp, read

/reviews/{reviewId}
  - fromUserId, fromUserName, toUserId, rating, comment, timestamp

/reports/{reportId}
  - reportedBy, reporterName, targetType: "PRODUCT" | "USER", targetId
  - reason, status: "PENDING" | "RESOLVED" | "DISMISSED", createdAt

/notifications/{notificationId}
  - title, message, targetType: "ALL" | "USER", targetUserId
  - sentBy, timestamp, read
```

---

## 🤖 Smart Match Algorithm

The **SmartMatch** engine compares every product owned by the current user against all available products from other users and computes a compatibility score based on:

- Category match between `wantedCategory` (what you want) and `category` (what they offer)
- Reverse match between your `category` and their `wantedCategory`
- Estimated value proximity

Results are sorted by score and surfaced as ranked swap suggestions.

---

## 🔐 Permissions

| Permission | Purpose |
|-----------|---------|
| `INTERNET` | Firebase & Cloudinary communication |
| `ACCESS_FINE_LOCATION` | Auto-detect user city for filtering |
| `ACCESS_COARSE_LOCATION` | Fallback location detection |
| `CAMERA` | Photo capture for product listings |
| `POST_NOTIFICATIONS` | Push notifications (Android 13+) |

---

## 👥 Authors

| Author | Role | GitHub |
|--------|------|--------|
| **Saad Alghem** | Master's Student in DevOps & Cloud Computing | [@alghemsaad](https://github.com/alghemsaad) |
| **Jihane Diouri** | Master's Student in DevOps & Cloud Computing | [@jihanediouri](https://github.com/jihanediouri) |

---

## 📄 License

This project was developed for educational purposes as part of a Master's program in DevOps & Cloud Computing.
All rights reserved © 2025 **Saad Alghem** & **Jihane Diouri**.
