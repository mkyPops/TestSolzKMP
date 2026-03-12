import mongoose, { Schema, Document } from 'mongoose';

export interface ILeave extends Document {
  employeeId: mongoose.Types.ObjectId;
  type: 'sick' | 'casual' | 'annual' | 'unpaid';
  startDate: Date;
  endDate: Date;
  reason: string;
  status: 'pending' | 'approved' | 'rejected';
  approvedBy?: mongoose.Types.ObjectId;
  appliedAt: Date;
}

const LeaveSchema = new Schema<ILeave>({
  employeeId:  { type: Schema.Types.ObjectId, ref: 'Employee', required: true },
  type:        { type: String, enum: ['sick', 'casual', 'annual', 'unpaid'], required: true },
  startDate:   { type: Date, required: true },
  endDate:     { type: Date, required: true },
  reason:      { type: String, required: true },
  status:      { type: String, enum: ['pending', 'approved', 'rejected'], default: 'pending' },
  approvedBy:  { type: Schema.Types.ObjectId, ref: 'Employee' },
  appliedAt:   { type: Date, default: Date.now }
}, { timestamps: true });

export default mongoose.model<ILeave>('Leave', LeaveSchema);