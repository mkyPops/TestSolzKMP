const { Router } = require('express');
const { getDashboardMetrics } = require('./dashboard.controller');
const { requireAuth, requireAdmin } = require('../utilities/auth.check');
 
const router = Router();
 
// GET /v1/admin/dashboard/metrics
router.get('/metrics', requireAuth, requireAdmin, getDashboardMetrics);
 
module.exports = router;
 