package io.getarrays.contanctapi.service;

import static io.getarrays.contanctapi.constant.Constant.PHOTO_DIRECTORY;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import io.getarrays.contanctapi.domain.Contact;
import io.getarrays.contanctapi.repository.ContactsRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional(rollbackOn = Exception.class)
@RequiredArgsConstructor
public class ContactService {
	@Autowired
	private ContactsRepository repo;
	
	public Page<Contact> getAllcontacts(int page, int size){
		return repo.findAll(PageRequest.of(page, size, Sort.by("name")));
	}
	
	public Contact getContact (String id) {
		return repo.findById(id).orElseThrow(() -> new RuntimeException("Contact not found"));
	}
	
	public Contact createContact(Contact contact) {
		return repo.save(contact);
	}
	
	public void deleteContact(Contact c) {
		repo.delete(c);
	}
	
	public String uploadPhoto(String id, MultipartFile file) {
//		log.info("Saving picture for user ID: {}" , id);
		Contact contact = getContact(id);
		String photoUrl = photoFunction.apply(id, file);
		contact.setPhotoUrl(photoUrl);
		repo.save(contact);
		
		return photoUrl;
	}
	
	private final Function<String, String> fileExtension = filename -> Optional.of(filename).filter(name -> name.contains(".")).map(name -> "." + name.substring(filename.lastIndexOf(".") + 1)).orElse(".png");
	
	private final BiFunction<String, MultipartFile, String> photoFunction = (id, image) -> {
		String fileName = id + fileExtension.apply(image.getOriginalFilename());
		try {
			Path fileStorageLocation = Paths.get(PHOTO_DIRECTORY).toAbsolutePath().normalize();
			if(!Files.exists(fileStorageLocation)) {Files.createDirectories(fileStorageLocation);}
			Files.copy(image.getInputStream(), fileStorageLocation.resolve(fileName), REPLACE_EXISTING);
			return ServletUriComponentsBuilder.fromCurrentContextPath().path("/contacts/image/" + fileName).toUriString();
		}catch (Exception exception){
			throw new RuntimeException("Unable to save image");
		}

	};

}
