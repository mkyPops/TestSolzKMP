import { Router } from 'express';
import * as leaveController from '../controllers/leaveController';
import { authMiddleware } from '../middleware/auth';

const router = Router();

// GET /api/leaves - List leave requests
router.get('/', authMiddleware, leaveController.getLeaves);

// POST /api/leaves - Submit leave request
router.post('/', authMiddleware, leaveController.submitLeave);

// PATCH /api/leaves/:id - Approve/reject leave
router.patch('/:id', authMiddleware, leaveController.updateLeaveStatus);

export default router;
