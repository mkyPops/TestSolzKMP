const express = require('express');
const cors = require('cors');
const helmet = require('helmet');
const authRouter = require('./auth/auth.route');
const iotrouter = require('./iot/iot.route');
const attendanceRouter = require('./attendance/attendance.route');
const requestsRouter = require('./requests/requests.routes');
const employeesRouter = require('./employees/employees.route');

const app = express();

app.use(helmet());
app.use(cors());
app.use(express.json());

// Health check
app.get('/', (req, res) => {
  res.send('TestSolz Backend is Running!');
});

// Auth routes
app.use('/v1/auth', authRouter);

// IoT routes
app.use('/v1/iot', iotrouter);  
// Attendance routes
app.use('/v1/attendance', attendanceRouter); 

// Requests routes
app.use('/v1/requests', requestsRouter);

// Employees routes
app.use('/v1/admin/employees', employeesRouter);

// 404 handler
app.use((req, res) => {
  res.status(404).json({
    error: {
      code: 'NOT_FOUND',
      message: 'Route not found',
      details: {},
    },
  });
});

// Global error handler
app.use((err, req, res, next) => {
  console.error(err);
  res.status(err.status || 500).json({
    error: {
      code: err.code || 'INTERNAL_ERROR',
      message: err.message || 'Something went wrong',
      details: err.details || {},
    },
  });
});

module.exports = app;