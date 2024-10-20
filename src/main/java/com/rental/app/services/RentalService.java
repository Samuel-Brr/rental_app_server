package com.rental.app.services;

import com.rental.app.dtos.UpdateRentalDto;
import com.rental.app.utils.Mapper;
import com.rental.app.dtos.CreateRentalDto;
import com.rental.app.entities.Rental;
import com.rental.app.repositories.RentalRepository;
import jakarta.servlet.ServletContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

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
    private final ServletContext servletContext;
    private final Environment environment;

    public RentalService(RentalRepository rentalRepository, JwtService jwtService, ServletContext servletContext, Environment environment) {
        this.rentalRepository = rentalRepository;
        this.jwtService = jwtService;
        this.servletContext = servletContext;
        this.environment = environment;
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
     * @param createRentalDto The DTO containing the rental details.
     * @return The created Rental entity.
     * @throws RuntimeException() if there's an error storing the picture file.
     */
    @Transactional
    public Rental addRental(CreateRentalDto createRentalDto) {
        logger.debug("Adding new rental: {}", createRentalDto);

        String filePath = storeFile(createRentalDto.getPicture());
        Rental rental = Mapper.mapRentalDtoToRental(createRentalDto, filePath);
        rental.setOwner(jwtService.getCurrentUser());

        Rental savedRental = rentalRepository.save(rental);
        logger.info("Rental added successfully with ID: {}", savedRental.getId());
        return savedRental;
    }

    /**
     * Updates an existing rental.
     *
     * @param updateRentalDto The DTO containing the updated rental details.
     * @param id The ID of the rental to update.
     * @return The updated Rental entity.
     * @throws RuntimeException if the rental is not found.
     */
    @Transactional
    public Rental updateRental(UpdateRentalDto updateRentalDto, Long id) {
        logger.debug("Updating rental with ID: {}", id);

        Rental rental = getRentalById(id);
        updateRentalFields(rental, updateRentalDto);

        Rental updatedRental = rentalRepository.save(rental);
        logger.info("Rental updated successfully with ID: {}", updatedRental.getId());
        return updatedRental;
    }

    /**
     * Stores a file in the server's file system and returns its URL.
     *
     * @param file The MultipartFile to be stored.
     * @return A String representing the URL of the stored file.
     * @throws RuntimeException If there's an error while storing the file.
     */
    private String storeFile(MultipartFile file) {
        try {
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
            String fileExtension = StringUtils.getFilenameExtension(fileName);
            String uniqueFileName = UUID.randomUUID() + "." + fileExtension;

            Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(uploadPath);

            Path filePath = uploadPath.resolve(uniqueFileName);
            Files.copy(file.getInputStream(), filePath);

            String baseUrl = getBaseUrl();
            return baseUrl + "/api/rentals/image/" + uniqueFileName;
        } catch (IOException e) {
            logger.error("Failed to store file", e);
            throw new RuntimeException("Failed to store file", e);
        }
    }

    /**
     * Generates the base URL for the current server environment.
     *
     * This method dynamically constructs the base URL using the server's
     * configuration. It determines the protocol (http/https), port,
     * host address, and context path.
     *
     * @return A String representing the base URL of the server.
     */
    private String getBaseUrl() {
        String protocol = environment.getProperty("server.ssl.key-store") != null ? "https" : "http";
        String serverPort = environment.getProperty("server.port", "8080");
        String contextPath = servletContext.getContextPath();

        return UriComponentsBuilder.newInstance()
                .scheme(protocol)
                .host(environment.getProperty("server.address", "localhost"))
                .port(serverPort)
                .path(contextPath)
                .build()
                .toUriString();
    }

    private void updateRentalFields(Rental rental, UpdateRentalDto updateRentalDto) {
        rental.setName(updateRentalDto.getName());
        rental.setSurface(new BigDecimal(updateRentalDto.getSurface()));
        rental.setPrice(new BigDecimal(updateRentalDto.getPrice()));
        rental.setDescription(updateRentalDto.getDescription());
    }
}