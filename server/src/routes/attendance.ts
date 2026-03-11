import { Router } from 'express';
import * as attendanceController from '../controllers/attendanceController';
import { authMiddleware } from '../middleware/auth';

const router = Router();

// GET /api/attendance/:employeeId - Get attendance records
router.get('/:employeeId', authMiddleware, attendanceController.getAttendance);

// POST /api/attendance/checkin - Mark check-in
router.post('/checkin', authMiddleware, attendanceController.checkin);

// POST /api/attendance/checkout - Mark check-out
router.post('/checkout', authMiddleware, attendanceController.checkout);

export default router;
