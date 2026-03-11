import mongoose from 'mongoose';

/**
 * Connect to MongoDB using Mongoose
 * Uses DATABASE_URL from environment variables
 */
export const connectDB = async () => {
  try {
    const mongoUri = process.env.DATABASE_URL || 'mongodb://localhost:27017/employee-management';
    
    await mongoose.connect(mongoUri);
    
    console.log('✓ MongoDB connected successfully');
    console.log(`Connected to: ${mongoUri}`);
  } catch (error) {
    console.error('✗ Database connection failed:', error);
    process.exit(1);
  }
};

/**
 * Disconnect from MongoDB
 */
export const disconnectDB = async () => {
  try {
    await mongoose.disconnect();
    console.log('✓ MongoDB disconnected');
  } catch (error) {
    console.error('✗ Database disconnection failed:', error);
    process.exit(1);
  }
};
