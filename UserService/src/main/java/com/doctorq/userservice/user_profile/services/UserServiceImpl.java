package com.doctorq.userservice.user_profile.services;

import com.doctorq.userservice.exception.BadRequestException;
import com.doctorq.userservice.response.ApiResponse;
import com.doctorq.userservice.user.entities.User;
import com.doctorq.userservice.user.repository.UserRepository;
import com.doctorq.userservice.user_profile.dtos.UserProfile;
import com.doctorq.userservice.user_profile.dtos.UserProfileRequest;
import com.doctorq.userservice.user_profile.dtos.UserResponseDto;
import com.doctorq.userservice.user_profile.mapper.UserMapper;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper mapper;
    private final Storage storage;

    @Override
    public List<UserResponseDto> getAllUsers() {
        try {
            List<User> users = userRepository.findAll();

            return users.stream().map(mapper::fromUser).toList();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public UserResponseDto addUserProfile(UserProfileRequest request, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new UsernameNotFoundException("User not found")
        );

        if (user.getUserProfile() != null) {
            throw new BadRequestException("User profile already added update the existing");
        }

        if (!user.isEnabled()) {
            throw new BadRequestException("Verify user account to proceed");
        }

        UserProfile userProfile = mapper.toUserProfile(request);

        user.setUserProfile(userProfile);
        userProfile.setUser(user);
        User savedUser = userRepository.save(user);
        return mapper.fromUser(savedUser);
    }

    @Override
    public UserResponseDto updateUserProfile(UserProfileRequest request, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new UsernameNotFoundException("User not found")
        );

        UserProfile userProfile = user.getUserProfile();
        if (userProfile != null) {
            userProfile.setGender(request.gender());
            userProfile.setDateOfBirth(request.dateOfBirth());
            userProfile.setAddress(request.address());
            userProfile.setProfileUrl(request.profileUrl());
        } else {
            throw new UsernameNotFoundException("User profile not found");
        }

        userRepository.save(user);
        return mapper.fromUser(user);
    }

    @Override
    public UserResponseDto getUserById(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new UsernameNotFoundException("User with id " + userId + " not found")
        );

        return mapper.fromUser(user);
    }

    @Override
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new UsernameNotFoundException("User with id " + userId + " not found")
        );

        userRepository.delete(user);
    }

    @Override
    public String uploadProfileImage(MultipartFile multipartFile, Long userId) throws Exception {
        try {
            String fileName = multipartFile.getOriginalFilename();
            assert fileName != null;

            String name = String.format("%s%s%s", "profile_", userId, ".jpeg");

            File jpegFile = this.convertToJpegFile(multipartFile, name);
            return this.uploadFile(jpegFile, name);

        } catch (Exception exception) {
            throw new Exception(exception.getMessage());
        }
    }

    private File convertToJpegFile(MultipartFile multipartFile, String name) throws IOException {
        File tempFile = new File(System.getProperty("java.io.tmpdir"), name);

        try {
            BufferedImage originalImage = ImageIO.read(multipartFile.getInputStream());

            if (originalImage == null) {
                throw new IOException("Invalid image file format");
            }

            BufferedImage jpegImage = new BufferedImage(
                    originalImage.getWidth(),
                    originalImage.getHeight(),
                    BufferedImage.TYPE_INT_RGB
            );

            //Fill background with white(in case original had transparency)
            Graphics2D graphics2D = jpegImage.createGraphics();
            graphics2D.setColor(Color.WHITE);
            graphics2D.fillRect(0, 0, jpegImage.getWidth(), jpegImage.getHeight());
            graphics2D.drawImage(originalImage, 0, 0, null);
            graphics2D.dispose();

            //Write as jpeg with quality setting
            ImageWriter jpegWriter = ImageIO.getImageWritersByFormatName("JPEG").next();
            ImageWriteParam jpegWriteParam = jpegWriter.getDefaultWriteParam();
            jpegWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            jpegWriteParam.setCompressionQuality(0.85f);

            try (FileOutputStream fileOutputStream = new FileOutputStream(tempFile);
                 ImageOutputStream imageOutputStream = ImageIO.createImageOutputStream(fileOutputStream)
            ) {
                jpegWriter.setOutput(imageOutputStream);
                jpegWriter.write(null, new IIOImage(jpegImage, null, null), jpegWriteParam);
                jpegWriter.dispose();
            }

        } catch (IOException exception) {
            throw new IOException("Error converting image to jpeg: " + exception.getMessage());
        }
        return tempFile;
    }

    private String uploadFile(File file, String fileName) throws IOException {

        BlobId blobId = BlobId.of("doctorq-q.firebasestorage.app", fileName);

        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("image/jpeg").build();

        storage.create(blobInfo, Files.readAllBytes(file.toPath()));

        file.delete();

        String DOWNLOAD_URL = "https://firebasestorage.googleapis.com/v0/b/doctorq-q.firebasestorage.app/o/%s?alt=media";

        return String.format(DOWNLOAD_URL, URLEncoder.encode(fileName, StandardCharsets.UTF_8));
    }
}
