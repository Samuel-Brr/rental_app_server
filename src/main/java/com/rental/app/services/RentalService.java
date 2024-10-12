package com.rental.app.services;

import com.rental.app.Utils.Mapper;
import com.rental.app.dtos.RentalDto;
import com.rental.app.entities.Rental;
import com.rental.app.repositories.RentalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Service responsible for rental-related operations.
 * This service handles the creation, retrieval, and update of rental listings in the application.
 */
@Service
public class RentalService {

    @Value("${app.upload.dir:${user.home}}")
    private String UPLOAD_DIR;

    @Autowired
    private RentalRepository rentalRepo;
    @Autowired
    private JwtService jwtService;

    public Rental getRentalById(Long id) {
        return rentalRepo.getReferenceById(id);
    }

    public List<Rental> getAllRentals() {
        return rentalRepo.findAll();
    }

    public Rental addRental(RentalDto rentalDto) {
        MultipartFile picture = rentalDto.getPicture();
        try {
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(picture.getOriginalFilename()));
            String fileExtension = StringUtils.getFilenameExtension(fileName);
            String uniqueFileName = UUID.randomUUID() + "." + fileExtension;

            Path uploadPath = Paths.get(UPLOAD_DIR).toAbsolutePath().normalize();
            Files.createDirectories(uploadPath);

            Path filePath = uploadPath.resolve(uniqueFileName);
            Files.copy(picture.getInputStream(), filePath);

            Rental rental = Mapper.MapRentalDtoToRental(rentalDto, filePath.toString());
            rental.setOwner(jwtService.getCurrentUser());
            return rentalRepo.save(rental);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to store file", e);
        }
    }

    public Rental updateRental(RentalDto rentalDto, Long id) {
        Rental rental = rentalRepo.getReferenceById(id);
        rental.setName(rentalDto.getName());
        rental.setSurface(new BigDecimal(rentalDto.getSurface()));
        rental.setPrice(new BigDecimal(rentalDto.getPrice()));
        rental.setDescription(rentalDto.getDescription());
        return rentalRepo.save(rental);
    }
}
