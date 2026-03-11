const mongoose = require('mongoose');


async function ConnectDB() {
  try {
    await mongoose.connect(process.env.mongo_db_uri)
    console.log('Connected to MongoDB');
  } catch (error) {
    console.error('Error connecting to MongoDB:', error);
    process.exit(1); // Exit the process with an error code
  }
}

module.exports = ConnectDB; 

