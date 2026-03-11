import mongoose, { Schema, Document } from 'mongoose';

export interface IAttendance extends Document {
  employeeId: mongoose.Types.ObjectId;
  date: Date;
  checkinTime?: Date;
  checkoutTime?: Date;
  workingHours?: number;
  status: 'present' | 'absent' | 'late' | 'half-day';
  remarks?: string;
  createdAt: Date;
  updatedAt: Date;
}

const attendanceSchema = new Schema<IAttendance>(
  {
    employeeId: {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'Employee',
      required: true
    },
    date: {
      type: Date,
      required: true,
      default: () => new Date().setHours(0, 0, 0, 0)
    },
    checkinTime: {
      type: Date
    },
    checkoutTime: {
      type: Date
    },
    workingHours: {
      type: Number,
      default: 0
    },
    status: {
      type: String,
      enum: ['present', 'absent', 'late', 'half-day'],
      default: 'absent'
    },
    remarks: {
      type: String
    }
  },
  { timestamps: true }
);

export const Attendance = mongoose.model<IAttendance>('Attendance', attendanceSchema);
