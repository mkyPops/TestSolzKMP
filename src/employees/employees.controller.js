const bcrypt = require('bcrypt');
const { z } = require('zod');
const User = require('../auth/auth.model');

// ─── Zod Schemas ─────────────────────────────────────────────────────────────

const createEmployeeSchema = z.object({
  email: z.string().email({ message: 'Invalid email format' }),
  name: z.string().min(2, 'Name must be at least 2 characters').max(100),
  department: z.string().optional().nullable(),
  password: z.string().min(6, 'Password must be at least 6 characters'),
  profileImageURL: z.string().url().optional().nullable(),
});

const updateEmployeeSchema = z.object({
  name: z.string().min(2).max(100).optional(),
  department: z.string().optional().nullable(),
  profileImageURL: z.string().url().optional().nullable(),
  password: z.string().min(6).optional(),
});

// ─── POST /v1/admin/employees ────────────────────────────────────────────────

async function createEmployee(req, res, next) {
  try {
    const parsed = createEmployeeSchema.safeParse(req.body);
    if (!parsed.success) {
      return res.status(400).json({
        error: {
          code: 'VALIDATION_ERROR',
          message: 'Invalid request body',
          details: parsed.error.flatten().fieldErrors,
        },
      });
    }

    const { email, name, department, password, profileImageURL } = parsed.data;
    const normalizedEmail = email.toLowerCase().trim();

    // Check for duplicate email
    const existing = await User.findOne({ email: normalizedEmail });
    if (existing) {
      return res.status(409).json({
        error: {
          code: 'EMAIL_TAKEN',
          message: 'An account with this email already exists',
          details: {},
        },
      });
    }

    const passwordHash = await bcrypt.hash(password, 10);

    const user = await User.create({
      email: normalizedEmail,
      name,
      role: 'EMPLOYEE', // always force EMPLOYEE
      department: department || null,
      profileImageURL: profileImageURL || null,
      passwordHash,
    });

    return res.status(201).json({ user: user.toJSON() });
  } catch (err) {
    next(err);
  }
}

// ─── GET /v1/admin/employees ─────────────────────────────────────────────────

async function listEmployees(req, res, next) {
  try {
    const page = Math.max(1, parseInt(req.query.page) || 1);
    const limit = Math.min(100, Math.max(1, parseInt(req.query.limit) || 20));
    const skip = (page - 1) * limit;
    const q = req.query.q?.trim();
    const department = req.query.department?.trim();

    const filter = {};

    if (q) {
      filter.$or = [
        { name: { $regex: q, $options: 'i' } },
        { email: { $regex: q, $options: 'i' } },
      ];
    }

    if (department) {
      filter.department = { $regex: department, $options: 'i' };
    }

    const [items, total] = await Promise.all([
      User.find(filter).sort({ createdAt: -1 }).skip(skip).limit(limit),
      User.countDocuments(filter),
    ]);

    return res.json({
      items: items.map((u) => u.toJSON()),
      page,
      limit,
      total,
    });
  } catch (err) {
    next(err);
  }
}

// ─── GET /v1/admin/employees/:id ─────────────────────────────────────────────

async function getEmployee(req, res, next) {
  try {
    const user = await User.findById(req.params.id);
    if (!user) {
      return res.status(404).json({
        error: {
          code: 'EMPLOYEE_NOT_FOUND',
          message: 'Employee not found',
          details: {},
        },
      });
    }
    return res.json({ user: user.toJSON() });
  } catch (err) {
    next(err);
  }
}

// ─── PATCH /v1/admin/employees/:id ───────────────────────────────────────────

async function updateEmployee(req, res, next) {
  try {
    const parsed = updateEmployeeSchema.safeParse(req.body);
    if (!parsed.success) {
      return res.status(400).json({
        error: {
          code: 'VALIDATION_ERROR',
          message: 'Invalid request body',
          details: parsed.error.flatten().fieldErrors,
        },
      });
    }

    const user = await User.findById(req.params.id);
    if (!user) {
      return res.status(404).json({
        error: {
          code: 'EMPLOYEE_NOT_FOUND',
          message: 'Employee not found',
          details: {},
        },
      });
    }

    const { name, department, profileImageURL, password } = parsed.data;

    if (name !== undefined) user.name = name;
    if (department !== undefined) user.department = department;
    if (profileImageURL !== undefined) user.profileImageURL = profileImageURL;
    if (password) user.passwordHash = await bcrypt.hash(password, 10);

    await user.save();

    return res.json({ user: user.toJSON() });
  } catch (err) {
    next(err);
  }
}

module.exports = { createEmployee, listEmployees, getEmployee, updateEmployee };