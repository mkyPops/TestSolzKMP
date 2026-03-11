const { z } = require('zod');
const Request = require('./requests.model');
const User = require('../auth/auth.model');

// ─── Zod Schemas ────────────────────────────────────────────────────────────

const leaveSchema = z.object({
  type: z.literal('LEAVE'),
  leaveType: z.enum(['SICK', 'VACATION', 'PERSONAL', 'EMERGENCY']),
  startDate: z.string().regex(/^\d{4}-\d{2}-\d{2}$/, 'startDate must be YYYY-MM-DD'),
  endDate: z
    .string()
    .regex(/^\d{4}-\d{2}-\d{2}$/, 'endDate must be YYYY-MM-DD')
    .optional(),
  reason: z.string().min(1, 'reason is required'),
});

const lateArrivalSchema = z.object({
  type: z.literal('LATE_ARRIVAL'),
  startDate: z
    .string()
    .regex(/^\d{4}-\d{2}-\d{2}$/, 'startDate must be YYYY-MM-DD')
    .optional(),
  expectedTime: z.string().min(1, 'expectedTime is required'),
  reason: z.string().min(1, 'reason is required'),
});

const reviewSchema = z.object({
  adminComment: z.string().optional(),
});

// ─── Helpers ────────────────────────────────────────────────────────────────

function getTodayDate() {
  return new Date().toISOString().slice(0, 10);
}

// ─── Employee: GET /v1/requests/me ──────────────────────────────────────────

async function getMyRequests(req, res, next) {
  try {
    const requests = await Request.find({ userId: req.user.id }).sort({ createdAt: -1 });
    return res.json({ requests: requests.map((r) => r.toJSON()) });
  } catch (err) {
    next(err);
  }
}

// ─── Employee: POST /v1/requests ────────────────────────────────────────────

async function createRequest(req, res, next) {
  try {
    const { type } = req.body;

    if (!type) {
      return res.status(400).json({
        error: {
          code: 'VALIDATION_ERROR',
          message: 'type is required',
          details: {},
        },
      });
    }

    let parsed;
    if (type === 'LEAVE') {
      parsed = leaveSchema.safeParse(req.body);
    } else if (type === 'LATE_ARRIVAL') {
      parsed = lateArrivalSchema.safeParse(req.body);
    } else {
      return res.status(400).json({
        error: {
          code: 'VALIDATION_ERROR',
          message: 'type must be LEAVE or LATE_ARRIVAL',
          details: {},
        },
      });
    }

    if (!parsed.success) {
      return res.status(400).json({
        error: {
          code: 'VALIDATION_ERROR',
          message: 'Invalid request body',
          details: parsed.error.flatten().fieldErrors,
        },
      });
    }

    const user = await User.findById(req.user.id);
    if (!user) {
      return res.status(404).json({
        error: { code: 'USER_NOT_FOUND', message: 'User not found', details: {} },
      });
    }

    const data = parsed.data;

    const request = await Request.create({
      userId: req.user.id,
      userName: user.name,
      userDepartment: user.department,
      type: data.type,
      leaveType: data.leaveType || null,
      startDate: data.startDate || getTodayDate(),
      endDate: data.endDate || null,
      expectedTime: data.expectedTime || null,
      reason: data.reason,
      status: 'PENDING',
    });

    return res.status(201).json({ request: request.toJSON() });
  } catch (err) {
    next(err);
  }
}

// ─── Admin: GET /v1/requests/admin?status=PENDING ───────────────────────────

async function getAdminRequests(req, res, next) {
  try {
    const status = req.query.status || 'PENDING';
    const validStatuses = ['PENDING', 'APPROVED', 'REJECTED'];

    if (!validStatuses.includes(status)) {
      return res.status(400).json({
        error: {
          code: 'VALIDATION_ERROR',
          message: 'status must be PENDING, APPROVED, or REJECTED',
          details: {},
        },
      });
    }

    const requests = await Request.find({ status }).sort({ createdAt: -1 });
    return res.json({ requests: requests.map((r) => r.toJSON()) });
  } catch (err) {
    next(err);
  }
}

// ─── Admin: POST /v1/requests/admin/:id/approve ─────────────────────────────

async function approveRequest(req, res, next) {
  try {
    const parsed = reviewSchema.safeParse(req.body);
    if (!parsed.success) {
      return res.status(400).json({
        error: {
          code: 'VALIDATION_ERROR',
          message: 'Invalid request body',
          details: parsed.error.flatten().fieldErrors,
        },
      });
    }

    const request = await Request.findById(req.params.id);
    if (!request) {
      return res.status(404).json({
        error: { code: 'REQUEST_NOT_FOUND', message: 'Request not found', details: {} },
      });
    }

    if (request.status !== 'PENDING') {
      return res.status(409).json({
        error: {
          code: 'ALREADY_REVIEWED',
          message: `Request has already been ${request.status.toLowerCase()}`,
          details: {},
        },
      });
    }

    request.status = 'APPROVED';
    request.reviewedAt = new Date();
    request.reviewedBy = req.user.id;
    request.adminComment = parsed.data.adminComment || null;
    await request.save();

    return res.json({ request: request.toJSON() });
  } catch (err) {
    next(err);
  }
}

// ─── Admin: POST /v1/requests/admin/:id/reject ──────────────────────────────

async function rejectRequest(req, res, next) {
  try {
    const parsed = reviewSchema.safeParse(req.body);
    if (!parsed.success) {
      return res.status(400).json({
        error: {
          code: 'VALIDATION_ERROR',
          message: 'Invalid request body',
          details: parsed.error.flatten().fieldErrors,
        },
      });
    }

    const request = await Request.findById(req.params.id);
    if (!request) {
      return res.status(404).json({
        error: { code: 'REQUEST_NOT_FOUND', message: 'Request not found', details: {} },
      });
    }

    if (request.status !== 'PENDING') {
      return res.status(409).json({
        error: {
          code: 'ALREADY_REVIEWED',
          message: `Request has already been ${request.status.toLowerCase()}`,
          details: {},
        },
      });
    }

    request.status = 'REJECTED';
    request.reviewedAt = new Date();
    request.reviewedBy = req.user.id;
    request.adminComment = parsed.data.adminComment || null;
    await request.save();

    return res.json({ request: request.toJSON() });
  } catch (err) {
    next(err);
  }
}

module.exports = {
  getMyRequests,
  createRequest,
  getAdminRequests,
  approveRequest,
  rejectRequest,
};