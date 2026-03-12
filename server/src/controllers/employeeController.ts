import { Request, Response } from 'express';
import Employee from '../models/Employee';
import User from '../models/User';

// GET /v1/admin/employees
export const getEmployees = async (req: Request, res: Response) => {
    try {
        const { page = 1, limit = 20, q, department } = req.query;
        const filter: any = { isActive: true };
        
        if (q) {
            filter.name = { $regex: q, $options: 'i' };
        }
        if (department) {
            filter.department = department;
        }

        const employees = await Employee.find(filter)
            .limit(Number(limit))
            .skip((Number(page) - 1) * Number(limit));
            
        const total = await Employee.countDocuments(filter);

        res.json({
            items: employees.map(e => ({
                id: e._id,
                email: e.email,
                name: e.name,
                role: 'EMPLOYEE',
                department: e.department,
                profileImageURL: null
            })),
            page: Number(page),
            limit: Number(limit),
            total
        });
    } catch (error) {
        res.status(500).json({ 
            error: { code: 'SERVER_ERROR', message: error instanceof Error ? error.message : 'Internal server error' } 
        });
    }
};

// GET /v1/admin/employees/:id
export const getEmployeeById = async (req: Request, res: Response) => {
    try {
        const employee = await Employee.findById(req.params.id);
        if (!employee) {
            return res.status(404).json({ 
                error: { code: 'NOT_FOUND', message: 'Employee not found' } 
            });
        }
        
        res.json({
            id: employee._id,
            email: employee.email,
            name: employee.name,
            role: 'EMPLOYEE',
            department: employee.department,
            profileImageURL: null
        });
    } catch (error) {
        res.status(500).json({ 
            error: { code: 'SERVER_ERROR', message: 'Internal server error' } 
        });
    }
};

// POST /v1/admin/employees
export const createEmployee = async (req: Request, res: Response) => {
    try {
        const { name, email, department, password } = req.body;

        if (!name || !email || !password) {
            return res.status(400).json({
                error: { code: 'VALIDATION_ERROR', message: 'Name, email and password are required' }
            });
        }

        const existing = await Employee.findOne({ email });
        if (existing) {
            return res.status(409).json({
                error: { code: 'EMAIL_TAKEN', message: 'Email already registered' }
            });
        }

        const employee = new Employee({ name, email, department });
        await employee.save();

        // Also create a User for login
        const user = new User({ 
            name, 
            email, 
            password, 
            role: 'employee',
            employeeId: employee._id 
        });
        await user.save();

        res.status(201).json({
            id: employee._id,
            email: employee.email,
            name: employee.name,
            role: 'EMPLOYEE',
            department: employee.department,
            profileImageURL: null
        });
    } catch (error) {
        console.error('Error creating employee:', error);
        res.status(500).json({ 
            error: { code: 'SERVER_ERROR', message: error instanceof Error ? error.message : 'Internal server error' } 
        });
    }
};

// PATCH /v1/admin/employees/:id
export const updateEmployee = async (req: Request, res: Response) => {
    try {
        const employee = await Employee.findByIdAndUpdate(req.params.id, req.body, { new: true });
        if (!employee) {
            return res.status(404).json({ 
                error: { code: 'NOT_FOUND', message: 'Employee not found' } 
            });
        }
        
        res.json({
            id: employee._id,
            email: employee.email,
            name: employee.name,
            role: 'EMPLOYEE',
            department: employee.department,
            profileImageURL: null
        });
    } catch (error) {
        res.status(500).json({ 
            error: { code: 'SERVER_ERROR', message: 'Internal server error' } 
        });
    }
};

// DELETE /v1/admin/employees/:id
export const deleteEmployee = async (req: Request, res: Response) => {
    try {
        const employee = await Employee.findByIdAndUpdate(req.params.id, { isActive: false }, { new: true });
        if (!employee) {
            return res.status(404).json({ 
                error: { code: 'NOT_FOUND', message: 'Employee not found' } 
            });
        }
        res.status(204).send();
    } catch (error) {
        res.status(500).json({ 
            error: { code: 'SERVER_ERROR', message: 'Internal server error' } 
        });
    }
};
