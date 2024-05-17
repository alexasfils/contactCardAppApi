package io.getarrays.contanctapi.resorce;

import static io.getarrays.contanctapi.constant.Constant.*;

import java.io.IOException;
import java.net.URI;
import java.nio.file.*;
import java.sql.SQLException;

import javax.sql.rowset.serial.SerialException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import static org.springframework.http.MediaType.*;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import io.getarrays.contanctapi.domain.Contact;
import io.getarrays.contanctapi.service.ContactService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/contacts")
@RequiredArgsConstructor
public class ContactResource {
	
	@Autowired
	private ContactService service;
	
	@PostMapping
	public ResponseEntity<Contact> createContact(@RequestBody Contact contact){
		//	return ResponseEntity.ok().body(service.createContact(contact));
		return ResponseEntity.created(URI.create("/contacts/userID")).body(service.createContact(contact));
	}
	
	@GetMapping
	public ResponseEntity<Page<Contact>> getContacts(@RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(value = "size", defaultValue = "10") int size){
		return ResponseEntity.ok().body(service.getAllcontacts(page, size));
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Contact> getContact(@PathVariable(value = "id") String id){
		return ResponseEntity.ok().body(service.getContact(id));
	}
	
	@PutMapping("/photo")
	public ResponseEntity<String> uplodPhoto(@RequestParam("id") String id, @RequestParam("file")MultipartFile file){
		return ResponseEntity.ok().body(service.uploadPhoto(id, file));
	}
	
	@GetMapping(path = "/image/{filename}", produces = { IMAGE_PNG_VALUE, IMAGE_JPEG_VALUE })
	public byte[] getPhoto(@PathVariable("filename") String filename) throws IOException, SerialException, SQLException {
	    return Files.readAllBytes(Paths.get(PHOTO_DIRECTORY + filename));
	}
	
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> rimuoviContatto(@PathVariable(value = "id") String id) {

		service.rimuoviContattoPerId(id);
		return  ResponseEntity.noContent().build();
	}

	

}
