import { Router } from 'express';
import { applyLeave, getAllLeaves, getLeavesByEmployee, approveLeave, rejectLeave } from '../controllers/leaveController';

const router = Router();
router.post('/',                applyLeave);
router.get('/',                 getAllLeaves);
router.get('/:employeeId',      getLeavesByEmployee);
router.patch('/:id/approve',    approveLeave);
router.patch('/:id/reject',     rejectLeave);
export default router;