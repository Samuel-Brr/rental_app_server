package com.rental.app.services;

import com.rental.app.utils.Mapper;
import com.rental.app.dtos.RentalDto;
import com.rental.app.entities.Rental;
import com.rental.app.repositories.RentalRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    private static final Logger logger = LoggerFactory.getLogger(RentalService.class);

    @Value("${app.upload.dir:${user.home}}")
    private String uploadDir;

    private final RentalRepository rentalRepository;
    private final JwtService jwtService;

    public RentalService(RentalRepository rentalRepository, JwtService jwtService) {
        this.rentalRepository = rentalRepository;
        this.jwtService = jwtService;
    }

    /**
     * Retrieves a rental by its ID.
     *
     * @param id The ID of the rental to retrieve.
     * @return The Rental entity.
     * @throws RuntimeException if the rental is not found.
     */
    public Rental getRentalById(Long id) {
        return rentalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rental not found with id: " + id));
    }

    /**
     * Retrieves all rentals.
     *
     * @return A list of all Rental entities.
     */
    public List<Rental> getAllRentals() {
        return rentalRepository.findAll();
    }

    /**
     * Adds a new rental.
     *
     * @param rentalDto The DTO containing the rental details.
     * @return The created Rental entity.
     * @throws RuntimeException() if there's an error storing the picture file.
     */
    @Transactional
    public Rental addRental(RentalDto rentalDto) {
        logger.debug("Adding new rental: {}", rentalDto);

        String filePath = storeFile(rentalDto.getPicture());
        Rental rental = Mapper.mapRentalDtoToRental(rentalDto, filePath);
        rental.setOwner(jwtService.getCurrentUser());

        Rental savedRental = rentalRepository.save(rental);
        logger.info("Rental added successfully with ID: {}", savedRental.getId());
        return savedRental;
    }

    /**
     * Updates an existing rental.
     *
     * @param rentalDto The DTO containing the updated rental details.
     * @param id The ID of the rental to update.
     * @return The updated Rental entity.
     * @throws RuntimeException if the rental is not found.
     */
    @Transactional
    public Rental updateRental(RentalDto rentalDto, Long id) {
        logger.debug("Updating rental with ID: {}", id);

        Rental rental = getRentalById(id);
        updateRentalFields(rental, rentalDto);

        Rental updatedRental = rentalRepository.save(rental);
        logger.info("Rental updated successfully with ID: {}", updatedRental.getId());
        return updatedRental;
    }

    private String storeFile(MultipartFile file) {
        try {
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
            String fileExtension = StringUtils.getFilenameExtension(fileName);
            String uniqueFileName = UUID.randomUUID() + "." + fileExtension;

            Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(uploadPath);

            Path filePath = uploadPath.resolve(uniqueFileName);
            Files.copy(file.getInputStream(), filePath);

            return filePath.toString();
        } catch (IOException e) {
            logger.error("Failed to store file", e);
            throw new RuntimeException("Failed to store file", e);
        }
    }

    private void updateRentalFields(Rental rental, RentalDto rentalDto) {
        rental.setName(rentalDto.getName());
        rental.setSurface(new BigDecimal(rentalDto.getSurface()));
        rental.setPrice(new BigDecimal(rentalDto.getPrice()));
        rental.setDescription(rentalDto.getDescription());
    }
}