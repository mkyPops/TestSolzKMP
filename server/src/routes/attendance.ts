import { Router } from 'express';
import { checkIn, checkOut, getAttendanceByEmployee, getTodayAttendance } from '../controllers/attendanceController';

const router = Router();
router.post('/checkin',        checkIn);
router.post('/checkout',       checkOut);
router.get('/today/all',       getTodayAttendance);
router.get('/:employeeId',     getAttendanceByEmployee);
export default router;