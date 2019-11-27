package lk.ijse.dep.pos;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

@Import(JPAConfig.class)
@Configuration
@ComponentScan("lk.ijse.dep.pos")
public class AppConfig {
}
