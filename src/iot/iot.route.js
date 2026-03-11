const { Router } = require('express');
const { handleEvent, handleHeartbeat } = require('./iot.controller');

const router = Router();

// IoT device check-in / check-out
router.post('/attendance/event', handleEvent);

// IoT device heartbeat
router.post('/heartbeat', handleHeartbeat);

module.exports = router;