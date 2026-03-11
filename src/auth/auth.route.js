const { Router } = require('express');
const { login, me } = require('./auth.controller');
const { requireAuth } = require('../utilities/auth.check');

const router = Router();

// POST /v1/auth/login
router.post('/login', login);

// GET /v1/auth/me  (protected)
router.get('/me', requireAuth, me);

module.exports = router;