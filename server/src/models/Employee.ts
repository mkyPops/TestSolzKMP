import mongoose, { Schema, Document } from 'mongoose';

export interface IEmployee extends Document {
  name: string;
  email: string;
  department: string;
  salary: number;
  position: string;
  phone: string;
  joinDate: Date;
  isActive: boolean;
}

const EmployeeSchema = new Schema<IEmployee>({
  name:       { type: String, required: true },
  email:      { type: String, required: true, unique: true },
  department: { type: String, default: '' },
  salary:     { type: Number, default: 0 },
  position:   { type: String, default: '' },
  phone:      { type: String, default: '' },
  joinDate:   { type: Date, default: Date.now },
  isActive:   { type: Boolean, default: true }
}, { timestamps: true });

export default mongoose.model<IEmployee>('Employee', EmployeeSchema);