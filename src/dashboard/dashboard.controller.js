const User = require('../auth/auth.model');
const Attendance = require('../attendance/attendance.model');
const Request = require('../requests/requests.model');

function getTodayDate() {
  return new Date().toISOString().slice(0, 10); // "YYYY-MM-DD"
}

async function getDashboardMetrics(req, res, next) {
  try {
    const today = getTodayDate();

    // 09:15 cutoff in UTC (adjust if your server is in a different timezone)
    const cutoffHour = 9;
    const cutoffMinute = 15;

    const [
      totalEmployees,
      todayAttendance,
      pendingRequests,
      approvedLeaves,
    ] = await Promise.all([
      // 1. Total employees
      User.countDocuments({ role: 'EMPLOYEE' }),

      // 2. Today's attendance records
      Attendance.find({ date: today }, 'userId checkInTime'),

      // 3. Pending requests
      Request.countDocuments({ status: 'PENDING' }),

      // 4. Approved leave requests covering today
      Request.find({
        type: 'LEAVE',
        status: 'APPROVED',
        startDate: { $lte: today },
      }, 'userId startDate endDate'),
    ]);

    // presentToday — distinct userIds with attendance today
    const presentUserIds = new Set(
      todayAttendance.map((a) => a.userId.toString())
    );
    const presentToday = presentUserIds.size;

    // lateToday — present employees whose checkInTime is after 09:15
    let lateToday = 0;
    for (const record of todayAttendance) {
      if (!record.checkInTime) continue;
      const checkIn = new Date(record.checkInTime);
      const hours = checkIn.getUTCHours();
      const minutes = checkIn.getUTCMinutes();
      if (
        hours > cutoffHour ||
        (hours === cutoffHour && minutes > cutoffMinute)
      ) {
        lateToday++;
      }
    }

    // onLeave — distinct employees with approved leave covering today
    const onLeaveUserIds = new Set();
    for (const leave of approvedLeaves) {
      const end = leave.endDate || leave.startDate;
      if (leave.startDate <= today && today <= end) {
        onLeaveUserIds.add(leave.userId.toString());
      }
    }
    const onLeave = onLeaveUserIds.size;

    return res.json({
      totalEmployees,
      presentToday,
      onLeave,
      lateToday,
      pendingRequests,
    });
  } catch (err) {
    next(err);
  }
}

module.exports = { getDashboardMetrics };