package com.example.kviz.service;

import java.util.List;

import com.example.kviz.model.Admin;
import com.example.kviz.repository.AdminRepository;

public class AdminService {
    private final AdminRepository adminRepository = new AdminRepository();
    public void save(Admin admin) {
        adminRepository.save(admin);
    }

    public void update(Admin admin) {
        adminRepository.update(admin);
    }

    public void delete(Admin admin) {
        adminRepository.delete(admin);
    }

    public Admin findById(Long id) {
        return adminRepository.findById(id);
    }

    public List<Admin> findAll() {
        return adminRepository.findAll();
    }

    public Admin findByUsername(String username) {
        return adminRepository.findByUsername(username);
    }

    public List<Admin> findWithPagination(int offset, int limit) {
        return adminRepository.findWithPagination(offset, limit);
    }

    public boolean doesAdminWithUsernameExist(String username) {
        Admin admin = findByUsername(username);
        if (admin == null) {
            return false;
        }
        return true;
    }

    public boolean isLoginDataCorrect(String username, String password) {
        Admin admin = findByUsername(username);
        if (admin == null) {
            return false;
        }
        if (admin.getPassword().equals(password)) {
            return true;
        }
        return false;
    }

    public boolean isAdminSuperAdmin(String username) {
        Admin admin = findByUsername(username);
        if (admin.isIsSuperAdmin()) {
            return true;
        }
        return false;
    }

    public void registerNewAdmin(Admin admin) throws Error {
        if (doesAdminWithUsernameExist(admin.getUsername())) {
            throw new RuntimeException("Admin with username already exists");
        }
        save(admin);
    }

    public void updateAdmin(Admin newAdmin) throws Error {
        Admin sameUsernameAdmin = findByUsername(newAdmin.getUsername());
        if (sameUsernameAdmin != null && !(sameUsernameAdmin.getId().equals(newAdmin.getId()))) {
            throw new RuntimeException("Cannot change username to one that is already taken");
        }
        update(newAdmin);
    }
}
