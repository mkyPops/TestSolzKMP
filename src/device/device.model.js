const mongoose = require('mongoose');

const deviceSchema = new mongoose.Schema(
  {
    deviceId: {
      type: String,
      required: true,
      unique: true,
      trim: true,
    },
    secretKey: {
      type: String,
      required: true,
    },
    name: {
      type: String,
      required: true,
    },
    isActive: {
      type: Boolean,
      default: true,
    },
    lastHeartbeat: {
      type: Date,
      default: null,
    },
  },
  { timestamps: true }
);

deviceSchema.set('toJSON', {
  transform(doc, ret) {
    ret.id = ret._id.toString();
    delete ret._id;
    delete ret.__v;
    delete ret.secretKey; // never expose secret
    return ret;
  },
});

const Device = mongoose.model('Device', deviceSchema);
module.exports = Device;