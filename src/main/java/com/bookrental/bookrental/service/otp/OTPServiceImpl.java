package com.bookrental.bookrental.service.otp;

import com.bookrental.bookrental.config.CustomMessageSource;
import com.bookrental.bookrental.constants.ModuleNameConstants;
import com.bookrental.bookrental.enums.Message;
import com.bookrental.bookrental.exception.AppException;
import com.bookrental.bookrental.model.OTPToken;
import com.bookrental.bookrental.model.User;
import com.bookrental.bookrental.repository.OTPRepository;
import com.bookrental.bookrental.repository.UserRepository;
import com.bookrental.bookrental.service.email.NewEmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class OTPServiceImpl implements OTPService {

    private final OTPRepository otpRepository;
    private final NewEmailService emailService;

    private final UserRepository userRepository;

    private final CustomMessageSource customMessageSource;
    private final PasswordEncoder passwordEncoder;

    @Override
    public int generateAndStore(String email) {
        userRepository.findByEmail(email).orElseThrow(() -> new AppException(customMessageSource.get(Message.ID_NOT_FOUND.getCode(), ModuleNameConstants.USER)));
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(5000);
        OTPToken otpToken = new OTPToken();
        otpToken.setEmail(email);
        otpToken.setOtp(otp);
        otpToken.setExpirationTime(expirationTime);
        try {
            emailService.sendEmail(email, "BookRental OTP", "Your OTP is: " + otp);
        } catch (Exception e) {
//            throw new AppException("failed to send email");
            throw new AppException(customMessageSource.get(Message.FAILED.getCode(), ModuleNameConstants.OTP));
        }
        try {
            otpRepository.save(otpToken);
        } catch (Exception e) {
//            throw new AppException("failed to save otp");
            throw new AppException(customMessageSource.get(Message.FAILED.getCode(), ModuleNameConstants.OTP));
        }
        return otp;
    }

    @Override
    public boolean verifyOTP(String email, int userEnteredOTP, String newPassword) {
        Optional<OTPToken> optionalOtpToken = otpRepository.findById(email);

        if (optionalOtpToken.isPresent()) {
            OTPToken otpToken = optionalOtpToken.get();
            if (otpToken.getExpirationTime().isAfter(LocalDateTime.now()) && otpToken.getOtp() == userEnteredOTP) {
                changePassword(newPassword, email);
                otpRepository.deleteById(email);
                return true;
            }
        }
        return false;
    }

    public Boolean changePassword(String newPassword, String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() ->
                new AppException(customMessageSource.get(Message.ID_NOT_FOUND.getCode(), ModuleNameConstants.USER)));
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return true;
    }
}

