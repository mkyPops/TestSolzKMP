const mongoose = require('mongoose');

const requestSchema = new mongoose.Schema(
  {
    userId: {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'User',
      required: true,
    },
    userName: {
      type: String,
      required: true,
    },
    userDepartment: {
      type: String,
      default: null,
    },
    type: {
      type: String,
      enum: ['LEAVE', 'LATE_ARRIVAL'],
      required: true,
    },
    leaveType: {
      type: String,
      enum: ['SICK', 'VACATION', 'PERSONAL', 'EMERGENCY', null],
      default: null,
    },
    startDate: {
      type: String, // "YYYY-MM-DD"
      required: true,
    },
    endDate: {
      type: String, // "YYYY-MM-DD"
      default: null,
    },
    expectedTime: {
      type: String, // e.g. "10:30"
      default: null,
    },
    reason: {
      type: String,
      required: true,
      trim: true,
    },
    status: {
      type: String,
      enum: ['PENDING', 'APPROVED', 'REJECTED'],
      default: 'PENDING',
    },
    adminComment: {
      type: String,
      default: null,
    },
    reviewedAt: {
      type: Date,
      default: null,
    },
    reviewedBy: {
      type: String, // admin email or id
      default: null,
    },
  },
  { timestamps: true }
);

requestSchema.set('toJSON', {
  transform(doc, ret) {
    ret.id = ret._id.toString();
    ret.userId = ret.userId.toString();
    delete ret._id;
    delete ret.__v;
    return ret;
  },
});

const Request = mongoose.model('Request', requestSchema);
module.exports = Request;