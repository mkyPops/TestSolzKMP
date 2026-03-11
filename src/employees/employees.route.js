const { Router } = require('express');
const {
  createEmployee,
  listEmployees,
  getEmployee,
  updateEmployee,
} = require('./employees.controller');
const { requireAuth, requireAdmin } = require('../utilities/auth.check');

const router = Router();

// All routes are admin-only
router.use(requireAuth, requireAdmin);

router.post('/', createEmployee);
router.get('/', listEmployees);
router.get('/:id', getEmployee);
router.patch('/:id', updateEmployee);

module.exports = router;