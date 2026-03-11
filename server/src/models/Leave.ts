import mongoose, { Schema, Document } from 'mongoose';

export interface ILeave extends Document {
  employeeId: mongoose.Types.ObjectId;
  startDate: Date;
  endDate: Date;
  reason: string;
  type: 'sick' | 'casual' | 'earned' | 'unpaid';
  status: 'pending' | 'approved' | 'rejected';
  remarks?: string;
  approvedBy?: mongoose.Types.ObjectId;
  createdAt: Date;
  updatedAt: Date;
}

const leaveSchema = new Schema<ILeave>(
  {
    employeeId: {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'Employee',
      required: true
    },
    startDate: {
      type: Date,
      required: true
    },
    endDate: {
      type: Date,
      required: true
    },
    reason: {
      type: String,
      required: true
    },
    type: {
      type: String,
      enum: ['sick', 'casual', 'earned', 'unpaid'],
      required: true
    },
    status: {
      type: String,
      enum: ['pending', 'approved', 'rejected'],
      default: 'pending'
    },
    remarks: {
      type: String
    },
    approvedBy: {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'Employee'
    }
  },
  { timestamps: true }
);

export const Leave = mongoose.model<ILeave>('Leave', leaveSchema);
