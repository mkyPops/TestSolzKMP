# Server Setup Guide

## Database Setup: MongoDB + Mongoose

### Required Dependencies
```json
{
  "dependencies": {
    "express": "^4.18.0",
    "cors": "^2.8.5",
    "dotenv": "^16.0.0",
    "mongoose": "^7.0.0"
  },
  "devDependencies": {
    "typescript": "^5.0.0",
    "@types/express": "^4.17.0",
    "@types/node": "^20.0.0",
    "ts-node": "^10.9.0",
    "nodemon": "^3.0.0"
  }
}
```

### Installation
```bash
cd server
npm install
npm install -D typescript @types/express @types/node ts-node nodemon
```

### Environment Variables (.env)
```
PORT=3000
NODE_ENV=development
DATABASE_URL=mongodb://localhost:27017/employee-management
JWT_SECRET=your_jwt_secret_key_here_change_in_production
```

## Database Models

Created Mongoose schemas for:
- **Employee**: name, email, department, salary, phone, joinDate
- **Attendance**: employeeId, date, checkinTime, checkoutTime, status, remarks
- **Leave**: employeeId, startDate, endDate, reason, type, status, approvedBy

## Running the Server

### Development Mode (with auto-reload)
```bash
npm run dev
```

### Production Mode
```bash
npm start
```

### Health Check
```bash
curl http://localhost:3000/health
```

## Connecting from KMP Frontend

Use Ktor HTTP Client (see KTOR_CLIENT_EXAMPLE.kt for implementation):

```kotlin
val employees = getEmployees()  // GET /api/employees
val attendance = getAttendance(employeeId)  // GET /api/attendance/:employeeId
httpClient.post("$BASE_URL/attendance/checkin") { ... }  // POST /api/attendance/checkin
```

## Recommended Order of Development

1. ✅ Set up the server/ folder with Express
2. ✅ Configure MongoDB + Mongoose models
3. ✅ Build Employee CRUD endpoints
4. ✅ Build Attendance endpoints (check-in/out)
5. ✅ Build Leave management endpoints
6. ⏳ Add JWT authentication middleware to auth.ts
7. ⏳ Connect from KMP frontend via Ktor
8. ⏳ Add validation and error handling
9. ⏳ Add logging and monitoring

## Database Connection

The `connectDB()` function in `src/db/connection.ts` automatically:
- Connects to MongoDB on server startup
- Handles connection errors
- Prepares the database for operations

## Next Steps

1. Install MongoDB locally or use MongoDB Atlas (cloud):
   - Local: `mongodb://localhost:27017/employee-management`
   - Atlas: Get connection string from MongoDB Atlas dashboard

2. Update package.json scripts:
   ```json
   {
     "scripts": {
       "dev": "nodemon --exec ts-node src/index.ts",
       "start": "ts-node src/index.ts",
       "build": "tsc"
     }
   }
   ```

3. Run `npm run dev` to start the development server
