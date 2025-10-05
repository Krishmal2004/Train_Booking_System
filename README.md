# 🚂 Train Booking System

A comprehensive web-based train ticket booking system that provides a seamless experience for searching trains, booking tickets, and managing reservations. Built with Java backend and HTML frontend.

## ✨ Features

### For Passengers
- 🔍 **Smart Train Search** - Find trains between any two stations with real-time availability
- 🎫 **Easy Booking** - Book tickets with intuitive seat selection
- 📋 **Booking Management** - View, modify, and cancel reservations
- 💳 **Secure Payments** - Safe and secure payment processing
- 📧 **Booking Confirmation** - Instant email confirmations and e-tickets

### For Administrators
- 🚆 **Train Management** - Add, update, and remove train schedules
- 👥 **User Management** - Manage passenger accounts and bookings
- 📊 **Reports & Analytics** - Track bookings, revenue, and system usage

## 🛠️ Tech Stack

| Technology | Usage | Percentage |
|------------|-------|------------|
| HTML | Frontend UI | 84.4% |
| Java | Backend Logic | 15.6% |

## 🚀 Installation & Setup

### 1. Clone the Repository
```bash
git clone https://github.com/Krishmal2004/Train_Booking_System.git
cd Train_Booking_System
```

### 2. Database Setup
```sql
CREATE DATABASE train_booking_db;
mysql -u root -p train_booking_db < database/schema.sql
```

### 3. Configure Database Connection
Update the database configuration in your Java files with your credentials.

### 4. Deploy to Server
```bash
# Compile Java files
javac -d bin src/**/*.java

# Deploy to Tomcat webapps directory
```

### 5. Access the Application
```
http://localhost:8080/train-booking/
```

## 💡 How to Use

1. **Register/Login** - Create account or login
2. **Search Trains** - Enter source, destination, and date
3. **Book Tickets** - Select train, class, and seats
4. **Manage Bookings** - View and manage your reservations

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 👤 Author

**Krishmal2004**
- GitHub: [@Krishmal2004](https://github.com/Krishmal2004)

## 📧 Support

- 🐛 [Open an issue](https://github.com/Krishmal2004/Train_Booking_System/issues)
- ⭐ Star this repository if you find it helpful!

---

<div align="center">
  <strong>Made with ❤️ for travelers everywhere</strong>
  <br>
  <sub>Happy Journey! 🚂</sub>
</div>
