# PredicTrain

SIH Project of Group 68 on Dynamic Forecast of ETA for Coaching Trains.

## 🚀 Live Demo

**[Open PredicTrain](https://predictrain-chi.vercel.app/)**

### Backend

- **Frontend:** Vercel
- **Backend API:** [Render Backend](https://predictrain.onrender.com)
- **Database:** PostgreSQL

## 📌 About the Project

PredicTrain is a dynamic ETA and delay prediction system for coaching trains.
It continuously estimates the expected arrival time of a train by considering
historical running times, station dwell times, weather conditions, and live
incident-related delays.

## ✨ Key Features

- 🚆 Dynamic ETA prediction
- 📍 Current train position and route status
- ⏱️ Delay-aware arrival predictions
- 🌦️ Weather-based delay consideration
- 🚧 Incident and congestion impact
- 📊 Cause-wise delay breakdown
- 🛤️ ETA predictions for upcoming stations
- 🔄 Continuously updated predictions

## 🧠 ETA Prediction Approach

PredicTrain combines multiple factors:

Historical Segment Runtime
        +
Station Dwell Time
        +
Weather Impact
        +
Incident / Operational Delay
        ↓
   Dynamic ETA

The ETA is recalculated as the train progresses through its route.

## 🏗️ Architecture

React Frontend
        ↓
Spring Boot REST API
        ↓
ETA Prediction Engine
        ↓
PostgreSQL

## 🛠️ Technology Stack

### Frontend
- React
- TypeScript
- Vite

### Backend
- Java
- Spring Boot
- Spring Data JPA
- Hibernate

### Database
- PostgreSQL

### Deployment
- Vercel — Frontend
- Render — Backend & PostgreSQL

## 📁 Project Structure

```text
PredicTrain/
├── Backend/
│   ├── src/
│   ├── Dockerfile
│   └── pom.xml
│
├── frontend/
│   ├── src/
│   ├── package.json
│   └── vite.config.ts
│
└── README.md
