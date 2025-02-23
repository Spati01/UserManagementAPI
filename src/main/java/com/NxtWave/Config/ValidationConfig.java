package com.NxtWave.Config;




import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
public class ValidationConfig {

    @Value("${validation.mob_num_regex}")
    private String mobileNumberRegex;

    @Value("${validation.pan_num_regex}")
    private String panNumberRegex;

    @Value("${error.full_name_required}")
    private String fullNameRequiredMessage;

    @Value("${error.invalid_mobile}")
    private String invalidMobileMessage;

    @Value("${error.invalid_pan}")
    private String invalidPanMessage;

    @Value("${error.invalid_manager}")
    private String invalidManagerMessage;

    @Value("${error.user_not_found}")
    private String userNotFoundMessage;
}
