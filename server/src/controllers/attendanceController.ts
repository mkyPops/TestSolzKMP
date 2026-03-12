import { Request, Response } from 'express';
import Attendance from '../models/Attendance';

// POST /api/attendance/checkin
export const checkIn = async (req: Request, res: Response) => {
  try {
    const { employeeId, notes } = req.body;
    if (!employeeId) return res.status(400).json({ success: false, error: 'employeeId is required' });

    const today = new Date();
    today.setHours(0, 0, 0, 0);

    const existing = await Attendance.findOne({ employeeId, date: today });
    if (existing) return res.status(400).json({ success: false, error: 'Already checked in today' });

    const checkInTime = new Date();
    const nineAM = new Date(); nineAM.setHours(9, 0, 0, 0);
    const status = checkInTime > nineAM ? 'late' : 'present';

    const attendance = new Attendance({ employeeId, date: today, checkIn: checkInTime, status, notes });
    await attendance.save();

    res.status(201).json({ success: true, data: attendance, message: 'Checked in successfully' });
  } catch (error) {
    res.status(500).json({ success: false, error: error instanceof Error ? error.message : 'Unknown error' });
  }
};

// POST /api/attendance/checkout
export const checkOut = async (req: Request, res: Response) => {
  try {
    const { employeeId } = req.body;
    if (!employeeId) return res.status(400).json({ success: false, error: 'employeeId is required' });

    const today = new Date(); today.setHours(0, 0, 0, 0);
    const attendance = await Attendance.findOne({ employeeId, date: today });

    if (!attendance) return res.status(404).json({ success: false, error: 'No check-in found for today' });
    if (attendance.checkOut) return res.status(400).json({ success: false, error: 'Already checked out today' });

    attendance.checkOut = new Date();
    const diffMs = attendance.checkOut.getTime() - attendance.checkIn.getTime();
    attendance.workHours = parseFloat((diffMs / (1000 * 60 * 60)).toFixed(2));
    await attendance.save();

    res.json({ success: true, data: attendance, message: 'Checked out successfully' });
  } catch (error) {
    res.status(500).json({ success: false, error: error instanceof Error ? error.message : 'Unknown error' });
  }
};

// GET /api/attendance/:employeeId
export const getAttendanceByEmployee = async (req: Request, res: Response) => {
  try {
    const records = await Attendance.find({ employeeId: req.params.employeeId }).sort({ date: -1 });
    res.json({ success: true, data: records });
  } catch (error) {
    res.status(500).json({ success: false, error: error instanceof Error ? error.message : 'Unknown error' });
  }
};

// GET /api/attendance/today/all
export const getTodayAttendance = async (req: Request, res: Response) => {
  try {
    const today = new Date(); today.setHours(0, 0, 0, 0);
    const records = await Attendance.find({ date: today }).populate('employeeId', 'name email department');
    res.json({ success: true, data: records });
  } catch (error) {
    res.status(500).json({ success: false, error: error instanceof Error ? error.message : 'Unknown error' });
  }
};