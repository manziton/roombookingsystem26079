package com.example.eventbooking.service;

import com.example.eventbooking.exception.ResourceAlreadyExistsException;
import com.example.eventbooking.exception.ResourceNotFoundException;
import com.example.eventbooking.model.Student;
import com.example.eventbooking.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    @Autowired
    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Student getStudentById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));
    }

    public Student getStudentByStudentId(String studentId) {
        return studentRepository.findByStudentId(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with studentId: " + studentId));
    }

    @Transactional
    public Student createStudent(Student student) {
        if (studentRepository.existsByStudentId(student.getStudentId())) {
            throw new ResourceAlreadyExistsException("Student with ID " + student.getStudentId() + " already exists.");
        }
        if (studentRepository.existsByEmail(student.getEmail())) {
            throw new ResourceAlreadyExistsException("Student with email " + student.getEmail() + " already exists.");
        }
        return studentRepository.save(student);
    }

    @Transactional
    public Student updateStudent(Long id, Student studentDetails) {
        Student student = getStudentById(id);

        if (!student.getStudentId().equals(studentDetails.getStudentId()) && 
            studentRepository.existsByStudentId(studentDetails.getStudentId())) {
            throw new ResourceAlreadyExistsException("Student with ID " + studentDetails.getStudentId() + " already exists.");
        }

        if (!student.getEmail().equals(studentDetails.getEmail()) && 
            studentRepository.existsByEmail(studentDetails.getEmail())) {
            throw new ResourceAlreadyExistsException("Student with email " + studentDetails.getEmail() + " already exists.");
        }

        student.setStudentId(studentDetails.getStudentId());
        student.setName(studentDetails.getName());
        student.setEmail(studentDetails.getEmail());
        student.setPhone(studentDetails.getPhone());
        student.setDepartment(studentDetails.getDepartment());

        return studentRepository.save(student);
    }

    @Transactional
    public void deleteStudent(Long id) {
        Student student = getStudentById(id);
        studentRepository.delete(student);
    }
}
