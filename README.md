# 🔄 Swapily — Android App

> **Swapily** is a peer-to-peer product swap platform built with Kotlin and Jetpack Compose. Users can list items they own, discover what others are offering nearby, and propose swaps — all without exchanging money.

---

## ✨ Features

- 🔐 **Authentication** — Email/Password, Google Sign-In, and Facebook Login
- 🏠 **Home Feed** — Browse available products with search, category and location filters
- 🤖 **Smart Match** — AI-powered matching engine that suggests the best swap pairs based on your wishlist and available items
- ➕ **Add Product** — List items with multiple images, condition, category, and location
- 🔄 **Swap Requests** — Send, accept, or reject swap proposals with real-time status updates
- 💬 **In-App Chat** — Negotiate swap details directly with the other user
- ❤️ **Favorites** — Save products you're interested in
- 👤 **Public Profiles** — View other users' listings and ratings
- ⭐ **Reviews & Ratings** — Rate swap partners after completed exchanges
- 🔔 **Push Notifications** — Get notified on new swap requests and messages (FCM)
- 📍 **Location Filtering** — Filter products by city or auto-detect your current location

---

## 🏗️ Architecture

The app follows the **MVVM** (Model-View-ViewModel) pattern with a clean separation of concerns:

```
com.swapily.app
├── data/
│   ├── model/          # Data classes: Product, User, Swap, Message, Review, MatchResult
│   └── repository/     # Firebase data access: AuthRepository, ProductRepository, SwapRepository
├── ui/
│   ├── screens/        # Composable screens: Home, Login, Chat, Profile, AddProduct, ...
│   ├── components/     # Shared UI components: BottomBar
│   ├── Navigation/     # NavGraph & Screen sealed class
│   └── theme/          # Colors, Typography, Theme
├── viewmodel/          # AuthViewModel, ProductViewModel, SwapViewModel, SmartMatchViewModel
└── service/            # Firebase Cloud Messaging service
```

---

## 🛠️ Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Kotlin |
| UI | Jetpack Compose + Material 3 |
| Navigation | Navigation Compose |
| Architecture | MVVM + StateFlow |
| Authentication | Firebase Auth (Email, Google, Facebook) |
| Database | Firebase Firestore (real-time) |
| Image Storage | Cloudinary |
| Image Loading | Coil |
| Push Notifications | Firebase Cloud Messaging (FCM) |
| Location | Google Play Services (FusedLocationProvider + Geocoder) |
| Min SDK | 31 (Android 12) |
| Target SDK | 36 |

---

## 🚀 Getting Started

### Prerequisites

- Android Studio Hedgehog or later
- JDK 11
- A Firebase project with **Authentication**, **Firestore**, and **Cloud Messaging** enabled
- A Cloudinary account with an unsigned upload preset named `swapily_preset`
- A Facebook Developer App (for Facebook Login)

### Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/alghemsaad/Projet-Android-Swapily.git
   cd Projet-Android-Swapily/front_end
   ```

2. **Add Firebase config**  
   Download your `google-services.json` from the Firebase Console and place it at:
   ```
   front_end/app/google-services.json
   ```

3. **Configure Cloudinary**  
   In `SwapilyApp.kt`, set your Cloudinary `cloud_name`:
   ```kotlin
   val config = mapOf("cloud_name" to "YOUR_CLOUD_NAME")
   MediaManager.init(this, config)
   ```

4. **Configure Facebook Login**  
   In `res/values/strings.xml`, add:
   ```xml
   <string name="facebook_app_id">YOUR_FB_APP_ID</string>
   <string name="facebook_client_token">YOUR_FB_CLIENT_TOKEN</string>
   <string name="fb_login_protocol_scheme">fbYOUR_FB_APP_ID</string>
   ```

5. **Build and run**  
   Open the project in Android Studio and run on a device or emulator (API 31+).

---

## 📋 Firestore Data Model

```
/users/{uid}
  - name, email, image, location, bio
  - rating, swapsCount, reviewsCount
  - favorites: [productId]
  - fcmToken

/products/{productId}
  - userId, title, description, category, wantedCategory
  - condition, estimatedValue, location
  - images: [url], status: "available" | "swapped"

/swaps/{swapId}
  - senderId, receiverId
  - senderProductId, receiverProductId
  - status: "PENDING" | "ACCEPTED" | "REJECTED" | "COMPLETED"
  - lastMessage, read, timestamp, acceptedAt

  /swaps/{swapId}/messages/{messageId}
    - senderId, text, timestamp
```

---

## 🤖 Smart Match Algorithm

The **SmartMatch** engine compares every product owned by the current user against all available products from other users. It computes a **compatibility score** based on:

- Category match between `wantedCategory` (what you want) and `category` (what they offer)
- Reverse match between your `category` and their `wantedCategory`
- Estimated value proximity

Results are sorted by score and displayed as ranked swap suggestions.

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

| Author | GitHub |
|--------|--------|
| **Saad Alghem** — Master's Student in DevOps & Cloud Computing | [@alghemsaad](https://github.com/alghemsaad) |
| **Jihane Diouri** — Master's Student in DevOps & Cloud Computing | [@jihanediouri](https://github.com/jihanediouri) |

---

## 📄 License

This project is for educational purposes. All rights reserved © 2025 Swapily Team.
