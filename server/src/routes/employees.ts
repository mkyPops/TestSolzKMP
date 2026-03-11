import { Router } from 'express';
import * as employeeController from '../controllers/employeeController';
import { authMiddleware } from '../middleware/auth';

const router = Router();

// GET /api/employees - List all employees
router.get('/', authMiddleware, employeeController.getEmployees);

// POST /api/employees - Add employee
router.post('/', authMiddleware, employeeController.createEmployee);

export default router;
