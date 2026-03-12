import mongoose, { Schema, Document } from 'mongoose';

export interface IAttendance extends Document {
  employeeId: mongoose.Types.ObjectId;
  date: Date;
  checkIn: Date;
  checkOut?: Date;
  status: 'present' | 'absent' | 'late' | 'half-day';
  workHours?: number;
  notes?: string;
}

const AttendanceSchema = new Schema<IAttendance>({
  employeeId: { type: Schema.Types.ObjectId, ref: 'Employee', required: true },
  date:       { type: Date, required: true },
  checkIn:    { type: Date, required: true },
  checkOut:   { type: Date },
  status:     { type: String, enum: ['present', 'absent', 'late', 'half-day'], default: 'present' },
  workHours:  { type: Number },
  notes:      { type: String }
}, { timestamps: true });

export default mongoose.model<IAttendance>('Attendance', AttendanceSchema);