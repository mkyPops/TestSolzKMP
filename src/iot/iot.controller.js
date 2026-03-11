const { z } = require('zod');
const Device = require('../device/device.model');
const Attendance = require('../attendance/attendance.model');
const User = require('../auth/auth.model');

const eventSchema = z.object({
  deviceId: z.string().min(1),
  secret: z.string().min(1),
  userId: z.string().min(1),
  type: z.enum(['CHECK_IN', 'CHECK_OUT']),
  timestamp: z.string().optional(),
});

function getTodayDate() {
  return new Date().toISOString().slice(0, 10); // "YYYY-MM-DD"
}

async function handleEvent(req, res, next) {
  try {
    const parsed = eventSchema.safeParse(req.body);
    if (!parsed.success) {
      return res.status(400).json({
        error: {
          code: 'VALIDATION_ERROR',
          message: 'Invalid request body',
          details: parsed.error.flatten().fieldErrors,
        },
      });
    }

    const { deviceId, secret, userId, type, timestamp } = parsed.data;

    // Validate device
    const device = await Device.findOne({ deviceId, isActive: true });
    if (!device || device.secretKey !== secret) {
      return res.status(401).json({
        error: {
          code: 'INVALID_DEVICE',
          message: 'Device not recognized or secret is incorrect',
          details: {},
        },
      });
    }

    // Validate user exists
    const user = await User.findById(userId);
    if (!user) {
      return res.status(404).json({
        error: {
          code: 'USER_NOT_FOUND',
          message: 'User not found',
          details: {},
        },
      });
    }

    const eventTime = timestamp ? new Date(timestamp) : new Date();
    const date = eventTime.toISOString().slice(0, 10);

    // Find or create attendance record for today
    let attendance = await Attendance.findOne({ userId, date });

    if (type === 'CHECK_IN') {
      if (attendance && attendance.checkInTime) {
        return res.status(409).json({
          error: {
            code: 'ALREADY_CHECKED_IN',
            message: 'User has already checked in today',
            details: {},
          },
        });
      }
      if (!attendance) {
        attendance = await Attendance.create({
          userId,
          date,
          checkInTime: eventTime,
          checkedInByDevice: device._id,
        });
      } else {
        attendance.checkInTime = eventTime;
        attendance.checkedInByDevice = device._id;
        await attendance.save();
      }
    }

    if (type === 'CHECK_OUT') {
      if (!attendance || !attendance.checkInTime) {
        return res.status(400).json({
          error: {
            code: 'NOT_CHECKED_IN',
            message: 'User has not checked in yet',
            details: {},
          },
        });
      }
      if (attendance.checkOutTime) {
        return res.status(409).json({
          error: {
            code: 'ALREADY_CHECKED_OUT',
            message: 'User has already checked out today',
            details: {},
          },
        });
      }
      attendance.checkOutTime = eventTime;
      attendance.checkedOutByDevice = device._id;
      await attendance.save();
    }

    return res.json({
      success: true,
      type,
      attendance: attendance.toJSON(),
    });
  } catch (err) {
    next(err);
  }
}

async function handleHeartbeat(req, res, next) {
  try {
    const { deviceId, secret } = req.body;

    const device = await Device.findOne({ deviceId });
    if (!device || device.secretKey !== secret) {
      return res.status(401).json({
        error: {
          code: 'INVALID_DEVICE',
          message: 'Device not recognized',
          details: {},
        },
      });
    }

    device.lastHeartbeat = new Date();
    await device.save();

    return res.json({ success: true, timestamp: device.lastHeartbeat });
  } catch (err) {
    next(err);
  }
}

module.exports = { handleEvent, handleHeartbeat };