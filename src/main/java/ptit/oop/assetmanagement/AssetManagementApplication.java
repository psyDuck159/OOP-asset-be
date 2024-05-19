package ptit.oop.assetmanagement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@EnableSpringDataWebSupport
class AssetManagementApplication {
    private final Logger logger = LoggerFactory.getLogger(AssetManagementApplication.class);

    @Value("${spring.profiles.active:}")
    private String profile;


    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(AssetManagementApplication.class, args);

        ConfigurableListableBeanFactory beanFactory = run.getBeanFactory();
        PasswordEncoder passwordEncoder = beanFactory.getBean(PasswordEncoder.class);
        System.out.println(passwordEncoder.encode("Admin@123"));
    }

    @Bean
    public void testProfile() {
        logger.info("Profiles: " + profile);
    }
}