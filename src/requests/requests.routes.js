const { Router } = require('express');
const {
  getMyRequests,
  createRequest,
  getAdminRequests,
  approveRequest,
  rejectRequest,
} = require('./requests.controller');
const { requireAuth, requireAdmin } = require('../utilities/auth.check');

const router = Router();

// ─── Employee routes ─────────────────────────────────────────────────────────
router.get('/me', requireAuth, getMyRequests);
router.post('/', requireAuth, createRequest);

// ─── Admin routes ─────────────────────────────────────────────────────────────
router.get('/admin', requireAuth, requireAdmin, getAdminRequests);
router.post('/admin/:id/approve', requireAuth, requireAdmin, approveRequest);
router.post('/admin/:id/reject', requireAuth, requireAdmin, rejectRequest);

module.exports = router;