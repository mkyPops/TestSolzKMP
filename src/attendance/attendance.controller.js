const Attendance = require('./attendance.model');
const User = require('../auth/auth.model');

// Employee: GET /v1/attendance/me?daysBack=30
async function getMyAttendance(req, res, next) {
  try {
    const daysBack = parseInt(req.query.daysBack) || 30;
    const fromDate = new Date();
    fromDate.setDate(fromDate.getDate() - daysBack);
    const fromDateStr = fromDate.toISOString().slice(0, 10);

    const records = await Attendance.find({
      userId: req.user.id,
      date: { $gte: fromDateStr },
    }).sort({ date: -1 });

    return res.json({ attendance: records.map((r) => r.toJSON()) });
  } catch (err) {
    next(err);
  }
}

// Admin: GET /v1/admin/attendance/status  (today's status)
async function getTodayStatus(req, res, next) {
  try {
    const today = new Date().toISOString().slice(0, 10);

    const allUsers = await User.find({}, '_id name email role department');
    const todayRecords = await Attendance.find({ date: today });

    const recordMap = {};
    for (const r of todayRecords) {
      recordMap[r.userId.toString()] = r;
    }

    const status = allUsers.map((user) => {
      const record = recordMap[user._id.toString()];
      return {
        userId: user._id.toString(),
        name: user.name,
        email: user.email,
        role: user.role,
        department: user.department,
        status: !record
          ? 'ABSENT'
          : record.checkInTime && record.checkOutTime
          ? 'CHECKED_OUT'
          : record.checkInTime
          ? 'PRESENT'
          : 'ABSENT',
        checkInTime: record?.checkInTime || null,
        checkOutTime: record?.checkOutTime || null,
      };
    });

    return res.json({ date: today, employees: status });
  } catch (err) {
    next(err);
  }
}

module.exports = { getMyAttendance, getTodayStatus };