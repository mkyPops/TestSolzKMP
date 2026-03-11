import mongoose, { Schema, Document } from 'mongoose';

export interface IEmployee extends Document {
  name: string;
  email: string;
  department: string;
  salary: number;
  phone?: string;
  joinDate: Date;
  createdAt: Date;
  updatedAt: Date;
}

const employeeSchema = new Schema<IEmployee>(
  {
    name: {
      type: String,
      required: true,
      trim: true
    },
    email: {
      type: String,
      required: true,
      unique: true,
      lowercase: true
    },
    department: {
      type: String,
      required: true
    },
    salary: {
      type: Number,
      required: true,
      min: 0
    },
    phone: {
      type: String,
      trim: true
    },
    joinDate: {
      type: Date,
      default: Date.now
    }
  },
  { timestamps: true }
);

export const Employee = mongoose.model<IEmployee>('Employee', employeeSchema);
