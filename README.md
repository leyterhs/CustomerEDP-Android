# CustomerEDP Android App

Android mobile client for the Customer Engagement & Delivery Platform.

## Setup

### 1. Configure Backend URL

Open `gradle.properties` and set the `BASE_URL` to match your backend:

```properties
# For Android Emulator (backend runs on localhost):
BASE_URL=http://10.0.2.2:8080/

# For a real device (same WiFi network):
# BASE_URL=http://<YOUR_COMPUTER_IP>:8080/

# For ngrok (public access):
# BASE_URL=https://<YOUR_NGROK_URL>/
Important: After changing BASE_URL, do a Rebuild Project in Android Studio.

2. Run the App
Open the project in Android Studio.

Click Run (green triangle).

Login with the default credentials:
Username: admin
Password: admin

Features
User Authentication (Login / Register)

Admin Panel – User Management (CRUD)

Clients – View, Create, Edit, Delete

Engagements – View, Create, Edit, Delete

Deliveries – View, Create, Edit, Delete

Real-time updates via backend REST API

Tech Stack
Language: Java

Networking: HttpURLConnection

JSON: org.json

Architecture: MVC (Activities + Adapters + Models)

Backend Repository
The backend is available at:
CustomerEDP

License
MIT