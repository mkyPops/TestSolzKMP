import { Request, Response } from 'express';
import Leave from '../models/Leave';

// POST /api/leaves
export const applyLeave = async (req: Request, res: Response) => {
  try {
    const { employeeId, type, startDate, endDate, reason } = req.body;
    if (!employeeId || !type || !startDate || !endDate || !reason) {
      return res.status(400).json({ success: false, error: 'All fields are required' });
    }

    const leave = new Leave({ employeeId, type, startDate, endDate, reason });
    await leave.save();

    res.status(201).json({ success: true, data: leave, message: 'Leave applied successfully' });
  } catch (error) {
    res.status(500).json({ success: false, error: error instanceof Error ? error.message : 'Unknown error' });
  }
};

// GET /api/leaves
export const getAllLeaves = async (req: Request, res: Response) => {
  try {
    const leaves = await Leave.find().populate('employeeId', 'name email department').sort({ appliedAt: -1 });
    res.json({ success: true, data: leaves });
  } catch (error) {
    res.status(500).json({ success: false, error: error instanceof Error ? error.message : 'Unknown error' });
  }
};

// GET /api/leaves/:employeeId
export const getLeavesByEmployee = async (req: Request, res: Response) => {
  try {
    const leaves = await Leave.find({ employeeId: req.params.employeeId }).sort({ appliedAt: -1 });
    res.json({ success: true, data: leaves });
  } catch (error) {
    res.status(500).json({ success: false, error: error instanceof Error ? error.message : 'Unknown error' });
  }
};

// PATCH /api/leaves/:id/approve
export const approveLeave = async (req: Request, res: Response) => {
  try {
    const leave = await Leave.findByIdAndUpdate(req.params.id, { status: 'approved', approvedBy: req.body.approvedBy }, { new: true });
    if (!leave) return res.status(404).json({ success: false, error: 'Leave not found' });
    res.json({ success: true, data: leave, message: 'Leave approved' });
  } catch (error) {
    res.status(500).json({ success: false, error: error instanceof Error ? error.message : 'Unknown error' });
  }
};

// PATCH /api/leaves/:id/reject
export const rejectLeave = async (req: Request, res: Response) => {
  try {
    const leave = await Leave.findByIdAndUpdate(req.params.id, { status: 'rejected' }, { new: true });
    if (!leave) return res.status(404).json({ success: false, error: 'Leave not found' });
    res.json({ success: true, data: leave, message: 'Leave rejected' });
  } catch (error) {
    res.status(500).json({ success: false, error: error instanceof Error ? error.message : 'Unknown error' });
  }
};