package com.campustrack.controller;

import com.campustrack.dto.ContactRequest;
import com.campustrack.model.Contact;
import com.campustrack.repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/contact")
public class ContactController {
    
    @Autowired
    private ContactRepository contactRepository;
    
    // Process contact form submissions and save to database
    @PostMapping
    public ResponseEntity<?> submitContact(@RequestBody ContactRequest request) {
        try {
            // Make sure all required fields are filled
            if (request.getName() == null || request.getEmail() == null || 
                request.getPhone() == null || request.getMessage() == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "All fields are required"));
            }
            
            // Create and save contact
            Contact contact = new Contact();
            contact.setName(request.getName());
            contact.setEmail(request.getEmail());
            contact.setPhone(request.getPhone());
            contact.setMessage(request.getMessage());
            
            contactRepository.save(contact);
            
            return ResponseEntity.ok(Map.of("message", "Message sent successfully!"));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("message", "Failed to send message", "error", e.getMessage()));
        }
    }
    
    // Get all contact messages (for admin)
    @GetMapping
    public ResponseEntity<?> getAllContacts() {
        try {
            List<Contact> contacts = contactRepository.findAllByOrderByCreatedAtDesc();
            return ResponseEntity.ok(contacts);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("message", "Failed to fetch contacts", "error", e.getMessage()));
        }
    }
    
    // Mark contact as read (for admin)
    @PutMapping("/{id}/read")
    public ResponseEntity<?> markAsRead(@PathVariable String id) {
        try {
            Contact contact = contactRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Contact not found"));
            contact.setStatus("read");
            contactRepository.save(contact);
            return ResponseEntity.ok(Map.of("message", "Marked as read"));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("message", "Failed to update contact", "error", e.getMessage()));
        }
    }
    
    // Delete contact (for admin)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteContact(@PathVariable String id) {
        try {
            contactRepository.deleteById(id);
            return ResponseEntity.ok(Map.of("message", "Contact deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("message", "Failed to delete contact", "error", e.getMessage()));
        }
    }
}
