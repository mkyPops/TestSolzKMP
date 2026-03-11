const { Router } = require('express');
const { getMyAttendance, getTodayStatus } = require('./attendance.controller');
const { requireAuth, requireAdmin } = require('../utilities/auth.check');

const router = Router();

// Employee: view own attendance history
router.get('/me', requireAuth, getMyAttendance);

// Admin: today's attendance status for all employees
router.get('/admin/status', requireAuth, requireAdmin, getTodayStatus);

module.exports = router;